package com.github.minecraftschurlimods.bibliocraft.content.swordpedestal;

import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.init.BCItems;
import com.github.minecraftschurlimods.bibliocraft.util.ShapeUtil;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCFacingInteractibleBlock;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class SwordPedestalBlock extends BCFacingInteractibleBlock {
    /** 1.20.1 replacement for DyedItemColor: rgb + showInTooltip, stored on item/block NBT. */
    public record DyedColor(int rgb, boolean showInTooltip) {
        public static final Codec<DyedColor> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Codec.INT.fieldOf("rgb").forGetter(DyedColor::rgb),
                Codec.BOOL.fieldOf("show_in_tooltip").forGetter(DyedColor::showInTooltip)
        ).apply(inst, DyedColor::new));
        public static void putOnStack(ItemStack stack, DyedColor color) {
            CompoundTag tag = stack.getOrCreateTag();
            tag.putInt("rgb", color.rgb());
            tag.putBoolean("show_in_tooltip", color.showInTooltip());
        }
        public static DyedColor getFromStack(ItemStack stack) {
            CompoundTag tag = stack.getOrCreateTag();
            if (!tag.contains("rgb")) return SwordPedestalBlock.DEFAULT_COLOR;
            return new DyedColor(tag.getInt("rgb"), tag.getBoolean("show_in_tooltip"));
        }
        public static int getRgbFromStack(ItemStack stack) {
            return getFromStack(stack).rgb();
        }
    }
    public static final DyedColor DEFAULT_COLOR = new DyedColor(DyeColor.GREEN.getTextColor(), true);

    /** Returns the rgb tint for the item stack (for color handler). */
    public static int getColorFromStack(ItemStack stack) {
        return DyedColor.getRgbFromStack(stack);
    }

    private static final VoxelShape Z_SHAPE = ShapeUtil.combine(
            Shapes.box(0, 0, 0.25, 1, 0.0625, 0.75),
            Shapes.box(0.0625, 0.0625, 0.25, 0.9375, 0.125, 0.75),
            Shapes.box(0.125, 0.125, 0.25, 0.875, 0.1875, 0.75),
            Shapes.box(0.1875, 0.1875, 0.25, 0.8125, 0.25, 0.75));
    private static final VoxelShape X_SHAPE = ShapeUtil.rotate(Z_SHAPE, Rotation.CLOCKWISE_90);

    public SwordPedestalBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FACING).getAxis() == Direction.Axis.X ? X_SHAPE : Z_SHAPE;
    }

    @Override
    public int lookingAtSlot(BlockState state, BlockHitResult hit) {
        return 0;
    }

    @Override
    protected boolean canAccessFromDirection(BlockState state, Direction direction) {
        return direction != Direction.DOWN;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SwordPedestalBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(level, pos, state, entity, stack);
        if (level.getBlockEntity(pos) instanceof SwordPedestalBlockEntity spbe) {
            spbe.setColor(DyedColor.getFromStack(stack));
        }
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type != BCBlockEntities.SWORD_PEDESTAL.get()) return null;
        return (l, p, s, b) -> SwordPedestalBlockEntity.tick(l, p, s, (SwordPedestalBlockEntity) b);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        ItemStack stack = super.getCloneItemStack(state, target, level, pos, player);
        if (level.getBlockEntity(pos) instanceof SwordPedestalBlockEntity spbe) {
            spbe.saveToItem(stack);
        }
        return stack;
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (level.isClientSide()) {
            super.playerDestroy(level, player, pos, state, blockEntity, tool);
            return;
        }
        if (level instanceof ServerLevel serverLevel) {
            ItemStack stack = new ItemStack(BCItems.SWORD_PEDESTAL.get());
            if (blockEntity instanceof SwordPedestalBlockEntity spbe) {
                spbe.saveToItem(stack);
            }
            Block.popResource(serverLevel, pos, stack);
            return;
        }
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (level.isClientSide() || !(level.getBlockEntity(pos) instanceof SwordPedestalBlockEntity spbe))
            return super.getAnalogOutputSignal(state, level, pos);
        return spbe.getItem(0).isEmpty() ? 0 : 15;
    }
}
