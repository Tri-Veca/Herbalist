package com.herbalist.init.custom.Kettle;

import com.google.gson.JsonArray;

import com.google.gson.JsonObject;
import com.herbalist.Herbalist;
import com.herbalist.init.custom.InfuserItem;
import com.herbalist.util.FluidJSONUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KettleRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> recipeItems;
    private final FluidStack fluidStack;

    public KettleRecipe(ResourceLocation id, ItemStack output,
                             NonNullList<Ingredient> recipeItems, FluidStack fluidStack) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
       this.fluidStack = fluidStack;
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        if (pLevel.isClientSide()) {
            return false;
        }

        // Check if the infuser contains the correct herbs
        ItemStack infuserStack = pContainer.getItem(0); // Replace with actual method to get the infuser
        if (infuserStack.getItem() instanceof InfuserItem) {
            InfuserItem infuserItem = (InfuserItem) infuserStack.getItem();
            List<String> herbs = infuserItem.getHerbs(infuserStack);
            for (String herb : herbs) {
                Item herbItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(herb));
                boolean herbInRecipe = false;
                for (Ingredient ingredient : recipeItems) {
                    if (ingredient.test(new ItemStack(herbItem))) {
                        herbInRecipe = true;
                        break;
                    }
                }
                if (!herbInRecipe) {
                    return false;
                }

            }
            System.out.println("Recipe matches: " + (infuserStack.getItem() instanceof InfuserItem && herbs.equals(recipeItems)));
            return infuserStack.getItem() instanceof InfuserItem && herbs.equals(recipeItems);


        }
        // Debug output

        return recipeItems.get(0).test(pContainer.getItem(1));


    }




    public FluidStack getFluid() {
        return fluidStack;
    }



    @Override
    public NonNullList<Ingredient> getIngredients() {
        return recipeItems;
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<KettleRecipe> {
        private Type() {
        }

        public static final Type INSTANCE = new Type();
        public static final String ID = "tea_making";
    }

    public static class Serializer implements RecipeSerializer<KettleRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID =
                new ResourceLocation(Herbalist.MOD_ID, "tea_making");



        @Override
        public KettleRecipe fromJson(ResourceLocation id, JsonObject json) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));

            JsonArray ingredients = GsonHelper.getAsJsonArray(json, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(1, Ingredient.EMPTY);
            FluidStack fluid = FluidJSONUtil.readFluid(json.get("fluid").getAsJsonObject());


            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            return new KettleRecipe(id, output, inputs, fluid);
        }

        @Override
        public KettleRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(buf.readInt(), Ingredient.EMPTY);
            FluidStack fluid = buf.readFluidStack();

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(buf));
            }



            ItemStack output = buf.readItem();
            return new KettleRecipe(id, output, inputs, fluid);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, KettleRecipe recipe) {

            buf.writeInt(recipe.getIngredients().size());
            buf.writeFluidStack(recipe.fluidStack);
            for (Ingredient ing : recipe.getIngredients()) {
                ing.toNetwork(buf);
            }

            buf.writeItemStack(recipe.getResultItem(), false);
        }


        @SuppressWarnings("unchecked") // Need this wrapper, because generics
        private static <G> Class<G> castClass(Class<?> cls) {
            return (Class<G>) cls;
        }


        @Override
        public RecipeSerializer<?> setRegistryName(ResourceLocation name) {
            return INSTANCE;
        }

        @Nullable
        @Override
        public ResourceLocation getRegistryName() {
            return ID;
        }

        @Override
        public Class<RecipeSerializer<?>> getRegistryType() {
            return Serializer.castClass(RecipeSerializer.class);
        }
    }
}
