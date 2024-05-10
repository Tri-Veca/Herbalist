package com.herbalist.common.blend;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.herbalist.util.RecipeUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;

// Your custom recipe class
public class InfuserRecipe extends CustomRecipe {
    private final Ingredient[] input;
    private final ItemStack result;

    public InfuserRecipe(ResourceLocation id, Ingredient[] input, ItemStack result) {
        super(id);
        this.input = input;
        this.result = result;
    }

    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {
        // This method should return true if the items in the CraftingContainer match the recipe
        // For simplicity, let's say it's a match if the input item is present in the crafting grid
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            for (Ingredient ingredient : input) {
                if (ingredient.test(stack)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv) {
        ItemStack output = result.copy();
        CompoundTag nbt = output.getOrCreateTag();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            for (Ingredient ingredient : input) {
                if (!stack.isEmpty() && ingredient.test(stack)) {
                    nbt.put("Herbs", stack.getOrCreateTag().copy());
                    break;
                }
            }
        }

        output.setTag(nbt);
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeUtil.INFUSER_RECIPE_SERIALIZER.get();
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<InfuserRecipe> {
        @Override
        public InfuserRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            // Parse the recipe from the JSON object
            // You'll need to extract the ingredients and result from the JSON
            // Then, you can use them to create a new instance of your InfuserRecipe

            JsonArray ingredientsJson = json.getAsJsonArray("ingredients");
            Ingredient[] ingredients = new Ingredient[ingredientsJson.size()];

            for (int i = 0; i < ingredientsJson.size(); i++) {
                ingredients[i] = Ingredient.fromJson(ingredientsJson.get(i));
            }

            JsonObject resultJson = json.getAsJsonObject("result");
            ItemStack result = ShapedRecipe.itemFromJson(resultJson).getDefaultInstance();

            return new InfuserRecipe(recipeId, ingredients, result);
        }


        @Override
        public InfuserRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            // Parse the recipe from the packet data
            // You'll need to read the ingredients and result from the buffer
            // Then, you can use them to create a new instance of your InfuserRecipe

            int numIngredients = buffer.readVarInt(); // Read the number of ingredients
            Ingredient[] ingredients = new Ingredient[numIngredients];
            for (int i = 0; i < numIngredients; i++) {
                ingredients[i] = Ingredient.fromNetwork(buffer);
            }

            ItemStack result = buffer.readItem();
            return new InfuserRecipe(recipeId, ingredients, result);
        }


        @Override
        public void toNetwork(FriendlyByteBuf buffer, InfuserRecipe recipe) {
            // Write the number of ingredients to the buffer
            buffer.writeVarInt(recipe.input.length);

            // Write each ingredient to the buffer
            for (Ingredient ingredient : recipe.input) {
                ingredient.toNetwork(buffer);
            }

            // Write the result item to the buffer
            buffer.writeItem(recipe.result);
        }
    }
}
