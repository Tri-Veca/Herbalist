package com.herbalist.common.event;

import com.herbalist.util.InitUtil;
import com.herbalist.util.KeybindHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;
public class HerbIdentifier {
    private static final int COOLDOWN_DURATION_IN_TICKS = 20; // Change this to your desired cooldown duration

    private static class DelayedMessage {
        private int delay;
        private String message;

        public DelayedMessage(int delay, String message) {
            this.delay = delay;
            this.message = message;
        }

        public boolean tick() {
            return --delay <= 0;
        }

        public String getMessage() {
            return message;
        }
    }

    private final List<DelayedMessage> delayedMessages = new ArrayList<>();

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                if (KeybindHandler.IDENTIFY_HERB.consumeClick()) {
                    Item heldItem = mc.player.getMainHandItem().getItem();
                    if (InitUtil.HERB_IDENTIFY.containsKey(heldItem)) {
                        // Add the cooldown
                        mc.player.getCooldowns().addCooldown(heldItem, COOLDOWN_DURATION_IN_TICKS);

                        // Schedule the message to be sent after the cooldown
                        String effect = InitUtil.HERB_IDENTIFY.get(heldItem);
                        delayedMessages.add(new DelayedMessage(COOLDOWN_DURATION_IN_TICKS, heldItem+ " " + effect));
                    }
                }

                // Process delayed messages
                Iterator<DelayedMessage> iterator = delayedMessages.iterator();
                while (iterator.hasNext()) {
                    DelayedMessage delayedMessage = iterator.next();
                    if (delayedMessage.tick()) {
                        mc.player.sendMessage(new TextComponent(delayedMessage.getMessage()), mc.player.getUUID());
                        iterator.remove();
                    }
                }
            }
        }
    }
}
