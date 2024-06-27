package com.cleannrooster.rpgmana;

import com.cleannrooster.rpgmana.api.ManaInstance;
import com.cleannrooster.rpgmana.api.ManaInterface;
import com.cleannrooster.rpgmana.api.SpellcostMixinInterface;
import com.cleannrooster.rpgmana.config.ClientConfig;
import com.cleannrooster.rpgmana.config.ClientConfigWrapper;
import com.cleannrooster.rpgmana.config.ServerConfig;
import com.cleannrooster.rpgmana.config.ServerConfigWrapper;
import com.cleannrooster.rpgmana.effects.BorrowedPower;
import com.cleannrooster.rpgmana.effects.Overdrive;
import com.cleannrooster.rpgmana.effects.Stability;
import com.cleannrooster.rpgmana.enchantment.*;
import com.cleannrooster.rpgmana.item.ItemInit;
import com.cleannrooster.rpgmana.loot.Default;
import com.cleannrooster.rpgmana.loot.LootConfig;
import com.cleannrooster.rpgmana.loot.LootHelper;
import com.cleannrooster.rpgmana.mixin.BrewingRecipeRegistryMixin;
import com.cleannrooster.rpgmana.network.ConfigSync;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.bettercombat.BetterCombat;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.runes.api.RuneItems;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.casting.SpellCast;
import net.spell_engine.internals.casting.SpellCasterEntity;
import net.spell_power.api.SpellPower;
import net.spell_power.api.SpellSchool;
import net.spell_power.api.SpellSchools;
import net.spell_power.api.enchantment.SpellPowerEnchanting;
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

	public static ConfigManager<LootConfig> lootConfig = new ConfigManager<>
			("loot_v2", Default.loot)
			.builder()
			.setDirectory(MOD_ID)
			.sanitize(true)
			.build();
	public static StatusEffect BORROWEDPOWER ;
	public static  StatusEffect OVERDRIVE;
	public static  StatusEffect STABILITY ;
	public static Potion BORROWEDPOWERPOTION;
	public static Potion OVERDRIVEPOTION;
	public static Potion STABILITYPOTION;
	public static final Enchantment ARCHMAGE = new Archmage(Enchantment.Rarity.RARE,EnchantmentTarget.BREAKABLE,EquipmentSlot.values());
	public static final Enchantment LUCIDITY = new Lucidity(Enchantment.Rarity.RARE,EnchantmentTarget.BREAKABLE,EquipmentSlot.values());
	public static final Enchantment MANAFUSED = new Manafused(Enchantment.Rarity.RARE,EnchantmentTarget.BREAKABLE,EquipmentSlot.values());
	public static final Enchantment MANASTABILIZED= new ManaStabilized(Enchantment.Rarity.RARE,EnchantmentTarget.BREAKABLE,EquipmentSlot.values());
	public static final Enchantment RESPLENDENT  = new Resplendent(Enchantment.Rarity.RARE,EnchantmentTarget.BREAKABLE,EquipmentSlot.values());
	public static final Enchantment MINDOVERMATTER  = new MindOverMatter(Enchantment.Rarity.RARE,EnchantmentTarget.BREAKABLE,EquipmentSlot.values());

	public static ServerConfig config;
	private static PacketByteBuf configSerialized = PacketByteBufs.create();
	public static ClientConfig clientConfig	;

	public static final ClampedEntityAttribute MANA = new ClampedEntityAttribute("attribute.name.rpgmana.mana", 0,0,999999);
	public static final ClampedEntityAttribute MANAREGEN = new ClampedEntityAttribute("attribute.name.rpgmana.manaregen", 4,-999999,999999);
	public static final ClampedEntityAttribute MANACOST = new ClampedEntityAttribute("attribute.name.rpgmana.manacost", 100,0,999999);

	private static float getManaCost(PlayerEntity player, Spell spell, SpellCast.Action action, float progress){
		float channelMultiplier = 1.0F;

		SpellHelper.ImpactContext context = new SpellHelper.ImpactContext(channelMultiplier, 1.0F, (Vec3d)null, new SpellPower.Result(SpellSchools.ARCANE,0,0,0), impactTargetingMode(spell));
		float coeff = 0;
		int proj = 1;
		for(Spell.Impact impact : spell.impact){
			if(impact.action != null && impact.action.damage != null) {
				coeff += impact.action.damage.spell_power_coefficient;
			}
		}
		if(spell.impact.length > 0){
			coeff /= spell.impact.length;
		}
		if(spell.release.target.projectile != null) {
			proj += spell.release.target.projectile.launch_properties.extra_launch_count;
		}
		return ((SpellcostMixinInterface)spell.cost).calculateManaCost() ? (float) Math.max( 20 * context.total(), (1 + 0.05 * (SpellPowerEnchanting.getEnchantmentLevel(Rpgmana.ARCHMAGE, player,player.getMainHandStack()) - SpellPowerEnchanting.getEnchantmentLevel(Rpgmana.MANASTABILIZED, player,player.getMainHandStack()))) *40 * context.total() * coeff * proj * ((float) player.getAttributeValue(Rpgmana.MANACOST) * 0.01F)) :
				((SpellcostMixinInterface)spell.cost).getManaCost();
	}
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		lootConfig.refresh();
		AutoConfig.register(ServerConfigWrapper.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
		AutoConfig.register(ClientConfigWrapper.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
		config = AutoConfig.getConfigHolder(ServerConfigWrapper.class).getConfig().server;
		clientConfig = AutoConfig.getConfigHolder(ClientConfigWrapper.class).getConfig().client;
		configSerialized = ConfigSync.write(config);
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			sender.sendPacket(ConfigSync.ID, configSerialized);
		});

		BORROWEDPOWER = new BorrowedPower(StatusEffectCategory.NEUTRAL, 3694022)
				.addAttributeModifier(Rpgmana.MANA,"ee7652b3-cb11-44c1-9730-befd558a1695",40, EntityAttributeModifier.Operation.ADDITION)
				.addAttributeModifier(Rpgmana.MANAREGEN,"3fe9ec66-b4ca-4be0-a4b4-678366f84865",-0.5, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
		OVERDRIVE  = new Overdrive(StatusEffectCategory.NEUTRAL, 11101546)
				.addAttributeModifier(Rpgmana.MANA,"719aaeb0-5d4b-4fcd-a9fa-7ca5d6ef0f1d",-20, EntityAttributeModifier.Operation.ADDITION)
				.addAttributeModifier(Rpgmana.MANAREGEN,"4ae741b0-e749-44cd-8e3a-62951910eac5",0.5, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
				.addAttributeModifier(Rpgmana.MANACOST,"95065ab2-71e7-4427-b8a3-97222074608a",0.5,EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
		STABILITY = new Stability(StatusEffectCategory.NEUTRAL, 9154528)
				.addAttributeModifier(Rpgmana.MANAREGEN,"a30c135a-4425-437d-a1cc-5f701e8ebdcc",0.25, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
				.addAttributeModifier(Rpgmana.MANACOST,"dd6b2040-bccb-4a25-a17d-0c65c23a4eea",-0.25,EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
		BORROWEDPOWERPOTION = new Potion(new StatusEffectInstance(BORROWEDPOWER,20*30,0));
		OVERDRIVEPOTION = new Potion(new StatusEffectInstance(OVERDRIVE,20*30,0));
		STABILITYPOTION = new Potion(new StatusEffectInstance(STABILITY,20*30,0));

		Registry.register(Registries.STATUS_EFFECT,new Identifier(MOD_ID,"borrowed_power"),BORROWEDPOWER);
		Registry.register(Registries.STATUS_EFFECT,new Identifier(MOD_ID,"overdrive"),OVERDRIVE);
		Registry.register(Registries.STATUS_EFFECT,new Identifier(MOD_ID,"magical_stability"),STABILITY);
		Registry.register(Registries.ATTRIBUTE,new Identifier(MOD_ID,"mana"),MANA);
		Registry.register(Registries.ATTRIBUTE,new Identifier(MOD_ID,"manaregen"),MANAREGEN);
		Registry.register(Registries.ATTRIBUTE,new Identifier(MOD_ID,"manacost"),MANACOST);
		Registry.register(Registries.ENCHANTMENT,new Identifier(MOD_ID,"inspiration"),ARCHMAGE);
		Registry.register(Registries.ENCHANTMENT,new Identifier(MOD_ID,"lucidity"),LUCIDITY);
		Registry.register(Registries.ENCHANTMENT,new Identifier(MOD_ID,"manafused"),MANAFUSED);
		Registry.register(Registries.ENCHANTMENT,new Identifier(MOD_ID,"manastabilized"),MANASTABILIZED);
		Registry.register(Registries.ENCHANTMENT,new Identifier(MOD_ID,"resplendent"),RESPLENDENT);
		Registry.register(Registries.POTION,new Identifier(MOD_ID,"borrowed_power"),BORROWEDPOWERPOTION);
		Registry.register(Registries.POTION,new Identifier(MOD_ID,"overdrive"),OVERDRIVEPOTION);
		Registry.register(Registries.POTION,new Identifier(MOD_ID,"stability"),STABILITYPOTION);
		Registry.register(Registries.ENCHANTMENT,new Identifier(MOD_ID,"mind_over_matter"),MINDOVERMATTER);

		BrewingRecipeRegistryMixin.invokeRegisterPotionRecipe(Potions.AWKWARD, RuneItems.get(RuneItems.RuneType.ARCANE),BORROWEDPOWERPOTION);
		BrewingRecipeRegistryMixin.invokeRegisterPotionRecipe(Potions.AWKWARD, RuneItems.get(RuneItems.RuneType.FROST),STABILITYPOTION);
		BrewingRecipeRegistryMixin.invokeRegisterPotionRecipe(Potions.AWKWARD, RuneItems.get(RuneItems.RuneType.FIRE),OVERDRIVEPOTION);

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
		LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
			LootHelper.configure(id, tableBuilder, lootConfig.value, ItemInit.entryMap);
		});
	}
}