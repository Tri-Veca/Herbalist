package com.util;

import com.herbalist.Herbalist;
import com.herbalist.init.BlockInit;
import com.herbalist.init.custom.Kettle.KettleEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityUtil {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Herbalist.MOD_ID);

    public static final RegistryObject<BlockEntityType<KettleEntity>> KETTLE_ENTITY =
            BLOCK_ENTITIES.register("kettle_entity", () ->
                    BlockEntityType.Builder.of(KettleEntity::new,
                            BlockInit.KETTLE.get()).build(null));
    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
