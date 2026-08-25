package com.github.minecraftschurlimods.bibliocraft.client.screen;

import com.github.minecraftschurlimods.bibliocraft.content.slottedbook.SlottedBookMenu;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.Translations;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.entity.player.PlayerInventory;

import java.util.List;

public class SlottedBookScreen extends ContainerScreen<SlottedBookMenu> {
    private static final ResourceLocation BACKGROUND = BCUtil.bcLoc("textures/gui/slotted_book.png");

    public SlottedBookScreen(SlottedBookMenu menu, PlayerInventory inventory, ITextComponent title) {
        super(menu, inventory, title);
        imageHeight = 223;
        inventoryLabelY = imageHeight - 92;
    }

    @Override
    public void renderBg(MatrixStack graphics, float partialTick, int mouseX, int mouseY) {
        this.minecraft.getTextureManager().bind(BACKGROUND);
        this.blit(graphics, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(MatrixStack guiGraphics, int mouseX, int mouseY) {
        font.draw(guiGraphics, this.inventory.getDisplayName(), inventoryLabelX, inventoryLabelY, 0x404040);
    }

    @Override
    public void render(MatrixStack graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        ITextProperties text = ITextProperties.of(Translations.SLOTTED_BOOK_TEXT.getString());
        List<IReorderingProcessor> lines = font.split(text, 114);
        int startX = (width - 192) / 2;
        int startY = topPos + 111 - lines.size() * 9;
        for (int i = 0; i < lines.size(); i++) {
            font.draw(graphics, lines.get(i), startX + 38, startY + i * 9, 0);
        }
        renderTooltip(graphics, mouseX, mouseY);
    }
}
