package com.github.minecraftschurlimods.bibliocraft.util.block;

import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResultType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockRayTraceResult;

/**
 * Abstract superclass for rotatable entity blocks that have in-world interactions.
 */
public abstract class BCFacingInteractibleBlock extends BCFacingEntityBlock {
    public BCFacingInteractibleBlock(Properties properties) {
        super(properties);
    }

    /**
     * Determines what slot a player is currently considered to be looking at.
     *
     * @param state The state of the block.
     * @param hit   The hit result of the player looking at the block.
     * @return The slot the player is currently looking at, or -1 if they are not looking at a slot.
     */
    public abstract int lookingAtSlot(BlockState state, BlockRayTraceResult hit);

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (hand != Hand.MAIN_HAND) return ActionResultType.PASS;
        if (player.isSecondaryUseActive()) return ActionResultType.PASS;
        if (!canAccessFromDirection(state, hit.getDirection()))
            return super.use(state, level, pos, player, hand, hit);
        int slot = lookingAtSlot(state, hit);
        if (slot != -1) {
            TileEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof BCBlockEntity) {
                BCBlockEntity bcbe = (BCBlockEntity) blockEntity;
                ItemStack stack = player.getItemInHand(Hand.MAIN_HAND);
                if (bcbe.getLockKey() != null && !LockableTileEntity.canUnlock(player, bcbe.getLockKey(), BCUtil.getNameAtPos(level, pos)))
                    return ActionResultType.CONSUME;
                ItemStack slotStack = bcbe.getItem(slot);
                if (stack.isEmpty() || bcbe.canPlaceItem(slot, stack)) {
                    bcbe.setItem(slot, stack);
                    player.setItemInHand(Hand.MAIN_HAND, slotStack);
                    return ActionResultType.SUCCESS;
                }
            }
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    /**
     * @param state     The state to check.
     * @param direction The direction to check.
     * @return Whether the player can access this block's inventory from the given direction.
     */
    protected boolean canAccessFromDirection(BlockState state, Direction direction) {
        Direction facing = state.getValue(FACING);
        return facing == direction || facing == direction.getOpposite();
    }
}
