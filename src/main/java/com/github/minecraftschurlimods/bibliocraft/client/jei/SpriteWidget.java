package com.github.minecraftschurlimods.bibliocraft.client.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("unused")
public class SpriteWidget {
    private final ResourceLocation sprite;
    private final int x;
    private final int y;
    private final int blitOffset;
    private final int width;
    private final int height;

    public SpriteWidget(ResourceLocation sprite, int x, int y, int blitOffset, int width, int height) {
        this.sprite = sprite;
        this.x = x;
        this.y = y;
        this.blitOffset = blitOffset;
        this.width = width;
        this.height = height;
    }

    public SpriteWidget(ResourceLocation sprite, int x, int y, int width, int height) {
        this(sprite, x, y, 0, width, height);
    }

    public void draw(MatrixStack graphics, double mouseX, double mouseY) {
        Minecraft.getInstance().getTextureManager().bind(sprite);
        AbstractGui.blit(graphics, x, y, blitOffset, 0, 0, width, height, width, height);
    }
}
