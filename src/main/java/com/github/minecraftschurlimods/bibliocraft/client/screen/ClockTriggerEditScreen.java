package com.github.minecraftschurlimods.bibliocraft.client.screen;

import net.minecraft.util.text.StringTextComponent;
import com.github.minecraftschurlimods.bibliocraft.content.clock.ClockTrigger;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.github.minecraftschurlimods.bibliocraft.util.Translations;
import net.minecraft.client.gui.FontRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.ResourceLocation;
import javax.annotation.Nullable;

public class ClockTriggerEditScreen extends Screen {
    private static final ResourceLocation BACKGROUND = BCUtil.bcLoc("textures/gui/clock_edit.png");
    private static final int WIDTH = 144;
    private static final int HEIGHT = 72;
    private final ClockScreen parent;
    @Nullable
    private final ClockTrigger old;
    private final int timeWidth;
    private final int separatorWidth;
    private final int redstoneWidth;
    private final int soundWidth;
    private int leftPos;
    private int topPos;
    private int contentLeftPos;
    private int contentTopPos;
    private TextFieldWidget hours;
    private TextFieldWidget minutes;
    private CheckboxButton redstone;
    private CheckboxButton sound;

    public ClockTriggerEditScreen(ClockScreen parent, @Nullable ClockTrigger old) {
        super(Translations.CLOCK_TITLE);
        this.parent = parent;
        this.old = old;
        FontRenderer font = ClientUtil.getFont();
        timeWidth = font.width(Translations.CLOCK_TIME);
        separatorWidth = font.width(Translations.CLOCK_TIME_SEPARATOR);
        redstoneWidth = font.width(Translations.CLOCK_EMIT_REDSTONE);
        soundWidth = font.width(Translations.CLOCK_EMIT_SOUND);
    }

    @Override
    protected void init() {
        leftPos = (width - WIDTH) / 2;
        topPos = (height - HEIGHT) / 2;
        contentLeftPos = (width - Math.min(WIDTH - 12, BCUtil.max(timeWidth + separatorWidth + 90, redstoneWidth + 19, soundWidth + 19))) / 2;
        contentTopPos = topPos + 6;
        FontRenderer font = ClientUtil.getFont();
        hours = addButton(new TextFieldWidget(font, contentLeftPos + timeWidth + 2, contentTopPos, 40, 20, Translations.CLOCK_HOURS));
        hours.setSuggestion(Translations.CLOCK_HOURS_HINT.getString());
        hours.setFilter(s -> {
            try {
                int i = Integer.parseInt(s);
                return i >= 0 && i < 24;
            } catch (NumberFormatException e) {
                return s.isEmpty();
            }
        });
        hours.setResponder(s -> hours.setSuggestion(s.isEmpty() ? Translations.CLOCK_HOURS_HINT.getString() : null));
        minutes = addButton(new TextFieldWidget(font, contentLeftPos + timeWidth + separatorWidth + 44, contentTopPos, 40, 20, Translations.CLOCK_MINUTES));
        minutes.setSuggestion(Translations.CLOCK_MINUTES_HINT.getString());
        minutes.setFilter(s -> {
            try {
                int i = Integer.parseInt(s);
                return i >= 0 && i < 60;
            } catch (NumberFormatException e) {
                return s.isEmpty();
            }
        });
        minutes.setResponder(s -> minutes.setSuggestion(s.isEmpty() ? Translations.CLOCK_MINUTES_HINT.getString() : null));
        redstone = addButton(ClockScreen.checkbox17(contentLeftPos, contentTopPos + 22, old != null && old.redstone()));
        sound = addButton(ClockScreen.checkbox17(contentLeftPos, contentTopPos + 41, old != null && old.sound()));
        addButton(new Button(leftPos, topPos + HEIGHT + 4, WIDTH, 20, net.minecraft.client.gui.DialogTexts.GUI_DONE, $ -> {
            try {
                if (old != null) {
                    parent.removeTrigger(old);
                }
                parent.addTrigger(new ClockTrigger(Integer.parseInt(hours.getValue()), Integer.parseInt(minutes.getValue()), redstone.selected(), sound.selected()));
            } catch (NumberFormatException ignored) {
            }
            onClose();
        }));
        if (old != null) {
            hours.setValue(String.valueOf(old.hour()));
            minutes.setValue(String.valueOf(old.minute()));
        }
    }

    @Override
    public void render(MatrixStack graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        ClientUtil.getFont().draw(graphics, Translations.CLOCK_TIME, contentLeftPos, contentTopPos + 6, 0x404040);
        ClientUtil.getFont().draw(graphics, Translations.CLOCK_TIME_SEPARATOR, contentLeftPos + timeWidth + 43, contentTopPos + 6, 0x404040);
        ClientUtil.getFont().draw(graphics, Translations.CLOCK_EMIT_REDSTONE, contentLeftPos + 19, contentTopPos + 27, 0x404040);
        ClientUtil.getFont().draw(graphics, Translations.CLOCK_EMIT_SOUND, contentLeftPos + 19, contentTopPos + 46, 0x404040);
    }

    @Override
    public void renderBackground(MatrixStack graphics) {
        super.renderBackground(graphics);
        this.minecraft.getTextureManager().bind(BACKGROUND);
        this.blit(graphics, leftPos, topPos, 0, 0, WIDTH, HEIGHT);
    }
}
