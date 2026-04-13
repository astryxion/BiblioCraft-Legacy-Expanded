package com.github.minecraftschurlimods.bibliocraft.content.table;

import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TableBlockEntity extends BCBlockEntity {
    public TableBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntities.TABLE.get(), 2, pos, state);
    }
}
