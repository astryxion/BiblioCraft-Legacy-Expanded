package com.github.minecraftschurlimods.bibliocraft.util.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class ViewSlot extends Slot {
    public ViewSlot(IInventory container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public boolean mayPickup(PlayerEntity player) {
        return false;
    }
}
