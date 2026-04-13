package com.github.minecraftschurlimods.bibliocraft.client.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;

/**
 * Simple scrollable panel. Replaces NeoForge ScrollPanel for Fabric.
 * Subclasses implement {@link #getContentHeight()} and {@link #drawPanel(GuiGraphics, int, int, int, int, float)}.
 */
public abstract class ScrollPanel extends AbstractWidget {
    protected final int panelWidth;
    protected final int panelHeight;
    /** Current scroll offset (same meaning as NeoForge ScrollPanel). */
    protected double scrollDistance;
    private boolean scrolling;

    public ScrollPanel(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.panelWidth = width;
        this.panelHeight = height;
    }

    protected abstract int getContentHeight();

    protected abstract void drawPanel(GuiGraphics graphics, int entryRight, int relativeY, int mouseX, int mouseY, float partialTick);

    protected int getScrollAmount() {
        return 20;
    }

    public boolean hasScrollbar(int contentHeight) {
        return contentHeight > panelHeight;
    }

    private int getMaxScroll() {
        return Math.max(0, getContentHeight() - panelHeight);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int maxScroll = getMaxScroll();
        if (maxScroll <= 0) scrollDistance = 0;
        else scrollDistance = Math.clamp(scrollDistance, 0, maxScroll);

        graphics.enableScissor(getX(), getY(), getX() + panelWidth, getY() + panelHeight);
        graphics.pose().pushPose();
        graphics.pose().translate(0, -scrollDistance, 0);
        drawPanel(graphics, getX() + panelWidth, (int) (getY() + scrollDistance), mouseX, mouseY, partialTick);
        graphics.pose().popPose();
        graphics.disableScissor();

        if (maxScroll > 0) {
            int scrollBarHeight = Math.max(32, panelHeight * panelHeight / getContentHeight());
            int scrollBarY = (int) ((float) (scrollDistance * (panelHeight - scrollBarHeight)) / maxScroll);
            graphics.fill(RenderType.guiOverlay(), getX() + panelWidth - 8, getY(), getX() + panelWidth, getY() + panelHeight, 0x80000000);
            graphics.fill(RenderType.guiOverlay(), getX() + panelWidth - 8, getY() + scrollBarY, getX() + panelWidth - 2, getY() + scrollBarY + scrollBarHeight, 0xFF808080);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!hasScrollbar(getContentHeight())) return false;
        scrollDistance = Math.clamp(scrollDistance - scrollY * getScrollAmount(), 0, getMaxScroll());
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isMouseOver(mouseX, mouseY)) return false;
        scrolling = button == 0 && mouseX >= getX() + panelWidth - 8;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        scrolling = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (scrolling && hasScrollbar(getContentHeight())) {
            int maxScroll = getMaxScroll();
            int scrollBarHeight = Math.max(32, panelHeight * panelHeight / getContentHeight());
            scrollDistance = Math.clamp(scrollDistance + dragY * maxScroll / (panelHeight - scrollBarHeight), 0, maxScroll);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }
}
