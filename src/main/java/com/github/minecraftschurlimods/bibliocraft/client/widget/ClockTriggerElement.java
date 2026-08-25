package com.github.minecraftschurlimods.bibliocraft.client.widget;

import net.minecraft.util.text.StringTextComponent;
import com.github.minecraftschurlimods.bibliocraft.client.screen.ClockTriggerEditScreen;
import com.github.minecraftschurlimods.bibliocraft.content.clock.ClockTrigger;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.github.minecraftschurlimods.bibliocraft.util.Translations;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ClockTriggerElement extends Screen {
    public static final int WIDTH = 160;
    public static final int HEIGHT = 20;
    private static final ResourceLocation BACKGROUND = BCUtil.bcLoc("textures/gui/clock_trigger.png");
    private static final ResourceLocation EDIT = BCUtil.bcLoc("textures/gui/sprites/edit.png");
    private static final ResourceLocation EDIT_HIGHLIGHTED = BCUtil.bcLoc("textures/gui/sprites/edit_highlighted.png");
    private static final ResourceLocation DELETE = BCUtil.bcLoc("textures/gui/sprites/delete.png");
    private static final ResourceLocation DELETE_HIGHLIGHTED = BCUtil.bcLoc("textures/gui/sprites/delete_highlighted.png");
    private static final ItemStack REDSTONE = new ItemStack(Items.REDSTONE);
    private static final ItemStack NOTE_BLOCK = new ItemStack(Items.NOTE_BLOCK);
    public final ClockTriggerPanel owner;
    private final ClockTrigger trigger;
    private final int listSize;
    private final Button editButton;
    private final Button deleteButton;

    public ClockTriggerElement(ClockTrigger trigger, ClockTriggerPanel owner, int listSize) {
        super(new StringTextComponent(String.format("%02d%s%02d", trigger.hour(), Translations.CLOCK_TIME_SEPARATOR.getString(), trigger.minute())));
        this.minecraft = ClientUtil.getMc();
        this.font = ClientUtil.getFont();
        this.width = WIDTH;
        this.height = HEIGHT;
        this.trigger = trigger;
        this.owner = owner;
        this.listSize = listSize;
        int width = owner.hasScrollbar(listSize) ? WIDTH - 6 : WIDTH;
        editButton = addButton(new SpriteButton.RegularAndHighlightSprite(EDIT, EDIT_HIGHLIGHTED, width - 34, 2, 16, 16, $ -> ClientUtil.getMc().pushGuiLayer(new ClockTriggerEditScreen(owner.owner, trigger))));
        deleteButton = addButton(new SpriteButton.RegularAndHighlightSprite(DELETE, DELETE_HIGHLIGHTED, width - 18, 2, 16, 16, $ -> owner.owner.removeTrigger(trigger)));
    }

    @Override
    public void render(MatrixStack graphics, int mouseX, int mouseY, float partialTick) {
        boolean hasScrollbar = owner.hasScrollbar(listSize);
        net.minecraft.client.Minecraft.getInstance().getTextureManager().bind(BACKGROUND);
        blit(graphics, 0, 0, 0, hasScrollbar ? 20 : 0, hasScrollbar ? WIDTH - 6 : WIDTH, HEIGHT);
        MatrixStack pose = graphics;
        IRenderTypeBuffer.Impl buffer = IRenderTypeBuffer.immediate(net.minecraft.client.renderer.Tessellator.getInstance().getBuilder());
        pose.pushPose();
        pose.translate(8, 12, 0);
        pose.scale(16, -16, 0);
        pose.translate(0.125, 0.125, 0);
        if (trigger.redstone()) {
            ClientUtil.renderGuiItem(REDSTONE, pose, buffer, LightTexture.pack(15, 15), OverlayTexture.NO_OVERLAY);
        }
        pose.translate(1.0625, 0, 0);
        if (trigger.sound()) {
            ClientUtil.renderGuiItem(NOTE_BLOCK, pose, buffer, LightTexture.pack(15, 15), OverlayTexture.NO_OVERLAY);
        }
        pose.popPose();
        buffer.endBatch();
        ClientUtil.getFont().draw(graphics, getTitle(), 36, 7, 0x404040);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    public void renderBackground(MatrixStack graphics) {
        // No background drawn for trigger element
    }

    public void renderTooltip(MatrixStack graphics, int mouseX, int mouseY) {
        if (mouseY >= 2 && mouseY < 18) {
            FontRenderer font = ClientUtil.getFont();
            if (trigger.redstone() && mouseX >= 2 && mouseX < 18) {
                this.renderTooltip(graphics, Translations.CLOCK_EMIT_REDSTONE, mouseX, mouseY);
            } else if (trigger.sound() && mouseX >= 19 && mouseX < 37) {
                this.renderTooltip(graphics, Translations.CLOCK_EMIT_SOUND, mouseX, mouseY);
            } else if (editButton.isHovered()) {
                this.renderTooltip(graphics, Translations.CLOCK_EDIT_TRIGGER, mouseX, mouseY);
            } else if (deleteButton.isHovered()) {
                this.renderTooltip(graphics, Translations.CLOCK_DELETE_TRIGGER, mouseX, mouseY);
            }
        }
    }
}
