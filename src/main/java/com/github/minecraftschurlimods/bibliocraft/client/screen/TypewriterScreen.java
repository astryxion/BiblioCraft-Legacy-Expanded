package com.github.minecraftschurlimods.bibliocraft.client.screen;

import com.github.minecraftschurlimods.bibliocraft.content.typewriter.TypewriterBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.content.typewriter.TypewriterPage;
import com.github.minecraftschurlimods.bibliocraft.content.typewriter.TypewriterSyncPacket;
import com.github.minecraftschurlimods.bibliocraft.init.BCSoundEvents;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.github.minecraftschurlimods.bibliocraft.util.Translations;
import net.minecraft.util.Util;
import net.minecraft.client.gui.FontRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.ResourceLocation;
import java.util.Random;
import net.minecraft.util.StringUtils;
import com.github.minecraftschurlimods.bibliocraft.BCEventHandler;
import org.lwjgl.glfw.GLFW;

public class TypewriterScreen extends Screen {
    public static final int IMAGE_WIDTH = 100;
    public static final int IMAGE_HEIGHT = 144;
    private static final ResourceLocation BACKGROUND = BCUtil.bcLoc("textures/gui/typewriter_page.png");
    private final Random random = new Random(Util.getNanos());
    private final BlockPos pos;
    private TypewriterPage page;
    private int leftPos;
    private int topPos;
    private int row;
    private String currentLine;
    private int frameTick;
    private boolean hasPendingSound = false;

    public TypewriterScreen(BlockPos pos) {
        super(Translations.TYPEWRITER_TITLE);
        this.pos = pos;
        net.minecraft.tileentity.TileEntity be = ClientUtil.getLevel().getBlockEntity(pos);
        page = be instanceof TypewriterBlockEntity ? ((TypewriterBlockEntity) be).getPage() : TypewriterPage.DEFAULT;
        row = page.line();
        if (row == TypewriterPage.MAX_LINES) {
            onClose();
        } else {
            currentLine = page.lines().get(row);
        }
    }

    @Override
    protected void init() {
        leftPos = (width - IMAGE_WIDTH) / 2;
        topPos = (height - IMAGE_HEIGHT) / 2;
        addButton(new Button(width / 2 - 100, topPos + IMAGE_HEIGHT + 4, 200, 20, net.minecraft.client.gui.DialogTexts.GUI_DONE, $ -> onClose()));
    }

    @Override
    public void tick() {
        super.tick();
        frameTick++;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        sync();
        super.onClose();
    }

    @Override
    public void render(MatrixStack graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        FontRenderer font = ClientUtil.getFont();
        for (int i = 0; i < row; i++) {
            if (i >= page.lines().size()) continue;
            font.draw(graphics, page.lines().get(i), leftPos + 2, topPos + 2 + i * 10, 0);
        }
        if (row < TypewriterPage.MAX_LINES) {
            int width = font.draw(graphics, currentLine, leftPos + 2, topPos + 2 + row * 10, 0);
            if (frameTick / 6 % 2 == 0) {
                font.draw(graphics, "_", width, topPos + 2 + row * 10, 0);
            }
        }
    }

    @Override
    public void renderBackground(MatrixStack graphics) {
        super.renderBackground(graphics);
        this.minecraft.getTextureManager().bind(BACKGROUND);
        this.blit(graphics, leftPos, topPos, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            lineBreak();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (net.minecraft.util.SharedConstants.isAllowedChatCharacter(codePoint)) {
            currentLine += codePoint;
            if (currentLine.length() >= TypewriterPage.MAX_LINE_LENGTH) {
                lineBreak();
            } else {
                ClientUtil.getPlayer().playSound(BCSoundEvents.TYPEWRITER_TYPE.get(), 0.9f + random.nextFloat() * 0.2f, 0.9f + random.nextFloat() * 0.2f);
            }
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    private void sync() {
        page = page.copy().withLine(row);
        if (row < page.lines().size()) {
            page.lines().set(row, currentLine);
        }
        if (ClientUtil.getLevel().getBlockEntity(pos) instanceof TypewriterBlockEntity) {
            TypewriterBlockEntity typewriter = (TypewriterBlockEntity) ClientUtil.getLevel().getBlockEntity(pos);
            typewriter.setPage(page);
        }
        BCEventHandler.getChannel().sendToServer(new TypewriterSyncPacket(pos, page, hasPendingSound));
        if (hasPendingSound) {
            ClientUtil.getPlayer().playSound(BCSoundEvents.TYPEWRITER_CHIME.get(), 1f, 1f);
            hasPendingSound = false;
        }
    }

    private void lineBreak() {
        page = page.copy();
        page.lines().set(row, currentLine);
        currentLine = "";
        row++;
        hasPendingSound = true;
        if (row >= TypewriterPage.MAX_LINES) {
            onClose();
        } else {
            sync();
        }
    }
}
