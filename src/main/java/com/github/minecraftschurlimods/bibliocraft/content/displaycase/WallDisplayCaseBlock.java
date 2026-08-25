package com.github.minecraftschurlimods.bibliocraft.content.displaycase;

import com.github.minecraftschurlimods.bibliocraft.api.woodtype.BibliocraftWoodType;
import com.github.minecraftschurlimods.bibliocraft.init.BCItems;
import com.github.minecraftschurlimods.bibliocraft.util.ShapeUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockReader;
import net.minecraft.util.Rotation;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.shapes.VoxelShape;
import javax.annotation.Nullable;

public class WallDisplayCaseBlock extends AbstractDisplayCaseBlock {
    private static final VoxelShape NORTH_SHAPE = VoxelShapes.box(0.0625, 0, 0.5, 0.9375, 1, 1);
    private static final VoxelShape EAST_SHAPE = ShapeUtil.rotate(NORTH_SHAPE, Rotation.CLOCKWISE_90);
    private static final VoxelShape SOUTH_SHAPE = ShapeUtil.rotate(NORTH_SHAPE, Rotation.CLOCKWISE_180);
    private static final VoxelShape WEST_SHAPE = ShapeUtil.rotate(NORTH_SHAPE, Rotation.COUNTERCLOCKWISE_90);
    private final BibliocraftWoodType woodType;
    private final DyeColor color;

    public WallDisplayCaseBlock(Properties properties) {
        this(properties, null, null);
    }

    public WallDisplayCaseBlock(Properties properties, @Nullable BibliocraftWoodType woodType, @Nullable DyeColor color) {
        super(properties);
        this.woodType = woodType;
        this.color = color;
    }

    @Override
    protected boolean canAccessFromDirection(BlockState state, Direction direction) {
        return state.getValue(FACING) == direction;
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
        return woodType != null && color != null ? new ItemStack(BCItems.DISPLAY_CASE.get(woodType, color)) : super.getPickBlock(state, target, level, pos, player);
    }
}
