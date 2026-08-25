package com.github.minecraftschurlimods.bibliocraft.content.discrack;

import com.github.minecraftschurlimods.bibliocraft.init.BCMenus;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCMenu;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.PlayerInventory;

public class DiscRackMenu extends BCMenu<DiscRackBlockEntity> {
    public DiscRackMenu(int id, PlayerInventory inventory, DiscRackBlockEntity blockEntity) {
        super(BCMenus.DISC_RACK.get(), id, inventory, blockEntity);
    }

    public DiscRackMenu(int id, PlayerInventory inventory, PacketBuffer data) {
        super(BCMenus.DISC_RACK.get(), id, inventory, data);
    }

    @Override
    protected void addSlots(PlayerInventory inventory) {
        for (int i = 0; i < 9; i++) {
            addSlot(new BCSlot(blockEntity, i, 8 + i * 18, 34));
        }
        addInventorySlots(inventory, 8, 84);
    }
}
