package com.github.minecraftschurlimods.bibliocraft.client.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

public class ColorButton extends Button {
    private final int color;

    public ColorButton(int color, int x, int y, int width, int height, ITextComponent title, Button.IPressable onPress, Button.ITooltip onTooltip) {
        super(x, y, width, height, title, onPress, onTooltip);
        this.color = 0xff000000 | color;
    }

    @Override
    public void renderButton(MatrixStack graphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bind(WIDGETS_LOCATION);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blit(graphics, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
        this.blit(graphics, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
        int xOffset = width / 8, yOffset = height / 8;
        fill(graphics, this.x + xOffset, this.y + yOffset, this.x + width - xOffset, this.y + height - yOffset, color);
        if (this.isHovered()) {
            this.renderToolTip(graphics, mouseX, mouseY);
        }
    }
}
