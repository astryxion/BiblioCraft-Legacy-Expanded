package com.github.minecraftschurlimods.bibliocraft.content.clock;

import net.minecraft.world.IBlockReader;

import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCFacingEntityBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResultType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.math.BlockRayTraceResult;
import javax.annotation.Nullable;

public abstract class AbstractClockBlock extends BCFacingEntityBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public AbstractClockBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false).setValue(POWERED, false));
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
        return new ClockBlockEntity(BlockPos.ZERO, state);
    }


    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (player.isSecondaryUseActive()) return ActionResultType.PASS;
        if (level.isClientSide()) {
            ClientUtil.openClockScreen(pos);
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return state.getValue(POWERED);
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, World level, BlockPos pos) {
        return state.getValue(POWERED) ? 15 : 0;
    }
}
