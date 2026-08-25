package com.github.minecraftschurlimods.bibliocraft.client.widget;

import net.minecraft.util.text.StringTextComponent;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import javax.annotation.Nullable;

public abstract class SpriteButton extends Button {
    public SpriteButton(int x, int y, int width, int height, Button.IPressable onPress) {
        super(x, y, width, height, new StringTextComponent(""), onPress);
    }

    @Nullable
    protected abstract ResourceLocation getSprite();

    @Override
    public void renderButton(MatrixStack guiGraphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation sprite = getSprite();
        if (sprite != null) {
            Minecraft.getInstance().getTextureManager().bind(sprite);
            AbstractGui.blit(guiGraphics, x, y, 0, 0, 0, getWidth(), getHeight(), getWidth(), getHeight());
        }
    }

    public static class RegularAndHighlightSprite extends SpriteButton {
        private final ResourceLocation sprite;
        private final ResourceLocation highlightedSprite;

        public RegularAndHighlightSprite(ResourceLocation sprite, ResourceLocation highlightedSprite, int x, int y, int width, int height, Button.IPressable onPress) {
            super(x, y, width, height, onPress);
            this.sprite = sprite;
            this.highlightedSprite = highlightedSprite;
        }

        @Override
        protected ResourceLocation getSprite() {
            return isHovered() ? highlightedSprite : sprite;
        }
    }
}
