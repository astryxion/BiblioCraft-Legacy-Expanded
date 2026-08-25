package com.github.minecraftschurlimods.bibliocraft.client.screen;

import com.github.minecraftschurlimods.bibliocraft.BCEventHandler;
import com.github.minecraftschurlimods.bibliocraft.util.Translations;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCMenu;
import com.github.minecraftschurlimods.bibliocraft.util.slot.HasToggleableSlots;
import com.github.minecraftschurlimods.bibliocraft.util.slot.ToggleableSlot;
import com.github.minecraftschurlimods.bibliocraft.util.slot.ToggleableSlotSyncPacket;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;

/**
 * Screen base for menus whose crafting grid slots can be disabled, matching vanilla crafter / 1.21.1 Bibliocraft.
 */
public class BCScreenWithToggleableSlots<T extends BCMenu<?> & HasToggleableSlots> extends BCMenuScreen<T> {
    private final PlayerEntity player;

    public BCScreenWithToggleableSlots(T menu, PlayerInventory inventory, ITextComponent title, ResourceLocation background) {
        super(menu, inventory, title, background);
        player = inventory.player;
    }

    @Override
    protected void renderBg(MatrixStack graphics, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(graphics, partialTicks, mouseX, mouseY);
        for (Slot slot : menu.slots) {
            if (slot instanceof ToggleableSlot && menu.isSlotDisabled(slot.getSlotIndex())) {
                int x = leftPos + slot.x - 1;
                int y = topPos + slot.y - 1;
                fill(graphics, x, y, x + 18, y + 18, 0xA08B8B8B);
                fill(graphics, x + 4, y + 8, x + 14, y + 10, 0xFF5A5A5A);
                fill(graphics, x + 8, y + 4, x + 10, y + 14, 0xFF5A5A5A);
            }
        }
    }

    @Override
    protected void renderTooltip(MatrixStack graphics, int mouseX, int mouseY) {
        if (hoveredSlot instanceof ToggleableSlot && !menu.isSlotDisabled(hoveredSlot.getSlotIndex()) && player.inventory.getCarried().isEmpty() && !hoveredSlot.hasItem() && !player.isSpectator()) {
            renderTooltip(graphics, Translations.VANILLA_TOGGLABLE_SLOT, mouseX, mouseY);
            return;
        }
        super.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
        if (slot instanceof ToggleableSlot && !slot.hasItem() && !player.isSpectator()) {
            int index = slot.getSlotIndex();
            if (type == ClickType.PICKUP) {
                if (menu.isSlotDisabled(index)) {
                    setSlotDisabled(index, false);
                } else if (player.inventory.getCarried().isEmpty()) {
                    setSlotDisabled(index, true);
                }
            } else if (type == ClickType.SWAP) {
                ItemStack stack = player.inventory.getItem(mouseButton);
                if (menu.isSlotDisabled(index) && !stack.isEmpty()) {
                    setSlotDisabled(index, false);
                }
            }
        }
        super.slotClicked(slot, slotId, mouseButton, type);
    }

    protected void setSlotDisabled(int slot, boolean disabled) {
        menu.setSlotDisabled(slot, disabled);
        BCEventHandler.getChannel().sendToServer(new ToggleableSlotSyncPacket(menu.getBlockEntity().getBlockPos(), slot, disabled));
        player.playSound(SoundEvents.UI_BUTTON_CLICK, 0.4F, disabled ? 0.75F : 1.0F);
    }
}
