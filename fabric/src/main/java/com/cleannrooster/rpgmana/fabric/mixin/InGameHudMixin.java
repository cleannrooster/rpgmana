package com.cleannrooster.rpgmana.fabric.mixin;

import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = InGameHud.class, priority = Integer.MAX_VALUE)
public class InGameHudMixin {

    @Inject(at = @At("RETURN"), method = "shouldRenderExperience", cancellable = true)
    private void shouldRenderExperience(CallbackInfoReturnable<Boolean> cir) {
        // NeoForge does the same by cancelling the experience-bar GUI layer
        if (com.cleannrooster.rpgmana.client.InGameHud.shouldSuppressExpBar()) {
            cir.setReturnValue(false);
        }
    }
}
