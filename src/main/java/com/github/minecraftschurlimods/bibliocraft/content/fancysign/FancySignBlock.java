package com.github.minecraftschurlimods.bibliocraft.content.fancysign;

import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.github.minecraftschurlimods.bibliocraft.util.ShapeUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResultType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.util.Rotation;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.shapes.VoxelShape;

public class FancySignBlock extends AbstractFancySignBlock {
    public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
    private static final VoxelShape Z_SHAPE = ShapeUtil.combine(
            VoxelShapes.box(0.9375, 0.1875, 0.4375, 1, 0.8125, 0.5625),
            VoxelShapes.box(0, 0.1875, 0.4375, 0.0625, 0.8125, 0.5625),
            VoxelShapes.box(0.0625, 0.75, 0.4375, 0.9375, 0.8125, 0.5625),
            VoxelShapes.box(0.0625, 0.1875, 0.4375, 0.9375, 0.25, 0.5625),
            VoxelShapes.box(0.0625, 0.25, 0.46875, 0.9375, 0.75, 0.53125),
            VoxelShapes.box(0.625, 0, 0.4375, 0.75, 0.0625, 0.5625),
            VoxelShapes.box(0.65625, 0.0625, 0.46875, 0.71875, 0.1875, 0.53125),
            VoxelShapes.box(0.25, 0, 0.4375, 0.375, 0.0625, 0.5625),
            VoxelShapes.box(0.28125, 0.0625, 0.46875, 0.34375, 0.1875, 0.53125));
    private static final VoxelShape X_SHAPE = ShapeUtil.rotate(Z_SHAPE, Rotation.CLOCKWISE_90);
    private static final VoxelShape Z_SHAPE_HANGING = ShapeUtil.combine(
            VoxelShapes.box(0.9375, 0.1875, 0.4375, 1, 0.8125, 0.5625),
            VoxelShapes.box(0, 0.1875, 0.4375, 0.0625, 0.8125, 0.5625),
            VoxelShapes.box(0.0625, 0.75, 0.4375, 0.9375, 0.8125, 0.5625),
            VoxelShapes.box(0.0625, 0.1875, 0.4375, 0.9375, 0.25, 0.5625),
            VoxelShapes.box(0.0625, 0.25, 0.46875, 0.9375, 0.75, 0.53125),
            VoxelShapes.box(0.625, 0.9375, 0.4375, 0.75, 1, 0.5625),
            VoxelShapes.box(0.65625, 0.8125, 0.46875, 0.71875, 0.9375, 0.53125),
            VoxelShapes.box(0.25, 0.9375, 0.4375, 0.375, 1, 0.5625),
            VoxelShapes.box(0.28125, 0.8125, 0.46875, 0.34375, 0.9375, 0.53125));
    private static final VoxelShape X_SHAPE_HANGING = ShapeUtil.rotate(Z_SHAPE_HANGING, Rotation.CLOCKWISE_90);

    public FancySignBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(HANGING, false).setValue(UPSIDE_DOWN, false).setValue(WAXED, false).setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HANGING);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return BCUtil.nonNull(super.getStateForPlacement(context)).setValue(HANGING, context.getClickedFace() == Direction.DOWN);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
        if (state.getValue(HANGING)) {
            return state.getValue(FACING).getAxis() == Direction.Axis.X ? X_SHAPE_HANGING : Z_SHAPE_HANGING;
        } else {
            return state.getValue(FACING).getAxis() == Direction.Axis.X ? X_SHAPE : Z_SHAPE;
        }
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (state.getValue(WAXED) || player.isSecondaryUseActive()) return super.use(state, level, pos, player, hand, hit);
        Direction direction = hit.getDirection();
        if (level.isClientSide() && direction.getAxis() == state.getValue(FACING).getAxis()) {
            ClientUtil.openFancySignScreen(pos, direction != state.getValue(FACING));
        }
        return ActionResultType.SUCCESS;
    }
}
