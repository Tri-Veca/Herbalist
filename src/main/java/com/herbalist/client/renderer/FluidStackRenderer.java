package com.herbalist.client.renderer;
import com.google.common.base.Preconditions;
import com.herbalist.Herbalist;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;


import com.herbalist.util.TickHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.material.Fluid;

import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;


import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.client.gui.GuiComponent.blit;

public class FluidStackRenderer implements IIngredientRenderer<FluidStack> {
    private static final NumberFormat nf = NumberFormat.getIntegerInstance();
    private static final int TEXTURE_SIZE = 16;
    private static final int MIN_FLUID_HEIGHT = 1; // ensure tiny amounts of fluid are still visible
    private static final ResourceLocation BUBBLE_TEXTURE = new ResourceLocation(Herbalist.MOD_ID, "textures/gui/bubbles.png");
    private final int capacityMb;
    private final TooltipMode tooltipMode;
    /**
     * we shouldn't draw an overlay like this anymore,
     * it is kept for backward compatibility for
     * {@link mezz.jei.api.gui.ingredient.IGuiFluidStackGroup}
     */
    @SuppressWarnings({"removal", "DeprecatedIsStillUsed"})
    @Nullable
    @Deprecated
    private final IDrawable overlay;
    private final int width;
    private final int height;



    enum TooltipMode {
        SHOW_AMOUNT,
        SHOW_AMOUNT_AND_CAPACITY,
        ITEM_LIST
    }

    public FluidStackRenderer() {
        this(FluidAttributes.BUCKET_VOLUME, TooltipMode.ITEM_LIST, 16, 16, null);
    }

    public FluidStackRenderer(int capacityMb, boolean showCapacity, int width, int height) {
        this(capacityMb, showCapacity ? TooltipMode.SHOW_AMOUNT_AND_CAPACITY : TooltipMode.SHOW_AMOUNT, width, height, null);
    }

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public FluidStackRenderer(int capacityMb, boolean showCapacity, int width, int height, @Nullable IDrawable overlay) {
        this(capacityMb, showCapacity ? TooltipMode.SHOW_AMOUNT_AND_CAPACITY : TooltipMode.SHOW_AMOUNT, width, height, overlay);
    }

    private FluidStackRenderer(int capacityMb, TooltipMode tooltipMode, int width, int height, @Nullable IDrawable overlay) {
        Preconditions.checkArgument(capacityMb > 0, "capacity must be > 0");
        Preconditions.checkArgument(width > 0, "width must be > 0");
        Preconditions.checkArgument(height > 0, "height must be > 0");
        this.capacityMb = capacityMb;
        this.tooltipMode = tooltipMode;
        this.width = width;
        this.height = height;
        this.overlay = overlay;
    }


    public void renderBubbles(PoseStack poseStack, int x, int y, int width, int height, boolean isBoiling) {
        if (isBoiling) {

            Minecraft minecraft = Minecraft.getInstance();
            int tick = TickHandler.TICKS;
            float offset = (tick % 16) / 16.0F;

            minecraft.getTextureManager().bindForSetup(BUBBLE_TEXTURE);
            RenderSystem.setShaderTexture(0, BUBBLE_TEXTURE);

            // Get the BufferBuilder instance
            BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();

            // Start a new buffer for quads (since we're rendering a 2D sprite)
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

            // Add vertices for the sprite with the UV offset
            bufferBuilder.vertex(x, y + height, 0).uv(0, offset + 1).endVertex();
            bufferBuilder.vertex(x + width, y + height, 0).uv(1, offset + 1).endVertex();
            bufferBuilder.vertex(x + width, y, 0).uv(1, offset).endVertex();
            bufferBuilder.vertex(x, y, 0).uv(0, offset).endVertex();

            // Draw the sprite
            Tesselator.getInstance().end();
        }
    }



    @Override
    public void render(PoseStack poseStack, FluidStack fluidStack) {
        RenderSystem.enableBlend();

        drawFluid(poseStack, width, height, fluidStack);

        RenderSystem.setShaderColor(1, 1, 1, 1);

        if (overlay != null) {
            poseStack.pushPose();
            {
                poseStack.translate(0, 0, 200);
                overlay.draw(poseStack);
            }
            poseStack.popPose();
        }
        RenderSystem.disableBlend();
    }

    @SuppressWarnings("removal")
    @Override
    public void render(PoseStack stack, int xPosition, int yPosition, @Nullable FluidStack ingredient) {
        if (ingredient != null) {
            stack.pushPose();
            {
                stack.translate(xPosition, yPosition, 0);
                render(stack, ingredient);
            }
            stack.popPose();
        }
    }

    private void drawFluid(PoseStack poseStack, final int width, final int height, FluidStack fluidStack) {
        Fluid fluid = fluidStack.getFluid();
        if (fluid == null) {
            return;
        }

        TextureAtlasSprite fluidStillSprite = getStillFluidSprite(fluidStack);

        FluidAttributes attributes = fluid.getAttributes();
        int fluidColor = attributes.getColor(fluidStack);

        int amount = fluidStack.getAmount();
        int scaledAmount = (amount * height) / capacityMb;
        if (amount > 0 && scaledAmount < MIN_FLUID_HEIGHT) {
            scaledAmount = MIN_FLUID_HEIGHT;
        }
        if (scaledAmount > height) {
            scaledAmount = height;
        }

        drawTiledSprite(poseStack, width, height, fluidColor, scaledAmount, fluidStillSprite);
    }

    private static void drawTiledSprite(PoseStack poseStack, final int tiledWidth, final int tiledHeight, int color, int scaledAmount, TextureAtlasSprite sprite) {
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        Matrix4f matrix = poseStack.last().pose();
        setGLColorFromInt(color);

        final int xTileCount = tiledWidth / TEXTURE_SIZE;
        final int xRemainder = tiledWidth - (xTileCount * TEXTURE_SIZE);
        final int yTileCount = scaledAmount / TEXTURE_SIZE;
        final int yRemainder = scaledAmount - (yTileCount * TEXTURE_SIZE);

        final int yStart = tiledHeight;

        for (int xTile = 0; xTile <= xTileCount; xTile++) {
            for (int yTile = 0; yTile <= yTileCount; yTile++) {
                int width = (xTile == xTileCount) ? xRemainder : TEXTURE_SIZE;
                int height = (yTile == yTileCount) ? yRemainder : TEXTURE_SIZE;
                int x = (xTile * TEXTURE_SIZE);
                int y = yStart - ((yTile + 1) * TEXTURE_SIZE);
                if (width > 0 && height > 0) {
                    int maskTop = TEXTURE_SIZE - height;
                    int maskRight = TEXTURE_SIZE - width;

                    drawTextureWithMasking(matrix, x, y, sprite, maskTop, maskRight, 100);
                }
            }
        }
    }

    private static TextureAtlasSprite getStillFluidSprite(FluidStack fluidStack) {
        Minecraft minecraft = Minecraft.getInstance();
        Fluid fluid = fluidStack.getFluid();
        FluidAttributes attributes = fluid.getAttributes();
        ResourceLocation fluidStill = attributes.getStillTexture(fluidStack);
        return minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidStill);
    }

    private static void setGLColorFromInt(int color) {
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        float alpha = ((color >> 24) & 0xFF) / 255F;

        RenderSystem.setShaderColor(red, green, blue, alpha);
    }

    private static void drawTextureWithMasking(Matrix4f matrix, float xCoord, float yCoord, TextureAtlasSprite textureSprite, int maskTop, int maskRight, float zLevel) {
        float uMin = textureSprite.getU0();
        float uMax = textureSprite.getU1();
        float vMin = textureSprite.getV0();
        float vMax = textureSprite.getV1();
        uMax = uMax - (maskRight / 16F * (uMax - uMin));
        vMax = vMax - (maskTop / 16F * (vMax - vMin));

        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(matrix, xCoord, yCoord + 16, zLevel).uv(uMin, vMax).endVertex();
        bufferBuilder.vertex(matrix, xCoord + 16 - maskRight, yCoord + 16, zLevel).uv(uMax, vMax).endVertex();
        bufferBuilder.vertex(matrix, xCoord + 16 - maskRight, yCoord + maskTop, zLevel).uv(uMax, vMin).endVertex();
        bufferBuilder.vertex(matrix, xCoord, yCoord + maskTop, zLevel).uv(uMin, vMin).endVertex();
        tessellator.end();
    }

    @Override
    public List<Component> getTooltip(FluidStack fluidStack, TooltipFlag tooltipFlag) {
        List<Component> tooltip = new ArrayList<>();
        Fluid fluidType = fluidStack.getFluid();
        if (fluidType == null) {
            return tooltip;
        }

        Component displayName = fluidStack.getDisplayName();
        tooltip.add(displayName);

        int amount = fluidStack.getAmount();
        if (tooltipMode == TooltipMode.SHOW_AMOUNT_AND_CAPACITY) {
            TranslatableComponent amountString = new TranslatableComponent("herb.tooltip.liquid.amount.with.capacity", nf.format(amount), nf.format(capacityMb));
            tooltip.add(amountString.withStyle(ChatFormatting.GRAY));
        } else if (tooltipMode == TooltipMode.SHOW_AMOUNT) {
            TranslatableComponent amountString = new TranslatableComponent("herb.tooltip.liquid.amount", nf.format(amount));
            tooltip.add(amountString.withStyle(ChatFormatting.GRAY));
        }

        return tooltip;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}

