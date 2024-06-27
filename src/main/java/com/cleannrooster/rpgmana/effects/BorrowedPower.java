package com.cleannrooster.rpgmana.effects;

import com.cleannrooster.rpgmana.api.ManaInterface;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class BorrowedPower extends StatusEffect {
    public BorrowedPower(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onRemoved(entity, attributes, amplifier);
        if(entity instanceof ManaInterface manaInterface){
            entity.damage(entity.getDamageSources().magic(), (float) Math.max(0,-manaInterface.getMana()/5));
        }
    }
}
