package com.github.minecraftschurlimods.bibliocraft.client.screen;

import com.github.minecraftschurlimods.bibliocraft.client.widget.SpriteButton;
import com.github.minecraftschurlimods.bibliocraft.content.clipboard.CheckboxState;
import com.github.minecraftschurlimods.bibliocraft.content.clipboard.ClipboardContent;
import com.github.minecraftschurlimods.bibliocraft.content.clipboard.ClipboardSyncPacket;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import com.github.minecraftschurlimods.bibliocraft.BCEventHandler;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ClipboardScreen extends Screen {
    private static final ResourceLocation BACKGROUND = BCUtil.bcLoc("textures/gui/clipboard.png");
    private final ItemStack stack;
    private final InteractionHand hand;
    private ClipboardContent data;
    private final CheckboxButton[] checkboxes = new CheckboxButton[ClipboardContent.MAX_LINES];
    private final EditBox[] lines = new EditBox[ClipboardContent.MAX_LINES];
    private EditBox titleBox;
    private PageButton forwardButton;
    private PageButton backButton;

    public ClipboardScreen(ItemStack stack, InteractionHand hand) {
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
        titleBox = addRenderableWidget(new EditBox(getMinecraft().font, x + 57, 14, 72, 8, Component.empty()));
        titleBox.setTextColor(0);
        titleBox.setBordered(false);
        setEditBoxTextShadow(titleBox);
        titleBox.setResponder(e -> data = data.setTitle(e));
        for (int i = 0; i < ClipboardContent.MAX_LINES; i++) {
            final int j = i; // I love Java
            checkboxes[i] = addRenderableWidget(new CheckboxButton(x + 30, 15 * i + 26, e -> {
                List<ClipboardContent.Page> pages = new ArrayList<>(data.pages());
                ClipboardContent.Page page = pages.get(data.active());
                List<CheckboxState> checkboxes = new ArrayList<>(page.checkboxes());
                checkboxes.set(j, ((CheckboxButton) e).getState());
                pages.set(data.active(), page.setCheckboxes(checkboxes));
                data = data.setPages(pages);
            }));
            lines[i] = addRenderableWidget(new EditBox(getMinecraft().font, x + 45, 15 * i + 28, 109, 8, Component.empty()));
            lines[i].setTextColor(0);
            lines[i].setBordered(false);
            setEditBoxTextShadow(lines[i]);
            lines[i].setResponder(e -> {
                List<ClipboardContent.Page> pages = new ArrayList<>(data.pages());
                ClipboardContent.Page page = pages.get(data.active());
                List<String> lines = new ArrayList<>(page.lines());
                lines.set(j, e);
                pages.set(data.active(), page.setLines(lines));
                data = data.setPages(pages);
            });
        }
        forwardButton = addRenderableWidget(new PageButton(x + 116, 159, true, $ -> {
            data = data.nextPage();
            updateContents();
        }, false));
        backButton = addRenderableWidget(new PageButton(x + 43, 159, false, $ -> {
            data = data.prevPage();
            updateContents();
        }, false));
        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, $ -> onClose()).bounds(width / 2 - 100, 196, 200, 20).build());
        updateContents();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(GuiGraphics graphics) {
        super.renderBackground(graphics);
        graphics.blit(BACKGROUND, (width - 192) / 2, 2, 0, 0, 192, 192);
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
        return switch (keyCode) {
            case GLFW.GLFW_KEY_PAGE_UP -> {
                backButton.onPress();
                yield true;
            }
            case GLFW.GLFW_KEY_PAGE_DOWN -> {
                forwardButton.onPress();
                yield true;
            }
            default -> false;
        };
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

    /** 1.20.1: EditBox.setTextShadow not available; use reflection to set shadow field. */
    private static void setEditBoxTextShadow(EditBox editBox) {
        try {
            java.lang.reflect.Field shadow = editBox.getClass().getDeclaredField("f_94117_");
            shadow.setAccessible(true);
            shadow.setBoolean(editBox, false);
        } catch (Exception e) {
            try {
                java.lang.reflect.Field shadow = editBox.getClass().getDeclaredField("shadow");
                shadow.setAccessible(true);
                shadow.setBoolean(editBox, false);
            } catch (Exception ignored) { /* obf name may vary */ }
        }
    }

    private static class CheckboxButton extends SpriteButton {
        private static final ResourceLocation CHECK_TEXTURE = BCUtil.bcLoc("textures/gui/sprites/check.png");
        private static final ResourceLocation X_TEXTURE = BCUtil.bcLoc("textures/gui/sprites/x.png");
        private CheckboxState state = CheckboxState.EMPTY;

        public CheckboxButton(int x, int y, OnPress onPress) {
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
            state = switch (state) {
                case EMPTY -> CheckboxState.CHECK;
                case CHECK -> CheckboxState.X;
                case X -> CheckboxState.EMPTY;
            };
            super.onClick(mouseX, mouseY);
        }

        @Override
        @Nullable
        protected ResourceLocation getSprite() {
            return switch (state) {
                case EMPTY -> null;
                case CHECK -> CHECK_TEXTURE;
                case X -> X_TEXTURE;
            };
        }
    }
}
