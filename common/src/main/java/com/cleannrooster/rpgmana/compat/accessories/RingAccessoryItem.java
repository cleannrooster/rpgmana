package com.cleannrooster.rpgmana.compat.accessories;

import io.wispforest.accessories.api.AccessoryItem;
import io.wispforest.accessories.api.attributes.AccessoryAttributeBuilder;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;

import java.util.List;

/** Accessories variant of the rings/amulets, used when the Accessories mod is present. */
public class RingAccessoryItem extends AccessoryItem {

    public final RegistryEntry<EntityAttribute> attribute;
    public final EntityAttributeModifier modifier;
    public final boolean uniqueRing;

    public RingAccessoryItem(Settings settings, RegistryEntry<EntityAttribute> attribute,
                             EntityAttributeModifier modifier, boolean uniqueRing) {
        super(settings);
        this.attribute = attribute;
        this.modifier = modifier;
        this.uniqueRing = uniqueRing;
    }

    @Override
    public void getDynamicModifiers(ItemStack stack, SlotReference reference, AccessoryAttributeBuilder builder) {
        super.getDynamicModifiers(stack, reference, builder);
        builder.addStackable(attribute, new EntityAttributeModifier(modifier.id(), modifier.value(), modifier.operation()));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (uniqueRing) {
            tooltip.add(Text.translatable("rpgmana.uniquerings"));
        }
        super.appendTooltip(stack, context, tooltip, type);
    }
}
