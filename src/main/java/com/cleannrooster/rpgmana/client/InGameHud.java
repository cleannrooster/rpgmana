package com.cleannrooster.rpgmana.client;

import com.cleannrooster.globehud.api.GlobeHudApi;
import com.cleannrooster.rpgmana.Rpgmana;
import com.cleannrooster.rpgmana.api.ManaInterface;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.spell_engine.internals.casting.SpellCaster;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class InGameHud  implements HudRenderCallback {
	private static final Identifier manabar = Identifier.of(Rpgmana.MOD_ID,"textures/gui/bar_mana.png");
	private static final Identifier manabead = Identifier.of(Rpgmana.MOD_ID,"textures/gui/manabead.png");
	private static final Identifier manabead2= Identifier.of(Rpgmana.MOD_ID,"textures/gui/manabead2.png");
	private static final Identifier manabead3 = Identifier.of(Rpgmana.MOD_ID,"textures/gui/manabead3.png");
	private static final Identifier manabead4= Identifier.of(Rpgmana.MOD_ID,"textures/gui/manabead4.png");
	private static final Identifier manabead12 = Identifier.of(Rpgmana.MOD_ID,"textures/gui/manabead12.png");
	private static final Identifier manabead22= Identifier.of(Rpgmana.MOD_ID,"textures/gui/manabead22.png");
	private static final Identifier manabead32 = Identifier.of(Rpgmana.MOD_ID,"textures/gui/manabead32.png");
	private static final Identifier manabead42= Identifier.of(Rpgmana.MOD_ID,"textures/gui/manabead42.png");

	private static final Identifier manabeadneg = Identifier.of(Rpgmana.MOD_ID,"textures/gui/manabeadneg.png");
	private static final Identifier manabead2neg= Identifier.of(Rpgmana.MOD_ID,"textures/gui/manabead2neg.png");
	private static final Identifier manabead3neg = Identifier.of(Rpgmana.MOD_ID,"textures/gui/manabead3neg.png");
	private static final Identifier manabead4neg= Identifier.of(Rpgmana.MOD_ID,"textures/gui/manabead4neg.png");
	private static final Identifier manabead12neg = Identifier.of(Rpgmana.MOD_ID,"textures/gui/manabead12neg.png");
	private static final Identifier manabead22neg= Identifier.of(Rpgmana.MOD_ID,"textures/gui/manabead22neg.png");
	private static final Identifier manabead32neg = Identifier.of(Rpgmana.MOD_ID,"textures/gui/manabead32neg.png");
	private static final Identifier manabead42neg= Identifier.of(Rpgmana.MOD_ID,"textures/gui/manabead42neg.png");


	private static final Identifier manabarneg = Identifier.of(Rpgmana.MOD_ID,"textures/gui/bar_mana_neg.png");

	private static final Identifier manaticks = Identifier.of(Rpgmana.MOD_ID,"textures/gui/bar_ticks.png");
	private static final Identifier manaback = Identifier.of(Rpgmana.MOD_ID,"textures/gui/bar_back.png");


	public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {

		PlayerEntity playerEntity = MinecraftClient.getInstance().player;
		if (playerEntity == null) {
			return;
		}

		// globehud renders the globes (including the mana globe registered by ManaElements);
		// fall through to the legacy mana display only when the mana globe isn't showing
		if (GlobeHudApi.hudEnabled() && GlobeHudApi.isElementEnabled(ManaElements.MANA_GLOBE)) {
			return;
		}

		if (playerEntity.getAttributeInstance(Rpgmana.MANA_ATTRIBUTE) != null) {
			ManaInterface manaInterface = (ManaInterface) playerEntity;
			if (manaInterface.getMaxMana() >= 1 && manaInterface.getTimeFull() < 60 && !(playerEntity instanceof SpellCaster.Client client && client.isCastingSpell())) {
				if(!Rpgmana.clientConfig.alt) {

					int scaledHeight = drawContext.getScaledWindowHeight();
					int scaledWidth = drawContext.getScaledWindowWidth();
					scaledHeight -= 7;
					MinecraftClient.getInstance().getProfiler().push("mana");
					RenderSystem.enableBlend();
					RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
					double manaprop = (manaInterface.getMana() / manaInterface.getMaxMana());
					int i = (int) (manaprop * (182));
					int k = scaledWidth;

					drawContext.drawTexture(manaback, (int) ((k / 2) - 182 / 2), scaledHeight - 11 - 3 - 8, 0, 0, 182, 5, 182, 5);
					if (manaprop > 0) {
						drawContext.drawTexture(manabar, (int) ((k / 2) - 182 / 2), scaledHeight - 11 - 3 - 8, 0, 0, Math.min(i, 182), 5, 182, 5);
					} else {
						drawContext.drawTexture(manabarneg, (int) ((k / 2) - 182 / 2), scaledHeight - 11 - 3 - 8, 0, 0, Math.min(-i, 182), 5, 182, 5);

					}
					drawContext.drawTexture(manaticks, (int) ((k / 2) - 182 / 2), scaledHeight - 11 - 3 - 8, 0, 0, Math.min(i, 182), 5, 182, 5);
					RenderSystem.disableBlend();
					RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
				}
				else{

					int x = 0;
					int y = 0;
					MinecraftClient client = MinecraftClient.getInstance();
					if (client != null) {
						int width = client.getWindow().getScaledWidth();
						int height = client.getWindow().getScaledHeight();

						x = width / 2;
						y = height;
					}
					int z = 0;
					if (manaInterface.getTimeFull() > 50) {
						z += 10 * (manaInterface.getTimeFull() - 50) / 10;
					}
					double manaprop = (manaInterface.getMana() / manaInterface.getMaxMana());
					if( manaprop > 0) {
						drawContext.drawTexture(manabead12, x - 8-6 - z, y / 2 -8-6-z, 0, 0, 12, 12, 12,12);

					}
					 if(manaprop >= 0.125 ) {
						 drawContext.drawTexture(manabead, x - 8 - 6 - z, y / 2 - 8 - 6 - z, 0, 0, 12, 12, 12, 12);
					 }
					if (manaprop >= 0.25 ) {
						drawContext.drawTexture(manabead22, x - 8-6-z, y / 2 +8-6+z, 0, 0, 12, 12, 12,12);

					}
					if(manaprop >= 0.375 ) {
						drawContext.drawTexture(manabead2, x - 8 - 6-z, y / 2 + 8 - 6+z, 0, 0, 12, 12, 12, 12);
					}
					if (manaprop >= 0.5 ) {
						drawContext.drawTexture(manabead32, x + 7-6+z, y / 2 +8-6+z, 0, 0, 12, 12, 12,12);

					}
					if(manaprop >= 0.625 ) {
						drawContext.drawTexture(manabead3, x + 7 - 6+z, y / 2 + 8 - 6+z	, 0, 0, 12, 12, 12, 12);
					}
					if (manaprop >= 0.75 ) {
						drawContext.drawTexture(manabead42	, x + 7-6+z, y / 2 -8-6-z, 0, 0, 12, 12, 12,12);

					}
					if(manaprop >= 0.875 ){
						drawContext.drawTexture(manabead4, x + 7-6+z, y / 2 -8-6-z, 0, 0, 12, 12, 12,12);

					}

					if( manaprop < 0) {
						drawContext.drawTexture(manabead12neg, x - 8-6, y / 2 -8-6, 0, 0, 12, 12, 12,12);

					}
					if( manaprop < -0.125) {
						drawContext.drawTexture(manabeadneg, x - 8-6, y / 2 - 8-6,0, 0, 12, 12, 12, 12);

					}
					if ( manaprop < -0.25) {
						drawContext.drawTexture(manabead22neg, x - 8-6, y / 2 +8-6, 0, 0, 12, 12, 12,12);

					}
					if( manaprop < -0.375) {
						drawContext.drawTexture(manabead2neg, x - 8-6, y / 2 + 8-6, 0, 0, 12, 12, 12, 12);

					}if ( manaprop < -0.5) {
						drawContext.drawTexture(manabead32neg, x + 7-6, y / 2 +8-6, 0, 0, 12, 12, 12,12);

					}
					if( manaprop < -0.625){
						drawContext.drawTexture(manabead3neg, x + 7-6, y / 2 +8-6, 0, 0, 12, 12, 12,12);

					}if ( manaprop < -0.75) {
						drawContext.drawTexture(manabead42neg	, x + 7-6, y / 2 -8-6, 0, 0, 12, 12, 12,12);

					}
					if(manaprop < -0.875){
						drawContext.drawTexture(manabead4neg, x+ 7-6, y / 2 -8-6, 0, 0, 12, 12, 12,12);

					}


				}
			}

		}
	}



}