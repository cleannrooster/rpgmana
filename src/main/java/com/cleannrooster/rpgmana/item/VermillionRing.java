package com.cleannrooster.rpgmana.item;

import com.cleannrooster.rpgmana.Rpgmana;
import com.google.common.collect.Multimap;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class VermillionRing extends TrinketItem {
    public float value = 0;
    EntityAttributeModifier modifier;
    public VermillionRing(Settings settings, float value, EntityAttributeModifier modifier) {
        super(settings);
        this.value = value;
        this.modifier = modifier;
    }

    public EntityAttributeModifier getModifier() {
        return modifier;
    }

    @Override
    public AttributeModifiersComponent getAttributeModifiers() {
        return super.getAttributeModifiers();
    }

    @Override
    public Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, Identifier slotIdentifier) {
        var modifiers = super.getModifiers(stack,slot,entity,slotIdentifier);
        // +10% movement speed
        modifiers.put(Rpgmana.MANA_COST_ATTRIBUTE, new EntityAttributeModifier(modifier.id(),modifier.value(),modifier.operation()));
        // If the player has access to ring slots, this will give them an extra one

        return modifiers;
    }



    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("rpgmana.uniquerings"));

        super.appendTooltip(stack, context, tooltip, type);
    }
}
