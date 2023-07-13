package com.herbalist.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;

public interface IDrawable {


        int getWidth();

        int getHeight();

        default void draw(PoseStack poseStack) {
            draw(poseStack, 0, 0);
        }

        void draw(PoseStack poseStack, int xOffset, int yOffset);

    }

