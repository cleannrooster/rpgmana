package com.cleannrooster.rpgmana.neoforge;

import com.cleannrooster.rpgmana.Rpgmana;
import com.cleannrooster.rpgmana.compat.accessories.AccessoriesCompat;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.RegistryKeys;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(Rpgmana.MOD_ID)
public final class NeoForgeMod {
    public NeoForgeMod(IEventBus modBus) {
        // Accessories is a required dependency here, so the ring items always
        // get their Accessories variant. Must run before item registration.
        AccessoriesCompat.init();

        // Run our common setup.
        Rpgmana.init();

        modBus.addListener(RegisterEvent.class, NeoForgeMod::register);
        modBus.addListener(EntityAttributeModificationEvent.class, NeoForgeMod::onModifyEntityAttributes);
    }

    public static void register(RegisterEvent event) {
        // The register* methods are idempotent and pull attributes in first,
        // so cross-registry references resolve regardless of event order.
        event.register(RegistryKeys.ATTRIBUTE, reg -> {
            Rpgmana.registerAttributes();
        });
        event.register(RegistryKeys.STATUS_EFFECT, reg -> {
            Rpgmana.registerEffects();
        });
        event.register(RegistryKeys.ITEM, reg -> {
            Rpgmana.registerItems();
        });
    }

    /**
     * The createLivingAttributes/createPlayerAttributes mixins are timing-fragile here:
     * Connector-translated Fabric mods can classload the vanilla default-attribute table
     * during early loading, before our attributes register, and the mixins then skip.
     * This event fires after registration and feeds the supplier map NeoForge actually
     * uses, so every living entity reliably gets the mana attributes.
     */
    public static void onModifyEntityAttributes(EntityAttributeModificationEvent event) {
        Rpgmana.registerAttributes();
        for (EntityType<? extends LivingEntity> type : event.getTypes()) {
            event.add(type, Rpgmana.MANA_ATTRIBUTE);
            event.add(type, Rpgmana.MANA_REGEN_ATTRIBUTE);
            event.add(type, Rpgmana.MANA_COST_ATTRIBUTE);
            event.add(type, Rpgmana.MANA_DAMAGE_TAKEN_ATTRIBUTE);
            event.add(type, Rpgmana.MIND_OVER_MATTER_ATTRIBUTE);
        }
    }
}
