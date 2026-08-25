package com.github.minecraftschurlimods.bibliocraft.content.label;

import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.ShapeUtil;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCFacingEntityBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ActionResultType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.util.Rotation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.shapes.VoxelShape;
import javax.annotation.Nullable;

import java.util.stream.IntStream;

public class LabelBlock extends BCFacingEntityBlock {
    private static final VoxelShape NORTH_SHAPE = ShapeUtil.combine(
            VoxelShapes.box(0.1875, 0.0625, 0.96875, 0.8125, 0.4375, 1),
            VoxelShapes.box(0.78125, 0.0625, 0.9375, 0.8125, 0.4375, 0.96875),
            VoxelShapes.box(0.1875, 0.0625, 0.9375, 0.21875, 0.4375, 0.96875),
            VoxelShapes.box(0.21875, 0.0625, 0.9375, 0.78125, 0.09375, 0.96875),
            VoxelShapes.box(0.21875, 0.40625, 0.9375, 0.78125, 0.4375, 0.96875));
    private static final VoxelShape EAST_SHAPE = ShapeUtil.rotate(NORTH_SHAPE, Rotation.CLOCKWISE_90);
    private static final VoxelShape SOUTH_SHAPE = ShapeUtil.rotate(NORTH_SHAPE, Rotation.CLOCKWISE_180);
    private static final VoxelShape WEST_SHAPE = ShapeUtil.rotate(NORTH_SHAPE, Rotation.COUNTERCLOCKWISE_90);

    public LabelBlock(Properties properties) {
        super(properties);
    }

    @Override
    @Nullable
    public TileEntity newBlockEntity(IBlockReader level) {
        return createTileEntity(defaultBlockState(), level);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader level) {
        return new LabelBlockEntity(BlockPos.ZERO, state);
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
    public int getLightValue(BlockState state, IBlockReader level, BlockPos pos) {
        TileEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof LabelBlockEntity) {
            LabelBlockEntity label = (LabelBlockEntity) blockEntity;
            return Math.min(15, IntStream.range(0, 3)
                    .map(e -> {
                        net.minecraft.item.Item item = label.getItem(e).getItem();
                        if (!(item instanceof BlockItem)) return 0;
                        BlockItem blockItem = (BlockItem) item;
                        if (blockItem.getBlock() instanceof LabelBlock) return 0;
                        return blockItem.getBlock().defaultBlockState().getLightValue(level, pos);
                    })
                    .sum());
        }
        return super.getLightValue(state, level, pos);
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, net.minecraft.util.Hand hand, BlockRayTraceResult hit) {
        if (hand != net.minecraft.util.Hand.MAIN_HAND) return ActionResultType.PASS;
        if (player.isSecondaryUseActive()) {
            BCUtil.openBEMenu(player, level, pos);
            return ActionResultType.sidedSuccess(level.isClientSide());
        }
        TileEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof LabelBlockEntity) {
            LabelBlockEntity label = (LabelBlockEntity) blockEntity;
            net.minecraft.item.ItemStack held = player.getItemInHand(hand);
            if (!held.isEmpty()) {
                for (int i = 0; i < 3; i++) {
                    if (label.getItem(i).isEmpty()) {
                        label.setItem(i, held.split(1));
                        return ActionResultType.sidedSuccess(level.isClientSide());
                    }
                }
                net.minecraft.item.ItemStack existing = label.getItem(0);
                label.setItem(0, held.split(1));
                if (held.isEmpty()) {
                    player.setItemInHand(hand, existing);
                } else if (!player.addItem(existing)) {
                    player.drop(existing, false);
                }
                return ActionResultType.sidedSuccess(level.isClientSide());
            }
            for (int i = 2; i >= 0; i--) {
                net.minecraft.item.ItemStack existing = label.getItem(i);
                if (!existing.isEmpty()) {
                    player.setItemInHand(hand, existing);
                    label.setItem(i, net.minecraft.item.ItemStack.EMPTY);
                    return ActionResultType.sidedSuccess(level.isClientSide());
                }
            }
        }
        BlockPos newPos = pos.offset(state.getValue(FACING).getOpposite().getNormal());
        BlockState other = level.getBlockState(newPos);
        return other.getBlock().use(other, level, newPos, player, hand, hit.withPosition(newPos));
    }
}
