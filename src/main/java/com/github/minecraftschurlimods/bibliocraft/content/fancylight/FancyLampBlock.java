package com.github.minecraftschurlimods.bibliocraft.content.fancylight;

import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.ShapeUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.world.server.ServerWorld;
import java.util.Random;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.util.Rotation;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.shapes.VoxelShape;

public class FancyLampBlock extends AbstractFancyLightBlock {
    private static final VoxelShape NORTH_STANDING_SHAPE = ShapeUtil.combine(
            VoxelShapes.box(0.3125, 0, 0.34375, 0.6875, 0.0625, 0.71875),
            VoxelShapes.box(0.375, 0.0625, 0.40625, 0.625, 0.125, 0.65625),
            VoxelShapes.box(0.4375, 0.125, 0.46875, 0.5625, 0.25, 0.59375),
            VoxelShapes.box(0.46875, 0.25, 0.5, 0.53125, 0.89375, 0.75),
            VoxelShapes.box(0.125, 0.71875, 0.0625, 0.875, 0.9375, 0.5),
            VoxelShapes.box(0.125, 0.71875, 0, 0.875, 0.875, 0.0625),
            VoxelShapes.box(0.125, 0.65625, 0, 0.875, 0.71875, 0.4375),
            VoxelShapes.box(0.125, 0.59375, 0, 0.875, 0.65625, 0.21875));
    private static final VoxelShape EAST_STANDING_SHAPE = ShapeUtil.rotate(NORTH_STANDING_SHAPE, Rotation.CLOCKWISE_90);
    private static final VoxelShape SOUTH_STANDING_SHAPE = ShapeUtil.rotate(NORTH_STANDING_SHAPE, Rotation.CLOCKWISE_180);
    private static final VoxelShape WEST_STANDING_SHAPE = ShapeUtil.rotate(NORTH_STANDING_SHAPE, Rotation.COUNTERCLOCKWISE_90);
    private static final VoxelShape NORTH_HANGING_SHAPE = ShapeUtil.combine(
            VoxelShapes.box(0.3125, 0.9375, 0.3125, 0.6875, 1, 0.6875),
            VoxelShapes.box(0.375, 0.875, 0.375, 0.625, 0.9375, 0.625),
            VoxelShapes.box(0.4375, 0.75, 0.4375, 0.5625, 0.875, 0.5625),
            VoxelShapes.box(0.46875, 0.28125, 0.46875, 0.53125, 0.75, 0.53125),
            VoxelShapes.box(0.125, 0.1875, 0.0625, 0.875, 0.40625, 0.5),
            VoxelShapes.box(0.125, 0.1875, 0, 0.875, 0.34375, 0.0625),
            VoxelShapes.box(0.125, 0.125, 0, 0.875, 0.1875, 0.4375),
            VoxelShapes.box(0.125, 0.0625, 0, 0.875, 0.125, 0.21875),
            VoxelShapes.box(0.125, 0.1875, 0.5, 0.875, 0.40625, 0.9375),
            VoxelShapes.box(0.125, 0.1875, 0.9375, 0.875, 0.34375, 1),
            VoxelShapes.box(0.125, 0.125, 0.5625, 0.875, 0.1875, 1),
            VoxelShapes.box(0.125, 0.0625, 0.78125, 0.875, 0.125, 1));
    private static final VoxelShape EAST_HANGING_SHAPE = ShapeUtil.rotate(NORTH_HANGING_SHAPE, Rotation.CLOCKWISE_90);
    private static final VoxelShape SOUTH_HANGING_SHAPE = ShapeUtil.rotate(NORTH_HANGING_SHAPE, Rotation.CLOCKWISE_180);
    private static final VoxelShape WEST_HANGING_SHAPE = ShapeUtil.rotate(NORTH_HANGING_SHAPE, Rotation.COUNTERCLOCKWISE_90);
    private static final VoxelShape NORTH_WALL_SHAPE = ShapeUtil.combine(
            VoxelShapes.box(0.4375, 0.09375, 0.9375, 0.5625, 0.34375, 1),
            VoxelShapes.box(0.46875, 0.14375, 0.875, 0.53125, 0.26875, 0.9375),
            VoxelShapes.box(0.46875, 0.2375, 0.5, 0.53125, 0.3625, 0.9375),
            VoxelShapes.box(0.125, 0.1875, 0.0625, 0.875, 0.40625, 0.5),
            VoxelShapes.box(0.125, 0.1875, 0, 0.875, 0.34375, 0.0625),
            VoxelShapes.box(0.125, 0.125, 0, 0.875, 0.1875, 0.4375),
            VoxelShapes.box(0.125, 0.0625, 0, 0.875, 0.125, 0.21875));
    private static final VoxelShape EAST_WALL_SHAPE = ShapeUtil.rotate(NORTH_WALL_SHAPE, Rotation.CLOCKWISE_90);
    private static final VoxelShape SOUTH_WALL_SHAPE = ShapeUtil.rotate(NORTH_WALL_SHAPE, Rotation.CLOCKWISE_180);
    private static final VoxelShape WEST_WALL_SHAPE = ShapeUtil.rotate(NORTH_WALL_SHAPE, Rotation.COUNTERCLOCKWISE_90);

    public FancyLampBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
        Type type = state.getValue(TYPE);
        Direction facing = state.getValue(FACING);
        switch (type) {
case STANDING:
switch (facing) {
default: return NORTH_STANDING_SHAPE; case SOUTH: return SOUTH_STANDING_SHAPE; case WEST: return WEST_STANDING_SHAPE; case EAST: return EAST_STANDING_SHAPE;}
case HANGING:
switch (facing) {
default: return NORTH_HANGING_SHAPE; case SOUTH: return SOUTH_HANGING_SHAPE; case WEST: return WEST_HANGING_SHAPE; case EAST: return EAST_HANGING_SHAPE;}
case WALL:
switch (facing) {
default: return NORTH_WALL_SHAPE; case SOUTH: return SOUTH_WALL_SHAPE; case WEST: return WEST_WALL_SHAPE; case EAST: return EAST_WALL_SHAPE;}
default: return NORTH_STANDING_SHAPE;
}
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return BCUtil.nonNull(super.getStateForPlacement(context)).setValue(LIT, !context.getLevel().hasNeighborSignal(context.getClickedPos()));
    }

    @Override
    public void neighborChanged(BlockState state, World level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide) return;
        boolean lit = state.getValue(LIT);
        if (lit != level.hasNeighborSignal(pos)) return;
        if (lit) {
            level.setBlock(pos, state.cycle(LIT), 2);
        } else {
            level.getBlockTicks().scheduleTick(pos, this, 4);
        }
    }

    @Override
    public void tick(BlockState state, ServerWorld level, BlockPos pos, Random random) {
        if (!state.getValue(LIT) && !level.hasNeighborSignal(pos)) {
            level.setBlock(pos, state.cycle(LIT), 2);
        }
    }
}
