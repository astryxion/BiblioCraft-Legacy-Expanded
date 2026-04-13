package com.github.minecraftschurlimods.bibliocraft.client.widget;

import com.github.minecraftschurlimods.bibliocraft.client.screen.ClockScreen;
import com.github.minecraftschurlimods.bibliocraft.content.clock.ClockTrigger;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ClockTriggerPanel extends ScrollPanel {
    public final ClockScreen owner;
    public final int width;
    public final int height;
    private final List<ClockTriggerElement> elements;

    public ClockTriggerPanel(int x, int y, int width, int height, List<ClockTrigger> triggers, ClockScreen owner) {
        super(x, y, width, height);
        this.owner = owner;
        this.width = width;
        this.height = height;
        elements = new ArrayList<>();
        rebuildElements(triggers);
    }

    @Override
    protected int getContentHeight() {
        return elements.size() * ClockTriggerElement.HEIGHT;
    }

    @Override
    protected void drawPanel(GuiGraphics graphics, int entryRight, int relativeY, int mouseX, int mouseY, float partialTick) {
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(getX(), getY(), 0);
        for (int i = 0; i < elements.size(); i++) {
            pose.pushPose();
            elements.get(i).render(graphics, mouseX - getX(), (int) (mouseY - getY() - i * ClockTriggerElement.HEIGHT + scrollDistance), 1);
            pose.popPose();
            pose.translate(0, ClockTriggerElement.HEIGHT, 0);
        }
        pose.popPose();
    }

    public void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        ClockTriggerElement hovered = getHovered(mouseX, mouseY);
        if (hovered == null) return;
        float y = (float) (mouseY - getY() + scrollDistance);
        PoseStack pose = graphics.pose();
        pose.pushPose();
        pose.translate(getX(), mouseY, 0);
        hovered.renderTooltip(graphics, mouseX - getX(), (int) (y % ClockTriggerElement.HEIGHT));
        pose.popPose();
    }

    @Override
    protected int getScrollAmount() {
        return hasScrollbar(elements.size()) ? ClockTriggerElement.HEIGHT / 2 : 0;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        ClockTriggerElement hovered = getHovered(mouseX, mouseY);
        return hovered == null ? super.mouseClicked(mouseX, mouseY, button) : hovered.mouseClicked(mouseX - getX(), (int) ((mouseY - getY() + scrollDistance) % ClockTriggerElement.HEIGHT), button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        ClockTriggerElement hovered = getHovered(mouseX, mouseY);
        return hovered == null ? super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) : hovered.mouseDragged(mouseX - getX(), (int) ((mouseY - getY() + scrollDistance) % ClockTriggerElement.HEIGHT), button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        ClockTriggerElement hovered = getHovered(mouseX, mouseY);
        return hovered == null ? super.mouseReleased(mouseX, mouseY, button) : hovered.mouseReleased(mouseX - getX(), (int) ((mouseY - getY() + scrollDistance) % ClockTriggerElement.HEIGHT), button);
    }

    @Override
    public boolean mouseScrolled(double p_94686_, double p_94687_, double p_94688_, double p_294830_) {
        return hasScrollbar(elements.size()) && super.mouseScrolled(p_94686_, p_94687_, p_94688_, p_294830_);
    }

    public boolean hasScrollbar(int elements) {
        return elements * ClockTriggerElement.HEIGHT > height;
    }

    public void rebuildElements(List<ClockTrigger> triggers) {
        elements.clear();
        for (int i = 0; i < triggers.size(); i++) {
            elements.add(new ClockTriggerElement(triggers.get(i), this, triggers.size()));
        }
    }

    @Nullable
    public ClockTriggerElement getHovered(double mouseX, double mouseY) {
        double x = mouseX - getX();
        if (x < 0 || x >= width) return null;
        double y = mouseY - getY() + scrollDistance;
        if (y < scrollDistance || y >= height + scrollDistance) return null;
        int index = (int) (y / ClockTriggerElement.HEIGHT);
        return index < 0 || index >= elements.size() ? null : elements.get(index);
    }
}
