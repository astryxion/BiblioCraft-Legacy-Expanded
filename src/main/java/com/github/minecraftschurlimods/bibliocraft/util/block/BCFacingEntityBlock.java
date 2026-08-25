package com.github.minecraftschurlimods.bibliocraft.util.block;

import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResultType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.util.math.BlockRayTraceResult;
import javax.annotation.Nullable;

/**
 * Abstract superclass for entity blocks in this mod.
 */
@SuppressWarnings({"DuplicatedCode"})
public abstract class BCFacingEntityBlock extends BCFacingBlock implements ITileEntityProvider {
    public BCFacingEntityBlock(Properties properties) {
        super(properties);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    public void setPlacedBy(World level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(level, pos, state, entity, stack);
        if (level.getBlockEntity(pos) instanceof BCMenuBlockEntity && stack.hasCustomHoverName()) {
            BCMenuBlockEntity blockEntity = (BCMenuBlockEntity) level.getBlockEntity(pos);
            blockEntity.setCustomName(stack.getHoverName());
        }
    }

    @Override
    public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean flag) {
        if (!state.is(newState.getBlock())) {
            TileEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof IInventory) {
                IInventory container = (IInventory) blockentity;
                InventoryHelper.dropContents(level, pos, container);
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, flag);
        }
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (hand != Hand.MAIN_HAND) return ActionResultType.PASS;
        if (player.isSecondaryUseActive()) return ActionResultType.PASS;
        BCUtil.openBEMenu(player, level, pos);
        return ActionResultType.SUCCESS;
    }

    @Override
    public boolean triggerEvent(BlockState state, World level, BlockPos pos, int id, int param) {
        super.triggerEvent(state, level, pos, id, param);
        TileEntity blockentity = level.getBlockEntity(pos);
        return blockentity != null && blockentity.triggerEvent(id, param);
    }

    @Override
    @Nullable
    public INamedContainerProvider getMenuProvider(BlockState state, World level, BlockPos pos) {
        TileEntity blockentity = level.getBlockEntity(pos);
        return blockentity instanceof INamedContainerProvider ? (INamedContainerProvider) blockentity : null;
    }
}
