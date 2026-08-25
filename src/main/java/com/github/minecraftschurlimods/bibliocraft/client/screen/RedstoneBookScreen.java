package com.github.minecraftschurlimods.bibliocraft.client.screen;

import com.github.minecraftschurlimods.bibliocraft.util.Translations;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ReadBookScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.IReorderingProcessor;

import java.util.List;

public class RedstoneBookScreen extends Screen {
    public RedstoneBookScreen() {
        super(Translations.REDSTONE_BOOK_TITLE);
    }

    @Override
    public void render(MatrixStack graphics, int x, int y, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, x, y, partialTick);
        ITextProperties text = ITextProperties.of(Translations.REDSTONE_BOOK_TEXT.getString());
        List<IReorderingProcessor> lines = font.split(text, 114);
        int startX = (width - 192) / 2;
        for (int i = 0; i < lines.size(); i++) {
            font.draw(graphics, lines.get(i), startX + 36, 20 + i * 9, 0);
        }
    }

    @Override
    public void renderBackground(MatrixStack graphics) {
        super.renderBackground(graphics);
        this.minecraft.getTextureManager().bind(ReadBookScreen.BOOK_LOCATION);
        this.blit(graphics, (this.width - 192) / 2, 2, 0, 0, 192, 192);
    }

    @Override
    protected void init() {
        addButton(new Button(width / 2 - 100, 196, 200, 20, net.minecraft.client.gui.DialogTexts.GUI_DONE, $ -> onClose()));
    }
}
