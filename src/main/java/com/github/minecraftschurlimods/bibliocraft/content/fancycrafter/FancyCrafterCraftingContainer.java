package com.github.minecraftschurlimods.bibliocraft.content.fancycrafter;

import com.github.minecraftschurlimods.bibliocraft.util.slot.HasToggleableSlots;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Crafting grid backed by the block entity; disabled slots are treated as empty for recipe matching.
 */
public class FancyCrafterCraftingContainer implements CraftingContainer, HasToggleableSlots {
    private final FancyCrafterBlockEntity blockEntity;
    private final AbstractContainerMenu menu;

    public FancyCrafterCraftingContainer(FancyCrafterBlockEntity blockEntity, AbstractContainerMenu menu) {
        this.blockEntity = blockEntity;
        this.menu = menu;
    }

    @Override
    public int getWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return 3;
    }

    @Override
    public List<ItemStack> getItems() {
        return blockEntity.getCraftingGridItems();
    }

    @Override
    public int getContainerSize() {
        return 9;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < getContainerSize(); i++) {
            if (!getItem(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        if (blockEntity.isSlotDisabled(slot)) return ItemStack.EMPTY;
        return blockEntity.getItem(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (blockEntity.isSlotDisabled(slot)) return ItemStack.EMPTY;
        ItemStack removed = blockEntity.removeItem(slot, amount);
        if (!removed.isEmpty()) {
            menu.slotsChanged(this);
        }
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (blockEntity.isSlotDisabled(slot)) return ItemStack.EMPTY;
        return blockEntity.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        blockEntity.setItem(slot, stack);
        menu.slotsChanged(this);
    }

    @Override
    public void setChanged() {
        blockEntity.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity.stillValid(player);
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < getContainerSize(); i++) {
            setItem(i, ItemStack.EMPTY);
        }
    }

    @Override
    public void fillStackedContents(StackedContents contents) {
        for (int i = 0; i < getContainerSize(); i++) {
            ItemStack stack = getItem(i);
            if (!stack.isEmpty()) {
                contents.accountSimpleStack(stack);
            }
        }
    }

    @Override
    public void setSlotDisabled(int slot, boolean disabled) {
        blockEntity.setSlotDisabled(slot, disabled);
    }

    @Override
    public boolean isSlotDisabled(int slot) {
        return blockEntity.isSlotDisabled(slot);
    }

    @Override
    public boolean canDisableSlot(int slot) {
        return blockEntity.canDisableSlot(slot);
    }
}
