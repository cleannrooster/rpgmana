package com.cleannrooster.rpgmana.item;

import com.cleannrooster.rpgmana.Rpgmana;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
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
import java.util.UUID;

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

    public static RegistryKey<ItemGroup> KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(),new Identifier(Rpgmana.MOD_ID,"generic"));
    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, new Identifier(Rpgmana.MOD_ID,name),item);
    }
    private static void addItemToGroup(Item item){
        ItemGroupEvents.modifyEntriesEvent(KEY).register((content) -> {
            content.add(item);
        });
    }
    public static void register() {
        CERULEANRING = registerItem("ceruleanring", new CeruleanRing(new FabricItemSettings().maxCount(1), 40,new EntityAttributeModifier(UUID.randomUUID(), "rpgmana:mana", 40, EntityAttributeModifier.Operation.ADDITION)));
        entryMap.put("rpgmana:ceruleanring",CERULEANRING);
        IOLITERING = registerItem("iolitering", new IoliteRing(new FabricItemSettings().maxCount(1), 0.4F,new EntityAttributeModifier(UUID.randomUUID(), "rpgmana:manaregen", 0.4F, EntityAttributeModifier.Operation.MULTIPLY_BASE)));
        entryMap.put("rpgmana:iolitering",IOLITERING);

        VERMILLIONRING = registerItem("vermillionring", new VermillionRing(new FabricItemSettings().maxCount(1), -0.1F,new EntityAttributeModifier(UUID.randomUUID(), "rpgmana:manacost", -0.1F, EntityAttributeModifier.Operation.MULTIPLY_BASE)));
        entryMap.put("rpgmana:vermillionring",VERMILLIONRING);

        ONYXRING = registerItem("onyxring", new VermillionRing(new FabricItemSettings().maxCount(1), 0.2F,new EntityAttributeModifier(UUID.randomUUID(), "rpgmana:manacost", 0.2F, EntityAttributeModifier.Operation.MULTIPLY_BASE)));
        entryMap.put("rpgmana:onyxring",ONYXRING);

        CERULEANAMULET = registerItem("cerulean_awakened", new CeruleanRing(new FabricItemSettings().maxCount(1), 80, new EntityAttributeModifier(UUID.randomUUID(), "rpgmana:mana", 80, EntityAttributeModifier.Operation.ADDITION)));
        entryMap.put("rpgmana:cerulean_awakened",CERULEANAMULET);
        IOLITEAMULET = registerItem("iolite_awakened", new IoliteRing(new FabricItemSettings().maxCount(1), 0.8F,new EntityAttributeModifier(UUID.randomUUID(), "rpgmana:manaregen", 0.8F, EntityAttributeModifier.Operation.MULTIPLY_BASE)));
        entryMap.put("rpgmana:iolite_awakened",IOLITEAMULET);

        VERMILLIONAMULET = registerItem("vermillion_awakened", new VermillionRing(new FabricItemSettings().maxCount(1), -0.2F,new EntityAttributeModifier(UUID.randomUUID(), "rpgmana:manacost", -0.2F, EntityAttributeModifier.Operation.MULTIPLY_BASE)));
        entryMap.put("rpgmana:vermillion_awakened",VERMILLIONAMULET);
        ONYXAMULET = registerItem("onyx_awakened", new VermillionRing(new FabricItemSettings().maxCount(1), 0.4F,new EntityAttributeModifier(UUID.randomUUID(), "rpgmana:manacost", 0.4F, EntityAttributeModifier.Operation.MULTIPLY_BASE)));
        entryMap.put("rpgmana:onyx_awakened",ONYXAMULET);
        UNSETRING = registerItem("unsetring", new CeruleanRing(new FabricItemSettings().maxCount(1),20F,new EntityAttributeModifier(UUID.randomUUID(), "rpgmana:mana", 20, EntityAttributeModifier.Operation.ADDITION)));
        entryMap.put("rpgmana:unsetring",UNSETRING);
        MANA = registerItem("mana", new Item(new FabricItemSettings()));

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




    }
}
