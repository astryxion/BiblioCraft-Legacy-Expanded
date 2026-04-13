package com.github.minecraftschurlimods.bibliocraft.util.block;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

/**
 * Item storage for {@link BCBlockEntity} that respects placement restrictions.
 * Replaces NeoForge ItemStackHandler; same API for slots, serialize/deserialize.
 */
public class BCItemHandler {
    private static final String ITEMS_TAG = "Items";
    private static final String SLOT_TAG = "Slot";

    private final int size;
    private final BCBlockEntity blockEntity;
    private final NonNullList<ItemStack> stacks;

    public BCItemHandler(int size, BCBlockEntity blockEntity) {
        this.size = size;
        this.blockEntity = blockEntity;
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    public int getSlots() {
        return size;
    }

    public ItemStack getStackInSlot(int slot) {
        if (slot < 0 || slot >= size) return ItemStack.EMPTY;
        return stacks.get(slot);
    }

    public void setStackInSlot(int slot, ItemStack stack) {
        if (slot < 0 || slot >= size) return;
        stacks.set(slot, stack.isEmpty() ? ItemStack.EMPTY : stack);
        onContentsChanged(slot);
    }

    public boolean isItemValid(int slot, ItemStack stack) {
        if (!blockEntity.canPlaceItem(slot, stack)) return false;
        ItemStack stackInSlot = getStackInSlot(slot);
        return (stackInSlot.isEmpty() || ItemStack.isSameItemSameComponents(stackInSlot, stack))
                && stackInSlot.getCount() + stack.getCount() <= blockEntity.getMaxStackSize();
    }

    protected void onContentsChanged(int slot) {
        blockEntity.setChanged();
        // BE sync is done only by ContainerSyncScheduler (end-of-tick) so we avoid races with
        // vanilla deferred sync from setBlock (e.g. display case open) overwriting client with empty.
    }

    public CompoundTag serializeNBT(HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (int i = 0; i < size; i++) {
            ItemStack stack = stacks.get(i);
            if (!stack.isEmpty()) {
                // Use save(registries) so we get a full NBT with "id" (1.21.1 format). Then add Slot.
                CompoundTag tag = (CompoundTag) stack.save(registries);
                tag.putByte(SLOT_TAG, (byte) i);
                list.add(tag);
            }
        }
        CompoundTag out = new CompoundTag();
        out.put(ITEMS_TAG, list);
        return out;
    }

    public void deserializeNBT(HolderLookup.Provider registries, CompoundTag tag) {
        for (int i = 0; i < size; i++) {
            stacks.set(i, ItemStack.EMPTY);
        }
        if (!tag.contains(ITEMS_TAG, 9)) return;
        ListTag list = tag.getList(ITEMS_TAG, 10);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag slotTag = list.getCompound(i);
            int slot = slotTag.getByte(SLOT_TAG) & 0xff;
            if (slot >= 0 && slot < size) {
                stacks.set(slot, ItemStack.parse(registries, slotTag).orElse(ItemStack.EMPTY));
            }
        }
    }
}
