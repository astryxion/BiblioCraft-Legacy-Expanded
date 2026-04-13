package com.github.minecraftschurlimods.bibliocraft.util.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.LockCode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract superclass for all block entities in this mod.
 */
public abstract class BCBlockEntity extends BlockEntity implements Container {
    private static final String ITEMS_TAG = "items";
    protected final BCItemHandler items;
    private LockCode lockKey = LockCode.NO_LOCK;

    /**
     * @param type          The {@link BlockEntityType} to use.
     * @param containerSize The size of the container.
     * @param pos           The position of this BE.
     * @param state         The state of this BE.
     */
    public BCBlockEntity(BlockEntityType<?> type, int containerSize, BlockPos pos, BlockState state) {
        super(type, pos, state);
        items = new BCItemHandler(containerSize, this);
    }

    public LockCode getLockKey() {
        return lockKey;
    }

    public void setLockKey(LockCode lockKey) {
        this.lockKey = lockKey;
        setChanged();
        level().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    @Override
    public int getContainerSize() {
        return items.getSlots();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < getContainerSize(); i++) {
            if (!items.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot < getContainerSize() ? items.getStackInSlot(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        if (slot >= getContainerSize()) return ItemStack.EMPTY;
        ItemStack stack = items.getStackInSlot(slot);
        ItemStack result = stack.split(count);
        if (!result.isEmpty()) {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
                ContainerSyncScheduler.schedule(this);
                syncSlotsToViewers();
            }
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot < getContainerSize()) {
            ItemStack stack = items.getStackInSlot(slot);
            items.setStackInSlot(slot, ItemStack.EMPTY);
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot < getContainerSize()) {
            items.setStackInSlot(slot, stack);
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
                ContainerSyncScheduler.schedule(this);
                syncSlotsToViewers();
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        BlockPos pos = getBlockPos();
        return level().getBlockEntity(pos) == this && player.distanceToSqr(Vec3.atCenterOf(pos)) <= 64;
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < getContainerSize(); i++) {
            items.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        lockKey = LockCode.fromTag(tag);
        if (tag.contains(ITEMS_TAG)) {
            items.deserializeNBT(registries, tag.getCompound(ITEMS_TAG));
        }
        // When client receives BE sync (packet or chunk load), force rerender so BERs see updated contents.
        // Fixes: cookie jar/bookshelf/display contents not rendering until GUI opened, fast-equip not updating.
        if (level != null && level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        lockKey.addToTag(tag);
        tag.put(ITEMS_TAG, items.serializeNBT(registries));
    }

    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, (be, reg) -> be.getUpdateTag(reg));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }


    public Level level() {
        return level;
    }

    /**
     * Sends current BE data to all nearby players immediately. Use when block state changes (e.g. container
     * open/close) so the client's BER still has correct data after the client may have recreated the BE.
     */
    protected void syncToClientsNow() {
        if (level != null && !level.isClientSide) syncSlotsToViewers();
    }

    /**
     * Pushes current slot state and BE data to clients. Sends block entity data to all nearby players
     * (so display cases and other in-world renders update). For players with this block's menu open,
     * also broadcasts full menu state. Called at end of tick by {@link ContainerSyncScheduler}.
     */
    void syncSlotsToViewers() {
        if (level == null || level.getServer() == null) return;
        Packet<ClientGamePacketListener> bePacket = ClientboundBlockEntityDataPacket.create(this, (be, reg) -> be.getUpdateTag(reg));
        Vec3 center = Vec3.atCenterOf(worldPosition);
        double maxDistSq = 64 * 64;
        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            if (player.level() != level || player.distanceToSqr(center) > maxDistSq) continue;
            player.connection.send(bePacket);
            if (player.containerMenu instanceof BCMenu<?> menu && menu.getBlockEntity() == this)
                menu.broadcastFullState();
        }
    }
}
