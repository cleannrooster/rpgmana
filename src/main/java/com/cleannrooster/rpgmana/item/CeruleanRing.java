package com.cleannrooster.rpgmana.item;

import com.cleannrooster.rpgmana.Rpgmana;
import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class CeruleanRing extends TrinketItem {
    public float value = 0;
    EntityAttributeModifier modifier;
    public CeruleanRing(Settings settings, float value, EntityAttributeModifier modifier) {
        super(settings);
        this.value = value;
        this.modifier = modifier;
    }



    public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
        var modifiers = super.getModifiers(stack, slot, entity, uuid);
        // +10% movement speed
        modifiers.put(Rpgmana.MANA, new EntityAttributeModifier(modifier.getId(),modifier.getName(),modifier.getValue(),modifier.getOperation()));
        // If the player has access to ring slots, this will give them an extra one
        return modifiers;
    }
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.add(Text.translatable("rpgmana.uniquerings"));
    }
}
