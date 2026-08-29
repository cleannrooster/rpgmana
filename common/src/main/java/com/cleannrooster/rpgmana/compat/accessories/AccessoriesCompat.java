package com.cleannrooster.rpgmana.compat.accessories;

import net.fabricmc.loader.api.FabricLoader;

public class AccessoriesCompat {
    public static final String MOD_ID = "accessories";

    /** Call before item registration. On NeoForge, Accessories is a required dependency. */
    public static void init() {
        // Fabric loader query works on NeoForge too, through the Forgified Fabric API
        if (!FabricLoader.getInstance().isModLoaded(MOD_ID)) {
            return;
        }
        AccessoriesItemHelper.register();
    }
}
