package com.github.minecraftschurlimods.bibliocraft.content.printingtable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.network.PacketDistributor;

public class PrintingTableTank implements IFluidHandler, IFluidTank {
    private static final TagKey<Fluid> EXPERIENCE_FLUID_TAG = TagKey.create(Registries.FLUID, new ResourceLocation("forge", "experience"));
    private static final String FLUID_KEY = "fluid";
    private static final String ID_KEY = "id";
    private static final String AMOUNT_KEY = "amount";
    private static final int EXPERIENCE_MULTIPLIER = 20;
    private final PrintingTableBlockEntity blockEntity;
    private final boolean acceptAutomation;
    private Fluid fluid = Fluids.EMPTY;
    private int amount = 0;

    public PrintingTableTank(PrintingTableBlockEntity blockEntity, boolean acceptAutomation) {
        this.blockEntity = blockEntity;
        this.acceptAutomation = acceptAutomation;
    }

    @Override
    public FluidStack getFluid() {
        return new FluidStack(fluid, amount);
    }

    @Override
    public int getFluidAmount() {
        return amount;
    }

    @Override
    public int getCapacity() {
        return blockEntity.getExperienceCost() * EXPERIENCE_MULTIPLIER;
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        if (blockEntity.level() == null) return false;
        var registry = blockEntity.level().registryAccess().registryOrThrow(Registries.FLUID);
        return registry.getTag(EXPERIENCE_FLUID_TAG).flatMap(holderSet -> registry.getResourceKey(stack.getFluid()).map(registry::getHolderOrThrow).map(holderSet::contains)).orElse(false);
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return tank == 0 ? getFluid() : FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        return tank == 0 ? getCapacity() : 0;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return tank == 0 && isFluidValid(stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return acceptAutomation ? fillManually(resource, action) : 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return FluidStack.EMPTY;
    }

    public int fillManually(FluidStack resource, FluidAction action) {
        if (!isFluidValid(resource) || blockEntity.isExperienceFull()) return 0;
        int maxAmount = Math.min(resource.getAmount(), blockEntity.getExperienceCost() * EXPERIENCE_MULTIPLIER - amount);
        if (action.execute()) {
            if (fluid == Fluids.EMPTY) {
                fluid = resource.getFluid();
            }
            amount += maxAmount;
        }
        return maxAmount;
    }

    public void fillFromCapability(IFluidHandler capability, Fluid fluid) {
        FluidStack drain = capability.drain(new FluidStack(fluid, getCapacity() - amount), FluidAction.EXECUTE);
        if (!drain.isEmpty()) {
            fillManually(drain, FluidAction.EXECUTE);
            if (blockEntity.level() instanceof ServerLevel serverLevel) {
                BlockPos pos = blockEntity.getBlockPos();
                com.github.minecraftschurlimods.bibliocraft.BCEventHandler.getChannel().send(PacketDistributor.TRACKING_CHUNK.with(() -> serverLevel.getChunkAt(pos)), new PrintingTableTankSyncPacket(pos, this.fluid, amount));
            }
        }
    }

    public void loadAdditional(CompoundTag tag) {
        if (!tag.contains(FLUID_KEY)) return;
        CompoundTag fluidTag = tag.getCompound(FLUID_KEY);
        if (fluidTag.contains(ID_KEY)) {
            fluid = BuiltInRegistries.FLUID.get(new ResourceLocation(fluidTag.getString(ID_KEY)));
        }
        if (fluidTag.contains(AMOUNT_KEY)) {
            amount = tag.getInt(AMOUNT_KEY);
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
}
