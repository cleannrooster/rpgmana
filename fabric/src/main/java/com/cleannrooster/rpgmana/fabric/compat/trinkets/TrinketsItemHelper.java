package com.cleannrooster.rpgmana.fabric.compat.trinkets;

import com.cleannrooster.rpgmana.item.ManaTrinketItem;

/**
 * Outsourced to avoid class loading RingTrinketItem -> TrinketItem
 * when Trinkets is not present.
 */
public class TrinketsItemHelper {
    public static void register() {
        ManaTrinketItem.factory = RingTrinketItem::new;
    }
}
