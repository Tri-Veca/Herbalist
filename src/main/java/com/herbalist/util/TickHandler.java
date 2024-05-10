package com.herbalist.util;

import com.herbalist.Herbalist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Herbalist.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TickHandler {
    public static int TICKS = 0;

    @SubscribeEvent
    public static void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            TICKS++;
        }
    }
}

