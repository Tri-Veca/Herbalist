package com.herbalist.common.blend;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ObjectHolder;
import org.jetbrains.annotations.Nullable;

public class TeaBlendRecipe implements CraftingRecipe {

    public static final RecipeSerializer<TeaBlendRecipe> SERIALIZER = null;

    private final ResourceLocation id;
    private final Ingredient[] ingredients;

    public TeaBlendRecipe(ResourceLocation id, Ingredient[] ingredients) {
        this.id = id;
        this.ingredients = ingredients;
    }



    @Override
    public boolean matches(CraftingContainer p_44002_, Level p_44003_) {
        return false;
    }

    @Override
    public ItemStack assemble(CraftingContainer p_44001_) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return false;
    }

    @Override
    public ItemStack getResultItem() {
        return null;
    }


    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    public static class Serializer implements RecipeSerializer<TeaBlendRecipe> {
        @Override
        public TeaBlendRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            // Parse your recipe JSON and create a TeaBlendRecipe instance
            return null;
        }

        @Override
        public TeaBlendRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            // Read your recipe from a network buffer
            return null;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, TeaBlendRecipe recipe) {
            // Write your recipe to a network buffer
        }

        @Override
        public RecipeSerializer<?> setRegistryName(ResourceLocation name) {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getRegistryName() {
            return null;
        }

        @Override
        public Class<RecipeSerializer<?>> getRegistryType() {
            return null;
        }
    }
}