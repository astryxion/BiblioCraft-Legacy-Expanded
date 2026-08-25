package com.github.minecraftschurlimods.bibliocraft.client.screen;

import net.minecraft.util.text.StringTextComponent;
import com.github.minecraftschurlimods.bibliocraft.client.widget.ClockTriggerPanel;
import com.github.minecraftschurlimods.bibliocraft.content.clock.ClockBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.content.clock.ClockSyncPacket;
import com.github.minecraftschurlimods.bibliocraft.content.clock.ClockTrigger;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.github.minecraftschurlimods.bibliocraft.util.Translations;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.ResourceLocation;
import com.github.minecraftschurlimods.bibliocraft.BCEventHandler;

import java.util.ArrayList;
import java.util.List;

public class ClockScreen extends Screen {
    private static final ResourceLocation BACKGROUND = BCUtil.bcLoc("textures/gui/clock.png");
    private static final int IMAGE_WIDTH = 176;
    private static final int IMAGE_HEIGHT = 166;
    private final BlockPos pos;
    private final ClockBlockEntity clock;
    private final List<ClockTrigger> triggers;
    private int leftPos;
    private int topPos;
    private CheckboxButton tickSound;
    private ClockTriggerPanel triggerPanel;

    public ClockScreen(BlockPos pos) {
        super(Translations.CLOCK_TITLE);
        this.pos = pos;
        clock = (ClockBlockEntity) BCUtil.nonNull(ClientUtil.getLevel().getBlockEntity(pos));
        triggers = new ArrayList<>(clock.getTriggers());
    }

    @Override
    protected void init() {
        leftPos = (width - IMAGE_WIDTH) / 2;
        topPos = (height - IMAGE_HEIGHT) / 2;
        tickSound = addButton(checkbox17(leftPos + 7, topPos + 6, clock.getTickSound()));
        triggerPanel = new ClockTriggerPanel(leftPos + 8, topPos + 36, 160, 122, triggers, this);
        this.children.add(triggerPanel);
        addButton(new Button(width / 2 - 100, topPos + 170, 98, 20, Translations.CLOCK_ADD_TRIGGER, $ -> ClientUtil.getMc().pushGuiLayer(new ClockTriggerEditScreen(this, null))));
        addButton(new Button(width / 2 + 2, topPos + 170, 98, 20, net.minecraft.client.gui.DialogTexts.GUI_DONE, $ -> onClose()));
    }

    @Override
    public void onClose() {
        BCEventHandler.getChannel().sendToServer(new ClockSyncPacket(pos, tickSound.selected(), triggers));
        super.onClose();
    }

    @Override
    public void render(MatrixStack graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        if (triggerPanel != null) {
            triggerPanel.render(graphics, mouseX, mouseY, partialTick);
        }
        super.render(graphics, mouseX, mouseY, partialTick);
        ClientUtil.getFont().draw(graphics, Translations.CLOCK_TICK, leftPos + 28, topPos + 11, 0x404040);
        ClientUtil.getFont().draw(graphics, Translations.CLOCK_TRIGGERS, leftPos + 8, topPos + 26, 0x404040);
        triggerPanel.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public void renderBackground(MatrixStack graphics) {
        super.renderBackground(graphics);
        this.minecraft.getTextureManager().bind(BACKGROUND);
        this.blit(graphics, leftPos, topPos, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
    }

    static CheckboxButton checkbox17(int x, int y, boolean selected) {
        return new CheckboxButton(x, y, 20, 20, new StringTextComponent(""), selected, false) {
            @Override
            public void renderButton(MatrixStack pose, int mouseX, int mouseY, float partialTick) {
                Minecraft.getInstance().getTextureManager().bind(new ResourceLocation("textures/gui/checkbox.png"));
                RenderSystem.enableDepthTest();
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                blit(pose, this.x, this.y, 17, 17, this.isFocused() ? 20.0F : 0.0F, this.selected() ? 20.0F : 0.0F, 20, 20, 64, 64);
            }
        };
    }

    public void addTrigger(ClockTrigger trigger) {
        triggers.add(trigger);
        triggers.sort(ClockTrigger::compareTo);
        triggerPanel.rebuildElements(triggers);
    }

    public void removeTrigger(ClockTrigger trigger) {
        triggers.remove(trigger);
        triggers.sort(ClockTrigger::compareTo);
        triggerPanel.rebuildElements(triggers);
    }
}
