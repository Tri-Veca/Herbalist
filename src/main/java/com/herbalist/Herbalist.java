package com.herbalist;

import com.herbalist.init.BlockInit;
import com.herbalist.init.ItemInit;
import com.herbalist.init.custom.InfuserItem;
import com.herbalist.networking.ModMessages;
import com.herbalist.util.BlockEntityUtil;
import com.herbalist.util.MenuUtil;
import com.herbalist.util.RecipeUtil;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("herb")
public class Herbalist {

    public static final String MOD_ID = "herb";

    public static CreativeModeTab HERB = new CreativeModeTab(MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ItemInit.ALFALFA_SEEDS.get());
        }
    };

    public Herbalist(){
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        ItemInit.ITEMS.register(bus);
        BlockInit.BLOCKS.register(bus);
        MenuUtil.MENUS.register(bus);
        BlockEntityUtil.BLOCK_ENTITIES.register(bus);
        RecipeUtil.SERIALIZERS.register(bus);
        ModMessages.register();

        bus.addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(BlockInit.ALFALFA_PLANT.get(), RenderType.cutout());
        InfuserItem.initValidItems();

        event.enqueueWork(() -> {

        });
    }
}
