package com.github.minecraftschurlimods.bibliocraft.content.fancycrafter;

import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.ShapeUtil;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCFacingEntityBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.util.Rotation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.shapes.VoxelShape;
import javax.annotation.Nullable;

public class FancyCrafterBlock extends BCFacingEntityBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private static final VoxelShape NORTH_SHAPE = ShapeUtil.combine(
            VoxelShapes.box(0, 0, 0, 1, 0.25, 1),
            VoxelShapes.box(0, 0.25, 0.0625, 1, 0.625, 1),
            VoxelShapes.box(0, 0.25, 0, 0.125, 0.625, 0.0625),
            VoxelShapes.box(0.875, 0.25, 0, 1, 0.625, 0.0625),
            VoxelShapes.box(0, 0.625, 0, 1, 0.875, 1),
            VoxelShapes.box(0.0625, 0.875, 0.0625, 0.9375, 0.9375, 0.9375),
            VoxelShapes.box(0, 0.9375, 0, 1, 1, 1));
    private static final VoxelShape EAST_SHAPE = ShapeUtil.rotate(NORTH_SHAPE, Rotation.CLOCKWISE_90);
    private static final VoxelShape SOUTH_SHAPE = ShapeUtil.rotate(NORTH_SHAPE, Rotation.CLOCKWISE_180);
    private static final VoxelShape WEST_SHAPE = ShapeUtil.rotate(NORTH_SHAPE, Rotation.COUNTERCLOCKWISE_90);

    public FancyCrafterBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }

    @Override
    @Nullable
    public TileEntity newBlockEntity(IBlockReader level) {
        return createTileEntity(defaultBlockState(), level);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader level) {
        return new FancyCrafterBlockEntity(BlockPos.ZERO, state);
    }

    @Override
    public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean flag) {
        if (!state.is(newState.getBlock())) {
            TileEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof FancyCrafterBlockEntity) {
                FancyCrafterBlockEntity crafter = (FancyCrafterBlockEntity) blockentity;
                for (int i = 0; i < 9; i++) {
                    InventoryHelper.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), crafter.getItem(i));
                }
                for (int i = 10; i < crafter.getContainerSize(); i++) {
                    InventoryHelper.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), crafter.getItem(i));
                }
                level.updateNeighbourForOutputSignal(pos, this);
            }
            if (state.hasTileEntity()) {
                level.removeBlockEntity(pos);
            }
        }
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
    public void neighborChanged(BlockState state, World level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        level.setBlock(pos, state.setValue(POWERED, level.hasNeighborSignal(pos)), 2);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return BCUtil.nonNull(super.getStateForPlacement(context)).setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
    }

    @Override
    public void setPlacedBy(World level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (state.getValue(POWERED)) {
            level.getBlockTicks().scheduleTick(pos, this, 1);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, World level, BlockPos pos) {
        TileEntity be = level.getBlockEntity(pos);
        return be instanceof FancyCrafterBlockEntity ? ((FancyCrafterBlockEntity) be).getRedstoneSignal() : 0;
    }
}
