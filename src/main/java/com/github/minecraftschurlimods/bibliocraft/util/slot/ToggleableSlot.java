package com.github.minecraftschurlimods.bibliocraft.util.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

/**
 * Represents a slot that can be disabled or enabled. If disabled, items will not be placed inside the slot.
 *
 * @param <T> The type of the owning block entity.
 */
public class ToggleableSlot<T extends IInventory & HasToggleableSlots> extends Slot {
    public final T container;

    public ToggleableSlot(T container, int slot, int x, int y) {
        super(container, slot, x, y);
        this.container = container;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return super.mayPlace(stack) && container.canPlaceItem(getSlotIndex(), stack) && !container.isSlotDisabled(getSlotIndex());
    }
}
