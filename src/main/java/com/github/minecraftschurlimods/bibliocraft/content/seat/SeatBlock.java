package com.github.minecraftschurlimods.bibliocraft.content.seat;

import com.github.minecraftschurlimods.bibliocraft.util.ShapeUtil;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCFacingBlock;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCWaterloggedBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ActionResultType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Rotation;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.BooleanProperty;
import net.minecraft.block.material.PushReaction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.shapes.VoxelShape;

public class SeatBlock extends BCWaterloggedBlock {
    public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;
    private static final VoxelShape SHAPE = ShapeUtil.combine(
            VoxelShapes.box(0.0625, 0.625, 0.0625, 0.9375, 0.8125, 0.9375),
            VoxelShapes.box(0.125, 0.8125, 0.125, 0.875, 0.875, 0.875),
            VoxelShapes.box(0.1875, 0, 0.1875, 0.3125, 0.625, 0.3125),
            VoxelShapes.box(0.6875, 0, 0.1875, 0.8125, 0.625, 0.3125),
            VoxelShapes.box(0.6875, 0, 0.6875, 0.8125, 0.625, 0.8125),
            VoxelShapes.box(0.1875, 0, 0.6875, 0.3125, 0.625, 0.8125),
            VoxelShapes.box(0.21875, 0.28125, 0.3125, 0.28125, 0.34375, 0.6875),
            VoxelShapes.box(0.71875, 0.28125, 0.3125, 0.78125, 0.34375, 0.6875),
            VoxelShapes.box(0.3125, 0.28125, 0.21875, 0.6875, 0.34375, 0.28125),
            VoxelShapes.box(0.3125, 0.28125, 0.71875, 0.6875, 0.34375, 0.78125));
    private static final VoxelShape SHAPE_SMALL_NORTH = ShapeUtil.combine(SHAPE,
            VoxelShapes.box(0.1875, 0.8125, 0.84375, 0.8125, 0.9375, 0.90625),
            VoxelShapes.box(0.125, 0.921875, 0.8125, 0.875, 1, 0.9375));
    private static final VoxelShape SHAPE_SMALL_EAST = ShapeUtil.rotate(SHAPE_SMALL_NORTH, Rotation.CLOCKWISE_90);
    private static final VoxelShape SHAPE_SMALL_SOUTH = ShapeUtil.rotate(SHAPE_SMALL_NORTH, Rotation.CLOCKWISE_180);
    private static final VoxelShape SHAPE_SMALL_WEST = ShapeUtil.rotate(SHAPE_SMALL_NORTH, Rotation.COUNTERCLOCKWISE_90);
    private static final VoxelShape SHAPE_RAISED_NORTH = ShapeUtil.combine(SHAPE,
            VoxelShapes.box(0.1875, 0.8125, 0.75, 0.3125, 1, 0.875),
            VoxelShapes.box(0.6875, 0.8125, 0.75, 0.8125, 1, 0.875));
    private static final VoxelShape SHAPE_RAISED_EAST = ShapeUtil.rotate(SHAPE_RAISED_NORTH, Rotation.CLOCKWISE_90);
    private static final VoxelShape SHAPE_RAISED_SOUTH = ShapeUtil.rotate(SHAPE_RAISED_NORTH, Rotation.CLOCKWISE_180);
    private static final VoxelShape SHAPE_RAISED_WEST = ShapeUtil.rotate(SHAPE_RAISED_NORTH, Rotation.COUNTERCLOCKWISE_90);
    private static final VoxelShape SHAPE_FLAT_NORTH = ShapeUtil.combine(SHAPE,
            VoxelShapes.box(0.125, 0.8125, 0.8125, 0.875, 1, 0.9375));
    private static final VoxelShape SHAPE_FLAT_EAST = ShapeUtil.rotate(SHAPE_FLAT_NORTH, Rotation.CLOCKWISE_90);
    private static final VoxelShape SHAPE_FLAT_SOUTH = ShapeUtil.rotate(SHAPE_FLAT_NORTH, Rotation.CLOCKWISE_180);
    private static final VoxelShape SHAPE_FLAT_WEST = ShapeUtil.rotate(SHAPE_FLAT_NORTH, Rotation.COUNTERCLOCKWISE_90);
    private static final VoxelShape SHAPE_FANCY_NORTH = ShapeUtil.combine(SHAPE_FLAT_NORTH,
            VoxelShapes.box(0.875, 0.6875, 0.125, 1, 1, 0.9375),
            VoxelShapes.box(0.875, 0.875, 0, 1, 1, 0.125),
            VoxelShapes.box(0.875, 0.75, 0.0625, 1, 0.875, 0.125),
            VoxelShapes.box(0, 0.6875, 0.125, 0.125, 1, 0.9375),
            VoxelShapes.box(0, 0.875, 0, 0.125, 1, 0.125),
            VoxelShapes.box(0, 0.75, 0.0625, 0.125, 0.875, 0.125));
    private static final VoxelShape SHAPE_FANCY_EAST = ShapeUtil.rotate(SHAPE_FANCY_NORTH, Rotation.CLOCKWISE_90);
    private static final VoxelShape SHAPE_FANCY_SOUTH = ShapeUtil.rotate(SHAPE_FANCY_NORTH, Rotation.CLOCKWISE_180);
    private static final VoxelShape SHAPE_FANCY_WEST = ShapeUtil.rotate(SHAPE_FANCY_NORTH, Rotation.COUNTERCLOCKWISE_90);

    public SeatBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(WATERLOGGED, false).setValue(OCCUPIED, false));
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, net.minecraft.util.Hand hand, BlockRayTraceResult hit) {
        if (state.getValue(OCCUPIED) || player.getVehicle() != null || !(level.getBlockState(pos.above()).isAir() || level.getBlockState(pos.above()).getBlock() instanceof SeatBackBlock))
            return super.use(state, level, pos, player, hand, hit);
        if (!level.isClientSide()) {
            SeatEntity entity = new SeatEntity(level);
            entity.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            level.addFreshEntity(entity);
            player.startRiding(entity);
            level.setBlockAndUpdate(pos, state.setValue(OCCUPIED, true));
        }
        return ActionResultType.sidedSuccess(level.isClientSide());
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, World level, BlockPos pos) {
        return state.getValue(OCCUPIED) ? 15 : 0;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
        BlockState back = level.getBlockState(pos.above());
        if (!(back.getBlock() instanceof SeatBackBlock)) return SHAPE;
        switch (back.getValue(SeatBackBlock.TYPE)) {
case SMALL:
switch (back.getValue(BCFacingBlock.FACING)) {
case NORTH: return SHAPE_SMALL_NORTH; case EAST: return SHAPE_SMALL_EAST; case SOUTH: return SHAPE_SMALL_SOUTH; case WEST: return SHAPE_SMALL_WEST;default: return SHAPE;}
case RAISED:
switch (back.getValue(BCFacingBlock.FACING)) {
case NORTH: return SHAPE_RAISED_NORTH; case EAST: return SHAPE_RAISED_EAST; case SOUTH: return SHAPE_RAISED_SOUTH; case WEST: return SHAPE_RAISED_WEST;default: return SHAPE;}
case FLAT: case TALL:
switch (back.getValue(BCFacingBlock.FACING)) {
case NORTH: return SHAPE_FLAT_NORTH; case EAST: return SHAPE_FLAT_EAST; case SOUTH: return SHAPE_FLAT_SOUTH; case WEST: return SHAPE_FLAT_WEST;default: return SHAPE;}
case FANCY:
switch (back.getValue(BCFacingBlock.FACING)) {
case NORTH: return SHAPE_FANCY_NORTH; case EAST: return SHAPE_FANCY_EAST; case SOUTH: return SHAPE_FANCY_SOUTH; case WEST: return SHAPE_FANCY_WEST;default: return SHAPE;}
default: return SHAPE;
}
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    public void playerWillDestroy(World level, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockPos above = pos.above();
        BlockState back = level.getBlockState(above);
        if (back.getBlock() instanceof SeatBackBlock) {
            Block.dropResources(back, level, above);
            level.setBlockAndUpdate(above, Blocks.AIR.defaultBlockState());
            level.levelEvent(player, 2001, above, Block.getId(back));
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OCCUPIED);
    }
}
