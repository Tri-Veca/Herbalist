package com.herbalist.init;

import com.herbalist.Herbalist;
import com.herbalist.init.custom.Kettle.KettleBlock;
import com.herbalist.init.custom.plant.AlfalfaPlant;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class BlockInit {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Herbalist.MOD_ID);

    public static final RegistryObject<Block> KETTLE = registerBlock("kettle",
            () -> new KettleBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).randomTicks().noOcclusion()), Herbalist.HERB);

    public static final RegistryObject<Block> ALFALFA_PLANT = registerBlockWithoutBlockItem("alfalfa_plant",
            () -> new AlfalfaPlant(BlockBehaviour.Properties.copy(Blocks.WHEAT).noOcclusion()));

    public static final RegistryObject<Block> MINT_PLANT = registerBlockWithoutBlockItem("mint_plant",
            () -> new AlfalfaPlant(BlockBehaviour.Properties.copy(Blocks.WHEAT).noOcclusion()));

    public static final RegistryObject<Block> LAVENDER_PLANT = registerBlockWithoutBlockItem("lavender_plant",
            () -> new AlfalfaPlant(BlockBehaviour.Properties.copy(Blocks.WHEAT).noOcclusion()));
    public static final DeferredRegister<Item> ITEMS = ItemInit.ITEMS;
    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, CreativeModeTab tab) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, tab);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block, CreativeModeTab tab) {
        return ItemInit.ITEMS.register(name, () -> new BlockItem(block.get(),
                new Item.Properties().tab(tab)));
    }
    private static <T extends Block> RegistryObject<T> registerBlockWithoutBlockItem(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
