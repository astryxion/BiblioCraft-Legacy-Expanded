package com.github.minecraftschurlimods.bibliocraft.content.slottedbook;

import com.github.minecraftschurlimods.bibliocraft.init.BCItems;
import com.github.minecraftschurlimods.bibliocraft.init.BCMenus;
import com.github.minecraftschurlimods.bibliocraft.util.slot.ViewSlot;
import net.minecraft.network.PacketBuffer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class SlottedBookMenu extends Container {
    private final Hand hand;

    public SlottedBookMenu(int id, PlayerInventory inventory, Hand hand) {
        super(BCMenus.SLOTTED_BOOK.get(), id);
        this.hand = hand;
        ItemStack stack = inventory.player.getItemInHand(hand);
        IInventory container = new SlottedBookContainer(stack);
        addSlot(new SlottedBookSlot(container, 0, 80, 34));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                int slot = i * 9 + j + 9;
                if (inventory.getItem(slot).getItem() == BCItems.SLOTTED_BOOK.get()) {
                    addSlot(new ViewSlot(inventory, slot, 8 + j * 18, 141 + i * 18));
                } else {
                    addSlot(new Slot(inventory, slot, 8 + j * 18, 141 + i * 18));
                }
            }
        }
        for (int i = 0; i < 9; i++) {
            if (inventory.getItem(i).getItem() == BCItems.SLOTTED_BOOK.get()) {
                addSlot(new ViewSlot(inventory, i, 8 + i * 18, 199));
            } else {
                addSlot(new Slot(inventory, i, 8 + i * 18, 199));
            }
        }
    }

    public SlottedBookMenu(int id, PlayerInventory inventory, PacketBuffer buf) {
        this(id, inventory, buf.readEnum(Hand.class));
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        int slotCount = 1;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack originalStack = stack.copy();
        if (index < slotCount) { // If slot is a BE slot
            // Try moving to the hotbar or inventory
            if (!moveItemStackTo(slot.getItem(), slotCount, slotCount + 36, false))
                return ItemStack.EMPTY;
        } else if (index < slotCount + 9) { // If slot is a hotbar slot
            // Try moving to the BE
            if (!moveItemStackTo(stack, 0, slotCount, false))
                return ItemStack.EMPTY;
            // Try moving to the inventory
            if (!moveItemStackTo(stack, slotCount + 9, slotCount + 36, false))
                return ItemStack.EMPTY;
        } else if (index < slotCount + 36) { // If slot is an inventory slot
            // Try moving to the BE
            if (!moveItemStackTo(stack, 0, slotCount, false))
                return ItemStack.EMPTY;
            // Try moving to the hotbar
            if (!moveItemStackTo(stack, slotCount, slotCount + 9, false))
                return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return originalStack;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return player.getItemInHand(hand).getItem() == BCItems.SLOTTED_BOOK.get();
    }

    public static class SlottedBookSlot extends Slot {
        public SlottedBookSlot(IInventory container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() != BCItems.SLOTTED_BOOK.get() && super.mayPlace(stack);
        }
    }
}
