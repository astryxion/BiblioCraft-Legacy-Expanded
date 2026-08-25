package com.github.minecraftschurlimods.bibliocraft.client.screen;

import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.StringTextComponent;
import com.github.minecraftschurlimods.bibliocraft.client.widget.ColorButton;
import com.github.minecraftschurlimods.bibliocraft.client.widget.FormattedTextArea;
import com.github.minecraftschurlimods.bibliocraft.content.fancysign.FancySignBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.content.fancysign.FancySignContent;
import com.github.minecraftschurlimods.bibliocraft.content.fancysign.FancySignSyncPacket;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.github.minecraftschurlimods.bibliocraft.util.FormattedLine;
import com.github.minecraftschurlimods.bibliocraft.util.Translations;
import net.minecraft.util.text.TextFormatting;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.Color;
import net.minecraft.util.ResourceLocation;
import com.github.minecraftschurlimods.bibliocraft.BCEventHandler;


public class FancySignScreen extends Screen {
    public static final int WIDTH = 140;
    public static final int HEIGHT = 80;
    private static final ResourceLocation BACKGROUND = BCUtil.bcLoc("textures/gui/fancy_sign.png");
    private final BlockPos pos;
    private final boolean back;
    private FormattedTextArea textArea;
    private Button modeButton;
    private Button alignmentButton;
    private TextFieldWidget colorBox;
    private TextFieldWidget sizeBox;
    private Button scaleDownButton;
    private Button scaleUpButton;

    public FancySignScreen(BlockPos pos, boolean back) {
        super(Translations.FANCY_SIGN_TITLE);
        this.pos = pos;
        this.back = back;
    }

    @Override
    public void onClose() {
        if (!(ClientUtil.getLevel().getBlockEntity(pos) instanceof FancySignBlockEntity))
            return;
        FancySignBlockEntity sign = (FancySignBlockEntity) ClientUtil.getLevel().getBlockEntity(pos);
        FancySignContent list = new FancySignContent(textArea.getLines());
        if (back) {
            sign.setBackContent(list);
        } else {
            sign.setFrontContent(list);
        }
        BCEventHandler.getChannel().sendToServer(new FancySignSyncPacket(list, pos, back));
        super.onClose();
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected void init() {
        if (!(ClientUtil.getLevel().getBlockEntity(pos) instanceof FancySignBlockEntity))
            return;
        FancySignBlockEntity sign = (FancySignBlockEntity) ClientUtil.getLevel().getBlockEntity(pos);
        int leftX = (width - WIDTH) / 2;
        int rightX = (width + WIDTH) / 2;
        int y = (height - HEIGHT) / 2 - 16;
        textArea = addButton(new FormattedTextArea(leftX, y, WIDTH, HEIGHT, back ? sign.getBackContent().lines() : sign.getFrontContent().lines()));
        textArea.setOnLineChange(this::onLineChange);

        // Formatting buttons
        addButton(new Button(leftX - 80, y, 16, 16, Translations.FANCY_TEXT_AREA_BOLD_SHORT, $ -> textArea.toggleStyle(Style::isBold, Style::withBold), (btn, ms, mx, my) -> this.renderTooltip(ms, Translations.FANCY_TEXT_AREA_BOLD, mx, my)));
        addButton(new Button(leftX - 64, y, 16, 16, Translations.FANCY_TEXT_AREA_ITALIC_SHORT, $ -> textArea.toggleStyle(Style::isItalic, Style::withItalic), (btn, ms, mx, my) -> this.renderTooltip(ms, Translations.FANCY_TEXT_AREA_ITALIC, mx, my)));
        addButton(new Button(leftX - 48, y, 16, 16, Translations.FANCY_TEXT_AREA_UNDERLINED_SHORT, $ -> textArea.toggleStyle(Style::isUnderlined, Style::withUnderlined), (btn, ms, mx, my) -> this.renderTooltip(ms, Translations.FANCY_TEXT_AREA_UNDERLINED, mx, my)));
        addButton(new Button(leftX - 32, y, 16, 16, Translations.FANCY_TEXT_AREA_STRIKETHROUGH_SHORT, $ -> textArea.toggleStyle(Style::isStrikethrough, Style::setStrikethrough), (btn, ms, mx, my) -> this.renderTooltip(ms, Translations.FANCY_TEXT_AREA_STRIKETHROUGH, mx, my)));
        addButton(new Button(leftX - 80, y + 16, 16, 16, Translations.FANCY_TEXT_AREA_OBFUSCATED_SHORT, $ -> textArea.toggleStyle(Style::isObfuscated, Style::setObfuscated), (btn, ms, mx, my) -> this.renderTooltip(ms, Translations.FANCY_TEXT_AREA_OBFUSCATED, mx, my)));
        modeButton = addButton(new Button(leftX - 64, y + 16, 48, 16, new TranslationTextComponent(textArea.getMode().getTranslationKey()), button -> {
                    textArea.toggleMode();
                    updateModeButton();
                }, (btn, ms, mx, my) -> this.renderTooltip(ms, Translations.FANCY_TEXT_AREA_MODE, mx, my)));
        alignmentButton = addButton(new Button(leftX - 80, y + 32, 64, 16, new TranslationTextComponent(textArea.getAlignment().getTranslationKey()), button -> {
                    textArea.toggleAlignment();
                    updateAlignmentButton();
                }, (btn, ms, mx, my) -> this.renderTooltip(ms, Translations.FANCY_TEXT_AREA_ALIGNMENT, mx, my)));

        // Size buttons and text box
        sizeBox = new TextFieldWidget(font, leftX - 64, y + 64, 32, 16, new StringTextComponent(""));
        sizeBox.setFilter(s -> {
            try {
                int i = Integer.parseInt(s);
                return i >= FormattedLine.MIN_SIZE && i <= FormattedLine.MAX_SIZE;
            } catch (NumberFormatException e) {
                return false;
            }
        });
        sizeBox.setResponder(s -> {
            try {
                textArea.setSize(Integer.parseInt(s));
            } catch (NumberFormatException ignored) {
            }
        });
        scaleDownButton = addButton(new Button(leftX - 80, y + 64, 16, 16, Translations.FANCY_TEXT_AREA_SCALE_DOWN, button -> {
            int size = textArea.getSize() - 1;
            sizeBox.setValue(String.valueOf(size));
            // call again to account for invalid values
            sizeBox.setValue(String.valueOf(textArea.getSize()));
            updateSizeButtons(size);
        }, (btn, ms, mx, my) -> this.renderTooltip(ms, Translations.FANCY_TEXT_AREA_SCALE_DOWN_TOOLTIP, mx, my)));
        addButton(sizeBox);
        scaleUpButton = addButton(new Button(leftX - 32, y + 64, 16, 16, Translations.FANCY_TEXT_AREA_SCALE_UP, button -> {
            int size = textArea.getSize() + 1;
            sizeBox.setValue(String.valueOf(size));
            // call again to account for invalid values
            sizeBox.setValue(String.valueOf(textArea.getSize()));
            updateSizeButtons(size);
        }, (btn, ms, mx, my) -> this.renderTooltip(ms, Translations.FANCY_TEXT_AREA_SCALE_UP_TOOLTIP, mx, my)));

        // Color buttons and text box
        TextFormatting[] colors = BCUtil.getChatFormattingColors().toArray(TextFormatting[]::new);
        int colorRows = Math.floorDiv(colors.length, 4);
        colorBox = new TextFieldWidget(font, rightX + 16, y + 16 * colorRows, 64, 16, new StringTextComponent(""));
        colorBox.setSuggestion(Translations.FANCY_TEXT_AREA_COLOR_HINT.getString());
        colorBox.setMaxLength(7);
        colorBox.setFilter(s -> s.isEmpty() || s.charAt(0) == '#' && s.substring(1).codePoints().allMatch(cp -> Character.digit(cp, 16) >= 0));
        colorBox.setResponder(s -> {
            if (s.length() <= 1) return;
            textArea.setColor(Integer.parseInt(s.substring(1), 16));
        });
        Color color = textArea.getLines().get(0).style().getColor();
        if (color != null) {
            setColor(color.getValue());
        }
        for (int i = 0; i < colors.length; i++) {
            final int j = i; // I love Java
            net.minecraft.util.text.ITextComponent colorName = new TranslationTextComponent("color." + colors[i].getName());
            addButton(new ColorButton(colors[i].getColor(), rightX + 80 - 16 * (4 - i % 4), y + 16 * Math.floorDiv(i, 4), 16, 16, colorName, $ -> setColor(colors[j].getColor()), (btn, ms, mx, my) -> this.renderTooltip(ms, colorName, mx, my)));
        }
        addButton(colorBox);

        onLineChange(textArea.getLines().get(0));
        addButton(new Button(leftX, y + HEIGHT + 8, WIDTH, 20, net.minecraft.client.gui.DialogTexts.GUI_DONE, $ -> onClose()));
    }

    @Override
    public void render(MatrixStack graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(MatrixStack graphics) {
        super.renderBackground(graphics);
        this.minecraft.getTextureManager().bind(BACKGROUND);
        blit(graphics, (width - WIDTH) / 2 - 4, (height - HEIGHT) / 2 - 20, 0.0F, 0.0F, 192, 192, 192, 192);
    }

    private void setColor(int color) {
        textArea.setColor(color);
        String hexString = Integer.toHexString(color);
        colorBox.setValue("#" + "000000".substring(0, Math.max(0, 6 - hexString.length())) + hexString);
    }

    private void updateSizeButtons(int size) {
        scaleDownButton.active = size > FormattedLine.MIN_SIZE;
        scaleUpButton.active = size < FormattedLine.MAX_SIZE;
    }

    private void onLineChange(FormattedLine line) {
        updateModeButton();
        updateAlignmentButton();
        Color color = line.style().getColor();
        setColor(color == null ? 0 : color.getValue());
        sizeBox.setValue(String.valueOf(line.size()));
        updateSizeButtons(line.size());
    }

    private void updateModeButton() {
        modeButton.setMessage(new TranslationTextComponent(textArea.getMode().getTranslationKey()));
    }

    private void updateAlignmentButton() {
        alignmentButton.setMessage(new TranslationTextComponent(textArea.getAlignment().getTranslationKey()));
    }
}
