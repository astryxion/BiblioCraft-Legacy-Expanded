package com.github.minecraftschurlimods.bibliocraft.content.fancyarmorstand;

import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.ShapeUtil;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCFacingInteractibleBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResultType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.util.Rotation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.state.EnumProperty;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.shapes.VoxelShape;
import javax.annotation.Nullable;

public class FancyArmorStandBlock extends BCFacingInteractibleBlock {
    private static final VoxelShape Z_SHAPE_BOTTOM = ShapeUtil.combine(
            VoxelShapes.box(0, 0, 0, 1, 0.125, 1),
            VoxelShapes.box(0.375, 0.125, 0.375, 0.625, 1, 0.625));
    private static final VoxelShape Z_SHAPE_TOP = ShapeUtil.combine(
            VoxelShapes.box(0.375, 0, 0.375, 0.625, 0.875, 0.625),
            VoxelShapes.box(0.0625, 0.125, 0.375, 0.9375, 0.5, 0.625));
    private static final VoxelShape X_SHAPE_BOTTOM = ShapeUtil.rotate(Z_SHAPE_BOTTOM, Rotation.CLOCKWISE_90);
    private static final VoxelShape X_SHAPE_TOP = ShapeUtil.rotate(Z_SHAPE_TOP, Rotation.CLOCKWISE_90);
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    public FancyArmorStandBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false).setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HALF);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            return state.getValue(FACING).getAxis() == Direction.Axis.X ? X_SHAPE_TOP : Z_SHAPE_TOP;
        } else {
            return state.getValue(FACING).getAxis() == Direction.Axis.X ? X_SHAPE_BOTTOM : Z_SHAPE_BOTTOM;
        }
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld level, BlockPos currentPos, BlockPos facingPos) {
        DoubleBlockHalf half = state.getValue(HALF);
        if (facing.getAxis() == Direction.Axis.Y && half == DoubleBlockHalf.LOWER == (facing == Direction.UP) && (!facingState.is(this) || facingState.getValue(HALF) == half))
            return Blocks.AIR.defaultBlockState();
        return half == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.canSurvive(level, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockPos pos = context.getClickedPos();
        World level = context.getLevel();
        return pos.getY() < 256 - 1 && level.getBlockState(pos.above()).canBeReplaced(context)
                ? BCUtil.nonNull(super.getStateForPlacement(context))
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(HALF, DoubleBlockHalf.LOWER)
                : null;
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader level, BlockPos pos) {
        if (state.getValue(HALF) != DoubleBlockHalf.UPPER) return super.canSurvive(state, level, pos);
        BlockState blockstate = level.getBlockState(pos.below());
        if (state.getBlock() != this) return super.canSurvive(state, level, pos);
        return blockstate.is(this) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER;
    }

    @Override
    public void setPlacedBy(World level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        BlockPos above = pos.above();
        BlockState upper = state.setValue(HALF, DoubleBlockHalf.UPPER);
        if (upper.hasProperty(net.minecraft.state.properties.BlockStateProperties.WATERLOGGED)) {
            upper = upper.setValue(net.minecraft.state.properties.BlockStateProperties.WATERLOGGED, level.getFluidState(above).getType() == net.minecraft.fluid.Fluids.WATER);
        }
        level.setBlock(above, upper, 3);
    }

    @Override
    public void playerWillDestroy(World level, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!level.isClientSide) {
            if (player.isCreative()) {
                //Copy of protected method DoublePlantBlock#preventDropFromBottomPart
                if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
                    BlockPos newPos = pos.below();
                    BlockState newState = level.getBlockState(newPos);
                    if (newState.is(state.getBlock()) && newState.getValue(HALF) == DoubleBlockHalf.LOWER) {
                        level.setBlock(newPos, newState.getFluidState().getType() == Fluids.WATER ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), net.minecraftforge.common.util.Constants.BlockFlags.NO_NEIGHBOR_DROPS | 3);
                        level.levelEvent(player, 2001, newPos, Block.getId(newState));
                    }
                }
            } else {
                dropResources(state, level, pos, null, player, player.getMainHandItem());
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void playerDestroy(World level, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity be, ItemStack stack) {
        super.playerDestroy(level, player, pos, Blocks.AIR.defaultBlockState(), be, stack);
    }

    @SuppressWarnings("deprecation")
    @Override
    public long getSeed(BlockState state, BlockPos pos) {
        return MathHelper.getSeed(pos.getX(), pos.below(state.getValue(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), pos.getZ());
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isSecondaryUseActive() && canAccessFromDirection(state, hit.getDirection())) {
            int slot = lookingAtSlot(state, hit);
            if (slot != -1 && trySwapArmor(stack, slot, hand == Hand.MAIN_HAND ? player.inventory.selected : 40, state, level, pos, player))
                return ActionResultType.SUCCESS;
        }
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            pos = pos.below();
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public int lookingAtSlot(BlockState state, BlockRayTraceResult hit) {
        EquipmentSlotType slot;
        double y = hit.getLocation().y - hit.getBlockPos().getY();
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            if (y < 0.5) {
                slot = EquipmentSlotType.CHEST;
            } else {
                slot = EquipmentSlotType.HEAD;
            }
        } else {
            if (y < 0.4375) {
                slot = EquipmentSlotType.FEET;
            } else {
                slot = EquipmentSlotType.LEGS;
            }
        }
        return 3 - slot.getIndex();
    }

    @Override
    protected boolean canAccessFromDirection(BlockState state, Direction direction) {
        return direction.getAxis() != Direction.Axis.Y;
    }

    @Override
    @Nullable
    public TileEntity newBlockEntity(IBlockReader level) {
        return createTileEntity(defaultBlockState(), level);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader level) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? new FancyArmorStandBlockEntity(BlockPos.ZERO, state) : null;
    }

    /**
     * Attempts to swap the armor in the given slot with the given armor stack.
     *
     * @param stack      The armor stack from the player inventory that should be swapped with.
     * @param slot       The slot index. A result from {@link FancyArmorStandBlock#lookingAtSlot(BlockState, BlockRayTraceResult)}.
     * @param playerSlot The player inventory slot the armor stack (first parameter) is in, and where a swapped item will end up.
     * @param state      The {@link BlockState} to use.
     * @param level      The {@link World} to use.
     * @param pos        The {@link BlockPos} to use.
     * @param player     The {@link PlayerEntity} attempting to swap the items.
     * @return Whether swapping the items was successful or not.
     */
    private boolean trySwapArmor(ItemStack stack, int slot, int playerSlot, BlockState state, World level, BlockPos pos, PlayerEntity player) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            pos = pos.below();
        }
        TileEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof BCBlockEntity)) return false;
        BCBlockEntity bcbe = (BCBlockEntity) blockEntity;
        ItemStack slotStack = bcbe.getItem(slot);
        if (!bcbe.canPlaceItem(slot, stack)) return false;
        bcbe.setItem(slot, stack);
        player.inventory.setItem(playerSlot, slotStack);
        if (slotStack.getItem() instanceof ArmorItem) {
            ArmorItem armor = (ArmorItem) slotStack.getItem();
            level.playSound(null, player.blockPosition(), armor.getMaterial().getEquipSound(), SoundCategory.PLAYERS, 1, 1);
        }
        return true;
    }
}
