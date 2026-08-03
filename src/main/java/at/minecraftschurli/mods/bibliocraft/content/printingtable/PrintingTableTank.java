package at.minecraftschurli.mods.bibliocraft.content.printingtable;

import at.minecraftschurli.mods.bibliocraft.init.BCFluids;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class PrintingTableTank extends SingleFluidStorage {
    static final int EXPERIENCE_MULTIPLIER = 20;
    private final PrintingTableBlockEntity blockEntity;
    private final boolean acceptAutomation;

    public PrintingTableTank(PrintingTableBlockEntity blockEntity, boolean acceptAutomation) {
        this.blockEntity = blockEntity;
        this.acceptAutomation = acceptAutomation;
    }

    public void fillFromCapability(Storage<FluidVariant> capability, TransactionContext transaction) {
        StorageUtil.move(capability, this, this::isExperience, Long.MAX_VALUE, transaction);
    }

    public void clear() {
        set(0, FluidVariant.blank(), 0);
    }

    public void update(PrintingTableTankSyncPacket packet) {
        set(0, packet.resource(), packet.amount());
    }

    public void serialize(ValueOutput output) {
        writeValue(output);
    }

    public void deserialize(ValueInput input) {
        readValue(input);
    }

    public FluidVariant getResource(int index) {
        return index == 0 ? getResource() : FluidVariant.blank();
    }

    public int getAmountAsInt(int index) {
        return index == 0 ? (int) getAmount() : 0;
    }

    public void set(int index, FluidVariant resource, int amount) {
        if (index != 0) {
            return;
        }
        this.variant = resource;
        this.amount = amount;
        onContentsChanged();
    }

    public boolean isFull() {
        return getCapacity() <= getAmount();
    }

    @Override
    protected long getCapacity(FluidVariant variant) {
        return blockEntity.getExperienceCost() * (long) EXPERIENCE_MULTIPLIER;
    }

    @Override
    protected boolean canInsert(FluidVariant variant) {
        return isExperience(variant);
    }

    @Override
    protected boolean canExtract(FluidVariant variant) {
        return false;
    }

    @Override
    public long insert(FluidVariant insertedVariant, long maxAmount, TransactionContext transaction) {
        if (!acceptAutomation) {
            return 0;
        }
        return super.insert(insertedVariant, maxAmount, transaction);
    }

    @Override
    protected void onFinalCommit() {
        super.onFinalCommit();
        onContentsChanged();
    }

    private void onContentsChanged() {
        blockEntity.setChanged();
        if (blockEntity.level() instanceof ServerLevel serverLevel) {
            BlockPos pos = blockEntity.getBlockPos();
            PrintingTableTankSyncPacket packet = new PrintingTableTankSyncPacket(pos, getResource(0), getAmountAsInt(0));
            for (ServerPlayer player : PlayerLookup.tracking(serverLevel, ChunkPos.containing(pos))) {
                ServerPlayNetworking.send(player, packet);
            }
        }
    }

    private boolean isExperience(FluidVariant variant) {
        return variant.isOf(BCFluids.EXPERIENCE.get());
    }
}
