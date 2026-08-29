package com.cleannrooster.rpgmana;

import com.cleannrooster.rpgmana.api.ManaInstance;
import com.cleannrooster.rpgmana.api.ManaInterface;
import com.cleannrooster.rpgmana.config.ClientConfig;
import com.cleannrooster.rpgmana.config.ClientConfigWrapper;
import com.cleannrooster.rpgmana.config.ServerConfig;
import com.cleannrooster.rpgmana.config.ServerConfigWrapper;
import com.cleannrooster.rpgmana.internals.ManaInject;
import com.cleannrooster.rpgmana.item.ItemInit;
import com.cleannrooster.rpgmana.network.ConfigSync;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
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
import net.minecraft.util.Identifier;

import net.spell_engine.api.effect.CustomStatusEffect;
import net.spell_engine.api.effect.Synchronized;
import net.spell_engine.api.render.CustomModels;
import net.spell_engine.internals.casting.SpellCasterEntity;
import net.spell_power.api.SpellSchool;
import net.spell_power.api.SpellSchools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Loader-independent mod core. Platform entrypoints call, in this order:
 * {@link #registerAttributes()}, {@link #registerEffects()}, {@link #registerItems()}
 * (from their registration hooks) and {@link #init()} (general setup, safe to run
 * before or after registration).
 */
public class Rpgmana {
    public static final Logger LOGGER = LoggerFactory.getLogger("rpgmana");
    public static final String MOD_ID = "rpgmana";


    public static StatusEffect BORROWEDPOWER;
    public static StatusEffect OVERDRIVE;
    public static StatusEffect STABILITY;
    public static Potion BORROWEDPOWERPOTION;
    public static Potion OVERDRIVEPOTION;
    public static Potion STABILITYPOTION;


    public static ServerConfig config;
    private static PacketByteBuf configSerialized = null;
    public static ClientConfig clientConfig;

    public static final EntityAttribute MANA = new ClampedEntityAttribute("attribute.name.rpgmana.mana", 0, 0, 999999);
    public static final EntityAttribute MANAREGEN = new ClampedEntityAttribute("attribute.name.rpgmana.manaregen", 4, -999999, 999999);
    public static final EntityAttribute MANACOST = new ClampedEntityAttribute("attribute.name.rpgmana.manacost", 100, 0, 999999);
    public static final EntityAttribute MANADAMAGEREDUCTION = new ClampedEntityAttribute("attribute.name.rpgmana.mana_damage_reduction", 100, 100, 999999);
    public static final EntityAttribute MINDOVERMATTER = new ClampedEntityAttribute("attribute.name.rpgmana.mind_over_matter", 0, 0, 999999);

    public static RegistryEntry.Reference<EntityAttribute> MANA_ATTRIBUTE;
    public static RegistryEntry.Reference<EntityAttribute> MANA_REGEN_ATTRIBUTE;
    public static RegistryEntry.Reference<EntityAttribute> MANA_COST_ATTRIBUTE;
    public static RegistryEntry.Reference<EntityAttribute> MANA_DAMAGE_TAKEN_ATTRIBUTE;
    public static RegistryEntry.Reference<EntityAttribute> MIND_OVER_MATTER_ATTRIBUTE;
    public static RegistryEntry.Reference<StatusEffect> MEDITATE;
    public static RegistryEntry.Reference<StatusEffect> STORM;

    private static boolean attributesRegistered = false;
    private static boolean effectsRegistered = false;
    private static boolean itemsRegistered = false;

    public static void registerAttributes() {
        if (attributesRegistered) {
            return;
        }
        attributesRegistered = true;
        MANA_ATTRIBUTE = Registry.registerReference(Registries.ATTRIBUTE, Identifier.of(MOD_ID, "mana"), MANA);
        MANA_REGEN_ATTRIBUTE = Registry.registerReference(Registries.ATTRIBUTE, Identifier.of(MOD_ID, "manaregen"), MANAREGEN);
        MANA_COST_ATTRIBUTE = Registry.registerReference(Registries.ATTRIBUTE, Identifier.of(MOD_ID, "manacost"), MANACOST);
        MANA_DAMAGE_TAKEN_ATTRIBUTE = Registry.registerReference(Registries.ATTRIBUTE, Identifier.of(MOD_ID, "mana_damage_reduction"), MANADAMAGEREDUCTION);
        MIND_OVER_MATTER_ATTRIBUTE = Registry.registerReference(Registries.ATTRIBUTE, Identifier.of(MOD_ID, "mind_over_matter"), MINDOVERMATTER);
    }

    public static void registerEffects() {
        if (effectsRegistered) {
            return;
        }
        effectsRegistered = true;
        registerAttributes();

        STORM = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MOD_ID, "manastorm"),
                new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0xff4bdd)
                        .addAttributeModifier(MANA_COST_ATTRIBUTE, Identifier.of(MOD_ID, "manacost_storm"), 0.1F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        Synchronized.configure(STORM.value(), true);

        MEDITATE = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MOD_ID, "meditate"),
                new CustomStatusEffect(StatusEffectCategory.BENEFICIAL, 0x8BAFE0)
                        .addAttributeModifier(MANA_REGEN_ATTRIBUTE, Identifier.of("mana_regen_meditate"), 4, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                        .addAttributeModifier(MANA_ATTRIBUTE, Identifier.of("mana_meditate"), 1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                        .addAttributeModifier(MANA_DAMAGE_TAKEN_ATTRIBUTE, Identifier.of("mana_taken_meditate"), 0.5, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE)
                        .addAttributeModifier(MIND_OVER_MATTER_ATTRIBUTE, Identifier.of("mind_meditate"), 20, EntityAttributeModifier.Operation.ADD_VALUE));
    }

    public static void registerItems() {
        if (itemsRegistered) {
            return;
        }
        itemsRegistered = true;
        registerAttributes();
        ItemInit.register();
    }

    public static void init() {
        CustomModels.registerModelIds(List.of(
                Identifier.of(MOD_ID, "projectile/manastorm")
        ));
        ManaInject.init();
        AutoConfig.register(ServerConfigWrapper.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
        AutoConfig.register(ClientConfigWrapper.class, PartitioningSerializer.wrap(JanksonConfigSerializer::new));
        config = AutoConfig.getConfigHolder(ServerConfigWrapper.class).getConfig().server;
        clientConfig = AutoConfig.getConfigHolder(ClientConfigWrapper.class).getConfig().client;
        configSerialized = ConfigSync.write(config);

        for (SpellSchool school : SpellSchools.all()) {
            school.addSource(SpellSchool.Trait.POWER, SpellSchool.Apply.MULTIPLY, queryArgs -> {
                if (queryArgs.entity() instanceof PlayerEntity player && player instanceof SpellCasterEntity casterEntity && player instanceof ManaInterface manaInterface && manaInterface.getMana() > 0.1) {
                    List<ManaInstance> list = manaInterface.getManaInstances();
                    double amount = 0;
                    for (ManaInstance instance : list) {
                        amount += instance.value;
                    }
                    return amount * 0.01 / 4;
                } else {
                    return 0D;
                }
            });
        }

        MANA.setTracked(true);
        MANAREGEN.setTracked(true);
        MANACOST.setTracked(true);

        LOGGER.info("Loading RPGMANA");
    }
}
