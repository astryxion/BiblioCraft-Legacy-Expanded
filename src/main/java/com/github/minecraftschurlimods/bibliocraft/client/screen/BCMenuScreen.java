package com.github.minecraftschurlimods.bibliocraft.client.screen;

import com.github.minecraftschurlimods.bibliocraft.util.block.BCMenu;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.PlayerInventory;

public class BCMenuScreen<T extends BCMenu<?>> extends ContainerScreen<T> {
    private final ResourceLocation background;

    public BCMenuScreen(T menu, PlayerInventory inventory, ITextComponent title, ResourceLocation background) {
        super(menu, inventory, title);
        this.background = background;
    }

    @Override
    protected void renderBg(MatrixStack guiGraphics, float partialTicks, int x, int y) {
        this.minecraft.getTextureManager().bind(background);
        this.blit(guiGraphics, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(MatrixStack guiGraphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public ITextComponent getTitle() {
        return menu.getDisplayName();
    }
}
