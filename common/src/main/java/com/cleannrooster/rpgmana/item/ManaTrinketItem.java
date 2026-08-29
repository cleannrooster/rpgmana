package com.cleannrooster.rpgmana.item;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;

import java.util.List;

/**
 * Plain-item fallback for the rings/amulets. The real slot behavior comes from
 * the platform variants created through {@link #factory}: Trinkets on Fabric,
 * Accessories when that mod is present (always, on NeoForge).
 */
public class ManaTrinketItem extends Item {

    public interface Factory {
        Item create(Item.Settings settings, RegistryEntry<EntityAttribute> attribute,
                    EntityAttributeModifier modifier, boolean uniqueRing);
    }

    /** Swapped by platform/compat init before item registration. */
    public static Factory factory = ManaTrinketItem::new;

    public final RegistryEntry<EntityAttribute> attribute;
    public final EntityAttributeModifier modifier;
    public final boolean uniqueRing;

    public ManaTrinketItem(Settings settings, RegistryEntry<EntityAttribute> attribute,
                           EntityAttributeModifier modifier, boolean uniqueRing) {
        super(settings);
        this.attribute = attribute;
        this.modifier = modifier;
        this.uniqueRing = uniqueRing;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (uniqueRing) {
            tooltip.add(Text.translatable("rpgmana.uniquerings"));
        }
        super.appendTooltip(stack, context, tooltip, type);
    }
}
