package com.cleannrooster.rpgmana.mixin;

import com.cleannrooster.rpgmana.Rpgmana;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ItemStack.class)
public class ItemstackMixin {

    @ModifyReturnValue(at = @At("TAIL"), method = "getTooltip")
    public List<Text> getTooltiprpgmana(List<Text> tooltip, @Nullable PlayerEntity player, TooltipContext context) {
        if(tooltip.stream().anyMatch(text -> text.toString().contains("rpgmana.mana")) ||
                tooltip.stream().anyMatch(text -> text.toString().contains("rpgmana.manacost")) ||
                tooltip.stream().anyMatch(text -> text.toString().contains("rpgmana.manaregen")))  {
            if (tooltip.stream().anyMatch(text -> text.toString().contains("rpgmana.mana"))) {
                tooltip.add(Text.translatable("desc.rpgmana.mana").formatted(Formatting.GRAY));
            }
            if (tooltip.stream().anyMatch(text -> text.toString().contains("rpgmana.manacost"))) {
                tooltip.add(Text.translatable("desc.rpgmana.manacost").formatted(Formatting.GRAY));
            }
            if (tooltip.stream().anyMatch(text -> text.toString().contains("rpgmana.manaregen"))) {
                tooltip.add(Text.translatable("desc.rpgmana.manaregen").formatted(Formatting.GRAY));

            }
        }
        ItemStack stack = (ItemStack) (Object) this;
        if(PotionUtil.getPotion(stack) != null && PotionUtil.getPotion(stack) == Rpgmana.BORROWEDPOWERPOTION){
            tooltip.add(Text.translatable("desc.rpgmana.borrowed_power").formatted(Formatting.BLUE));

        }
        return tooltip;

    }

}
