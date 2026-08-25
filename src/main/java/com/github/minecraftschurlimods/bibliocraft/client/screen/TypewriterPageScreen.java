package com.github.minecraftschurlimods.bibliocraft.client.screen;

import com.github.minecraftschurlimods.bibliocraft.content.typewriter.TypewriterPage;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import net.minecraft.client.gui.FontRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;

public class TypewriterPageScreen extends Screen {
    private static final ResourceLocation BACKGROUND = BCUtil.bcLoc("textures/gui/typewriter_page.png");
    private final TypewriterPage page;
    private int leftPos;
    private int topPos;

    public TypewriterPageScreen(ItemStack stack) {
        super(stack.getHoverName());
        page = TypewriterPage.getFromStack(stack);
    }

    @Override
    protected void init() {
        leftPos = (width - TypewriterScreen.IMAGE_WIDTH) / 2;
        topPos = (height - TypewriterScreen.IMAGE_HEIGHT) / 2;
        addButton(new Button(width / 2 - 100, topPos + TypewriterScreen.IMAGE_HEIGHT + 4, 200, 20, net.minecraft.client.gui.DialogTexts.GUI_DONE, $ -> onClose()));
    }

    @Override
    public void render(MatrixStack graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        FontRenderer font = ClientUtil.getFont();
        for (int i = 0; i < page.lines().size(); i++) {
            font.draw(graphics, page.lines().get(i), leftPos + 2, topPos + 2 + i * 10, 0);
        }
    }

    @Override
    public void renderBackground(MatrixStack graphics) {
        super.renderBackground(graphics);
        this.minecraft.getTextureManager().bind(BACKGROUND);
        this.blit(graphics, leftPos, topPos, 0, 0, TypewriterScreen.IMAGE_WIDTH, TypewriterScreen.IMAGE_HEIGHT);
    }
}
