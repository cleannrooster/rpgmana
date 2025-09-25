package com.cleannrooster.rpgmana;

import com.cleannrooster.rpgmana.api.ManaInstance;
import com.cleannrooster.rpgmana.api.ManaInterface;
import com.cleannrooster.rpgmana.api.SpellcostMixinInterface;
import com.cleannrooster.rpgmana.client.StormRenderer;
import com.cleannrooster.rpgmana.config.ClientConfig;
import com.cleannrooster.rpgmana.config.ClientConfigWrapper;
import com.cleannrooster.rpgmana.config.ServerConfig;
import com.cleannrooster.rpgmana.config.ServerConfigWrapper;
import com.cleannrooster.rpgmana.item.ItemInit;
import com.cleannrooster.rpgmana.network.ConfigSync;
import com.cleannrooster.spellblades.SpellbladesAndSuch;
import com.cleannrooster.spellblades.effect.CustomEffect;
import com.cleannrooster.spellblades.effect.DeathchillRenderer;
import com.cleannrooster.spellblades.effect.Spellstrike;
import com.extraspellattributes.api.SpellStatusEffect;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.stat.Stat;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.spell_engine.api.effect.CustomModelStatusEffect;
import net.spell_engine.api.effect.CustomStatusEffect;
import net.spell_engine.api.effect.Synchronized;
import net.spell_engine.api.render.CustomModels;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.internals.casting.SpellCast;
import net.spell_engine.internals.casting.SpellCasterEntity;
import net.spell_engine.internals.target.SpellTarget;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellPowerMechanics;
import net.spell_power.api.SpellSchool;
import net.spell_power.api.SpellSchools;
import net.tinyconfig.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static net.spell_engine.internals.SpellHelper.*;

public class Rpgmana implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("rpgmana");
	public static final String MOD_ID = "rpgmana";


	public static StatusEffect BORROWEDPOWER ;
	public static  StatusEffect OVERDRIVE;
	public static  StatusEffect STABILITY ;
	public static Potion BORROWEDPOWERPOTION;
	public static Potion OVERDRIVEPOTION;
	public static Potion STABILITYPOTION;


	public static ServerConfig config;
	private static PacketByteBuf configSerialized = PacketByteBufs.create();
	public static ClientConfig clientConfig	;

	public static final EntityAttribute MANA = new ClampedEntityAttribute("attribute.name.rpgmana.mana", 0,0,999999);
	public static final EntityAttribute MANAREGEN = new ClampedEntityAttribute("attribute.name.rpgmana.manaregen", 4,-999999,999999);
	public static final EntityAttribute MANACOST = new ClampedEntityAttribute("attribute.name.rpgmana.manacost", 100,0,999999);
	public static final EntityAttribute MANADAMAGEREDUCTION = new ClampedEntityAttribute("attribute.name.rpgmana.mana_damage_reduction", 100,100,999999);
	public static final EntityAttribute MINDOVERMATTER = new ClampedEntityAttribute("attribute.name.rpgmana.mind_over_matter", 0,0,999999);

	public static  RegistryEntry.Reference<EntityAttribute> MANA_ATTRIBUTE;
	public static  RegistryEntry.Reference<EntityAttribute> MANA_REGEN_ATTRIBUTE;
	public static  RegistryEntry.Reference<EntityAttribute> MANA_COST_ATTRIBUTE;
	public static  RegistryEntry.Reference<EntityAttribute> MANA_DAMAGE_TAKEN_ATTRIBUTE;
	public static  RegistryEntry.Reference<EntityAttribute> MIND_OVER_MATTER_ATTRIBUTE;
	public static RegistryEntry.Reference<StatusEffect> MEDITATE;

	private static float getManaCost(PlayerEntity player, Spell spell, SpellCast.Action action, float progress){
		if(spell.cost == null || (spell.cost.item.id == null || !spell.cost.item.id.contains("rune") ||  spell.cost.item.id.contains("arrow") || spell.cost.item.id.contains("bullet") || spell.cost.item.id.contains("jacket") || spell.cost.item.id.contains("round") || spell.cost.item.id.contains("cartrdge"))){
			return 0;
		}
		float channelMultiplier = 1.0F;
		switch (action) {
			case CHANNEL:
				channelMultiplier = channelValueMultiplier(spell);
				break;
			case RELEASE:
				if (isChanneled(spell)) {
					channelMultiplier = 0F;
				} else {
					channelMultiplier = progress >= 1.0F ? 1.0F : 0.0F;
				}

		}
		ImpactContext context = new ImpactContext(channelMultiplier, 1.0F, (Vec3d)null, SpellPower.getSpellPower(spell.school, player), SpellTarget.FocusMode.DIRECT,0);
		float coeff = 0;
		int proj = 1;
		if(spell.impacts != null && ((SpellcostMixinInterface) spell.cost).calculateManaCost() ==true) {

			for (Spell.Impact impact : spell.impacts) {
				if (impact.action != null && impact.action.damage != null) {
					coeff += impact.action.damage.spell_power_coefficient;
				}
			}
			if (spell.impacts != null && spell.impacts.size() > 0) {
				coeff /= spell.impacts.size();
			}
			if (spell.release != null && spell.target != null && spell.deliver != null && spell.deliver.projectile != null) {
				proj += spell.deliver.projectile.launch_properties.extra_launch_count;
			}
		}
		float mult = (float) ((player.getAttributeValue(Rpgmana.MANA_COST_ATTRIBUTE) * 0.01F));

		return mult* context.total()*(((SpellcostMixinInterface)spell.cost).calculateManaCost() ? (float) Math.max( 20 , 40  * coeff * proj ) :
				((SpellcostMixinInterface)spell.cost).getManaCost());
	}
	public static  RegistryEntry.Reference<StatusEffect> STORM;

	static{

		MANA_ATTRIBUTE =  Registry.registerReference(Registries.ATTRIBUTE,Identifier.of(MOD_ID,"mana"),MANA);
		MANA_REGEN_ATTRIBUTE = Registry.registerReference(Registries.ATTRIBUTE,Identifier.of(MOD_ID,"manaregen"),MANAREGEN);
		MANA_COST_ATTRIBUTE  =Registry.registerReference(Registries.ATTRIBUTE,Identifier.of(MOD_ID,"manacost"),MANACOST);
		MANA_DAMAGE_TAKEN_ATTRIBUTE  =Registry.registerReference(Registries.ATTRIBUTE,Identifier.of(MOD_ID,"mana_damage_reduction"),MANADAMAGEREDUCTION);
		MIND_OVER_MATTER_ATTRIBUTE  =Registry.registerReference(Registries.ATTRIBUTE,Identifier.of(MOD_ID,"mind_over_matter"),MINDOVERMATTER);
	}
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		CustomModels.registerModelIds(List.of(
				Identifier.of(MOD_ID, "projectile/manastorm")
		));
		STORM =  Registry.registerReference(Registries.STATUS_EFFECT,Identifier.of(MOD_ID,"manastorm"),new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0xff4bdd).addAttributeModifier(MANA_COST_ATTRIBUTE,Identifier.of(MOD_ID,"manacost_storm"),0.1F,EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

		AutoConfig.register(ServerConfigWrapper.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
		AutoConfig.register(ClientConfigWrapper.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
		config = AutoConfig.getConfigHolder(ServerConfigWrapper.class).getConfig().server;
		clientConfig = AutoConfig.getConfigHolder(ClientConfigWrapper.class).getConfig().client;
		configSerialized = ConfigSync.write(config);
		Synchronized.configure(STORM.value(),true);

		MEDITATE = Registry.registerReference(Registries.STATUS_EFFECT,Identifier.of(MOD_ID,"meditate"),new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0x8BAFE0)
				.addAttributeModifier(MANA_REGEN_ATTRIBUTE,Identifier.of("mana_regen_meditate"),4, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
				.addAttributeModifier(MANA_ATTRIBUTE,Identifier.of("mana_meditate"),1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
				.addAttributeModifier(MANA_DAMAGE_TAKEN_ATTRIBUTE,Identifier.of("mana_taken_meditate"),0.5, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE)
				.addAttributeModifier(MIND_OVER_MATTER_ATTRIBUTE,Identifier.of("mind_meditate"),20, EntityAttributeModifier.Operation.ADD_VALUE));

	




		for(SpellSchool school : SpellSchools.all()) {
			school.addSource(SpellSchool.Trait.POWER, SpellSchool.Apply.MULTIPLY, queryArgs -> {
				if (queryArgs.entity() instanceof PlayerEntity player && player instanceof SpellCasterEntity casterEntity && player instanceof ManaInterface manaInterface && manaInterface.getMana() > 0.1) {
					List<ManaInstance> list = manaInterface.getManaInstances();
					double amount = 0;
					for(ManaInstance instance : list){
						amount += instance.value;
					}
					return amount*0.01/4;
				} else {
					return 0D;
				}
			});
		}

		MANA.setTracked(true);
		MANAREGEN.setTracked(true);
		MANACOST.setTracked(true);

		ItemInit.register();
		LOGGER.info("Loading RPGMANA");

	}
}