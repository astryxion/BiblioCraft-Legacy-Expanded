package com.github.minecraftschurlimods.bibliocraft.content.discrack;

import com.github.minecraftschurlimods.bibliocraft.init.BCBlocks;
import net.minecraft.util.Direction;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import javax.annotation.Nullable;

public class DiscRackItem extends BlockItem {
    public DiscRackItem(Properties properties) {
        super(BCBlocks.DISC_RACK.get(), properties);
    }

    @Override
    @Nullable
    protected BlockState getPlacementState(BlockItemUseContext context) {
        Block block = context.getClickedFace() == Direction.UP ? BCBlocks.DISC_RACK.get() : BCBlocks.WALL_DISC_RACK.get();
        BlockState state = block.defaultBlockState().setValue(DiscRackBlock.FACING, context.getHorizontalDirection().getOpposite());
        return canPlace(context, state) ? state : null;
    }
}
