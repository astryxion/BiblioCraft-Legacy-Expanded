package com.github.minecraftschurlimods.bibliocraft.content.bookcase;

import com.github.minecraftschurlimods.bibliocraft.content.redstonebook.RedstoneBookItem;
import com.github.minecraftschurlimods.bibliocraft.util.ShapeUtil;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCFacingEntityBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.IWorldReader;
import net.minecraft.util.Rotation;
import net.minecraft.util.Direction;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.shapes.VoxelShape;
import javax.annotation.Nullable;

import java.util.stream.IntStream;

public class BookcaseBlock extends BCFacingEntityBlock {
    private static final VoxelShape NORTH_SHAPE = ShapeUtil.combine(
            VoxelShapes.box(0.0625, 0, 0.5, 0.9375, 0.0625, 0.9375),
            VoxelShapes.box(0, 0, 0.9375, 1, 1, 1),
            VoxelShapes.box(0.0625, 0.9375, 0.5, 0.9375, 1, 0.9375),
            VoxelShapes.box(0.0625, 0.4375, 0.5, 0.9375, 0.5625, 0.9375),
            VoxelShapes.box(0.9375, 0, 0.5, 1, 1, 0.9375),
            VoxelShapes.box(0, 0, 0.5, 0.0625, 1, 0.9375));
    private static final VoxelShape EAST_SHAPE = ShapeUtil.rotate(NORTH_SHAPE, Rotation.CLOCKWISE_90);
    private static final VoxelShape SOUTH_SHAPE = ShapeUtil.rotate(NORTH_SHAPE, Rotation.CLOCKWISE_180);
    private static final VoxelShape WEST_SHAPE = ShapeUtil.rotate(NORTH_SHAPE, Rotation.COUNTERCLOCKWISE_90);

    public BookcaseBlock(Properties properties) {
        super(properties);
    }

    @Override
    @Nullable
    public TileEntity newBlockEntity(IBlockReader level) {
        return createTileEntity(defaultBlockState(), level);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader level) {
        return new BookcaseBlockEntity(BlockPos.ZERO, state);
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
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, IBlockReader level, BlockPos pos, Direction direction) {
        if (direction == state.getValue(FACING).getOpposite()) {
            return 0;
        }
        return redstoneBookPower(level, pos);
    }

    @Override
    public int getDirectSignal(BlockState state, IBlockReader level, BlockPos pos, Direction direction) {
        return getSignal(state, level, pos, direction);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader level, BlockPos pos, @Nullable Direction side) {
        return side != null && side != state.getValue(FACING).getOpposite();
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, World level, BlockPos pos) {
        if (level.isClientSide()) return super.getAnalogOutputSignal(state, level, pos);
        return redstoneBookPower(level, pos);
    }

    private static int redstoneBookPower(IBlockReader level, BlockPos pos) {
        TileEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof BookcaseBlockEntity)) return 0;
        BookcaseBlockEntity bcbe = (BookcaseBlockEntity) blockEntity;
        for (int i = 15; i >= 0; i--) {
            if (bcbe.getItem(i).getItem() instanceof RedstoneBookItem) return i;
        }
        return 0;
    }

    @Override
    public float getEnchantPowerBonus(BlockState state, IWorldReader level, BlockPos pos) {
        TileEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof BookcaseBlockEntity)) return super.getEnchantPowerBonus(state, level, pos);
        BookcaseBlockEntity bcbe = (BookcaseBlockEntity) blockEntity;
        return super.getEnchantPowerBonus(state, level, pos) + 0.125f * IntStream.range(0, bcbe.getContainerSize())
                .mapToObj(bcbe::getItem)
                .filter(e -> !e.isEmpty())
                .count();
    }
}
