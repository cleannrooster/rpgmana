package com.cleannrooster.rpgmana.compat.accessories;

import com.cleannrooster.rpgmana.item.ManaTrinketItem;

/**
 * Outsourced to avoid class loading RingAccessoryItem -> AccessoryItem
 * when the Accessories mod is not present.
 */
public class AccessoriesItemHelper {
    public static void register() {
        ManaTrinketItem.factory = RingAccessoryItem::new;
    }
}
