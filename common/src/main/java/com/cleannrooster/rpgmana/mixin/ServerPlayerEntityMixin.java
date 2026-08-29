package com.cleannrooster.rpgmana.mixin;

import com.cleannrooster.rpgmana.Rpgmana;
import com.cleannrooster.rpgmana.api.ManaInterface;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(at = @At("HEAD"), method = "increaseStat")
    public void increaseStatRPGManaAbsorbed(Identifier stat, int amount, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object)this;
        if(player instanceof ServerPlayerEntity serverPlayerEntity && stat.equals(Stats.DAMAGE_ABSORBED)&&  player.getAttributeValue(Rpgmana.MIND_OVER_MATTER_ATTRIBUTE) > 0  && player instanceof ManaInterface manaInterface){
            double multiplier =  player.getAttributeValue(Rpgmana.MIND_OVER_MATTER_ATTRIBUTE);
            manaInterface.spendMana(-multiplier*amount/10.0F);
        }
    }
}
