package com.github.minecraftschurlimods.bibliocraft.content.fancysign;

import com.github.minecraftschurlimods.bibliocraft.api.woodtype.BibliocraftWoodType;
import com.github.minecraftschurlimods.bibliocraft.init.BCItems;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.github.minecraftschurlimods.bibliocraft.util.ShapeUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ActionResultType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.util.Rotation;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.shapes.VoxelShape;
import javax.annotation.Nullable;

public class WallFancySignBlock extends AbstractFancySignBlock {
    private static final VoxelShape NORTH_SHAPE = ShapeUtil.combine(
            VoxelShapes.box(0.9375, 0.1875, 0.875, 1, 0.8125, 1),
            VoxelShapes.box(0, 0.1875, 0.875, 0.0625, 0.8125, 1),
            VoxelShapes.box(0.0625, 0.75, 0.875, 0.9375, 0.8125, 1),
            VoxelShapes.box(0.0625, 0.1875, 0.875, 0.9375, 0.25, 1),
            VoxelShapes.box(0.0625, 0.25, 0.90625, 0.9375, 0.75, 1));
    private static final VoxelShape EAST_SHAPE = ShapeUtil.rotate(NORTH_SHAPE, Rotation.CLOCKWISE_90);
    private static final VoxelShape SOUTH_SHAPE = ShapeUtil.rotate(NORTH_SHAPE, Rotation.CLOCKWISE_180);
    private static final VoxelShape WEST_SHAPE = ShapeUtil.rotate(NORTH_SHAPE, Rotation.COUNTERCLOCKWISE_90);
    private final BibliocraftWoodType woodType;

    public WallFancySignBlock(Properties properties) {
        this(properties, null);
    }

    public WallFancySignBlock(Properties properties, @Nullable BibliocraftWoodType woodType) {
        super(properties);
        this.woodType = woodType;
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
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader level, BlockPos pos, PlayerEntity player) {
        return woodType != null ? new ItemStack(BCItems.FANCY_SIGN.get(woodType)) : super.getPickBlock(state, target, level, pos, player);
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, net.minecraft.util.Hand hand, BlockRayTraceResult hit) {
        if (state.getValue(WAXED) || player.isSecondaryUseActive()) return super.use(state, level, pos, player, hand, hit);
        if (level.isClientSide()) {
            ClientUtil.openFancySignScreen(pos, false);
        }
        return ActionResultType.SUCCESS;
    }
}
