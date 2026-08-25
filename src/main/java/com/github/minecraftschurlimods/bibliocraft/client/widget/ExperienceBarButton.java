package com.github.minecraftschurlimods.bibliocraft.client.widget;

import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.ResourceLocation;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class ExperienceBarButton extends Button {
    private final ResourceLocation backgroundTexture;
    private final ResourceLocation progressTexture;
    private final IntSupplier levelGetter;
    private final Supplier<Float> progressGetter;

    public ExperienceBarButton(ITextComponent message, int x, int y, int width, int height, ResourceLocation backgroundTexture, ResourceLocation progressTexture, IntSupplier levelGetter, Supplier<Float> progressGetter, Button.IPressable onPress) {
        super(x, y, width, height, message, onPress, (btn, ms, mx, my) -> {
            if (btn instanceof ExperienceBarButton) {
                ((ExperienceBarButton) btn).renderXpTooltip(ms, mx, my);
            }
        });
        this.backgroundTexture = backgroundTexture;
        this.progressTexture = progressTexture;
        this.levelGetter = levelGetter;
        this.progressGetter = progressGetter;
    }

    private void renderXpTooltip(MatrixStack ms, int mx, int my) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null) {
            mc.screen.renderTooltip(ms, getMessage(), mx, my);
        }
    }

    @Override
    public void renderButton(MatrixStack graphics, int mouseX, int mouseY, float partialTick) {
        Minecraft.getInstance().getTextureManager().bind(backgroundTexture);
        AbstractGui.blit(graphics, x, y, 0, 0, 0, getWidth(), getHeight(), getWidth(), getHeight());
        Minecraft.getInstance().getTextureManager().bind(progressTexture);
        AbstractGui.blit(graphics, x, y, 0, 0, 0, (int) (getWidth() * progressGetter.get()), getHeight(), getWidth(), getHeight());
        ClientUtil.renderXpText(levelGetter.getAsInt() + "", graphics, x + getWidth() / 2, y - 4);
    }
}
