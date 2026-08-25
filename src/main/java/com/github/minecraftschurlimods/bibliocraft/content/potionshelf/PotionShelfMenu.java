package com.github.minecraftschurlimods.bibliocraft.content.potionshelf;

import com.github.minecraftschurlimods.bibliocraft.init.BCMenus;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCMenu;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.PlayerInventory;

public class PotionShelfMenu extends BCMenu<PotionShelfBlockEntity> {
    public PotionShelfMenu(int id, PlayerInventory inventory, PotionShelfBlockEntity blockEntity) {
        super(BCMenus.POTION_SHELF.get(), id, inventory, blockEntity);
    }

    public PotionShelfMenu(int id, PlayerInventory inventory, PacketBuffer buf) {
        super(BCMenus.POTION_SHELF.get(), id, inventory, buf);
    }

    @Override
    protected void addSlots(PlayerInventory inventory) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                addSlot(new BCSlot(blockEntity, i * 4 + j, 53 + j * 18, 15 + i * 19));
            }
        }
        addInventorySlots(inventory, 8, 84);
    }
}
