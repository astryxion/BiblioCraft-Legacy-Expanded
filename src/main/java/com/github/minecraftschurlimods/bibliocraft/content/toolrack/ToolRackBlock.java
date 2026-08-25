package com.github.minecraftschurlimods.bibliocraft.content.toolrack;

import com.github.minecraftschurlimods.bibliocraft.util.ShapeUtil;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCFacingInteractibleBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockReader;
import net.minecraft.util.Rotation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.shapes.VoxelShape;
import javax.annotation.Nullable;

public class ToolRackBlock extends BCFacingInteractibleBlock {
    private static final VoxelShape NORTH_SHAPE = ShapeUtil.combine(
            VoxelShapes.box(0.0625, 0.9375, 0.5, 0.9375, 1, 1),
            VoxelShapes.box(0.9375, 0, 0.5, 1, 1, 1),
            VoxelShapes.box(0, 0, 0.5, 0.0625, 1, 1),
            VoxelShapes.box(0.0625, 0, 0.5, 0.9375, 0.0625, 1),
            VoxelShapes.box(0.0625, 0.0625, 0.5625, 0.9375, 0.9375, 1),
            VoxelShapes.box(0.75, 0.0625, 0.5, 0.8125, 0.125, 0.5625),
            VoxelShapes.box(0.625, 0.1875, 0.5, 0.6875, 0.25, 0.5625),
            VoxelShapes.box(0.3125, 0.0625, 0.5, 0.375, 0.125, 0.5625),
            VoxelShapes.box(0.1875, 0.1875, 0.5, 0.25, 0.25, 0.5625),
            VoxelShapes.box(0.75, 0.5, 0.5, 0.8125, 0.5625, 0.5625),
            VoxelShapes.box(0.625, 0.625, 0.5, 0.6875, 0.6875, 0.5625),
            VoxelShapes.box(0.3125, 0.5, 0.5, 0.375, 0.5625, 0.5625),
            VoxelShapes.box(0.1875, 0.625, 0.5, 0.25, 0.6875, 0.5625));
    private static final VoxelShape EAST_SHAPE = ShapeUtil.rotate(NORTH_SHAPE, Rotation.CLOCKWISE_90);
    private static final VoxelShape SOUTH_SHAPE = ShapeUtil.rotate(NORTH_SHAPE, Rotation.CLOCKWISE_180);
    private static final VoxelShape WEST_SHAPE = ShapeUtil.rotate(NORTH_SHAPE, Rotation.COUNTERCLOCKWISE_90);

    public ToolRackBlock(Properties properties) {
        super(properties);
    }

    @Override
    @Nullable
    public TileEntity newBlockEntity(IBlockReader level) {
        return createTileEntity(defaultBlockState(), level);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader level) {
        return new ToolRackBlockEntity(BlockPos.ZERO, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
        switch (state.getValue(FACING)) {
default: return NORTH_SHAPE;
case EAST: return EAST_SHAPE;
case SOUTH: return SOUTH_SHAPE;
case WEST: return WEST_SHAPE;
}
    }

    @Override
    public int lookingAtSlot(BlockState state, BlockRayTraceResult hit) {
        Direction direction = state.getValue(FACING).getClockWise();
        Direction.Axis axis = direction.getAxis();
        double hitX = hit.getLocation().get(axis) - hit.getBlockPos().get(axis);
        if (direction.getStepX() > 0 || direction.getStepZ() > 0) {
            hitX = 1 - hitX;
        }
        double hitY = hit.getLocation().y - hit.getBlockPos().getY();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                double minX = 0.0625 + j * 0.4375;
                double maxX = minX + 0.4375;
                double maxY = 0.9375 - i * 0.4375;
                double minY = maxY - 0.4375;
                if (hitX >= minX && hitX < maxX && hitY >= minY && hitY < maxY) return i * 2 + j;
            }
        }
        return -1;
    }

    @Override
    protected boolean canAccessFromDirection(BlockState state, Direction direction) {
        return state.getValue(FACING) == direction;
    }
}
