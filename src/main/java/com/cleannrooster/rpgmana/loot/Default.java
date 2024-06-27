package com.cleannrooster.rpgmana.loot;

import net.spell_engine.api.item.ItemConfig;

import java.util.List;

public class Default {
    public static final ItemConfig items;
    public static final LootConfig loot;

    static {
        items = new ItemConfig();
        var weight = 2;
        var limit = 1;
        loot = new LootConfig();




        var jewelry_tier_2 = "mana_tier1";
        loot.item_groups.put(jewelry_tier_2, new LootConfig.ItemGroup(List.of(
                        "rpgmana:ceruleanring",
                        "rpgmana:iolitering",
                        "rpgmana:onyxring",
                        "rpgmana:vermillionring"
                ),
                        1)
                        .chance(0.2F)
        );

        var jewelry_tier_3 = "mana_tier2";
        loot.item_groups.put(jewelry_tier_3, new LootConfig.ItemGroup(List.of(
                "rpgmana:ceruleanring",
                "rpgmana:iolitering",
                "rpgmana:onyxring",
                "rpgmana:vermillionring"
                ),
                        1)
                        .chance(0.2F)
        );

        var jewelry_tier_4 = "mana_tier3";
        loot.item_groups.put(jewelry_tier_4, new LootConfig.ItemGroup(List.of(
                        "rpgmana:cerulean_awakened",
                        "rpgmana:iolite_awakened",
                        "rpgmana:onyx_awakened",
                        "rpgmana:vermillion_awakened"
//                JewelryItems.unique_lightning_ring.id().toString(),
//                JewelryItems.unique_lightning_necklace.id().toString(),
//                JewelryItems.unique_soul_ring.id().toString(),
//                JewelryItems.unique_soul_necklace.id().toString()
                ), 1)
                        .chance(0.5F)
        );


        List.of("minecraft:chests/stronghold_crossing",
                        "minecraft:chests/stronghold_library",
                        "minecraft:chests/underwater_ruin_big",
                        "minecraft:chests/simple_dungeon",
                        "minecraft:chests/woodland_mansion")
                .forEach(id -> loot.loot_tables.put(id, List.of(jewelry_tier_2)));

        List.of("minecraft:chests/bastion_treasure",
                        "minecraft:chests/bastion_other",
                        "minecraft:chests/nether_bridge")
                .forEach(id -> loot.loot_tables.put(id, List.of(jewelry_tier_3)));

        List.of("minecraft:chests/ancient_city",
                        "minecraft:chests/end_city_treasure")
                .forEach(id -> loot.loot_tables.put(id, List.of(jewelry_tier_4)));
    }
}