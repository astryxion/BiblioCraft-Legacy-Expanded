package com.github.minecraftschurlimods.bibliocraft.content.printingtable;

import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.util.ShapeUtil;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCFacingEntityBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.util.Rotation;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.shapes.VoxelShape;
import javax.annotation.Nullable;

public class PrintingTableBlock extends BCFacingEntityBlock {
    private static final VoxelShape Z_SHAPE = ShapeUtil.combine(
            VoxelShapes.box(0, 0, 0, 1, 0.9375, 1),
            VoxelShapes.box(0, 0.9375, 0, 0.0625, 1, 1),
            VoxelShapes.box(0.1875, 0.9375, 0, 0.25, 1, 1),
            VoxelShapes.box(0.75, 0.9375, 0, 0.8125, 1, 1),
            VoxelShapes.box(0.9375, 0.9375, 0, 1, 1, 1));
    private static final VoxelShape X_SHAPE = ShapeUtil.rotate(Z_SHAPE, Rotation.CLOCKWISE_90);

    public PrintingTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    @Nullable
    public TileEntity newBlockEntity(IBlockReader level) {
        return createTileEntity(defaultBlockState(), level);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader level) {
        return new PrintingTableBlockEntity(BlockPos.ZERO, state);
    }


    @Override
    public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext context) {
        return state.getValue(FACING).getAxis() == Direction.Axis.X ? X_SHAPE : Z_SHAPE;
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, net.minecraft.util.Hand hand, BlockRayTraceResult hit) {
        if (level.getBlockEntity(pos) instanceof PrintingTableBlockEntity && player instanceof ServerPlayerEntity) {
            PrintingTableBlockEntity be = (PrintingTableBlockEntity) level.getBlockEntity(pos);
            ServerPlayerEntity sp = (ServerPlayerEntity) player;
            be.setPlayerName(sp.getName());
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public boolean removedByPlayer(BlockState state, World level, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
        if (level.getBlockEntity(pos) instanceof PrintingTableBlockEntity && level instanceof ServerWorld) {
            PrintingTableBlockEntity blockEntity = (PrintingTableBlockEntity) level.getBlockEntity(pos);
            ServerWorld serverLevel = (ServerWorld) level;
            int experience = blockEntity.getExperience();
            while (experience > 0) {
                int orbValue = ExperienceOrbEntity.getExperienceValue(experience);
                experience -= orbValue;
                serverLevel.addFreshEntity(new ExperienceOrbEntity(serverLevel, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, orbValue));
            }
        }
        return super.removedByPlayer(state, level, pos, player, willHarvest, fluid);
    }
}
