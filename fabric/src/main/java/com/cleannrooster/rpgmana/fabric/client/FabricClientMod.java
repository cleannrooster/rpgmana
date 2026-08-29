package com.cleannrooster.rpgmana.fabric.client;

import com.cleannrooster.rpgmana.RpgmanaClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public final class FabricClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RpgmanaClient.init();

        HudRenderCallback.EVENT.register(RpgmanaClient.HUD::render);
    }
}
