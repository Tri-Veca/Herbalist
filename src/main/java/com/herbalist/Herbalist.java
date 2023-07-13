package com.herbalist;

import com.herbalist.init.BlockInit;
import com.herbalist.init.ItemInit;
import com.util.BlockEntityUtil;
import com.util.MenuUtil;
import com.util.RecipeUtil;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
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


        bus.addListener(Herbalist::clientSetup);
    }
    private static void clientSetup(final FMLClientSetupEvent event) {
       
    }
    private void setup(final FMLCommonSetupEvent event) {

        event.enqueueWork(() -> {
           
        });
    }
}