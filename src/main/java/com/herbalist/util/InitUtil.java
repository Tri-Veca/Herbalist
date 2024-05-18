package com.herbalist.util;

import com.herbalist.init.ItemInit;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InitUtil {
    // initializes after items have been registered
    // required or else it will output a null pointer; item not found
    public static final Map<Item, MobEffectInstance> HERB_EFFECT_MAP = new HashMap<>();
    public static final Map<Item, String> HERB_IDENTIFY = new HashMap<>();

    public static final List<Item> VALID_ITEMS = new ArrayList<>();

    public static void initialize() {

        HERB_EFFECT_MAP.put(ItemInit.ALFALFA.get(), new MobEffectInstance(MobEffects.HUNGER, 400, 0));
        HERB_EFFECT_MAP.put(ItemInit.LAVENDER.get(), new MobEffectInstance(MobEffects.SLOW_FALLING, 400, 0)); // Replace with actual effect
        HERB_EFFECT_MAP.put(ItemInit.MINT.get(), new MobEffectInstance(MobEffects.REGENERATION, 400, 0)); // Replace with actual effect
            // Debug output
            System.out.println("HERB_EFFECT_MAP: " + HERB_EFFECT_MAP);

        HERB_IDENTIFY.put(ItemInit.ALFALFA.get(), "gives the player hunger");
        HERB_IDENTIFY.put(ItemInit.LAVENDER.get(), "makes mobs sleepy");
        // Add herbs to identification map here


        // Add herbs to effect map here

        VALID_ITEMS.add(ItemInit.LAVENDER.get());
        VALID_ITEMS.add(ItemInit.ALFALFA.get());
        VALID_ITEMS.add(ItemInit.MINT.get());
        // Add herbs to valid infuser list


    }

}



