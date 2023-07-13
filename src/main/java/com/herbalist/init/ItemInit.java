package com.herbalist.init;

import com.herbalist.Herbalist;
import com.herbalist.init.custom.TeaItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ItemInit {
    public static DeferredRegister<Item> ITEMS =  DeferredRegister.create(ForgeRegistries.ITEMS, Herbalist.MOD_ID);

    public static final RegistryObject<Item> ALFALFA_SEEDS = register("alfalfa_seeds",
            () -> new Item(new Item.Properties().tab(Herbalist.HERB)));



    public static final RegistryObject<Item> HUNGER_TEA = ITEMS.register("hunger_tea",
            () -> new TeaItem(new Item.Properties()
                    .tab(Herbalist.HERB), 3, 0.4F,  new MobEffectInstance(MobEffects.HUNGER, 400, 0)));


    public static <T extends Item> RegistryObject<T> register(String name, final Supplier<T> item){
        return ITEMS.register(name, item);
    }
}
