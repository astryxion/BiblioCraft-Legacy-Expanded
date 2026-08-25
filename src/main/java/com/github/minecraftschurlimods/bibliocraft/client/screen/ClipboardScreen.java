package com.github.minecraftschurlimods.bibliocraft.client.screen;

import net.minecraft.util.text.StringTextComponent;
import com.github.minecraftschurlimods.bibliocraft.client.widget.SpriteButton;
import com.github.minecraftschurlimods.bibliocraft.content.clipboard.CheckboxState;
import com.github.minecraftschurlimods.bibliocraft.content.clipboard.ClipboardContent;
import com.github.minecraftschurlimods.bibliocraft.content.clipboard.ClipboardSyncPacket;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.ChangePageButton;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Hand;
import net.minecraft.item.ItemStack;
import com.github.minecraftschurlimods.bibliocraft.BCEventHandler;
import javax.annotation.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ClipboardScreen extends Screen {
    private static final ResourceLocation BACKGROUND = BCUtil.bcLoc("textures/gui/clipboard.png");
    private final ItemStack stack;
    private final Hand hand;
    private ClipboardContent data;
    private final CheckboxButton[] checkboxes = new CheckboxButton[ClipboardContent.MAX_LINES];
    private final TextFieldWidget[] lines = new TextFieldWidget[ClipboardContent.MAX_LINES];
    private TextFieldWidget titleBox;
    private ChangePageButton forwardButton;
    private ChangePageButton backButton;

    public ClipboardScreen(ItemStack stack, Hand hand) {
        super(stack.getHoverName());
        this.stack = stack;
        this.hand = hand;
        this.data = ClipboardContent.getFromStack(stack);
    }

    @Override
    public void onClose() {
        super.onClose();
        ClipboardContent.setOnStack(stack, data);
        BCEventHandler.getChannel().sendToServer(new ClipboardSyncPacket(data, hand));
    }

    @Override
    protected void init() {
        int x = (width - 192) / 2;
        titleBox = addButton(new NoShadowTextField(getMinecraft().font, x + 57, 14, 72, 8));
        titleBox.setTextColor(0);
        titleBox.setBordered(false);
        titleBox.setResponder(e -> data = data.setTitle(e));
        for (int i = 0; i < ClipboardContent.MAX_LINES; i++) {
            final int j = i; // I love Java
            checkboxes[i] = addButton(new CheckboxButton(x + 30, 15 * i + 26, e -> {
                List<ClipboardContent.Page> pages = new ArrayList<>(data.pages());
                ClipboardContent.Page page = pages.get(data.active());
                List<CheckboxState> checkboxes = new ArrayList<>(page.checkboxes());
                checkboxes.set(j, ((CheckboxButton) e).getState());
                pages.set(data.active(), page.setCheckboxes(checkboxes));
                data = data.setPages(pages);
            }));
            lines[i] = addButton(new NoShadowTextField(getMinecraft().font, x + 45, 15 * i + 28, 109, 8));
            lines[i].setTextColor(0);
            lines[i].setBordered(false);
            lines[i].setResponder(e -> {
                List<ClipboardContent.Page> pages = new ArrayList<>(data.pages());
                ClipboardContent.Page page = pages.get(data.active());
                List<String> lines = new ArrayList<>(page.lines());
                lines.set(j, e);
                pages.set(data.active(), page.setLines(lines));
                data = data.setPages(pages);
            });
        }
        forwardButton = addButton(new ChangePageButton(x + 116, 159, true, $ -> {
            data = data.nextPage();
            updateContents();
        }, false));
        backButton = addButton(new ChangePageButton(x + 43, 159, false, $ -> {
            data = data.prevPage();
            updateContents();
        }, false));
        addButton(new Button(width / 2 - 100, 196, 200, 20, net.minecraft.client.gui.DialogTexts.GUI_DONE, $ -> onClose()));
        updateContents();
    }

    @Override
    public void tick() {
        super.tick();
        if (titleBox != null) {
            titleBox.tick();
        }
        for (TextFieldWidget line : lines) {
            if (line != null) {
                line.tick();
            }
        }
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
        this.blit(graphics, (width - 192) / 2, 2, 0, 0, 192, 192);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        if (scrollAmount < 0 && forwardButton.visible) {
            forwardButton.onPress();
            return true;
        }
        if (scrollAmount > 0 && backButton.visible) {
            backButton.onPress();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) return true;
        switch (keyCode) {
case GLFW.GLFW_KEY_PAGE_UP: {
                backButton.onPress();
                return true;
            }
case GLFW.GLFW_KEY_PAGE_DOWN: {
                forwardButton.onPress();
                return true;
            }
default: return false;
}
    }

    private void updateContents() {
        backButton.visible = data.active() > 0;
        titleBox.setValue(data.title());
        ClipboardContent.Page page = data.pages().get(data.active());
        for (int i = 0; i < checkboxes.length; i++) {
            checkboxes[i].setState(page.checkboxes().get(i));
            lines[i].setValue(page.lines().get(i));
        }
    }

    /**
     * 1.20 {@code TextFieldWidget.setTextShadow(false)}. 1.16 always uses {@code drawShadow}; there is no shadow field.
     */
    private static class NoShadowTextField extends TextFieldWidget {
        public NoShadowTextField(net.minecraft.client.gui.FontRenderer font, int x, int y, int width, int height) {
            super(font, x, y, width, height, new StringTextComponent(""));
        }

        @Override
        public void renderButton(MatrixStack pose, int mouseX, int mouseY, float partialTick) {
            if (!this.isVisible()) {
                return;
            }
            try {
                boolean bordered = NoShadowFields.BORDERED.getBoolean(this);
                if (bordered) {
                    int borderColor = this.isFocused() ? -1 : -6250336;
                    fill(pose, this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, borderColor);
                    fill(pose, this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
                }
                net.minecraft.client.gui.FontRenderer font = (net.minecraft.client.gui.FontRenderer) NoShadowFields.FONT.get(this);
                boolean editable = NoShadowFields.IS_EDITABLE.getBoolean(this);
                int color = editable ? NoShadowFields.TEXT_COLOR.getInt(this) : NoShadowFields.TEXT_COLOR_UNEDITABLE.getInt(this);
                int cursorPos = NoShadowFields.CURSOR_POS.getInt(this);
                int displayPos = NoShadowFields.DISPLAY_POS.getInt(this);
                int highlightPos = NoShadowFields.HIGHLIGHT_POS.getInt(this);
                int frame = NoShadowFields.FRAME.getInt(this);
                String value = (String) NoShadowFields.VALUE.get(this);
                @SuppressWarnings("unchecked")
                java.util.function.BiFunction<String, Integer, net.minecraft.util.IReorderingProcessor> formatter =
                        (java.util.function.BiFunction<String, Integer, net.minecraft.util.IReorderingProcessor>) NoShadowFields.FORMATTER.get(this);
                String suggestion = (String) NoShadowFields.SUGGESTION.get(this);
                int cursorOffset = cursorPos - displayPos;
                int highlightOffset = highlightPos - displayPos;
                String visible = font.plainSubstrByWidth(value.substring(displayPos), this.getInnerWidth());
                boolean cursorInVisible = cursorOffset >= 0 && cursorOffset <= visible.length();
                boolean showCursor = this.isFocused() && frame / 6 % 2 == 0 && cursorInVisible;
                int textX = bordered ? this.x + 4 : this.x;
                int textY = bordered ? this.y + (this.height - 8) / 2 : this.y;
                int nextX = textX;
                if (highlightOffset > visible.length()) {
                    highlightOffset = visible.length();
                }
                if (!visible.isEmpty()) {
                    String beforeCursor = cursorInVisible ? visible.substring(0, cursorOffset) : visible;
                    nextX = font.draw(pose, formatter.apply(beforeCursor, displayPos), (float) textX, (float) textY, color);
                }
                boolean cursorAtEndOrFull = cursorPos < value.length() || value.length() >= getMaxLengthValue();
                int cursorX = nextX;
                if (!cursorInVisible) {
                    cursorX = cursorOffset > 0 ? textX + this.width : textX;
                } else if (cursorAtEndOrFull) {
                    cursorX = nextX - 1;
                    --nextX;
                }
                if (!visible.isEmpty() && cursorInVisible && cursorOffset < visible.length()) {
                    font.draw(pose, formatter.apply(visible.substring(cursorOffset), cursorPos), (float) nextX, (float) textY, color);
                }
                if (!cursorAtEndOrFull && suggestion != null) {
                    font.draw(pose, suggestion, (float) (cursorX - 1), (float) textY, -8355712);
                }
                if (showCursor) {
                    if (cursorAtEndOrFull) {
                        fill(pose, cursorX, textY - 1, cursorX + 1, textY + 1 + 9, -3092272);
                    } else {
                        font.draw(pose, "_", (float) cursorX, (float) textY, color);
                    }
                }
                if (highlightOffset != cursorOffset) {
                    int highlightEnd = textX + font.width(visible.substring(0, highlightOffset));
                    renderSelection(cursorX, textY - 1, highlightEnd - 1, textY + 1 + 9);
                }
            } catch (IllegalAccessException e) {
                super.renderButton(pose, mouseX, mouseY, partialTick);
            }
        }

        private int getMaxLengthValue() {
            try {
                return NoShadowFields.MAX_LENGTH.getInt(this);
            } catch (IllegalAccessException e) {
                return 32;
            }
        }

        private void renderSelection(int x1, int y1, int x2, int y2) {
            if (x1 < x2) {
                int tmp = x1;
                x1 = x2;
                x2 = tmp;
            }
            if (y1 < y2) {
                int tmp = y1;
                y1 = y2;
                y2 = tmp;
            }
            if (x2 > this.x + this.width) {
                x2 = this.x + this.width;
            }
            if (x1 > this.x + this.width) {
                x1 = this.x + this.width;
            }
            net.minecraft.client.renderer.Tessellator tessellator = net.minecraft.client.renderer.Tessellator.getInstance();
            net.minecraft.client.renderer.BufferBuilder buffer = tessellator.getBuilder();
            com.mojang.blaze3d.systems.RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
            com.mojang.blaze3d.systems.RenderSystem.disableTexture();
            com.mojang.blaze3d.systems.RenderSystem.enableColorLogicOp();
            com.mojang.blaze3d.systems.RenderSystem.logicOp(com.mojang.blaze3d.platform.GlStateManager.LogicOp.OR_REVERSE);
            buffer.begin(7, net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION);
            buffer.vertex((double) x1, (double) y2, 0.0D).endVertex();
            buffer.vertex((double) x2, (double) y2, 0.0D).endVertex();
            buffer.vertex((double) x2, (double) y1, 0.0D).endVertex();
            buffer.vertex((double) x1, (double) y1, 0.0D).endVertex();
            tessellator.end();
            com.mojang.blaze3d.systems.RenderSystem.disableColorLogicOp();
            com.mojang.blaze3d.systems.RenderSystem.enableTexture();
        }
    }

    private static final class NoShadowFields {
        private static final java.lang.reflect.Field FONT = field("font");
        private static final java.lang.reflect.Field VALUE = field("value");
        private static final java.lang.reflect.Field MAX_LENGTH = field("maxLength");
        private static final java.lang.reflect.Field FRAME = field("frame");
        private static final java.lang.reflect.Field BORDERED = field("bordered");
        private static final java.lang.reflect.Field IS_EDITABLE = field("isEditable");
        private static final java.lang.reflect.Field DISPLAY_POS = field("displayPos");
        private static final java.lang.reflect.Field CURSOR_POS = field("cursorPos");
        private static final java.lang.reflect.Field HIGHLIGHT_POS = field("highlightPos");
        private static final java.lang.reflect.Field TEXT_COLOR = field("textColor");
        private static final java.lang.reflect.Field TEXT_COLOR_UNEDITABLE = field("textColorUneditable");
        private static final java.lang.reflect.Field SUGGESTION = field("suggestion");
        private static final java.lang.reflect.Field FORMATTER = field("formatter");

        private static java.lang.reflect.Field field(String name) {
            try {
                java.lang.reflect.Field f = TextFieldWidget.class.getDeclaredField(name);
                f.setAccessible(true);
                return f;
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class CheckboxButton extends SpriteButton {
        private static final ResourceLocation CHECK_TEXTURE = BCUtil.bcLoc("textures/gui/sprites/check.png");
        private static final ResourceLocation X_TEXTURE = BCUtil.bcLoc("textures/gui/sprites/x.png");
        private CheckboxState state = CheckboxState.EMPTY;

        public CheckboxButton(int x, int y, Button.IPressable onPress) {
            super(x, y, 14, 14, onPress);
        }

        public CheckboxState getState() {
            return state;
        }

        public void setState(CheckboxState state) {
            this.state = state;
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            switch (state) {
case EMPTY: state = CheckboxState.CHECK; break;
case CHECK: state = CheckboxState.X; break;
case X: state = CheckboxState.EMPTY; break;
}
            super.onClick(mouseX, mouseY);
        }

        @Override
        @Nullable
        protected ResourceLocation getSprite() {
            switch (state) {
case EMPTY: return null;
case CHECK: return CHECK_TEXTURE;
case X: return X_TEXTURE;
default: return null;
}
        }
    }
}
