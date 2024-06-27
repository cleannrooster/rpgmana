package com.cleannrooster.rpgmana;

import com.cleannrooster.rpgmana.client.InGameHud;
import com.cleannrooster.rpgmana.network.ConfigSync;
import com.invoke.client.HudOverlay;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class RpgmanaClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(new InGameHud());
        ClientPlayNetworking.registerGlobalReceiver(ConfigSync.ID, (client, handler, buf, responseSender) -> {
            var config = ConfigSync.read(buf);
            Rpgmana.config = config;
        });
    }
}
