package com.github.minecraftschurlimods.bibliocraft.content.label;

import com.github.minecraftschurlimods.bibliocraft.init.BCMenus;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCMenu;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;

public class LabelMenu extends BCMenu<LabelBlockEntity> {
    public LabelMenu(int id, PlayerInventory inventory, LabelBlockEntity blockEntity) {
        super(BCMenus.LABEL.get(), id, inventory, blockEntity);
    }

    public LabelMenu(int id, PlayerInventory inventory, PacketBuffer data) {
        super(BCMenus.LABEL.get(), id, inventory, data);
    }

    @Override
    protected void addSlots(PlayerInventory inventory) {
        addSlot(new Slot(blockEntity, 0, 80, 45));
        addSlot(new Slot(blockEntity, 1, 35, 26));
        addSlot(new Slot(blockEntity, 2, 125, 26));
        addInventorySlots(inventory, 8, 84);
    }
}
