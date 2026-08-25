package com.github.minecraftschurlimods.bibliocraft.content.clock;

import com.github.minecraftschurlimods.bibliocraft.util.ShapeUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockReader;
import net.minecraft.util.Rotation;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.shapes.VoxelShape;

public class FancyClockBlock extends AbstractClockBlock {
    private static final VoxelShape X_SHAPE = ShapeUtil.combine(
            VoxelShapes.box(0.25, 0, 0.25, 0.75, 0.0625, 0.75),
            VoxelShapes.box(0.3125, 0.0625, 0.25, 0.6875, 0.875, 0.75),
            VoxelShapes.box(0.3125, 0.875, 0.25, 0.375, 0.9375, 0.3125),
            VoxelShapes.box(0.3125, 0.875, 0.4375, 0.375, 0.9375, 0.5625),
            VoxelShapes.box(0.3125, 0.875, 0.6875, 0.375, 0.9375, 0.75),
            VoxelShapes.box(0.625, 0.875, 0.25, 0.6875, 0.9375, 0.3125),
            VoxelShapes.box(0.625, 0.875, 0.4375, 0.6875, 0.9375, 0.5625),
            VoxelShapes.box(0.625, 0.875, 0.6875, 0.6875, 0.9375, 0.75));
    private static final VoxelShape Z_SHAPE = ShapeUtil.rotate(X_SHAPE, Rotation.CLOCKWISE_90);

    public FancyClockBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
        return state.getValue(FACING).getAxis() == Direction.Axis.X ? X_SHAPE : Z_SHAPE;
    }
}
