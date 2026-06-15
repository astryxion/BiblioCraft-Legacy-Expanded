package com.github.minecraftschurlimods.bibliocraft.content.label;

import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.ShapeUtil;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCFacingInteractibleBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.stream.IntStream;

public class LabelBlock extends BCFacingInteractibleBlock {
    private static final VoxelShape NORTH_SHAPE = ShapeUtil.combine(
            Shapes.box(0.1875, 0.0625, 0.96875, 0.8125, 0.4375, 1),
            Shapes.box(0.78125, 0.0625, 0.9375, 0.8125, 0.4375, 0.96875),
            Shapes.box(0.1875, 0.0625, 0.9375, 0.21875, 0.4375, 0.96875),
            Shapes.box(0.21875, 0.0625, 0.9375, 0.78125, 0.09375, 0.96875),
            Shapes.box(0.21875, 0.40625, 0.9375, 0.78125, 0.4375, 0.96875));
    private static final VoxelShape EAST_SHAPE = ShapeUtil.rotate(NORTH_SHAPE, Rotation.CLOCKWISE_90);
    private static final VoxelShape SOUTH_SHAPE = ShapeUtil.rotate(NORTH_SHAPE, Rotation.CLOCKWISE_180);
    private static final VoxelShape WEST_SHAPE = ShapeUtil.rotate(NORTH_SHAPE, Rotation.COUNTERCLOCKWISE_90);

    public LabelBlock(Properties properties) {
        super(properties);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LabelBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case EAST -> EAST_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            default -> NORTH_SHAPE;
        };
    }

    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof LabelBlockEntity label) return Math.min(15, IntStream.range(0, 3)
                .map(e -> label.getItem(e).getItem() instanceof BlockItem blockItem
                        ? blockItem.getBlock() instanceof LabelBlock
                        ? 0
                        : blockItem.getBlock().defaultBlockState().getLightEmission()
                        : 0)
                .sum());
        return state.getLightEmission();
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!player.isSecondaryUseActive()) {
            BlockPos newPos = pos.relative(state.getValue(FACING).getOpposite());
            return level.getBlockState(newPos).useWithoutItem(level, player, hit.withPosition(newPos));
        }
        BCUtil.openBEMenu(player, level, pos);
        return InteractionResult.SUCCESS;
    }

    @Override
    public int lookingAtSlot(BlockState state, BlockHitResult hit) {
        Direction facing = state.getValue(FACING);
        double x = hit.getLocation().x - hit.getBlockPos().getX();
        double y = hit.getLocation().y - hit.getBlockPos().getY();
        double z = hit.getLocation().z - hit.getBlockPos().getZ();
        double u;
        double v = y;
        switch (facing) {
            case NORTH -> u = x;
            case SOUTH -> u = 1 - x;
            case EAST -> u = 1 - z;
            case WEST -> u = z;
            default -> {
                return -1;
            }
        }
        if (u >= 0.3125 && u < 0.6875 && v >= 0.0625 && v < 0.25) return 0;
        if (u >= 0.5 && u < 0.8125 && v >= 0.1875 && v < 0.4375) return 1;
        if (u >= 0.1875 && u < 0.5 && v >= 0.1875 && v < 0.4375) return 2;
        return -1;
    }

    @Override
    protected boolean canAccessFromDirection(BlockState state, Direction direction) {
        return state.getValue(FACING) == direction;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return WallTorchBlock.canSurvive(level, pos, state.getValue(FACING));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        return facing.getOpposite() == state.getValue(FACING) && !state.canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : state;
    }
}
