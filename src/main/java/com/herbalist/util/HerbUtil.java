package com.herbalist.util;

import com.herbalist.init.ItemInit;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

public class HerbUtil {
    public static final Map<Item, String> HERB_EFFECTS = new HashMap<>();

    public static void initialize() {
        HERB_EFFECTS.put(ItemInit.ALFALFA.get(), "gives the player hunger");
        HERB_EFFECTS.put(ItemInit.LAVENDER.get(), "makes mobs sleepy");
        // Add more herbs and their effects here
    }
}
