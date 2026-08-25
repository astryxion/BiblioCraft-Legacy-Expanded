package com.github.minecraftschurlimods.bibliocraft.content.seat;

import com.github.minecraftschurlimods.bibliocraft.init.BCEntities;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraft.block.BlockState;
import net.minecraftforge.fml.network.NetworkHooks;

public class SeatEntity extends Entity {
    public SeatEntity(EntityType<?> entityType, World level) {
        super(entityType, level);
    }

    public SeatEntity(World level) {
        this(BCEntities.SEAT.get(), level);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void tick() {
        BlockPos pos = blockPosition();
        BlockState state = this.level.getBlockState(pos);
        if (this.level.isClientSide()) return;
        if (!(state.getBlock() instanceof SeatBlock)) {
            getPassengers().forEach(Entity::stopRiding);
            remove();
        } else if (getPassengers().isEmpty()) {
            remove();
            if (state.getBlock() instanceof SeatBlock) {
                this.level.setBlockAndUpdate(pos, state.setValue(SeatBlock.OCCUPIED, false));
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT tag) {
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
