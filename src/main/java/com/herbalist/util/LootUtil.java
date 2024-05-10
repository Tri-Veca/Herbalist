package com.herbalist.util;

import com.herbalist.Herbalist;
import com.herbalist.loot.AlfalfaSeedsFromGrass;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(modid = Herbalist.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class LootUtil {
    @SubscribeEvent
    public static void registerModifierSerializers(@Nonnull final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
        event.getRegistry().registerAll(
                new AlfalfaSeedsFromGrass.Serializer().setRegistryName(new ResourceLocation(Herbalist.MOD_ID, "alfalfa_seeds_from_grass"))
        );
    }
}

