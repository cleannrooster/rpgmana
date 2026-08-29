package com.cleannrooster.rpgmana;

import com.cleannrooster.rpgmana.client.InGameHud;
import com.cleannrooster.rpgmana.client.ManaElements;
import com.cleannrooster.rpgmana.client.StormRenderer;
import net.spell_engine.api.effect.CustomModelStatusEffect;

import static com.cleannrooster.rpgmana.Rpgmana.STORM;

/**
 * Loader-independent client setup. Platform entrypoints call {@link #init()}
 * after registration and hook {@link #HUD}'s render into their HUD pipeline.
 */
public class RpgmanaClient {

    public static final InGameHud HUD = new InGameHud();

    public static void init() {
        CustomModelStatusEffect.register(STORM.value(), new StormRenderer());
        ManaElements.register();
    }
}
