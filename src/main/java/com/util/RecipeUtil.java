package com.util;

import com.herbalist.Herbalist;
import com.herbalist.init.custom.Kettle.KettleRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RecipeUtil {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Herbalist.MOD_ID);

    public static final RegistryObject<RecipeSerializer<KettleRecipe>> TEA_MAKING_SERIALIZER =
            SERIALIZERS.register("tea_making", () -> KettleRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
