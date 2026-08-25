package com.github.minecraftschurlimods.bibliocraft.content.seat;

import com.github.minecraftschurlimods.bibliocraft.util.ShapeUtil;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCFacingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.block.Block;
import net.minecraft.util.Rotation;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.shapes.VoxelShape;

public class SeatBackBlock extends BCFacingBlock {
    public static final EnumProperty<SeatBackType> TYPE = EnumProperty.create("type", SeatBackType.class);
    private static final VoxelShape SHAPE_SMALL_NORTH = VoxelShapes.box(0.125, 0, 0.8125, 0.875, 0.296875, 0.9375);
    private static final VoxelShape SHAPE_SMALL_EAST = ShapeUtil.rotate(SHAPE_SMALL_NORTH, Rotation.CLOCKWISE_90);
    private static final VoxelShape SHAPE_SMALL_SOUTH = ShapeUtil.rotate(SHAPE_SMALL_NORTH, Rotation.CLOCKWISE_180);
    private static final VoxelShape SHAPE_SMALL_WEST = ShapeUtil.rotate(SHAPE_SMALL_NORTH, Rotation.COUNTERCLOCKWISE_90);
    private static final VoxelShape SHAPE_RAISED_NORTH = ShapeUtil.combine(
            VoxelShapes.box(0.1875, 0, 0.75, 0.3125, 0.5, 0.96875),
            VoxelShapes.box(0.6875, 0, 0.75, 0.8125, 0.5, 0.96875),
            VoxelShapes.box(0.125, 0.25, 0.71875, 0.875, 0.625, 0.84375));
    private static final VoxelShape SHAPE_RAISED_EAST = ShapeUtil.rotate(SHAPE_RAISED_NORTH, Rotation.CLOCKWISE_90);
    private static final VoxelShape SHAPE_RAISED_SOUTH = ShapeUtil.rotate(SHAPE_RAISED_NORTH, Rotation.CLOCKWISE_180);
    private static final VoxelShape SHAPE_RAISED_WEST = ShapeUtil.rotate(SHAPE_RAISED_NORTH, Rotation.COUNTERCLOCKWISE_90);
    private static final VoxelShape SHAPE_FLAT_NORTH = VoxelShapes.box(0.125, 0, 0.8125, 0.875, 0.625, 0.9375);
    private static final VoxelShape SHAPE_FLAT_EAST = ShapeUtil.rotate(SHAPE_FLAT_NORTH, Rotation.CLOCKWISE_90);
    private static final VoxelShape SHAPE_FLAT_SOUTH = ShapeUtil.rotate(SHAPE_FLAT_NORTH, Rotation.CLOCKWISE_180);
    private static final VoxelShape SHAPE_FLAT_WEST = ShapeUtil.rotate(SHAPE_FLAT_NORTH, Rotation.COUNTERCLOCKWISE_90);
    private static final VoxelShape SHAPE_TALL_NORTH = ShapeUtil.combine(
            VoxelShapes.box(0.125, 0, 0.8125, 0.875, 0.6875, 0.9375),
            VoxelShapes.box(0.625, 0.6875, 0.8125, 0.6875, 0.8125, 0.9375),
            VoxelShapes.box(0.3125, 0.6875, 0.8125, 0.375, 0.8125, 0.9375),
            VoxelShapes.box(0.3125, 0.8125, 0.8125, 0.6875, 0.875, 0.9375));
    private static final VoxelShape SHAPE_TALL_EAST = ShapeUtil.rotate(SHAPE_TALL_NORTH, Rotation.CLOCKWISE_90);
    private static final VoxelShape SHAPE_TALL_SOUTH = ShapeUtil.rotate(SHAPE_TALL_NORTH, Rotation.CLOCKWISE_180);
    private static final VoxelShape SHAPE_TALL_WEST = ShapeUtil.rotate(SHAPE_TALL_NORTH, Rotation.COUNTERCLOCKWISE_90);
    private static final VoxelShape SHAPE_FANCY_NORTH = ShapeUtil.combine(
            VoxelShapes.box(0.125, 0, 0.8125, 0.875, 0.8125, 0.9375),
            VoxelShapes.box(0.25, 0.8125, 0.8125, 0.75, 0.84375, 0.9375),
            VoxelShapes.box(0.375, 0.84375, 0.8125, 0.625, 0.875, 0.9375),
            VoxelShapes.box(0.875, 0, 0.0625, 1, 0.03125, 0.4375),
            VoxelShapes.box(0.875, 0.03125, 0.1875, 1, 0.0625, 0.3125),
            VoxelShapes.box(0.875, 0, 0.6875, 1, 0.0625, 0.75),
            VoxelShapes.box(0.875, 0, 0.75, 1, 0.8125, 0.9375),
            VoxelShapes.box(0.875, 0.375, 0.71875, 1, 0.75, 0.75),
            VoxelShapes.box(0.875, 0.5, 0.6875, 1, 0.625, 0.71875),
            VoxelShapes.box(0, 0, 0.0625, 0.125, 0.03125, 0.4375),
            VoxelShapes.box(0, 0.03125, 0.1875, 0.125, 0.0625, 0.3125),
            VoxelShapes.box(0, 0, 0.6875, 0.125, 0.0625, 0.75),
            VoxelShapes.box(0, 0, 0.75, 0.125, 0.8125, 0.9375),
            VoxelShapes.box(0, 0.375, 0.71875, 0.125, 0.75, 0.75),
            VoxelShapes.box(0, 0.5, 0.6875, 0.125, 0.625, 0.71875));
    private static final VoxelShape SHAPE_FANCY_EAST = ShapeUtil.rotate(SHAPE_FANCY_NORTH, Rotation.CLOCKWISE_90);
    private static final VoxelShape SHAPE_FANCY_SOUTH = ShapeUtil.rotate(SHAPE_FANCY_NORTH, Rotation.CLOCKWISE_180);
    private static final VoxelShape SHAPE_FANCY_WEST = ShapeUtil.rotate(SHAPE_FANCY_NORTH, Rotation.COUNTERCLOCKWISE_90);

    public SeatBackBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false).setValue(TYPE, SeatBackType.SMALL));
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).getBlock() instanceof SeatBlock;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
        switch (state.getValue(SeatBackBlock.TYPE)) {
case SMALL:
switch (state.getValue(FACING)) {
case NORTH: return SHAPE_SMALL_NORTH; case EAST: return SHAPE_SMALL_EAST; case SOUTH: return SHAPE_SMALL_SOUTH; case WEST: return SHAPE_SMALL_WEST;default: return VoxelShapes.empty();}
case RAISED:
switch (state.getValue(FACING)) {
case NORTH: return SHAPE_RAISED_NORTH; case EAST: return SHAPE_RAISED_EAST; case SOUTH: return SHAPE_RAISED_SOUTH; case WEST: return SHAPE_RAISED_WEST;default: return VoxelShapes.empty();}
case FLAT:
switch (state.getValue(FACING)) {
case NORTH: return SHAPE_FLAT_NORTH; case EAST: return SHAPE_FLAT_EAST; case SOUTH: return SHAPE_FLAT_SOUTH; case WEST: return SHAPE_FLAT_WEST;default: return VoxelShapes.empty();}
case TALL:
switch (state.getValue(FACING)) {
case NORTH: return SHAPE_TALL_NORTH; case EAST: return SHAPE_TALL_EAST; case SOUTH: return SHAPE_TALL_SOUTH; case WEST: return SHAPE_TALL_WEST;default: return VoxelShapes.empty();}
case FANCY:
switch (state.getValue(FACING)) {
case NORTH: return SHAPE_FANCY_NORTH; case EAST: return SHAPE_FANCY_EAST; case SOUTH: return SHAPE_FANCY_SOUTH; case WEST: return SHAPE_FANCY_WEST;default: return VoxelShapes.empty();}
default: return VoxelShapes.empty();
}
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(TYPE);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader level, BlockPos pos, PlayerEntity player) {
        return new ItemStack(SeatBackItem.BLOCK_MAP.get(this).get(state.getValue(TYPE)).get());
    }
}
