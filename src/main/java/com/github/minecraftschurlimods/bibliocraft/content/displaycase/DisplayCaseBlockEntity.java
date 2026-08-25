package com.github.minecraftschurlimods.bibliocraft.content.displaycase;

import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;

public class DisplayCaseBlockEntity extends BCBlockEntity {
    public DisplayCaseBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntities.DISPLAY_CASE.get(), 1, pos, state);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
