package com.cleannrooster.rpgmana.mixin;

import com.cleannrooster.rpgmana.Rpgmana;
import com.cleannrooster.rpgmana.api.ManaInterface;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DamageTracker.class)
public class DamageTrackerMixin {
    @Shadow
    private  LivingEntity entity;

    @Inject(at = @At("HEAD"), method = "onDamage", cancellable = true)
    public void onDamageRPGMana(DamageSource damageSource, float damage, CallbackInfo ci) {
        DamageTracker tracker = (DamageTracker) (Object) this;
        if( Rpgmana.MIND_OVER_MATTER_ATTRIBUTE != null && entity.getAttributes().hasAttribute(Rpgmana.MIND_OVER_MATTER_ATTRIBUTE)
                && entity.getAttributeValue(Rpgmana.MIND_OVER_MATTER_ATTRIBUTE) > 0 &&  entity instanceof ManaInterface manaInterface){
            double multiplier =  entity.getAttributeValue(Rpgmana.MIND_OVER_MATTER_ATTRIBUTE);
            manaInterface.spendMana(-Math.max(0,damage*multiplier));
        }
    }
}
