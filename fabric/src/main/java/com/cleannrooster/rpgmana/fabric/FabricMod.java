package com.cleannrooster.rpgmana.fabric;

import com.cleannrooster.rpgmana.Rpgmana;
import com.cleannrooster.rpgmana.compat.accessories.AccessoriesCompat;
import com.cleannrooster.rpgmana.fabric.compat.trinkets.TrinketsCompat;
import net.fabricmc.api.ModInitializer;

public final class FabricMod implements ModInitializer {
    @Override
    public void onInitialize() {
        // Slot-mod glue must pick the item factory before items register.
        // Trinkets wins over Accessories when both are present, matching the shipped trinkets data.
        AccessoriesCompat.init();
        TrinketsCompat.init();

        Rpgmana.registerAttributes();
        Rpgmana.registerEffects();
        Rpgmana.registerItems();
        Rpgmana.init();
    }
}
