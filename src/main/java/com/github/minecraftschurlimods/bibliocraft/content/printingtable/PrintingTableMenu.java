package com.github.minecraftschurlimods.bibliocraft.content.printingtable;

import com.github.minecraftschurlimods.bibliocraft.init.BCMenus;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCMenu;
import com.github.minecraftschurlimods.bibliocraft.util.slot.HasToggleableSlots;
import com.github.minecraftschurlimods.bibliocraft.util.slot.ResultSlot;
import com.github.minecraftschurlimods.bibliocraft.util.slot.ToggleableSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;

public class PrintingTableMenu extends BCMenu<PrintingTableBlockEntity> implements HasToggleableSlots {
    public PrintingTableMenu(int id, PlayerInventory inventory, PrintingTableBlockEntity blockEntity) {
        super(BCMenus.PRINTING_TABLE.get(), id, inventory, blockEntity);
    }

    public PrintingTableMenu(int id, PlayerInventory inventory, PacketBuffer buf) {
        super(BCMenus.PRINTING_TABLE.get(), id, inventory, buf);
    }

    @Override
    protected void addSlots(PlayerInventory inventory) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                addSlot(new ToggleableSlot<>(blockEntity, j + i * 3, 17 + j * 18, 17 + i * 18));
            }
        }
        addSlot(new Slot(blockEntity, 9, 90, 35));
        addSlot(new ResultSlot(blockEntity, 10, 142, 35));
        addInventorySlots(inventory, 8, 84);
    }

    @Override
    public boolean isSlotDisabled(int slot) {
        return blockEntity.isSlotDisabled(slot);
    }

    @Override
    public void setSlotDisabled(int slot, boolean disabled) {
        if (slot > 8) return;
        blockEntity.setSlotDisabled(slot, disabled);
        broadcastChanges();
    }

    @Override
    public boolean canDisableSlot(int slot) {
        return blockEntity.canDisableSlot(slot);
    }
}
