package com.cleannrooster.rpgmana.fabric.compat.trinkets;

import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

/** Trinkets variant of the rings/amulets, used on Fabric when Trinkets is present. */
public class RingTrinketItem extends TrinketItem {

    public final RegistryEntry<EntityAttribute> attribute;
    public final EntityAttributeModifier modifier;
    public final boolean uniqueRing;

    public RingTrinketItem(Settings settings, RegistryEntry<EntityAttribute> attribute,
                           EntityAttributeModifier modifier, boolean uniqueRing) {
        super(settings);
        this.attribute = attribute;
        this.modifier = modifier;
        this.uniqueRing = uniqueRing;
    }

    @Override
    public Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, Identifier slotIdentifier) {
        var modifiers = super.getModifiers(stack, slot, entity, slotIdentifier);
        modifiers.put(attribute, new EntityAttributeModifier(modifier.id(), modifier.value(), modifier.operation()));
        return modifiers;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (uniqueRing) {
            tooltip.add(Text.translatable("rpgmana.uniquerings"));
        }
        super.appendTooltip(stack, context, tooltip, type);
    }
}
