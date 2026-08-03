package at.minecraftschurli.mods.bibliocraft.util.block;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.LockCode;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/// Abstract superclass for all block entities in this mod.
public abstract class BCBlockEntity extends BlockEntity implements ItemOwner, Container {
    private final BCItemHandler itemHandler;
    private final int slotCapacity;
    private LockCode lockKey = LockCode.NO_LOCK;

    /// @param type          The [BlockEntityType] to use.
    /// @param containerSize The size of the container.
    /// @param pos           The position of this BE.
    /// @param state         The state of this BE.
    public BCBlockEntity(BlockEntityType<?> type, int containerSize, BlockPos pos, BlockState state) {
        this(type, containerSize, Item.ABSOLUTE_MAX_STACK_SIZE, pos, state);
    }

    /// @param type          The [BlockEntityType] to use.
    /// @param containerSize The size of the container.
    /// @param slotCapacity  The max capacity of each slot.
    /// @param pos           The position of this BE.
    /// @param state         The state of this BE.
    public BCBlockEntity(BlockEntityType<?> type, int containerSize, int slotCapacity, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.itemHandler = new BCItemHandler(containerSize, this::isValid, this::getCapacity, this::setChanged);
        this.slotCapacity = slotCapacity;
    }

    public BCItemHandler getItemHandler() {
        return this.itemHandler;
    }

    public int getContainerSize() {
        return this.itemHandler.size();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < getContainerSize(); i++) {
            if (!isEmpty(i)) return false;
        }
        return true;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack existing = getItem(slot);
        if (existing.isEmpty()) return ItemStack.EMPTY;
        int toRemove = Math.min(amount, existing.getCount());
        ItemStack result = existing.copyWithCount(toRemove);
        existing.shrink(toRemove);
        setItem(slot, existing);
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack existing = getItem(slot);
        if (existing.isEmpty()) return ItemStack.EMPTY;
        ItemStack result = existing.copyWithCount(existing.getCount());
        setItem(slot, ItemStack.EMPTY);
        return result;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.itemHandler.set(slot, ItemVariant.of(stack), stack.getCount());
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return isValid(slot, ItemVariant.of(stack));
    }

    @Override
    public void setChanged() {
        super.setChanged();
        setComponents(collectComponents());
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < getContainerSize(); i++) {
            setItem(i, ItemStack.EMPTY);
        }
    }

    public LockCode getLockKey() {
        return this.lockKey;
    }

    public void setLockKey(LockCode lockKey) {
        this.lockKey = lockKey;
        setChanged();
        level().sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public boolean isLocked() {
        return LockCode.NO_LOCK.equals(this.lockKey);
    }

    public int getCapacity(ItemVariant resource) {
        return Math.min(this.slotCapacity, resource.toStack(1).getMaxStackSize());
    }

    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public ItemStack getItem(int index) {
        return this.itemHandler.getStack(index);
    }

    public boolean isValid(int slot, ItemVariant resource) {
        return true;
    }

    public boolean isEmpty(int index) {
        return this.itemHandler.isEmpty(index);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter componentGetter) {
        super.applyImplicitComponents(componentGetter);
        this.lockKey = componentGetter.getOrDefault(DataComponents.LOCK, LockCode.NO_LOCK);
        this.itemHandler.fillFromComponent(componentGetter.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        if (isLocked()) {
            components.set(DataComponents.LOCK, this.lockKey);
        }
        components.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.itemHandler.copyToList()));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void removeComponentsFromTag(ValueOutput output) {
        output.discard(LockCode.TAG_LOCK);
        output.discard(BCItemHandler.ITEMS_TAG);
    }

    public void requestModelDataUpdate() {
        if (level == null) {
            return;
        }
        if (!level.isClientSide()) {
            var server = level.getServer();
            if (server != null && !server.isSameThread()) {
                server.execute(() -> {
                    if (level != null) {
                        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
                    }
                });
                return;
            }
        }
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.lockKey = LockCode.fromTag(input);
        this.itemHandler.deserialize(input);
        if (this.isEmpty() && input.keySet().contains("components")) {
            input.read("components", DataComponentMap.CODEC).ifPresent(this::applyImplicitComponents);
        }
        requestModelDataUpdate();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        this.lockKey.addToTag(output);
        this.itemHandler.serialize(output);
    }

    @Override
    public void saveWithoutMetadata(ValueOutput output) {
        setComponents(collectComponents());
        super.saveWithoutMetadata(output);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }

    @Override
    public Level level() {
        return Objects.requireNonNull(this.level);
    }

    @Override
    public Vec3 position() {
        return Vec3.atCenterOf(this.worldPosition);
    }

    @Override
    public float getVisualRotationYInDegrees() {
        BlockState blockState = getBlockState();
        return blockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING) ? blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() : 0;
    }
    
    public NonNullList<ItemStack> getContents() {
        return this.itemHandler.copyToList();
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level != null) {
            Containers.dropContents(level, pos, getContents());
        }
    }
}
