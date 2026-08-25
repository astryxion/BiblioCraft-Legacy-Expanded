package com.github.minecraftschurlimods.bibliocraft.content.dinnerplate;

import com.github.minecraftschurlimods.bibliocraft.util.ShapeUtil;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCEntityBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import java.util.Random;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResultType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.shapes.VoxelShape;
import javax.annotation.Nullable;

public class DinnerPlateBlock extends BCEntityBlock {
    public static final IntegerProperty PROGRESS = IntegerProperty.create("progress", 0, 3);
    public static final VoxelShape SHAPE = ShapeUtil.combine(
            VoxelShapes.box(0.25, 0, 0.3125, 0.75, 0.0625, 0.6875),
            VoxelShapes.box(0.1875, 0, 0.375, 0.25, 0.0625, 0.625),
            VoxelShapes.box(0.75, 0, 0.375, 0.8125, 0.0625, 0.625),
            VoxelShapes.box(0.71875, 0.03125, 0.34375, 0.78125, 0.09375, 0.65625),
            VoxelShapes.box(0.34375, 0.03125, 0.21875, 0.65625, 0.09375, 0.28125),
            VoxelShapes.box(0.21875, 0.03125, 0.34375, 0.28125, 0.09375, 0.65625),
            VoxelShapes.box(0.34375, 0.03125, 0.71875, 0.65625, 0.09375, 0.78125),
            VoxelShapes.box(0.28125, 0.03125, 0.28125, 0.40625, 0.09375, 0.34375),
            VoxelShapes.box(0.28125, 0.03125, 0.34375, 0.34375, 0.09375, 0.40625),
            VoxelShapes.box(0.59375, 0.03125, 0.28125, 0.71875, 0.09375, 0.34375),
            VoxelShapes.box(0.65625, 0.03125, 0.34375, 0.71875, 0.09375, 0.40625),
            VoxelShapes.box(0.59375, 0.03125, 0.65625, 0.71875, 0.09375, 0.71875),
            VoxelShapes.box(0.65625, 0.03125, 0.59375, 0.71875, 0.09375, 0.65625),
            VoxelShapes.box(0.28125, 0.03125, 0.65625, 0.40625, 0.09375, 0.71875),
            VoxelShapes.box(0.28125, 0.03125, 0.59375, 0.34375, 0.09375, 0.65625));

    public DinnerPlateBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(WATERLOGGED, false).setValue(PROGRESS, 0));
    }

    @Override
    public VoxelShape getShape(BlockState pState, IBlockReader pLevel, BlockPos pPos, ISelectionContext pContext) {
        return SHAPE;
    }

    @Override
    @Nullable
    public TileEntity newBlockEntity(IBlockReader level) {
        return createTileEntity(defaultBlockState(), level);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader level) {
        return new DinnerPlateBlockEntity(BlockPos.ZERO, state);
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        BlockState newState = state;
        TileEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof DinnerPlateBlockEntity))
            return super.use(state, level, pos, player, hand, hit);
        DinnerPlateBlockEntity plate = (DinnerPlateBlockEntity) blockEntity;
        ItemStack slotStack = plate.getItem(0);
        if (stack.getItem().getFoodProperties() != null) {
            if (slotStack.isEmpty()) {
                ItemStack foodStack = stack.copy();
                foodStack.setCount(1);
                plate.setItem(0, foodStack);
                stack.shrink(1);
                newState = newState.setValue(PROGRESS, 0);
            } else {
                newState = newState.setValue(PROGRESS, newState.getValue(PROGRESS) + 1);
                triggerItemUseEffects(player, slotStack, 5);
            }
        } else if (!slotStack.isEmpty()) {
            newState = newState.setValue(PROGRESS, newState.getValue(PROGRESS) + 1);
            triggerItemUseEffects(player, slotStack, 5);
        } else return ActionResultType.PASS;
        if (newState.getValue(PROGRESS) == 3) {
            newState = newState.setValue(PROGRESS, 0);
            triggerItemUseEffects(player, slotStack, 16);
            player.eat(level, slotStack);
            plate.setItem(0, ItemStack.EMPTY);
        }
        level.setBlock(pos, newState, 3);
        return ActionResultType.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PROGRESS);
    }

    /**
     * @see net.minecraft.entity.LivingEntity#triggerItemUseEffects(ItemStack, int)
     */
    private void triggerItemUseEffects(PlayerEntity player, ItemStack stack, int amount) {
        if (stack.getUseAnimation() == UseAction.DRINK) {
            player.playSound(stack.getDrinkingSound(), 0.5f, player.level.random.nextFloat() * 0.1f + 0.9f);
        }
        if (stack.getUseAnimation() == UseAction.EAT) {
            Random random = player.getRandom();
            for (int i = 0; i < amount; ++i) {
                Vector3d speed = new Vector3d(((double) random.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0).xRot(-player.xRot * (float) (Math.PI / 180)).yRot(-player.yRot * (float) (Math.PI / 180));
                Vector3d pos = new Vector3d(((double) random.nextFloat() - 0.5) * 0.3, (double) (-random.nextFloat()) * 0.6 - 0.3, 0.6).xRot(-player.xRot * (float) (Math.PI / 180)).yRot(-player.yRot * (float) (Math.PI / 180)).add(player.getX(), player.getEyeY(), player.getZ());
                player.level.addParticle(new ItemParticleData(ParticleTypes.ITEM, stack), pos.x, pos.y, pos.z, speed.x, speed.y + 0.05, speed.z);
            }
            player.playSound(player.getEatingSound(stack), 0.5f + 0.5f * (float) random.nextInt(2), (random.nextFloat() - random.nextFloat()) * 0.2f + 1f);
        }
    }
}
