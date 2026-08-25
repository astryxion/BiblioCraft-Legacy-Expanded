package com.github.minecraftschurlimods.bibliocraft.content.label;

import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCMenuBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.block.BlockState;

public class LabelBlockEntity extends BCMenuBlockEntity {
    public LabelBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntities.LABEL.get(), 3, defaultName("label"), pos, state);
    }

    @Override
    protected Container createMenu(int id, PlayerInventory inventory) {
        return new LabelMenu(id, inventory, this);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
