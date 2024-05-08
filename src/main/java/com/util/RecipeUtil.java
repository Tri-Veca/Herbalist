package com.util;

import com.herbalist.Herbalist;
import com.herbalist.common.blend.InfuserRecipe;
import com.herbalist.common.blend.TeaBlendRecipe;
import com.herbalist.init.ItemInit;
import com.herbalist.init.custom.Kettle.KettleRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RecipeUtil {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Herbalist.MOD_ID);

    public static final RegistryObject<RecipeSerializer<KettleRecipe>> TEA_MAKING_SERIALIZER =
            SERIALIZERS.register("tea_making", () -> KettleRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<InfuserRecipe>> INFUSER_RECIPE_SERIALIZER = SERIALIZERS.register("infusing", InfuserRecipe.Serializer::new);
/*
    public static final RegistryObject<RecipeSerializer<TeaBlendRecipe>> TEA_BLEND_SERIALIZER =
            SERIALIZERS.register("tea_blend", () -> TeaBlendRecipe.Serializer.INSTANCE);

 */


        // Register your tea blend recipes here



    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
