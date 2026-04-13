package com.github.minecraftschurlimods.bibliocraft.content.slottedbook;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class SlottedBookContainer extends SimpleContainer {
    private static final String SLOT_KEY = "Slot0";
    private final ItemStack stack;

    public SlottedBookContainer(ItemStack stack) {
        super(1);
        this.stack = stack;
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains(SLOT_KEY)) {
            setItem(0, ItemStack.of(tag.getCompound(SLOT_KEY)));
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        CompoundTag tag = stack.getOrCreateTag();
        ItemStack slot0 = getItem(0);
        if (slot0.isEmpty()) {
            tag.remove(SLOT_KEY);
        } else {
            tag.put(SLOT_KEY, slot0.save(new CompoundTag()));
        }
    }
}
