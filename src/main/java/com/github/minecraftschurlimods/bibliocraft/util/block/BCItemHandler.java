package com.github.minecraftschurlimods.bibliocraft.util.block;

import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraftforge.items.ItemStackHandler;

/**
 * Specialization of {@link ItemStackHandler} that respects placement restrictions of a {@link BCBlockEntity}.
 */
public class BCItemHandler extends ItemStackHandler {
    private final BCBlockEntity blockEntity;

    public BCItemHandler(int size, BCBlockEntity blockEntity) {
        super(size);
        this.blockEntity = blockEntity;
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        BlockState state = blockEntity.getBlockState();
        blockEntity.setChanged();
        blockEntity.getLevel().sendBlockUpdated(blockEntity.getBlockPos(), state, state, 3);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (!blockEntity.canPlaceItem(slot, stack)) return false;
        ItemStack stackInSlot = getStackInSlot(slot);
        return (stackInSlot.isEmpty() || (ItemStack.isSame(stackInSlot, stack) && ItemStack.tagMatches(stackInSlot, stack))) && stackInSlot.getCount() + stack.getCount() <= blockEntity.getMaxStackSize();
    }
}
