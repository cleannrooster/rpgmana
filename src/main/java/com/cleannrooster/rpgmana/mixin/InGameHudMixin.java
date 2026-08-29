package com.cleannrooster.rpgmana.mixin;

import com.cleannrooster.rpgmana.Rpgmana;
import com.cleannrooster.rpgmana.api.ManaInterface;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.spell_engine.internals.casting.SpellCaster;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = InGameHud.class,priority = Integer.MAX_VALUE)
public class InGameHudMixin {
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



    @Inject(at = @At("RETURN"), method = "shouldRenderExperience",  cancellable = true)
    private void shouldRenderExperience(CallbackInfoReturnable<Boolean> cir) {
        if(Rpgmana.clientConfig.noEXP){
            PlayerEntity playerEntity = MinecraftClient.getInstance().player;

            if (playerEntity != null ) {
                ManaInterface manaInterface = (ManaInterface) playerEntity;

                if (playerEntity instanceof SpellCaster.Client client && client.getSpellCastProcess() != null) return;
                if (manaInterface.getMaxMana() <= 0) return;
                if (manaInterface.getTimeFull() > 60) return;

                cir.setReturnValue(false);
            }
        }

    }

}
