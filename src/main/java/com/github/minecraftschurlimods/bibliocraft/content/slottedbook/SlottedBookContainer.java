package com.github.minecraftschurlimods.bibliocraft.content.slottedbook;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class SlottedBookContainer extends Inventory {
    private static final String SLOT_KEY = "Slot0";
    private final ItemStack stack;

    public SlottedBookContainer(ItemStack stack) {
        super(1);
        this.stack = stack;
        CompoundNBT tag = stack.getOrCreateTag();
        if (tag.contains(SLOT_KEY)) {
            setItem(0, ItemStack.of(tag.getCompound(SLOT_KEY)));
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        CompoundNBT tag = stack.getOrCreateTag();
        ItemStack slot0 = getItem(0);
        if (slot0.isEmpty()) {
            tag.remove(SLOT_KEY);
        } else {
            tag.put(SLOT_KEY, slot0.save(new CompoundNBT()));
        }
    }
}
