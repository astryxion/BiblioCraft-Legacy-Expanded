package com.github.minecraftschurlimods.bibliocraft.content.typewriter;

import com.github.minecraftschurlimods.bibliocraft.init.BCTags;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.github.minecraftschurlimods.bibliocraft.util.ShapeUtil;
import com.github.minecraftschurlimods.bibliocraft.util.Translations;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCFacingEntityBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResultType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.util.Rotation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.shapes.VoxelShape;
import javax.annotation.Nullable;

public class TypewriterBlock extends BCFacingEntityBlock {
    private static final VoxelShape NORTH_SHAPE = ShapeUtil.combine(
            VoxelShapes.box(0.125, 0, 0.125, 0.875, 0.125, 0.875),
            VoxelShapes.box(0.125, 0.125, 0.21875, 0.875, 0.15625, 0.875),
            VoxelShapes.box(0.125, 0.15625, 0.3125, 0.875, 0.1875, 0.875),
            VoxelShapes.box(0.125, 0.1875, 0.40625, 0.875, 0.21875, 0.875),
            VoxelShapes.box(0.125, 0.21875, 0.5, 0.25, 0.375, 0.6875),
            VoxelShapes.box(0.75, 0.21875, 0.5, 0.875, 0.375, 0.6875),
            VoxelShapes.box(0.125, 0.21875, 0.6875, 0.875, 0.375, 0.875),
            VoxelShapes.box(0.125, 0.375, 0.6875, 0.25, 0.5625, 0.875),
            VoxelShapes.box(0.75, 0.375, 0.6875, 0.875, 0.5625, 0.875),
            VoxelShapes.box(0.25, 0.375, 0.84375, 0.75, 0.5625, 0.875),
            VoxelShapes.box(0.0625, 0.40625, 0.71875, 0.9375, 0.53125, 0.84375));
    private static final VoxelShape EAST_SHAPE = ShapeUtil.rotate(NORTH_SHAPE, Rotation.CLOCKWISE_90);
    private static final VoxelShape SOUTH_SHAPE = ShapeUtil.rotate(NORTH_SHAPE, Rotation.CLOCKWISE_180);
    private static final VoxelShape WEST_SHAPE = ShapeUtil.rotate(NORTH_SHAPE, Rotation.COUNTERCLOCKWISE_90);
    private static final VoxelShape NORTH_COLLISION_SHAPE = ShapeUtil.combine(
            VoxelShapes.box(0.125, 0, 0.125, 0.875, 0.15625, 0.875),
            VoxelShapes.box(0.125, 0.15625, 0.3125, 0.875, 0.21875, 0.875),
            VoxelShapes.box(0.125, 0.21875, 0.5, 0.875, 0.375, 0.875),
            VoxelShapes.box(0.0625, 0.375, 0.6875, 0.9375, 0.5625, 0.875));
    private static final VoxelShape EAST_COLLISION_SHAPE = ShapeUtil.rotate(NORTH_COLLISION_SHAPE, Rotation.CLOCKWISE_90);
    private static final VoxelShape SOUTH_COLLISION_SHAPE = ShapeUtil.rotate(NORTH_COLLISION_SHAPE, Rotation.CLOCKWISE_180);
    private static final VoxelShape WEST_COLLISION_SHAPE = ShapeUtil.rotate(NORTH_COLLISION_SHAPE, Rotation.COUNTERCLOCKWISE_90);
    public static final IntegerProperty PAPER = IntegerProperty.create("paper", 0, 7);

    public TypewriterBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(PAPER, 0).setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
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
    public VoxelShape getCollisionShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
        switch (state.getValue(FACING)) {
default: return NORTH_COLLISION_SHAPE;
case EAST: return EAST_COLLISION_SHAPE;
case SOUTH: return SOUTH_COLLISION_SHAPE;
case WEST: return WEST_COLLISION_SHAPE;
}
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PAPER);
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (level.getBlockEntity(pos) instanceof TypewriterBlockEntity) {
            TypewriterBlockEntity typewriter = (TypewriterBlockEntity) level.getBlockEntity(pos);
            ItemStack held = player.getItemInHand(hand);
            if (BCTags.Items.contains(BCTags.Items.TYPEWRITER_PAPER, held.getItem()) || held.getItem() == Items.PAPER) {
                return typewriter.insertPaper(held) ? ActionResultType.sidedSuccess(level.isClientSide()) : ActionResultType.FAIL;
            }
            ItemStack item = typewriter.getItem(TypewriterBlockEntity.OUTPUT);
            if (!item.isEmpty()) {
                player.addItem(typewriter.takeOutput());
                return ActionResultType.SUCCESS;
            }
            if (typewriter.getItem(TypewriterBlockEntity.INPUT).isEmpty()) {
                player.displayClientMessage(Translations.TYPEWRITER_NO_PAPER, true);
                return ActionResultType.SUCCESS;
            }
            if (level.isClientSide()) {
                ClientUtil.openTypewriterScreen(pos);
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
        return new TypewriterBlockEntity(BlockPos.ZERO, state);
    }
}
