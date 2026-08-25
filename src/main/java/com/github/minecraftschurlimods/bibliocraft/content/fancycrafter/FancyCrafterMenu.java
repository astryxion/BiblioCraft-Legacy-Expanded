package com.github.minecraftschurlimods.bibliocraft.content.fancycrafter;

import com.github.minecraftschurlimods.bibliocraft.init.BCMenus;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCMenu;
import com.github.minecraftschurlimods.bibliocraft.util.slot.HasToggleableSlots;
import com.github.minecraftschurlimods.bibliocraft.util.slot.ToggleableSlot;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class FancyCrafterMenu extends BCMenu<FancyCrafterBlockEntity> implements HasToggleableSlots {
    public FancyCrafterMenu(int id, PlayerInventory inventory, FancyCrafterBlockEntity blockEntity) {
        super(BCMenus.FANCY_CRAFTER.get(), id, inventory, blockEntity);
    }

    public FancyCrafterMenu(int id, PlayerInventory inventory, PacketBuffer data) {
        super(BCMenus.FANCY_CRAFTER.get(), id, inventory, data);
    }

    @Override
    protected void addSlots(PlayerInventory inventory) {
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                addSlot(new ToggleableSlot<>(blockEntity, x + y * 3, 30 + x * 18, 17 + y * 18));
            }
        }
        addSlot(new FancyCrafterResultSlot(blockEntity, 9, 124, 35));
        for (int i = 0; i < 8; i++) {
            addSlot(new Slot(blockEntity, i + 10, 17 + i * 18, 78));
        }
        addInventorySlots(inventory, 8, 110);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        int slotCount = blockEntity.getContainerSize();
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack originalStack = stack.copy();
        if (index < slotCount) { // If slot is a BE slot
            if (index == 9) {
                if (!stack.isEmpty() && moveItemStackTo(stack, slotCount, slotCount + 36, false)) {
                    slot.set(ItemStack.EMPTY);
                    blockEntity.consumeIngredientsForResult();
                }
                return originalStack;
            }
            // Try moving to the hotbar or inventory
            if (!moveItemStackTo(slot.getItem(), slotCount, slotCount + 36, false))
                return ItemStack.EMPTY;
        } else if (index < slotCount + 9) { // If slot is a hotbar slot
            // Try moving to the crafting grid
            if (!moveItemStackTo(stack, 0, 9, false))
                return ItemStack.EMPTY;
            // Try moving to the container
            if (!moveItemStackTo(stack, 10, slotCount, false))
                return ItemStack.EMPTY;
            // Try moving to the inventory
            if (!moveItemStackTo(stack, slotCount + 9, slotCount + 36, false))
                return ItemStack.EMPTY;
        } else if (index < slotCount + 36) { // If slot is an inventory slot
            // Try moving to the crafting grid
            if (!moveItemStackTo(stack, 0, 9, false))
                return ItemStack.EMPTY;
            // Try moving to the container
            if (!moveItemStackTo(stack, 10, slotCount, false))
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
    public void setSlotDisabled(int slot, boolean disabled) {
        if (slot >= FancyCrafterBlockEntity.CRAFTING_SLOTS || slot < 0) return;
        blockEntity.setSlotDisabled(slot, disabled);
        broadcastChanges();
    }

    @Override
    public boolean isSlotDisabled(int slot) {
        return blockEntity.isSlotDisabled(slot);
    }

    @Override
    public boolean canDisableSlot(int slot) {
        return blockEntity.canDisableSlot(slot);
    }

    private static final class FancyCrafterResultSlot extends Slot {
        private final FancyCrafterBlockEntity blockEntity;

        FancyCrafterResultSlot(FancyCrafterBlockEntity blockEntity, int slot, int x, int y) {
            super(blockEntity, slot, x, y);
            this.blockEntity = blockEntity;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public boolean mayPickup(PlayerEntity player) {
            return !getItem().isEmpty();
        }

        @Override
        public ItemStack remove(int amount) {
            return super.remove(getItem().getCount());
        }

        @Override
        public ItemStack onTake(PlayerEntity player, ItemStack stack) {
            blockEntity.consumeIngredientsForResult();
            return super.onTake(player, stack);
        }
    }
}
