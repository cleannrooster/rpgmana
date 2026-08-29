package com.cleannrooster.rpgmana.fabric.compat.trinkets;

import net.fabricmc.loader.api.FabricLoader;

public class TrinketsCompat {
    public static void init() {
        if (!FabricLoader.getInstance().isModLoaded("trinkets")) {
            return;
        }
        TrinketsItemHelper.register();
    }
}
