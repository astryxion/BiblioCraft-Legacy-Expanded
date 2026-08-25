package com.github.minecraftschurlimods.bibliocraft.content.swordpedestal;

import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.init.BCItems;
import com.github.minecraftschurlimods.bibliocraft.util.ShapeUtil;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCFacingInteractibleBlock;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.util.Rotation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.shapes.VoxelShape;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class SwordPedestalBlock extends BCFacingInteractibleBlock {
    /** 1.20.1 replacement for DyedItemColor: rgb + showInTooltip, stored on item/block NBT. */
    public static final class DyedColor  {
    private final int rgb;
    private final boolean showInTooltip;

    public DyedColor(int rgb, boolean showInTooltip) {
        this.rgb = rgb;
        this.showInTooltip = showInTooltip;
    }

    public int rgb() { return this.rgb; }
    public boolean showInTooltip() { return this.showInTooltip; }

        public static final Codec<DyedColor> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Codec.INT.fieldOf("rgb").forGetter(DyedColor::rgb),
                Codec.BOOL.fieldOf("show_in_tooltip").forGetter(DyedColor::showInTooltip)
        ).apply(inst, DyedColor::new));
        public static void putOnStack(ItemStack stack, DyedColor color) {
            CompoundNBT tag = stack.getOrCreateTag();
            tag.putInt("rgb", color.rgb());
            tag.putBoolean("show_in_tooltip", color.showInTooltip());
        }
        public static DyedColor getFromStack(ItemStack stack) {
            CompoundNBT tag = stack.getOrCreateTag();
            if (!tag.contains("rgb")) return SwordPedestalBlock.DEFAULT_COLOR;
            return new DyedColor(tag.getInt("rgb"), tag.getBoolean("show_in_tooltip"));
        }
        public static int getRgbFromStack(ItemStack stack) {
            return getFromStack(stack).rgb();
        }
    

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DyedColor other = (DyedColor) o;
        return this.rgb == other.rgb && this.showInTooltip == other.showInTooltip;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.rgb, this.showInTooltip);
    }

    @Override
    public String toString() {
        return "DyedColor[" + "rgb=" + this.rgb + ", " + "showInTooltip=" + this.showInTooltip + "]";
    }

}
    public static final DyedColor DEFAULT_COLOR = new DyedColor(DyeColor.GREEN.getTextColor(), true);

    /** Returns the rgb tint for the item stack (for color handler). */
    public static int getColorFromStack(ItemStack stack) {
        return DyedColor.getRgbFromStack(stack);
    }

    private static final VoxelShape Z_SHAPE = ShapeUtil.combine(
            VoxelShapes.box(0, 0, 0.25, 1, 0.0625, 0.75),
            VoxelShapes.box(0.0625, 0.0625, 0.25, 0.9375, 0.125, 0.75),
            VoxelShapes.box(0.125, 0.125, 0.25, 0.875, 0.1875, 0.75),
            VoxelShapes.box(0.1875, 0.1875, 0.25, 0.8125, 0.25, 0.75));
    private static final VoxelShape X_SHAPE = ShapeUtil.rotate(Z_SHAPE, Rotation.CLOCKWISE_90);

    public SwordPedestalBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
        return state.getValue(FACING).getAxis() == Direction.Axis.X ? X_SHAPE : Z_SHAPE;
    }

    @Override
    public int lookingAtSlot(BlockState state, BlockRayTraceResult hit) {
        return 0;
    }

    @Override
    protected boolean canAccessFromDirection(BlockState state, Direction direction) {
        return direction != Direction.DOWN;
    }

    @Override
    @Nullable
    public TileEntity newBlockEntity(IBlockReader level) {
        return createTileEntity(defaultBlockState(), level);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader level) {
        return new SwordPedestalBlockEntity(BlockPos.ZERO, state);
    }

    @Override
    public void setPlacedBy(World level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(level, pos, state, entity, stack);
        if (level.getBlockEntity(pos) instanceof SwordPedestalBlockEntity) {
            SwordPedestalBlockEntity spbe = (SwordPedestalBlockEntity) level.getBlockEntity(pos);
            spbe.setColor(DyedColor.getFromStack(stack));
        }
    }


    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader level, BlockPos pos, PlayerEntity player) {
        ItemStack stack = super.getPickBlock(state, target, level, pos, player);
        if (level.getBlockEntity(pos) instanceof SwordPedestalBlockEntity) {
            SwordPedestalBlockEntity spbe = (SwordPedestalBlockEntity) level.getBlockEntity(pos);
            spbe.saveToItem(stack);
        }
        return stack;
    }

    @Override
    public void playerDestroy(World level, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity blockEntity, ItemStack tool) {
        if (level.isClientSide()) {
            super.playerDestroy(level, player, pos, state, blockEntity, tool);
            return;
        }
        if (level instanceof ServerWorld) {
            ServerWorld serverLevel = (ServerWorld) level;
            ItemStack stack = new ItemStack(BCItems.SWORD_PEDESTAL.get());
            if (blockEntity instanceof SwordPedestalBlockEntity) {
                SwordPedestalBlockEntity spbe = (SwordPedestalBlockEntity) blockEntity;
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
    public int getAnalogOutputSignal(BlockState state, World level, BlockPos pos) {
        if (level.isClientSide() || !(level.getBlockEntity(pos) instanceof SwordPedestalBlockEntity))
            return super.getAnalogOutputSignal(state, level, pos);
        SwordPedestalBlockEntity spbe = (SwordPedestalBlockEntity) level.getBlockEntity(pos);
        return spbe.getItem(0).isEmpty() ? 0 : 15;
    }
}
