package com.github.minecraftschurlimods.bibliocraft.content.printingtable;

import net.minecraft.util.NonNullList;
import net.minecraft.inventory.IInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.List;

/** 1.20.1: recipe input for PrintingTable implementing IInventory so Recipe&lt;PrintingTableRecipeInput&gt; is valid. */
public class PrintingTableRecipeInput implements IInventory {
    private final NonNullList<ItemStack> items;

    public PrintingTableRecipeInput(List<ItemStack> left, ItemStack right) {
        items = NonNullList.withSize(10, ItemStack.EMPTY);
        for (int i = 0; i < 9 && i < left.size(); i++) {
            items.set(i, left.get(i));
        }
        items.set(9, right);
    }

    public ItemStack getLeft(int index) {
        return index >= 0 && index < 9 ? items.get(index) : ItemStack.EMPTY;
    }

    public ItemStack getRight() {
        return items.get(9);
    }

    /** Backward-compatible access to left grid (slots 0–8). */
    public List<ItemStack> left() {
        return items.subList(0, 9);
    }

    /** Backward-compatible access to right slot (slot 9). */
    public ItemStack right() {
        return items.get(9);
    }

    @Override
    public int getContainerSize() {
        return 10;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        if (slot >= 0 && slot < 10) return items.get(slot);
        throw new IllegalArgumentException("No item for index " + slot);
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        if (slot < 0 || slot >= 10) return ItemStack.EMPTY;
        return items.get(slot).split(count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot < 0 || slot >= 10) return ItemStack.EMPTY;
        ItemStack stack = items.get(slot);
        items.set(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot >= 0 && slot < 10) items.set(slot, stack == null ? ItemStack.EMPTY : stack);
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return false;
    }

    @Override
    public void setChanged() {}

    public int size() {
        return 10;
    }
}
