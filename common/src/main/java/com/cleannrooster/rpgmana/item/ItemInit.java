package com.cleannrooster.rpgmana.item;

import com.cleannrooster.rpgmana.Rpgmana;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class ItemInit {
    public static final HashMap<String, Item> entryMap = new HashMap<>();
    public static ItemGroup RPGMANA;
    public static Item CERULEANRING;
    public static Item VERMILLIONRING;
    public static Item ONYXRING;
    public static Item UNSETRING;

    public static Item IOLITERING;
    public static Item CERULEANAMULET;
    public static Item VERMILLIONAMULET;
    public static Item IOLITEAMULET;


    public static Item ONYXAMULET;
    public static Item MANA;

    public static Item FOIBLE;
    public static Item MANASTORM;


    public static RegistryKey<ItemGroup> KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(Rpgmana.MOD_ID, "generic"));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Rpgmana.MOD_ID, name), item);
    }

    private static void addItemToGroup(Item item) {
        ItemGroupEvents.modifyEntriesEvent(KEY).register((content) -> {
            content.add(item);
        });
    }

    /** Rings/amulets go through {@link ManaTrinketItem#factory} so each platform gets its slot-mod variant. */
    private static Item trinket(net.minecraft.registry.entry.RegistryEntry<net.minecraft.entity.attribute.EntityAttribute> attribute,
                                EntityAttributeModifier modifier, boolean uniqueRing) {
        return ManaTrinketItem.factory.create(new Item.Settings().maxCount(1), attribute, modifier, uniqueRing);
    }

    public static void register() {
        CERULEANRING = registerItem("ceruleanring", trinket(Rpgmana.MANA_ATTRIBUTE,
                new EntityAttributeModifier(Identifier.of("rpgmana:mana"), 40, EntityAttributeModifier.Operation.ADD_VALUE), true));
        entryMap.put("rpgmana:ceruleanring", CERULEANRING);

        IOLITERING = registerItem("iolitering", trinket(Rpgmana.MANA_REGEN_ATTRIBUTE,
                new EntityAttributeModifier(Identifier.of("rpgmana:manaregen"), 0.4F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE), true));
        entryMap.put("rpgmana:iolitering", IOLITERING);

        VERMILLIONRING = registerItem("vermillionring", trinket(Rpgmana.MANA_COST_ATTRIBUTE,
                new EntityAttributeModifier(Identifier.of("rpgmana:manacost"), -0.1F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE), true));
        entryMap.put("rpgmana:vermillionring", VERMILLIONRING);

        ONYXRING = registerItem("onyxring", trinket(Rpgmana.MANA_COST_ATTRIBUTE,
                new EntityAttributeModifier(Identifier.of("rpgmana:manacost"), 0.2F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE), true));
        entryMap.put("rpgmana:onyxring", ONYXRING);

        CERULEANAMULET = registerItem("cerulean_awakened", trinket(Rpgmana.MANA_ATTRIBUTE,
                new EntityAttributeModifier(Identifier.of("rpgmana:mana"), 80, EntityAttributeModifier.Operation.ADD_VALUE), true));
        entryMap.put("rpgmana:cerulean_awakened", CERULEANAMULET);

        IOLITEAMULET = registerItem("iolite_awakened", trinket(Rpgmana.MANA_REGEN_ATTRIBUTE,
                new EntityAttributeModifier(Identifier.of("rpgmana:manaregen"), 0.8F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE), true));
        entryMap.put("rpgmana:iolite_awakened", IOLITEAMULET);

        FOIBLE = registerItem("manaflow_medallion", trinket(Rpgmana.MANA_ATTRIBUTE,
                new EntityAttributeModifier(Identifier.of("rpgmana:mana"), 80, EntityAttributeModifier.Operation.ADD_VALUE), false));
        entryMap.put("rpgmana:manaflow_medallion", FOIBLE);

        MANASTORM = registerItem("eye_of_the_storm", trinket(Rpgmana.MANA_ATTRIBUTE,
                new EntityAttributeModifier(Identifier.of("rpgmana:mana"), 80, EntityAttributeModifier.Operation.ADD_VALUE), false));
        entryMap.put("rpgmana:eye_of_the_storm", MANASTORM);

        VERMILLIONAMULET = registerItem("vermillion_awakened", trinket(Rpgmana.MANA_COST_ATTRIBUTE,
                new EntityAttributeModifier(Identifier.of("rpgmana:manacost"), -0.2F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE), true));
        entryMap.put("rpgmana:vermillion_awakened", VERMILLIONAMULET);

        ONYXAMULET = registerItem("onyx_awakened", trinket(Rpgmana.MANA_COST_ATTRIBUTE,
                new EntityAttributeModifier(Identifier.of("rpgmana:manacost"), 0.4F, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE), true));
        entryMap.put("rpgmana:onyx_awakened", ONYXAMULET);

        UNSETRING = registerItem("unsetring", trinket(Rpgmana.MANA_ATTRIBUTE,
                new EntityAttributeModifier(Identifier.of("rpgmana:mana"), 20, EntityAttributeModifier.Operation.ADD_VALUE), true));
        entryMap.put("rpgmana:unsetring", UNSETRING);

        MANA = registerItem("mana", new Item(new Item.Settings()));

        RPGMANA = FabricItemGroup.builder()
                .icon(() -> new ItemStack(MANA))
                .displayName(Text.translatable("itemGroup.rpgmana.general"))
                .build();
        Registry.register(Registries.ITEM_GROUP, KEY, RPGMANA);
        addItemToGroup(UNSETRING);
        addItemToGroup(CERULEANRING);
        addItemToGroup(VERMILLIONRING);
        addItemToGroup(IOLITERING);
        addItemToGroup(ONYXRING);
        addItemToGroup(CERULEANAMULET);
        addItemToGroup(VERMILLIONAMULET);
        addItemToGroup(IOLITEAMULET);
        addItemToGroup(ONYXAMULET);
        addItemToGroup(FOIBLE);
        addItemToGroup(MANASTORM);
    }
}
