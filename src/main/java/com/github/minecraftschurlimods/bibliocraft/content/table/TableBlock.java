package com.github.minecraftschurlimods.bibliocraft.content.table;

import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.ShapeUtil;
import com.github.minecraftschurlimods.bibliocraft.util.StringRepresentableEnum;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCFacingEntityBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResultType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.block.Block;
import net.minecraft.util.Rotation;
import net.minecraft.block.CarpetBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.shapes.VoxelShape;
import javax.annotation.Nullable;

public class TableBlock extends BCFacingEntityBlock {
    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);
    private static final VoxelShape NONE_SHAPE = ShapeUtil.combine(
            VoxelShapes.box(0.0625, 0.875, 0, 0.9375, 1, 1),
            VoxelShapes.box(0, 0.875, 0.0625, 0.0625, 1, 0.9375),
            VoxelShapes.box(0.9375, 0.875, 0.0625, 1, 1, 0.9375),
            VoxelShapes.box(0.4375, 0.0625, 0.4375, 0.5625, 0.875, 0.5625),
            VoxelShapes.box(0.0625, 0, 0.4375, 0.3125, 0.09375, 0.5625),
            VoxelShapes.box(0.1875, 0.09375, 0.4375, 0.4375, 0.1875, 0.5625),
            VoxelShapes.box(0.6875, 0, 0.4375, 0.9375, 0.09375, 0.5625),
            VoxelShapes.box(0.5625, 0.09375, 0.4375, 0.8125, 0.1875, 0.5625),
            VoxelShapes.box(0.4375, 0, 0.0625, 0.5625, 0.09375, 0.3125),
            VoxelShapes.box(0.4375, 0.09375, 0.1875, 0.5625, 0.1875, 0.4375),
            VoxelShapes.box(0.4375, 0, 0.6875, 0.5625, 0.09375, 0.9375),
            VoxelShapes.box(0.4375, 0.09375, 0.5625, 0.5625, 0.1875, 0.8125));
    private static final VoxelShape ONE_SHAPE_NORTH = ShapeUtil.combine(
            VoxelShapes.box(0, 0.875, 0, 1, 1, 0.9375),
            VoxelShapes.box(0.0625, 0.875, 0.9375, 0.9375, 1, 1),
            VoxelShapes.box(0.1875, 0.1875, 0.6875, 0.3125, 0.875, 0.8125),
            VoxelShapes.box(0.21875, 0.125, 0.71875, 0.28125, 0.1875, 0.78125),
            VoxelShapes.box(0.1875, 0, 0.6875, 0.3125, 0.125, 0.8125),
            VoxelShapes.box(0.6875, 0.1875, 0.6875, 0.8125, 0.875, 0.8125),
            VoxelShapes.box(0.71875, 0.125, 0.71875, 0.78125, 0.1875, 0.78125),
            VoxelShapes.box(0.6875, 0, 0.6875, 0.8125, 0.125, 0.8125));
    private static final VoxelShape ONE_SHAPE_EAST = ShapeUtil.rotate(ONE_SHAPE_NORTH, Rotation.CLOCKWISE_90);
    private static final VoxelShape ONE_SHAPE_SOUTH = ShapeUtil.rotate(ONE_SHAPE_NORTH, Rotation.CLOCKWISE_180);
    private static final VoxelShape ONE_SHAPE_WEST = ShapeUtil.rotate(ONE_SHAPE_NORTH, Rotation.COUNTERCLOCKWISE_90);
    private static final VoxelShape STRAIGHT_SHAPE = VoxelShapes.box(0, 0.875, 0, 1, 1, 1);
    private static final VoxelShape CURVE_SHAPE_NORTH = ShapeUtil.combine(
            VoxelShapes.box(0, 0.875, 0, 1, 1, 0.9375),
            VoxelShapes.box(0, 0.875, 0.9375, 0.9375, 1, 1),
            VoxelShapes.box(0.6875, 0.1875, 0.6875, 0.8125, 0.875, 0.8125),
            VoxelShapes.box(0.71875, 0.125, 0.71875, 0.78125, 0.1875, 0.78125),
            VoxelShapes.box(0.6875, 0, 0.6875, 0.8125, 0.125, 0.8125));
    private static final VoxelShape CURVE_SHAPE_EAST = ShapeUtil.rotate(CURVE_SHAPE_NORTH, Rotation.CLOCKWISE_90);
    private static final VoxelShape CURVE_SHAPE_SOUTH = ShapeUtil.rotate(CURVE_SHAPE_NORTH, Rotation.CLOCKWISE_180);
    private static final VoxelShape CURVE_SHAPE_WEST = ShapeUtil.rotate(CURVE_SHAPE_NORTH, Rotation.COUNTERCLOCKWISE_90);

    public TableBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false).setValue(TYPE, Type.NONE));
    }

    @Override
    @Nullable
    public TileEntity newBlockEntity(IBlockReader level) {
        return createTileEntity(defaultBlockState(), level);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader level) {
        return new TableBlockEntity(BlockPos.ZERO, state);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(TYPE);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
        switch (state.getValue(TYPE)) {
case NONE: return NONE_SHAPE;
case ONE:
switch (state.getValue(FACING)) {
case SOUTH: return ONE_SHAPE_SOUTH; case WEST: return ONE_SHAPE_WEST; case EAST: return ONE_SHAPE_EAST;default: return ONE_SHAPE_NORTH;}
case STRAIGHT: case THREE: case ALL: return STRAIGHT_SHAPE;
case CURVE:
switch (state.getValue(FACING)) {
case SOUTH: return CURVE_SHAPE_SOUTH; case WEST: return CURVE_SHAPE_WEST; case EAST: return CURVE_SHAPE_EAST;default: return CURVE_SHAPE_NORTH;}
default: return NONE_SHAPE;
}
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        World level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return getNewState(level, pos, BCUtil.nonNull(super.getStateForPlacement(context)));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, IWorld level, BlockPos pos, BlockPos neighborPos) {
        BlockState updated = getNewState(level, pos, defaultBlockState());
        TileEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof TableBlockEntity) {
            blockEntity.requestModelDataUpdate();
        }
        return updated;
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (hand != Hand.MAIN_HAND) return ActionResultType.PASS;
        TileEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof TableBlockEntity))
            return super.use(state, level, pos, player, hand, hit);
        TableBlockEntity table = (TableBlockEntity) blockEntity;
        Direction direction = hit.getDirection();
        if (direction == Direction.DOWN) return super.use(state, level, pos, player, hand, hit);
        ItemStack stack = player.getItemInHand(Hand.MAIN_HAND);
        boolean isCarpetItem = stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof CarpetBlock;
        boolean useCarpet = direction != Direction.UP && (isCarpetItem || (stack.isEmpty() && !table.getItem(1).isEmpty()));
        ItemStack originalStack = table.getItem(useCarpet ? 1 : 0);
        if (ItemStack.isSame(stack, originalStack)) return ActionResultType.FAIL;
        ItemStack placed = stack.copy();
        placed.setCount(1);
        table.setItem(useCarpet ? 1 : 0, placed);
        stack.shrink(1);
        if (!originalStack.isEmpty()) {
            if (stack.isEmpty()) {
                player.setItemInHand(Hand.MAIN_HAND, originalStack);
            } else {
                player.inventory.add(originalStack);
            }
        }
        return ActionResultType.SUCCESS;
    }

    private BlockState getNewState(IWorld level, BlockPos pos, BlockState state) {
        boolean north = level.getBlockState(pos.north()).getBlock() instanceof TableBlock;
        boolean east = level.getBlockState(pos.east()).getBlock() instanceof TableBlock;
        boolean south = level.getBlockState(pos.south()).getBlock() instanceof TableBlock;
        boolean west = level.getBlockState(pos.west()).getBlock() instanceof TableBlock;
        if (north && east && south && west) return state.setValue(TYPE, Type.ALL);
        if (north && south && west) return state.setValue(TYPE, Type.THREE).setValue(FACING, Direction.WEST);
        if (east && west && north) return state.setValue(TYPE, Type.THREE).setValue(FACING, Direction.NORTH);
        if (south && north && east) return state.setValue(TYPE, Type.THREE).setValue(FACING, Direction.EAST);
        if (west && east && south) return state.setValue(TYPE, Type.THREE).setValue(FACING, Direction.SOUTH);
        if (north && south) return state.setValue(TYPE, Type.STRAIGHT).setValue(FACING, Direction.NORTH);
        if (east && west) return state.setValue(TYPE, Type.STRAIGHT).setValue(FACING, Direction.EAST);
        if (north && east) return state.setValue(TYPE, Type.CURVE).setValue(FACING, Direction.EAST);
        if (east && south) return state.setValue(TYPE, Type.CURVE).setValue(FACING, Direction.SOUTH);
        if (south && west) return state.setValue(TYPE, Type.CURVE).setValue(FACING, Direction.WEST);
        if (west && north) return state.setValue(TYPE, Type.CURVE).setValue(FACING, Direction.NORTH);
        if (north) return state.setValue(TYPE, Type.ONE).setValue(FACING, Direction.NORTH);
        if (east) return state.setValue(TYPE, Type.ONE).setValue(FACING, Direction.EAST);
        if (south) return state.setValue(TYPE, Type.ONE).setValue(FACING, Direction.SOUTH);
        if (west) return state.setValue(TYPE, Type.ONE).setValue(FACING, Direction.WEST);
        return state;
    }

    public void onBlockStateChange(IWorldReader level, BlockPos pos, BlockState oldState, BlockState newState) {
        TileEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof TableBlockEntity) {
            blockEntity.requestModelDataUpdate();
        }
    }

    public enum Type implements StringRepresentableEnum {
        NONE, ONE, STRAIGHT, CURVE, THREE, ALL
    }
}
