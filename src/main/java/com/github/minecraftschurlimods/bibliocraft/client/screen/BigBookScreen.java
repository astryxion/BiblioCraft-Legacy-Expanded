package com.github.minecraftschurlimods.bibliocraft.client.screen;

import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.StringTextComponent;
import com.github.minecraftschurlimods.bibliocraft.client.widget.ColorButton;
import com.github.minecraftschurlimods.bibliocraft.client.widget.FormattedTextArea;
import com.github.minecraftschurlimods.bibliocraft.content.bigbook.BigBookContent;
import com.github.minecraftschurlimods.bibliocraft.content.bigbook.BigBookSignPacket;
import com.github.minecraftschurlimods.bibliocraft.content.bigbook.BigBookSyncPacket;
import com.github.minecraftschurlimods.bibliocraft.content.bigbook.SetBigBookPageInLecternPacket;
import com.github.minecraftschurlimods.bibliocraft.content.bigbook.WrittenBigBookContent;
import com.github.minecraftschurlimods.bibliocraft.init.BCItems;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.github.minecraftschurlimods.bibliocraft.util.FormattedLine;
import com.github.minecraftschurlimods.bibliocraft.util.Translations;
import com.github.minecraftschurlimods.bibliocraft.util.lectern.LecternUtil;
import com.github.minecraftschurlimods.bibliocraft.util.lectern.TakeLecternBookPacket;
import com.mojang.datafixers.util.Either;
import net.minecraft.util.text.TextFormatting;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.ChangePageButton;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.Color;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import com.github.minecraftschurlimods.bibliocraft.BCEventHandler;
import javax.annotation.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class BigBookScreen extends Screen {
    private static final ResourceLocation BACKGROUND = BCUtil.bcLoc("textures/gui/big_book.png");
    private static final ITextComponent OWNER = new TranslationTextComponent(Translations.VANILLA_BY_AUTHOR_KEY, ClientUtil.getPlayer().getName()).withStyle(TextFormatting.DARK_GRAY);
    private static final int BACKGROUND_WIDTH = 220;
    private static final int BACKGROUND_HEIGHT = 256;
    private static final int TEXT_WIDTH = 188;
    private static final int TEXT_HEIGHT = 204;
    private final ItemStack stack;
    private final PlayerEntity player;
    private final Hand hand;
    private final BlockPos lectern;
    private final boolean writable;
    private final List<List<FormattedLine>> pages;
    private int currentPage;
    private boolean isSigning = false;
    private FormattedTextArea textArea;
    private Button modeButton;
    private Button alignmentButton;
    private TextFieldWidget colorBox;
    private TextFieldWidget sizeBox;
    private Button scaleDownButton;
    private Button scaleUpButton;
    private ChangePageButton backButton;
    private ChangePageButton forwardButton;
    private Button finalizeButton;
    private TextFieldWidget titleBox;

    public BigBookScreen(ItemStack stack, PlayerEntity player, Hand hand) {
        this(stack, player, hand, null);
    }

    public BigBookScreen(ItemStack stack, PlayerEntity player, BlockPos lectern) {
        this(stack, player, null, lectern);
    }

    private BigBookScreen(ItemStack stack, PlayerEntity player, @Nullable Hand hand, @Nullable BlockPos lectern) {
        super(stack.getHoverName());
        this.stack = stack;
        this.player = player;
        this.hand = hand;
        this.lectern = lectern;
        if (stack.getItem() == BCItems.WRITTEN_BIG_BOOK.get()) {
            WrittenBigBookContent content = WrittenBigBookContent.getFromStack(stack);
            pages = new ArrayList<>(content.pages());
            currentPage = content.currentPage();
            writable = false;
        } else if (stack.getItem() == BCItems.BIG_BOOK.get()) {
            BigBookContent content = BigBookContent.getFromStack(stack);
            pages = new ArrayList<>(content.pages());
            currentPage = content.currentPage();
            writable = lectern == null;
        } else {
            pages = new ArrayList<>();
            currentPage = 0;
            writable = lectern == null;
        }
        if (pages.isEmpty()) {
            addPage();
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected void init() {
        int leftX = (width - BACKGROUND_WIDTH - 80) / 2;
        int rightX = (width + BACKGROUND_WIDTH - 80) / 2;
        if (isSigning) {
            titleBox = addButton(new TextFieldWidget(font, (width - 80) / 2, 50, TEXT_WIDTH, 20, new StringTextComponent("")));
            titleBox.setTextColor(0);
            titleBox.setBordered(false);
            try {
                java.lang.reflect.Field shadow = titleBox.getClass().getDeclaredField("shadow");
                shadow.setAccessible(true);
                shadow.setBoolean(titleBox, false);
            } catch (Exception ignored) { /* 1.20.1: TextFieldWidget may not have shadow field */ }
            titleBox.setResponder(s -> {
                titleBox.setX((width - 80 - font.width(s)) / 2);
                finalizeButton.active = s != null && !s.trim().isEmpty();
            });
            setFocused(titleBox);
            finalizeButton = addButton(new Button(rightX + 16, BACKGROUND_HEIGHT - 48, 64, 16, Translations.VANILLA_FINALIZE_BUTTON, $ -> finalizeBook()));
            finalizeButton.active = false;
            addButton(new Button(rightX + 16, BACKGROUND_HEIGHT - 32, 64, 16, net.minecraft.client.gui.DialogTexts.GUI_CANCEL, $ -> {
                        isSigning = false;
                        init(this.minecraft, this.width, this.height);
                    }));
        } else if (writable) {
            updateTextArea();

            // Page buttons
            backButton = addButton(new ChangePageButton(leftX + 43, BACKGROUND_HEIGHT - 32, false, $ -> {
                pages.set(currentPage, textArea.getLines());
                if (currentPage > 0) {
                    currentPage--;
                }
                updateButtonVisibility();
                updateTextArea();
            }, true));
            forwardButton = addButton(new ChangePageButton(leftX + 144, BACKGROUND_HEIGHT - 32, true, $ -> {
                pages.set(currentPage, textArea.getLines());
                if (currentPage < 255) {
                    currentPage++;
                    while (currentPage >= pages.size()) {
                        addPage();
                    }
                }
                updateButtonVisibility();
                updateTextArea();
            }, true));
            updateButtonVisibility();

            // Formatting buttons
            addButton(new Button(rightX + 16, 16, 16, 16, Translations.FANCY_TEXT_AREA_BOLD_SHORT, $ -> textArea.toggleStyle(Style::isBold, Style::withBold), (btn, ms, mx, my) -> this.renderTooltip(ms, Translations.FANCY_TEXT_AREA_BOLD, mx, my)));
            addButton(new Button(rightX + 32, 16, 16, 16, Translations.FANCY_TEXT_AREA_ITALIC_SHORT, $ -> textArea.toggleStyle(Style::isItalic, Style::withItalic), (btn, ms, mx, my) -> this.renderTooltip(ms, Translations.FANCY_TEXT_AREA_ITALIC, mx, my)));
            addButton(new Button(rightX + 48, 16, 16, 16, Translations.FANCY_TEXT_AREA_UNDERLINED_SHORT, $ -> textArea.toggleStyle(Style::isUnderlined, Style::withUnderlined), (btn, ms, mx, my) -> this.renderTooltip(ms, Translations.FANCY_TEXT_AREA_UNDERLINED, mx, my)));
            addButton(new Button(rightX + 64, 16, 16, 16, Translations.FANCY_TEXT_AREA_STRIKETHROUGH_SHORT, $ -> textArea.toggleStyle(Style::isStrikethrough, Style::setStrikethrough), (btn, ms, mx, my) -> this.renderTooltip(ms, Translations.FANCY_TEXT_AREA_STRIKETHROUGH, mx, my)));
            addButton(new Button(rightX + 16, 32, 16, 16, Translations.FANCY_TEXT_AREA_OBFUSCATED_SHORT, $ -> textArea.toggleStyle(Style::isObfuscated, Style::setObfuscated), (btn, ms, mx, my) -> this.renderTooltip(ms, Translations.FANCY_TEXT_AREA_OBFUSCATED, mx, my)));
            modeButton = addButton(new Button(rightX + 32, 32, 48, 16, new TranslationTextComponent(textArea.getMode().getTranslationKey()), $ -> {
                        textArea.toggleMode();
                        updateModeButton();
                    }, (btn, ms, mx, my) -> this.renderTooltip(ms, Translations.FANCY_TEXT_AREA_MODE, mx, my)));
            alignmentButton = addButton(new Button(rightX + 16, 48, 64, 16, new TranslationTextComponent(textArea.getAlignment().getTranslationKey()), $ -> {
                        textArea.toggleAlignment();
                        updateAlignmentButton();
                    }, (btn, ms, mx, my) -> this.renderTooltip(ms, Translations.FANCY_TEXT_AREA_ALIGNMENT, mx, my)));

            // Color buttons and text box
            TextFormatting[] colors = BCUtil.getChatFormattingColors().toArray(TextFormatting[]::new);
            int colorRows = Math.floorDiv(colors.length, 4);
            colorBox = new TextFieldWidget(font, rightX + 16, 80 + 16 * colorRows, 64, 16, new StringTextComponent(""));
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
                addButton(new ColorButton(colors[i].getColor(), rightX + 80 - 16 * (4 - i % 4), 80 + 16 * Math.floorDiv(i, 4), 16, 16, colorName, $ -> setColor(colors[j].getColor()), (btn, ms, mx, my) -> this.renderTooltip(ms, colorName, mx, my)));
            }
            addButton(colorBox);

            // Size buttons and text box
            sizeBox = new TextFieldWidget(font, rightX + 32, 112 + 16 * colorRows, 32, 16, new StringTextComponent(""));
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
            scaleDownButton = addButton(new Button(rightX + 16, 112 + 16 * colorRows, 16, 16, Translations.FANCY_TEXT_AREA_SCALE_DOWN, $ -> {
                        int size = textArea.getSize() - 1;
                        sizeBox.setValue(String.valueOf(size));
                        // call again to account for invalid values
                        sizeBox.setValue(String.valueOf(textArea.getSize()));
                        updateSizeButtons(size);
                    }, (btn, ms, mx, my) -> this.renderTooltip(ms, Translations.FANCY_TEXT_AREA_SCALE_DOWN_TOOLTIP, mx, my)));
            addButton(sizeBox);
            scaleUpButton = addButton(new Button(rightX + 64, 112 + 16 * colorRows, 16, 16, Translations.FANCY_TEXT_AREA_SCALE_UP, $ -> {
                        int size = textArea.getSize() + 1;
                        sizeBox.setValue(String.valueOf(size));
                        // call again to account for invalid values
                        sizeBox.setValue(String.valueOf(textArea.getSize()));
                        updateSizeButtons(size);
                    }, (btn, ms, mx, my) -> this.renderTooltip(ms, Translations.FANCY_TEXT_AREA_SCALE_UP_TOOLTIP, mx, my)));

            onLineChange(textArea.getLines().get(0));
            addButton(new Button(rightX + 16, BACKGROUND_HEIGHT - 48, 64, 16, Translations.VANILLA_SIGN_BUTTON, $ -> {
                        isSigning = true;
                        init(this.minecraft, this.width, this.height);
                    }));
            addButton(new Button(rightX + 16, BACKGROUND_HEIGHT - 32, 64, 16, net.minecraft.client.gui.DialogTexts.GUI_DONE, $ -> onClose()));
        } else {
            backButton = addButton(new ChangePageButton(leftX + 83, BACKGROUND_HEIGHT - 32, false, $ -> {
                if (currentPage > 0) {
                    currentPage--;
                }
                updateButtonVisibility();
                if (lectern != null) {
                    BCEventHandler.getChannel().sendToServer(new SetBigBookPageInLecternPacket(currentPage, Either.right(lectern)));
                }
            }, true));
            forwardButton = addButton(new ChangePageButton(leftX + 184, BACKGROUND_HEIGHT - 32, true, $ -> {
                if (currentPage < pages.size()) {
                    currentPage++;
                }
                updateButtonVisibility();
                if (lectern != null) {
                    BCEventHandler.getChannel().sendToServer(new SetBigBookPageInLecternPacket(currentPage, Either.right(lectern)));
                }
            }, true));
            updateButtonVisibility();
            if (lectern != null) {
                addButton(new Button((width - BACKGROUND_WIDTH) / 2, BACKGROUND_HEIGHT + 4, BACKGROUND_WIDTH / 2 - 4, 20, Translations.VANILLA_TAKE_BOOK, button -> {
                            onClose();
                            LecternUtil.takeLecternBook(player, player.level, lectern);
                            BCEventHandler.getChannel().sendToServer(new TakeLecternBookPacket(lectern));
                        }));
                addButton(new Button(width / 2 + 2, BACKGROUND_HEIGHT + 4, BACKGROUND_WIDTH / 2 - 4, 20, net.minecraft.client.gui.DialogTexts.GUI_DONE, $ -> onClose()));
            } else {
                addButton(new Button((width - BACKGROUND_WIDTH) / 2, BACKGROUND_HEIGHT + 4, BACKGROUND_WIDTH, 20, net.minecraft.client.gui.DialogTexts.GUI_DONE, $ -> onClose()));
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return lectern == null;
    }

    @Override
    public void render(MatrixStack graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        if (isSigning) {
            int x = (width - BACKGROUND_WIDTH - 80) / 2;
            font.draw(graphics, Translations.VANILLA_EDIT_TITLE, x + 18 + (TEXT_WIDTH - font.width(Translations.VANILLA_EDIT_TITLE)) / 2, 34, 0);
            font.draw(graphics, OWNER, x + 18 + (TEXT_WIDTH - font.width(OWNER)) / 2, 60, 0);
            font.drawWordWrap(Translations.VANILLA_FINALIZE_WARNING, x + 18, 82, TEXT_WIDTH, 0);
        } else {
            ITextComponent pageIndicator = new TranslationTextComponent(Translations.VANILLA_PAGE_INDICATOR_KEY, currentPage + 1, pages.size());
            if (writable) {
                font.draw(graphics, pageIndicator, (width - BACKGROUND_WIDTH - 80) / 2 + 16 + TEXT_WIDTH - font.width(pageIndicator), 18, 0);
            } else {
                int x = (width - BACKGROUND_WIDTH) / 2 + 16;
                IRenderTypeBuffer.Impl pageBuffer = IRenderTypeBuffer.immediate(net.minecraft.client.renderer.Tessellator.getInstance().getBuilder());
                FormattedTextArea.renderLines(pages.get(currentPage), graphics, pageBuffer, x, 26, TEXT_WIDTH);
                pageBuffer.endBatch();
                font.draw(graphics, pageIndicator, x + TEXT_WIDTH - font.width(pageIndicator), 18, 0);
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isSigning) {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                finalizeBook();
                return true;
            }
            setFocused(null);
            setFocused(titleBox);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void renderBackground(MatrixStack graphics) {
        super.renderBackground(graphics);
        int x = (width - (writable ? BACKGROUND_WIDTH + 80 : BACKGROUND_WIDTH)) / 2;
        this.minecraft.getTextureManager().bind(BACKGROUND);
        this.blit(graphics, x, 0, 0, 0, 256, 256);
    }

    @Override
    public void onClose() {
        super.onClose();
        if (writable) {
            savePages();
            BigBookContent content = new BigBookContent(pages, currentPage);
            BigBookContent.setOnStack(stack, content);
            BCEventHandler.getChannel().sendToServer(new BigBookSyncPacket(content, hand));
        } else if (hand != null) {
            BCEventHandler.getChannel().sendToServer(new SetBigBookPageInLecternPacket(currentPage, Either.left(hand)));
        } else if (lectern != null) {
            BCEventHandler.getChannel().sendToServer(new SetBigBookPageInLecternPacket(currentPage, Either.right(lectern)));
        }
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

    private void updateSizeButtons(int size) {
        scaleDownButton.active = size > FormattedLine.MIN_SIZE;
        scaleUpButton.active = size < FormattedLine.MAX_SIZE;
    }

    private void setColor(int color) {
        textArea.setColor(color);
        String hexString = Integer.toHexString(color);
        colorBox.setValue("#" + "000000".substring(0, Math.max(0, 6 - hexString.length())) + hexString);
    }

    private void updateButtonVisibility() {
        this.backButton.visible = !isSigning && currentPage > 0;
        this.forwardButton.visible = !isSigning && currentPage < 255 && (writable || currentPage < pages.size() - 1);
    }

    private void updateTextArea() {
        if (textArea != null) {
            this.buttons.remove(textArea);
            this.children.remove(textArea);
        }
        textArea = addButton(new FormattedTextArea((width - BACKGROUND_WIDTH - 80) / 2 + 16, 26, TEXT_WIDTH, TEXT_HEIGHT, pages.get(currentPage)));
        textArea.setOnLineChange(this::onLineChange);
    }

    private void addPage() {
        List<FormattedLine> lines = new ArrayList<>();
        for (int i = 0; i < TEXT_HEIGHT / FormattedLine.MIN_SIZE; i++) {
            lines.add(FormattedLine.DEFAULT);
        }
        pages.add(lines);
    }

    private void savePages() {
        pages.set(currentPage, textArea.getLines());
        for (int i = pages.size() - 1; i >= 0; i--) {
            List<FormattedLine> lines = pages.get(i);
            if (lines.stream().anyMatch(e -> e != FormattedLine.DEFAULT)) break;
            pages.remove(i);
        }
        currentPage = Math.max(0, Math.min(pages.size() - 1, currentPage));
    }

    private void finalizeBook() {
        savePages();
        ItemStack stack = new ItemStack(BCItems.WRITTEN_BIG_BOOK.get());
        WrittenBigBookContent content = new WrittenBigBookContent(pages, titleBox.getValue(), player.getName().getString(), 0, currentPage);
        WrittenBigBookContent.setOnStack(stack, content);
        player.setItemInHand(hand, stack);
        BCEventHandler.getChannel().sendToServer(new BigBookSignPacket(content, hand));
        super.onClose();
    }
}
