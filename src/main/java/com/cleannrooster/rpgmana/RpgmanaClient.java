package com.cleannrooster.rpgmana;

import com.cleannrooster.rpgmana.client.InGameHud;
import com.cleannrooster.rpgmana.client.StormRenderer;
import com.cleannrooster.rpgmana.network.ConfigSync;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.util.Identifier;
import net.spell_engine.api.effect.CustomModelStatusEffect;
import net.spell_engine.api.render.CustomModels;

import java.util.List;

import static com.cleannrooster.rpgmana.Rpgmana.MOD_ID;
import static com.cleannrooster.rpgmana.Rpgmana.STORM;

public class RpgmanaClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CustomModelStatusEffect.register(STORM.value(), new StormRenderer());

        HudRenderCallback.EVENT.register(new InGameHud());
/*        ClientPlayNetworking.registerGlobalReceiver(ConfigSync.ID, (client, handler, buf, responseSender) -> {
            var config = ConfigSync.read(buf);
            Rpgmana.config = config;
        });*/
    }
}
