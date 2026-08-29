package com.cleannrooster.rpgmana.neoforge;

import com.cleannrooster.rpgmana.Rpgmana;
import com.cleannrooster.rpgmana.compat.accessories.AccessoriesCompat;
import net.minecraft.registry.RegistryKeys;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
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
}
