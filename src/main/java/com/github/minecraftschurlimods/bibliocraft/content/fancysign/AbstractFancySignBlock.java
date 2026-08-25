package com.github.minecraftschurlimods.bibliocraft.content.fancysign;

import net.minecraft.world.IBlockReader;

import com.github.minecraftschurlimods.bibliocraft.init.BCTags;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCFacingEntityBlock;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResultType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.state.BooleanProperty;

import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraftforge.common.ToolType;
import javax.annotation.Nullable;

public abstract class AbstractFancySignBlock extends BCFacingEntityBlock {
    public static final BooleanProperty UPSIDE_DOWN = BooleanProperty.create("upside_down");
    public static final BooleanProperty WAXED = BooleanProperty.create("waxed");

    public AbstractFancySignBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(UPSIDE_DOWN, false).setValue(WAXED, false).setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(UPSIDE_DOWN, WAXED);
    }

    @Override
    @Nullable
    public TileEntity newBlockEntity(IBlockReader level) {
        return createTileEntity(defaultBlockState(), level);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader level) {
        return new FancySignBlockEntity(BlockPos.ZERO, state);
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
        if (BCTags.Items.contains(BCTags.Items.FANCY_SIGN_WAX, stack.getItem()) && !state.getValue(WAXED)) {
            handleWaxing(true, stack, state, level, pos, player, hand);
            return ActionResultType.SUCCESS;
        }
        if (stack.getToolTypes().contains(ToolType.AXE) && state.getValue(WAXED)) {
            handleWaxing(false, stack, state, level, pos, player, hand);
            return ActionResultType.SUCCESS;
        }
        if (stack.hasCustomHoverName()) {
            String text = stack.getHoverName().getString();
            if ("Dinnerbone".equals(text) || "Grumm".equals(text)) {
                level.setBlockAndUpdate(pos, state.setValue(UPSIDE_DOWN, !state.getValue(UPSIDE_DOWN)));
                return ActionResultType.SUCCESS;
            }
        }
        return super.use(state, level, pos, player, hand, hitResult);
    }

    private static void handleWaxing(boolean wax, ItemStack stack, BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand) {
        BlockState newState = state.setValue(WAXED, wax);
        if (player instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);
        }
        if (stack.isDamageableItem()) {
            stack.hurtAndBreak(1, player, p -> {});
        } else {
            stack.shrink(1);
        }
        level.setBlockAndUpdate(pos, newState);
        level.levelEvent(player, wax ? 3003 : 3004, pos, 0);
    }
}
