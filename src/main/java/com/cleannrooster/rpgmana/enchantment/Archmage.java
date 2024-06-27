package com.cleannrooster.rpgmana.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.spell_power.api.enchantment.ItemType;

public class Archmage extends Enchantment {


    public Archmage(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }


    public int getMaxLevel() {
        return 4;
    }

    public int getMinPower(int level) {
        return 1;
    }

    public int getMaxPower(int level) {
        return 1+level*10;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return ItemType.MAGICAL_WEAPON.matches(stack);
    }

}
