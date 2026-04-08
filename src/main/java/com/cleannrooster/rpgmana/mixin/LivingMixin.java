package com.cleannrooster.rpgmana.mixin;

import com.cleannrooster.rpgmana.Rpgmana;
import com.cleannrooster.rpgmana.api.ManaInstance;
import com.cleannrooster.rpgmana.api.ManaInterface;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.spell_engine.api.spell.Spell;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;


@Mixin(LivingEntity.class)
public class LivingMixin {
	@Inject(at = @At("RETURN"), method = "createLivingAttributes")
	private static void createLivingAttributesRPGMANA(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
		cir.getReturnValue().add(Rpgmana.MANA_ATTRIBUTE);
		cir.getReturnValue().add(Rpgmana.MANA_COST_ATTRIBUTE);
		cir.getReturnValue().add(Rpgmana.MANA_REGEN_ATTRIBUTE);
		cir.getReturnValue().add(Rpgmana.MIND_OVER_MATTER_ATTRIBUTE);
		cir.getReturnValue().add(Rpgmana.MANA_DAMAGE_TAKEN_ATTRIBUTE);

	}
	@ModifyVariable(at = @At("HEAD"), method = "modifyAppliedDamage", argsOnly = true)
	protected float modifyAppliedDamageRPGMana(float amount, DamageSource source, float originalAmount) {
		LivingEntity living = (LivingEntity) (Object) this;
		amount = originalAmount;
		if(living.getAttributeValue(Rpgmana.MANA_DAMAGE_TAKEN_ATTRIBUTE) > 100F && living instanceof ManaInterface manaInterface && manaInterface.getMana() > 0){
			double multiplier =  ((living.getAttributeValue(Rpgmana.MANA_DAMAGE_TAKEN_ATTRIBUTE)-100F)*0.01F);
			amount *= Math.max(0,1F-(float) multiplier);
		}
		return amount;
	}
}