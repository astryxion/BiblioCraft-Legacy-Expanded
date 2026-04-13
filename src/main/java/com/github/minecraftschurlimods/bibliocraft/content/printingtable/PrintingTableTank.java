package com.github.minecraftschurlimods.bibliocraft.content.printingtable;

import com.github.minecraftschurlimods.bibliocraft.util.BCPackets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

/**
 * Single-fluid tank for the printing table. Stores experience-type fluid (by tag) for recipe progress.
 * No NeoForge capabilities; compatible blocks can implement {@link ExperienceFluidSource} for pull.
 */
public class PrintingTableTank {
    private static final String FLUID_KEY = "fluid";
    private static final String ID_KEY = "id";
    private static final String AMOUNT_KEY = "amount";
    private static final int EXPERIENCE_MULTIPLIER = 20;

    /** Convention tag for "experience" fluids (e.g. c:experience or mod-specific). */
    public static final TagKey<Fluid> EXPERIENCE_FLUID_TAG = TagKey.create(Registries.FLUID, ResourceLocation.parse("c:experience"));

    private final PrintingTableBlockEntity blockEntity;
    private final boolean acceptAutomation;
    private Fluid fluid = Fluids.EMPTY;
    private int amount = 0;

    public PrintingTableTank(PrintingTableBlockEntity blockEntity, boolean acceptAutomation) {
        this.blockEntity = blockEntity;
        this.acceptAutomation = acceptAutomation;
    }

    public Fluid getFluid() {
        return fluid;
    }

    public int getFluidAmount() {
        return amount;
    }

    public int getCapacity() {
        return blockEntity.getExperienceCost() * EXPERIENCE_MULTIPLIER;
    }

    public static boolean isExperienceFluid(Fluid f) {
        return f != null && !f.isSame(Fluids.EMPTY) && BuiltInRegistries.FLUID.getResourceKey(f).flatMap(BuiltInRegistries.FLUID::getHolder).map(h -> h.is(EXPERIENCE_FLUID_TAG)).orElse(false);
    }

    public int fillManually(Fluid fluidIn, int maxAmount, boolean simulate) {
        if (!isExperienceFluid(fluidIn) || blockEntity.isExperienceFull()) return 0;
        int capacity = getCapacity() - amount;
        int toAdd = Math.min(maxAmount, capacity);
        if (toAdd <= 0) return 0;
        if (!simulate) {
            if (fluid.isSame(Fluids.EMPTY)) fluid = fluidIn;
            amount += toAdd;
        }
        return toAdd;
    }

    /**
     * Fill from another source (e.g. a block entity implementing {@link ExperienceFluidSource}).
     */
    public void fillFromSource(ExperienceFluidSource source, Fluid fluidIn) {
        int capacity = getCapacity() - amount;
        if (capacity <= 0) return;
        int drained = source.drain(fluidIn, capacity);
        if (drained > 0) {
            if (fluid.isSame(Fluids.EMPTY)) fluid = fluidIn;
            amount += drained;
            if (blockEntity.level() instanceof ServerLevel serverLevel) {
                BlockPos pos = blockEntity.getBlockPos();
                BCPackets.sendToTracking(serverLevel, new ChunkPos(pos), new PrintingTableTankSyncPacket(pos, fluid, amount));
            }
        }
    }

    public void loadAdditional(CompoundTag tag) {
        if (!tag.contains(FLUID_KEY)) return;
        CompoundTag fluidTag = tag.getCompound(FLUID_KEY);
        if (fluidTag.contains(ID_KEY)) {
            fluid = BuiltInRegistries.FLUID.get(ResourceLocation.parse(fluidTag.getString(ID_KEY)));
        }
        if (fluidTag.contains(AMOUNT_KEY)) {
            amount = fluidTag.getInt(AMOUNT_KEY);
        }
    }

    public void saveAdditional(CompoundTag tag) {
        CompoundTag fluidTag = new CompoundTag();
        fluidTag.putString(ID_KEY, BuiltInRegistries.FLUID.getKey(fluid).toString());
        fluidTag.putInt(AMOUNT_KEY, amount);
        tag.put(FLUID_KEY, fluidTag);
    }

    public int getExperience() {
        return amount / EXPERIENCE_MULTIPLIER;
    }

    public void addExperience(int experience) {
        if (!blockEntity.isExperienceFull()) {
            amount += experience * EXPERIENCE_MULTIPLIER;
            amount = Math.min(amount, getCapacity());
        }
    }

    public void clear() {
        fluid = Fluids.EMPTY;
        amount = 0;
    }

    public void update(PrintingTableTankSyncPacket packet) {
        fluid = packet.fluid();
        amount = packet.amount();
    }

    /**
     * Implemented by block entities that can provide experience fluid to the printing table.
     * Replaces NeoForge fluid capability for cross-block fluid transfer.
     */
    public interface ExperienceFluidSource {
        /** Drain up to maxAmount of the given fluid. Returns amount actually drained. */
        int drain(Fluid fluid, int maxAmount);
    }
}
