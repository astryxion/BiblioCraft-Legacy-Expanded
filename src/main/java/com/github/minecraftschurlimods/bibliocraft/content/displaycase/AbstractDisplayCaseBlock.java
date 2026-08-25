package com.github.minecraftschurlimods.bibliocraft.content.displaycase;

import net.minecraft.world.IBlockReader;

import com.github.minecraftschurlimods.bibliocraft.init.BCSoundEvents;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCFacingInteractibleBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResultType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.math.BlockRayTraceResult;
import javax.annotation.Nullable;

public abstract class AbstractDisplayCaseBlock extends BCFacingInteractibleBlock {
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    public AbstractDisplayCaseBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false).setValue(OPEN, false));
    }

    @Override
    public int lookingAtSlot(BlockState state, BlockRayTraceResult hit) {
        return 0;
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isSecondaryUseActive()) {
            setOpen(level, pos, state, !state.getValue(OPEN));
            return ActionResultType.SUCCESS;
        }
        if (!state.getValue(OPEN)) {
            setOpen(level, pos, state, true);
            return ActionResultType.SUCCESS;
        }
        if (stack.isEmpty() && level.getBlockEntity(pos) instanceof DisplayCaseBlockEntity) {
            DisplayCaseBlockEntity dcbe = (DisplayCaseBlockEntity) level.getBlockEntity(pos);
            if (dcbe.getItem(0).isEmpty()) {
                setOpen(level, pos, state, false);
                return ActionResultType.SUCCESS;
            }
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    @Nullable
    public TileEntity newBlockEntity(IBlockReader level) {
        return createTileEntity(defaultBlockState(), level);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader level) {
        return new DisplayCaseBlockEntity(BlockPos.ZERO, state);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OPEN);
    }

    private void setOpen(World level, BlockPos pos, BlockState state, boolean open) {
        level.setBlock(pos, state.setValue(OPEN, open), 3);
        level.playSound(null, pos, open ? BCSoundEvents.DISPLAY_CASE_OPEN.get() : BCSoundEvents.DISPLAY_CASE_CLOSE.get(), SoundCategory.BLOCKS, 1f, 1f);
    }
}
