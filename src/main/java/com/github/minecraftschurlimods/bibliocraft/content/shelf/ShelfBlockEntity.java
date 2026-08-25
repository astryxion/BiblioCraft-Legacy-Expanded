package com.github.minecraftschurlimods.bibliocraft.content.shelf;

import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCMenuBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.block.BlockState;

public class ShelfBlockEntity extends BCMenuBlockEntity {
    public ShelfBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntities.SHELF.get(), 4, defaultName("shelf"), pos, state);
    }

    @Override
    public Container createMenu(int id, PlayerInventory inventory) {
        return new ShelfMenu(id, inventory, this);
    }
}
