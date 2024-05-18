package com.herbalist.init.custom;

import com.herbalist.Herbalist;
import com.herbalist.init.ItemInit;
import com.herbalist.util.InitUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Herbalist.MOD_ID)
public class InfuserItem extends Item {
    private static final String TAG_HERBS = "Herbs";



    public InfuserItem(Properties properties) {
        super(properties);
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        List<String> herbs = getHerbs(stack);
        if (!herbs.isEmpty()) {
            tooltip.add(new TextComponent("Stored Herbs:"));
            for (String herb : herbs) {
                tooltip.add(new TextComponent("- " + herb));
            }
        } else {
            tooltip.add(new TextComponent("Infuser is empty"));
        }
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        return ItemStack.EMPTY; // Prevent crafting result from being an infuser item
    }

    // Handle storing herbs in the infuser when crafting
    @SubscribeEvent
    public static void onCrafting(PlayerEvent.ItemCraftedEvent event) {
        ItemStack result = event.getCrafting();
        if (result.getItem() instanceof InfuserItem) {
            CraftingContainer craftingMatrix = getCraftMatrix(event.getPlayer());
            if (craftingMatrix != null) {
                boolean hasValidItem = false;
                ListTag herbList = new ListTag();
                for (int i = 0; i < craftingMatrix.getContainerSize(); i++) {
                    ItemStack stack = craftingMatrix.getItem(i);
                    if (!stack.isEmpty() && isValidItem(stack.getItem())) {
                        hasValidItem = true;
                        herbList.add(StringTag.valueOf(stack.getItem().getRegistryName().toString()));
                    }
                }
                if (hasValidItem) {
                    CompoundTag nbt = result.getOrCreateTag();
                    nbt.put(TAG_HERBS, herbList);
                }
            }
        }
    }

    // Method to check if an item can be stored in the infuser
    private static boolean isValidItem(Item item) {
        return InitUtil.VALID_ITEMS.contains(item);
    }

    // Method to get the list of stored herbs in the infuser
    public List<String> getHerbs(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        if (nbt != null && nbt.contains(TAG_HERBS)) {
            ListTag herbList = nbt.getList(TAG_HERBS, Tag.TAG_STRING);
            List<String> herbs = new ArrayList<>();
            for (Tag tag : herbList) {
                herbs.add(tag.getAsString());
            }
            // Debug output
            System.out.println("Herbs in infuser: " + herbs);
            return herbs;
        }
        return new ArrayList<>();
    }

    // Method to get the crafting matrix from the player's inventory
    private static CraftingContainer getCraftMatrix(Player player) {
        AbstractContainerMenu containerMenu = player.containerMenu;
        if (containerMenu instanceof CraftingMenu) {
            CraftingMenu craftingMenu = (CraftingMenu) containerMenu;
            for (Slot slot : craftingMenu.slots) {
                if (slot.container instanceof CraftingContainer) {
                    return (CraftingContainer) slot.container;
                }
            }
        }
        return null;
    }
}

