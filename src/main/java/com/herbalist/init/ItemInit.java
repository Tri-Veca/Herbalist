package com.herbalist.init;

import com.herbalist.Herbalist;
import com.herbalist.init.custom.InfuserItem;
import com.herbalist.init.custom.TeaItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.function.Supplier;

public class ItemInit {
    public static DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Herbalist.MOD_ID);

    public static final RegistryObject<Item> ALFALFA_SEEDS = register("alfalfa_seeds",
            () -> new ItemNameBlockItem(BlockInit.ALFALFA_PLANT.get(),
                    new Item.Properties().tab(Herbalist.HERB)));

    // ALFALFA_SEEDS registered before ALFALFA
    public static final RegistryObject<Item> ALFALFA = register("alfalfa",
            () -> new Item(new Item.Properties().tab(Herbalist.HERB)));

    public static final RegistryObject<Item> INFUSER = register("infuser",
            () -> new InfuserItem(new Item.Properties().tab(Herbalist.HERB)));
    public static final RegistryObject<Item> EMPTY_TEA = register("empty_tea",
            () -> new Item(new Item.Properties().tab(Herbalist.HERB)));

    public static final RegistryObject<Item> TEA = register("tea",
            () -> new TeaItem(new Item.Properties().tab(Herbalist.HERB), 3, 0.4F, new ArrayList<>()));


    public static final RegistryObject<Item> MINT_SEEDS = register("mint_seeds",
            () -> new ItemNameBlockItem(BlockInit.MINT_PLANT.get(),
                    new Item.Properties().tab(Herbalist.HERB)));

    public static final RegistryObject<Item> MINT = register("mint",
            () -> new Item(new Item.Properties().tab(Herbalist.HERB)));

    public static final RegistryObject<Item> LAVENDER_SEEDS = register("lavender_seeds",
            () -> new ItemNameBlockItem(BlockInit.LAVENDER_PLANT.get(),
                    new Item.Properties().tab(Herbalist.HERB)));

    public static final RegistryObject<Item> LAVENDER = register("lavender",
            () -> new Item(new Item.Properties().tab(Herbalist.HERB)));



    public static <T extends Item> RegistryObject<T> register(String name, final Supplier<T> item) {
        return ITEMS.register(name, item);
    }
}
