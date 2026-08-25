package com.github.minecraftschurlimods.bibliocraft.content.fancylight;

import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.ShapeUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.SoundCategory;
import java.util.Random;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResultType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.util.Rotation;
import net.minecraft.block.BlockState;

import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraftforge.common.ToolType;

public class FancyLanternBlock extends AbstractFancyLightBlock {
    private static final VoxelShape STANDING_SHAPE = ShapeUtil.combine(
            VoxelShapes.box(0.25, 0, 0.25, 0.75, 0.0625, 0.75),
            VoxelShapes.box(0.3125, 0.0625, 0.3125, 0.375, 0.625, 0.375),
            VoxelShapes.box(0.625, 0.0625, 0.3125, 0.6875, 0.625, 0.375),
            VoxelShapes.box(0.3125, 0.0625, 0.625, 0.375, 0.625, 0.6875),
            VoxelShapes.box(0.625, 0.0625, 0.625, 0.6875, 0.625, 0.6875),
            VoxelShapes.box(0.34375, 0.0625, 0.34375, 0.65625, 0.625, 0.65625),
            VoxelShapes.box(0.25, 0.625, 0.25, 0.75, 0.6875, 0.75),
            VoxelShapes.box(0.3125, 0.6875, 0.3125, 0.6875, 0.75, 0.6875),
            VoxelShapes.box(0.375, 0.75, 0.375, 0.625, 0.8125, 0.625));
    private static final VoxelShape HANGING_SHAPE = ShapeUtil.combine(STANDING_SHAPE,
            VoxelShapes.box(0.40625, 0.8125, 0.40625, 0.59375, 1.0, 0.59375));
    private static final VoxelShape NORTH_WALL_SHAPE = ShapeUtil.combine(STANDING_SHAPE,
            VoxelShapes.box(0.4375, 0.65625, 0.9375, 0.5625, 0.90625, 1),
            VoxelShapes.box(0.46875, 0.70625, 0.875, 0.53125, 0.83125, 0.9375),
            VoxelShapes.box(0.46875, 0.8125, 0.46875, 0.53125, 0.925, 0.9375));
    private static final VoxelShape EAST_WALL_SHAPE = ShapeUtil.rotate(NORTH_WALL_SHAPE, Rotation.CLOCKWISE_90);
    private static final VoxelShape SOUTH_WALL_SHAPE = ShapeUtil.rotate(NORTH_WALL_SHAPE, Rotation.CLOCKWISE_180);
    private static final VoxelShape WEST_WALL_SHAPE = ShapeUtil.rotate(NORTH_WALL_SHAPE, Rotation.COUNTERCLOCKWISE_90);
    private static final ResourceLocation DEFAULT_PARTICLE = BCUtil.mcLoc("small_flame");
    private final ResourceLocation particleId;
    private IParticleData particleCache;

    public FancyLanternBlock(Properties properties) {
        this(properties, DEFAULT_PARTICLE);
    }

    public FancyLanternBlock(Properties properties, ResourceLocation particle) {
        super(properties);
        this.particleId = particle;
    }

    private IParticleData getParticle() {
        if (particleCache == null) {
            net.minecraft.particles.ParticleType<?> type = Registry.PARTICLE_TYPE.get(particleId);
            particleCache = type instanceof IParticleData ? (IParticleData) type : ParticleTypes.FLAME;
        }
        return particleCache;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
        switch (state.getValue(TYPE)) {
case STANDING: return STANDING_SHAPE;
case HANGING: return HANGING_SHAPE;
case WALL:
switch (state.getValue(FACING)) {
default: return NORTH_WALL_SHAPE; case SOUTH: return SOUTH_WALL_SHAPE; case WEST: return WEST_WALL_SHAPE; case EAST: return EAST_WALL_SHAPE;}
default: return STANDING_SHAPE;
}
    }

    @Override
    public void animateTick(BlockState state, World level, BlockPos pos, Random random) {
        if (!state.getValue(LIT)) return;
        Vector3d offset = Vector3d.atCenterOf(pos);
        if (random.nextFloat() < 0.3f) {
            level.addParticle(ParticleTypes.SMOKE, offset.x, offset.y, offset.z, 0, 0, 0);
        }
        level.addParticle(getParticle(), offset.x, offset.y, offset.z, 0, 0, 0);
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof net.minecraft.item.FlintAndSteelItem && !state.getValue(LIT)) {
            if (!level.isClientSide()) {
                level.setBlock(pos, state.setValue(LIT, true), 11);
                level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1f, 1f);
                stack.hurtAndBreak(1, player, p -> {});
            }
            return ActionResultType.sidedSuccess(level.isClientSide());
        }
        if (player.abilities.mayBuild && state.getValue(LIT)) {
            level.setBlock(pos, state.setValue(LIT, false), 11);
            level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1f, 1f);
            return ActionResultType.sidedSuccess(level.isClientSide());
        }
        return super.use(state, level, pos, player, hand, hitResult);
    }
}
