package com.herbalist.util;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.controls.KeyBindsList;
import net.minecraftforge.client.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class KeybindHandler {

    public static final KeyMapping IDENTIFY_HERB = new KeyMapping(
            "key.herb.identify",
            GLFW.GLFW_KEY_I,
            "key.categories.herb"
    );

    public static void registerKeyBindings() {
        ClientRegistry.registerKeyBinding(IDENTIFY_HERB);
    }
}