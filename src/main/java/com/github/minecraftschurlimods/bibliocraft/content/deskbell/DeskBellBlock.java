package com.github.minecraftschurlimods.bibliocraft.content.deskbell;

import com.github.minecraftschurlimods.bibliocraft.init.BCSoundEvents;
import com.github.minecraftschurlimods.bibliocraft.util.ShapeUtil;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCWaterloggedBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResultType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.shapes.VoxelShape;

public class DeskBellBlock extends BCWaterloggedBlock {
    private static final VoxelShape SHAPE = ShapeUtil.combine(
            VoxelShapes.box(0.375, 0, 0.375, 0.625, 0.125, 0.625),
            VoxelShapes.box(0.40625, 0.125, 0.40625, 0.59375, 0.15625, 0.59375),
            VoxelShapes.box(0.40625, 0, 0.34375, 0.59375, 0.09375, 0.375),
            VoxelShapes.box(0.40625, 0, 0.625, 0.59375, 0.09375, 0.65625),
            VoxelShapes.box(0.34375, 0, 0.40625, 0.375, 0.09375, 0.59375),
            VoxelShapes.box(0.625, 0, 0.40625, 0.65625, 0.09375, 0.59375),
            VoxelShapes.box(0.484375, 0.15625, 0.484375, 0.515625, 0.171875, 0.515625),
            VoxelShapes.box(0.46875, 0.171875, 0.46875, 0.53125, 0.203125, 0.53125));

    public DeskBellBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState pState, IBlockReader pLevel, BlockPos pPos, ISelectionContext pContext) {
        return SHAPE;
    }

    @Override
    public void neighborChanged(BlockState state, World level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if (!level.isClientSide() && level.hasNeighborSignal(pos.below())) {
            playSound(level, pos);
        }
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        playSound(level, pos);
        return ActionResultType.SUCCESS;
    }

    private void playSound(World level, BlockPos pos) {
        level.playSound(null, pos, BCSoundEvents.DESK_BELL.get(), SoundCategory.BLOCKS, 1, 1);
    }
}
