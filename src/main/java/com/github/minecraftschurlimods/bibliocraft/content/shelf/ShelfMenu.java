package com.github.minecraftschurlimods.bibliocraft.content.shelf;

import com.github.minecraftschurlimods.bibliocraft.init.BCMenus;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCMenu;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;

public class ShelfMenu extends BCMenu<ShelfBlockEntity> {
    public ShelfMenu(int id, PlayerInventory inventory, ShelfBlockEntity blockEntity) {
        super(BCMenus.SHELF.get(), id, inventory, blockEntity);
    }

    public ShelfMenu(int id, PlayerInventory inventory, PacketBuffer data) {
        super(BCMenus.SHELF.get(), id, inventory, data);
    }

    @Override
    protected void addSlots(PlayerInventory inventory) {
        addSlot(new Slot(blockEntity, 0, 53, 15));
        addSlot(new Slot(blockEntity, 1, 107, 15));
        addSlot(new Slot(blockEntity, 2, 53, 53));
        addSlot(new Slot(blockEntity, 3, 107, 53));
        addInventorySlots(inventory, 8, 84);
    }
}
