package com.github.minecraftschurlimods.bibliocraft.content.discrack;

import com.github.minecraftschurlimods.bibliocraft.util.ShapeUtil;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCFacingInteractibleBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockReader;
import net.minecraft.util.Rotation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.shapes.VoxelShape;
import javax.annotation.Nullable;

public class DiscRackBlock extends BCFacingInteractibleBlock {
    private static final VoxelShape Z_SHAPE = ShapeUtil.combine(
            VoxelShapes.box(0.3125, 0, 0.1875, 0.6875, 0.0625, 0.8125),
            VoxelShapes.box(0.375, 0.0625, 0.203125, 0.625, 0.25, 0.796875));
    private static final VoxelShape X_SHAPE = ShapeUtil.rotate(Z_SHAPE, Rotation.CLOCKWISE_90);

    public DiscRackBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
        return state.getValue(FACING).getAxis() == Direction.Axis.X ? X_SHAPE : Z_SHAPE;
    }

    @Override
    public int lookingAtSlot(BlockState state, BlockRayTraceResult hit) {
        Vector3d pos = hit.getLocation().subtract(Vector3d.atLowerCornerOf(hit.getBlockPos())).scale(16);
        double value;
        switch (state.getValue(FACING)) {
case SOUTH: value = pos.z() - 3.5; break;
case EAST: value = pos.x() - 3.5; break;
case NORTH: value = 12.5 - pos.z(); break;
case WEST: value = 12.5 - pos.x(); break;
default: value = -1; break;
}
        return value == -1 ? -1 : (int) value;
    }

    @Override
    protected boolean canAccessFromDirection(BlockState state, Direction direction) {
        return direction == Direction.UP ||
                state.getValue(FACING).getAxis() == Direction.Axis.X && direction.getAxis() == Direction.Axis.Z ||
                state.getValue(FACING).getAxis() == Direction.Axis.Z && direction.getAxis() == Direction.Axis.X;
    }

    @Override
    @Nullable
    public TileEntity newBlockEntity(IBlockReader level) {
        return createTileEntity(defaultBlockState(), level);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader level) {
        return new DiscRackBlockEntity(BlockPos.ZERO, state);
    }
}
