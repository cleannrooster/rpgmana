package com.cleannrooster.rpgmana.client;

import com.cleannrooster.globehud.api.GlobeHudApi;
import com.cleannrooster.globehud.api.GlobeRenderer;
import com.cleannrooster.globehud.api.HudElement;
import com.cleannrooster.globehud.api.HudRect;
import com.cleannrooster.globehud.api.Sheen;
import com.cleannrooster.rpgmana.Rpgmana;
import com.cleannrooster.rpgmana.api.ManaInterface;
import com.cleannrooster.rpgmana.config.ClientConfigWrapper;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

/**
 * Rpgmana's mana globe and mana number, registered into the globehud element
 * registry. Meditating turns the globe arcane purple with a surging sheen.
 */
public final class ManaElements {

    public static final Identifier MANA_GLOBE = Identifier.of(Rpgmana.MOD_ID, "mana_globe");
    public static final Identifier MANA_TEXT = Identifier.of(Rpgmana.MOD_ID, "mana_text");

    private static final Identifier MANA_ICON = Identifier.of(Rpgmana.MOD_ID, "textures/item/mana.png");

    private static final float[] MANA_COLOR = {0.25f, 0.45f, 1.0f};
    private static final float[] MANA_NEG_COLOR = {0.45f, 0.2f, 0.55f};
    private static final float[] ARCANE_COLOR = {0.55f, 0.25f, 0.9f};
    private static final int MANA_TEXT_COLOR = 0xFF5C7CFF;
    private static final int ARCANE_TEXT_COLOR = 0xFFB388FF;

    /** Two out-of-phase shimmers plus rising bubbles — swirling, effervescent mana. */
    private static final Sheen MANA_BUBBLES = (context, fill, rect, fraction, time) -> {
        GlobeRenderer.tintedFill(context, fill, rect, 0.55f, 0.8f, 1.0f, 0.08f + 0.05f * (float) Math.sin(time * 0.003));
        GlobeRenderer.tintedFill(context, fill, rect, 0.7f, 0.9f, 1.0f, 0.05f + 0.04f * (float) Math.sin(time * 0.0047 + 2.1));

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        float radius = rect.w() / 2.0f;
        float centerX = rect.x() + radius;
        float centerY = rect.y() + rect.h() / 2.0f;
        int fillTop = rect.y() + (int) ((1.0f - Math.min(fraction, 1.0f)) * rect.h());
        int fillBottom = rect.y() + rect.h() - Math.max(1, rect.h() / 12);
        if (fillBottom <= fillTop) {
            return;
        }
        for (int i = 0; i < 8; i++) {
            long period = 2600 + 337L * i;
            float phase = ((time + i * 7919L) % period) / (float) period;
            float bubbleY = fillBottom - phase * (fillBottom - fillTop);
            float dy = bubbleY - centerY;
            float halfWidth = (float) Math.sqrt(Math.max(0, radius * radius - dy * dy));
            if (halfWidth < 2) {
                continue;
            }
            // bubbles wobble side to side while rising, staying inside the sphere
            float bubbleX = centerX + (float) Math.sin(i * 2.399f + time * 0.0008f * (1 + 0.13f * i)) * halfWidth * 0.65f;
            int size = Math.max(1, Math.round(rect.w() / 16.0f * (1 + (i % 3) * 0.5f)));
            // fade in near the bottom and out near the top, rather than popping in at full strength
            int alpha = (int) (MathHelper.clamp(0.6f * (float) Math.sin(Math.PI * phase) * (1.0f - phase * 0.4f), 0, 1) * 255);
            if (alpha <= 8) {
                continue;
            }
            int argb = (alpha << 24)
                    | ((int) (GlobeRenderer.adjustChannel(0.8f) * 255) << 16)
                    | ((int) (GlobeRenderer.adjustChannel(0.933f) * 255) << 8)
                    | (int) (GlobeRenderer.adjustChannel(1.0f) * 255);
            int bx = (int) bubbleX;
            int by = (int) bubbleY;
            context.fill(bx, by, bx + size, by + size, argb);
        }
    };

    /** Energized pulse with bright bands rushing upward — arcane power surging in. */
    private static final Sheen SURGE = (context, fill, rect, fraction, time) -> {
        GlobeRenderer.tintedFill(context, fill, rect, 0.8f, 0.6f, 1.0f, 0.10f + 0.07f * (float) Math.sin(time * 0.006));

        // two surges climbing at different speeds, fading in and out so they never pop
        for (int k = 0; k < 2; k++) {
            long period = 1400 + 500L * k;
            float cycle = ((time + 300L * k) % period) / (float) period;
            int band = Math.max(4, rect.h() / 5);
            int bandY = rect.y() + rect.h() - band - (int) (cycle * (rect.h() - band));
            float fade = (float) Math.sin(Math.PI * cycle);
            GlobeRenderer.featheredBandH(context, fill, rect, bandY, band,
                    0.9f, 0.75f, 1.0f, 0.4f * fade);
        }
    };

    private ManaElements() {}

    public static boolean hasMana(PlayerEntity player) {
        return player instanceof ManaInterface mana && mana.getMaxMana() >= 1
                && player.getAttributeInstance(Rpgmana.MANA_ATTRIBUTE) != null;
    }

    private static boolean meditating(PlayerEntity player) {
        return player != null && Rpgmana.MEDITATE != null && player.hasStatusEffect(Rpgmana.MEDITATE);
    }

    private static String manaText(PlayerEntity player) {
        return (player instanceof ManaInterface mana) ? String.valueOf((int) mana.getMana()) : "0";
    }

    public static void register() {
        // mana globe, bottom-right
        GlobeHudApi.register(new HudElement(MANA_GLOBE, "Mana Globe", true, true,
                (client, player, screenW, screenH) -> {
                    int s = GlobeHudApi.globeSize();
                    return new HudRect(screenW - s - GlobeHudApi.EDGE_MARGIN,
                            screenH - s - GlobeHudApi.BOTTOM_MARGIN, s, s);
                },
                (context, client, player, rect, editMode) -> {
                    boolean meditating = meditating(player);
                    float[] color = meditating ? ARCANE_COLOR : MANA_COLOR;
                    double fraction = 0;
                    if (player instanceof ManaInterface mana && mana.getMaxMana() >= 1) {
                        fraction = mana.getMana() / mana.getMaxMana();
                    }
                    if (fraction >= 0) {
                        GlobeRenderer.drawGlobe(context, rect, (float) fraction, color, color,
                                meditating ? SURGE : MANA_BUBBLES);
                    } else {
                        // mana debt: outline keeps the mana color, fill shows the debt in a dark purple
                        GlobeRenderer.drawGlobe(context, rect, (float) -fraction, color, MANA_NEG_COLOR, Sheen.NONE);
                    }
                },
                (player, editMode) -> editMode || hasMana(player)));

        // mana icon + mana number, right of the mana globe
        GlobeHudApi.register(new HudElement(MANA_TEXT, "Mana Number", true, true,
                (client, player, screenW, screenH) -> {
                    int s = GlobeHudApi.globeSize();
                    int fontH = client.textRenderer.fontHeight;
                    int w = GlobeHudApi.ICON_SIZE + GlobeHudApi.ICON_GAP + client.textRenderer.getWidth(manaText(player));
                    return new HudRect(screenW - s - GlobeHudApi.EDGE_MARGIN + s + GlobeHudApi.TEXT_GAP,
                            screenH - s - GlobeHudApi.BOTTOM_MARGIN + s / 2 - fontH / 2, w, fontH);
                },
                (context, client, player, rect, editMode) -> {
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                    context.drawTexture(MANA_ICON, rect.x(), rect.y(), GlobeHudApi.ICON_SIZE, GlobeHudApi.ICON_SIZE,
                            0, 0, 16, 16, 16, 16);
                    context.drawTextWithShadow(client.textRenderer, manaText(player),
                            rect.x() + GlobeHudApi.ICON_SIZE + GlobeHudApi.ICON_GAP, rect.y(),
                            meditating(player) ? ARCANE_TEXT_COLOR : MANA_TEXT_COLOR);
                },
                (player, editMode) -> editMode || hasMana(player)));

        // rpgmana's own display settings, surfaced in the globehud editor
        GlobeHudApi.registerEditorToggle("Legacy Bead Style",
                () -> Rpgmana.clientConfig.alt,
                v -> { Rpgmana.clientConfig.alt = v; saveClientConfig(); });
        GlobeHudApi.registerEditorToggle("Hide EXP Bar",
                () -> Rpgmana.clientConfig.noEXP,
                v -> { Rpgmana.clientConfig.noEXP = v; saveClientConfig(); });
    }

    private static void saveClientConfig() {
        AutoConfig.getConfigHolder(ClientConfigWrapper.class).save();
    }
}
