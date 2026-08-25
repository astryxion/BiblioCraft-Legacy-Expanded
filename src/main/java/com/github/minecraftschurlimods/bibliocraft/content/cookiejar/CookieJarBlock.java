package com.github.minecraftschurlimods.bibliocraft.content.cookiejar;

import com.github.minecraftschurlimods.bibliocraft.util.ShapeUtil;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCEntityBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.shapes.VoxelShape;
import javax.annotation.Nullable;

public class CookieJarBlock extends BCEntityBlock {
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    private static final VoxelShape OPEN_SHAPE = ShapeUtil.combine(
            VoxelShapes.box(0.125, 0, 0.125, 0.875, 0.625, 0.875),
            VoxelShapes.box(0.25, 0.625, 0.25, 0.75, 0.75, 0.75));
    private static final VoxelShape CLOSED_SHAPE = ShapeUtil.combine(OPEN_SHAPE,
            VoxelShapes.box(0.1875, 0.75, 0.1875, 0.8125, 0.875, 0.8125));

    public CookieJarBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(OPEN, false).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OPEN);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
        return state.getValue(OPEN) ? OPEN_SHAPE : CLOSED_SHAPE;
    }

    @Override
    @Nullable
    public TileEntity newBlockEntity(IBlockReader level) {
        return createTileEntity(defaultBlockState(), level);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader level) {
        return new CookieJarBlockEntity(BlockPos.ZERO, state);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, World level, BlockPos pos) {
        return state.getValue(OPEN) ? 15 : 0;
    }
}
