package com.cleannrooster.rpgmana.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class MindOverMatter extends Enchantment {


    public MindOverMatter(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, EnchantmentTarget.ARMOR_HEAD, slotTypes);
    }

    public int getMaxLevel() {
        return 1;
    }
    public boolean isCursed() {
        return true;
    }
    public boolean isTreasure() {
        return true;
    }



    public int getMinPower(int level) {
        return 25;
    }

    public int getMaxPower(int level) {
        return 50;
    }



}
