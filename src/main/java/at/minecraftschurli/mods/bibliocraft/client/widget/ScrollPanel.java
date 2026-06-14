package at.minecraftschurli.mods.bibliocraft.client.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

/**
 * Fabric replacement for NeoForge's scroll panel widget.
 */
public abstract class ScrollPanel extends AbstractWidget {
    protected final Minecraft minecraft;
    protected int left;
    protected int top;
    protected double scrollDistance;
    private boolean scrolling;

    public ScrollPanel(Minecraft minecraft, int width, int height, int top, int left, int border) {
        super(left, top, width, height, Component.empty());
        this.minecraft = minecraft;
        this.left = left;
        this.top = top;
    }

    protected abstract int getContentHeight();

    protected abstract void drawPanel(GuiGraphicsExtractor graphics, int entryRight, int relativeY, int mouseX, int mouseY);

    protected int getScrollAmount() {
        return 20;
    }

    protected int getMaxScroll() {
        return Math.max(0, getContentHeight() - height);
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int maxScroll = getMaxScroll();
        if (maxScroll <= 0) {
            scrollDistance = 0;
        } else {
            scrollDistance = Math.clamp(scrollDistance, 0, maxScroll);
        }

        graphics.enableScissor(getX(), getY(), getX() + width, getY() + height);
        drawPanel(graphics, getX() + width, (int) (getY() + scrollDistance), mouseX, mouseY);
        graphics.disableScissor();

        if (maxScroll > 0) {
            int scrollBarHeight = Math.max(32, height * height / getContentHeight());
            int scrollBarY = (int) ((float) (scrollDistance * (height - scrollBarHeight)) / maxScroll);
            graphics.fill(RenderPipelines.GUI, getX() + width - 8, getY(), getX() + width, getY() + height, 0x80000000);
            graphics.fill(RenderPipelines.GUI, getX() + width - 8, getY() + scrollBarY, getX() + width - 2, getY() + scrollBarY + scrollBarHeight, 0xFF808080);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (getMaxScroll() <= 0) {
            return false;
        }
        scrollDistance = Math.clamp(scrollDistance - scrollY * getScrollAmount(), 0, getMaxScroll());
        return true;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!isMouseOver(event.x(), event.y())) {
            return super.mouseClicked(event, doubleClick);
        }
        scrolling = event.button() == 0 && event.x() >= getX() + width - 8;
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        scrolling = false;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        int maxScroll = getMaxScroll();
        if (scrolling && maxScroll > 0) {
            int scrollBarHeight = Math.max(32, height * height / getContentHeight());
            scrollDistance = Math.clamp(scrollDistance + dragY * maxScroll / (height - scrollBarHeight), 0, maxScroll);
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        defaultButtonNarrationText(narrationElementOutput);
    }
}
