package com.github.minecraftschurlimods.bibliocraft.content.clock;

import com.github.minecraftschurlimods.bibliocraft.api.woodtype.BibliocraftWoodType;
import com.github.minecraftschurlimods.bibliocraft.init.BCBlocks;
import com.github.minecraftschurlimods.bibliocraft.util.block.WoodTypeBlockItem;
import net.minecraft.util.Direction;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import javax.annotation.Nullable;

public class FancyClockItem extends WoodTypeBlockItem {
    public FancyClockItem(BibliocraftWoodType woodType) {
        super(BCBlocks.FANCY_CLOCK, woodType);
    }

    @Override
    @Nullable
    protected BlockState getPlacementState(BlockItemUseContext context) {
        Block block = context.getClickedFace() == Direction.UP ? BCBlocks.FANCY_CLOCK.get(woodType) : BCBlocks.WALL_FANCY_CLOCK.get(woodType);
        BlockState state = block.defaultBlockState().setValue(AbstractClockBlock.FACING, context.getHorizontalDirection().getOpposite());
        return canPlace(context, state) ? state : null;
    }
}
