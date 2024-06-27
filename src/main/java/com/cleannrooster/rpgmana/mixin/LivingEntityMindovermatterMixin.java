package com.cleannrooster.rpgmana.mixin;

import com.cleannrooster.rpgmana.Rpgmana;
import com.cleannrooster.rpgmana.api.ManaInterface;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntity.class)
public class LivingEntityMindovermatterMixin {

	@ModifyVariable(at = @At("HEAD"), method = "applyDamage", argsOnly = true)
	private float mindovermatter(float originalamount, DamageSource source, float amount) {
		PlayerEntity living = (PlayerEntity) (Object) this;
		float normal = originalamount;
		amount = this.applyArmorToDamage(source, amount);
		amount = this.modifyAppliedDamage(source, amount);
		float f = amount;
		amount = Math.max(amount - living.getAbsorptionAmount(), 0.0F);
		float g = f - amount;

		if (amount != 0.0F) {
			if (amount > 0 && living instanceof ManaInterface manaInterface && EnchantmentHelper.getEquipmentLevel(Rpgmana.MINDOVERMATTER, living) > 0 && manaInterface.getMaxMana() > 0) {
				if (manaInterface.getMana() > 0) {
					normal /= 2;

				}
				manaInterface.spendMana(-normal * 5);
				return normal;
			}
		}

		return originalamount;
	}
	protected float modifyAppliedDamage(DamageSource source, float amount) {
		PlayerEntity living = (PlayerEntity) (Object) this;

		if (source.isIn(DamageTypeTags.BYPASSES_EFFECTS)) {
			return amount;
		} else {
			int i;
			if (living.hasStatusEffect(StatusEffects.RESISTANCE) && !source.isIn(DamageTypeTags.BYPASSES_RESISTANCE)) {
				i = (living.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier() + 1) * 5;
				int j = 25 - i;
				float f = amount * (float)j;
				float g = amount;
				amount = Math.max(f / 25.0F, 0.0F);
				float h = g - amount;
				if (h > 0.0F && h < 3.4028235E37F) {
					if (living instanceof ServerPlayerEntity) {
						((ServerPlayerEntity)living).increaseStat(Stats.DAMAGE_RESISTED, Math.round(h * 10.0F));
					} else if (source.getAttacker() instanceof ServerPlayerEntity) {
						((ServerPlayerEntity)source.getAttacker()).increaseStat(Stats.DAMAGE_DEALT_RESISTED, Math.round(h * 10.0F));
					}
				}
			}

			if (amount <= 0.0F) {
				return 0.0F;
			} else if (source.isIn(DamageTypeTags.BYPASSES_ENCHANTMENTS)) {
				return amount;
			} else {
				i = EnchantmentHelper.getProtectionAmount(living.getArmorItems(), source);
				if (i > 0) {
					amount = DamageUtil.getInflictedDamage(amount, (float)i);
				}

				return amount;
			}
		}
	}
	protected float applyArmorToDamage(DamageSource source, float amount) {
		PlayerEntity living = (PlayerEntity) (Object) this;

		if (!source.isIn(DamageTypeTags.BYPASSES_ARMOR)) {
			amount = DamageUtil.getDamageLeft(amount, (float)living.getArmor(), (float)living.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS));
		}

		return amount;
	}

}