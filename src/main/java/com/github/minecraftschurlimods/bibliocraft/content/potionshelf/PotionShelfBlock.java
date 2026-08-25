package com.github.minecraftschurlimods.bibliocraft.content.potionshelf;

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

public class PotionShelfBlock extends BCFacingInteractibleBlock {
    private static final VoxelShape NORTH_SHAPE = ShapeUtil.combine(
            VoxelShapes.box(0, 0.0625, 0.75, 1, 1, 1),
            VoxelShapes.box(0, 0, 0.75, 0.0625, 0.0625, 1),
            VoxelShapes.box(0.9375, 0, 0.75, 1, 0.0625, 1));
    private static final VoxelShape EAST_SHAPE = ShapeUtil.rotate(NORTH_SHAPE, Rotation.CLOCKWISE_90);
    private static final VoxelShape SOUTH_SHAPE = ShapeUtil.rotate(NORTH_SHAPE, Rotation.CLOCKWISE_180);
    private static final VoxelShape WEST_SHAPE = ShapeUtil.rotate(NORTH_SHAPE, Rotation.COUNTERCLOCKWISE_90);

    public PotionShelfBlock(Properties properties) {
        super(properties);
    }

    @Override
    @Nullable
    public TileEntity newBlockEntity(IBlockReader level) {
        return createTileEntity(defaultBlockState(), level);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader level) {
        return new PotionShelfBlockEntity(BlockPos.ZERO, state);
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
        double hitX = hit.getLocation().get(axis) - hit.getBlockPos().get(axis) + 0.125;
        if (direction.getStepX() > 0 || direction.getStepZ() > 0) {
            hitX = 1.25 - hitX;
        }
        double hitY = hit.getLocation().y - hit.getBlockPos().getY();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                double minX = 0.171875 + j * 0.21875;
                double maxX = minX + 0.21875;
                double maxY = 0.90625 - i * 0.3125;
                double minY = maxY - 0.21875;
                if (hitX >= minX && hitX < maxX && hitY >= minY && hitY < maxY) return i * 4 + j;
            }
        }
        return -1;
    }
}
