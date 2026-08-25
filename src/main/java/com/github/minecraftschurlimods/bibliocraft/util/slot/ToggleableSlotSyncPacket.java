package com.github.minecraftschurlimods.bibliocraft.util.slot;

import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public final class ToggleableSlotSyncPacket  {
    private final BlockPos pos;
    private final int slot;
    private final boolean disabled;

    public ToggleableSlotSyncPacket(BlockPos pos, int slot, boolean disabled) {
        this.pos = pos;
        this.slot = slot;
        this.disabled = disabled;
    }

    public BlockPos pos() { return this.pos; }
    public int slot() { return this.slot; }
    public boolean disabled() { return this.disabled; }

    public void encode(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(slot);
        buf.writeBoolean(disabled);
    }

    public static ToggleableSlotSyncPacket decode(PacketBuffer buf) {
        return new ToggleableSlotSyncPacket(buf.readBlockPos(), buf.readInt(), buf.readBoolean());
    }

    public static void handle(ToggleableSlotSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() == null) return;
            net.minecraft.world.World level = ctx.get().getSender().level;
            TileEntity blockEntity = level.getBlockEntity(msg.pos());
            if (blockEntity instanceof HasToggleableSlots) {
                HasToggleableSlots slots = (HasToggleableSlots) blockEntity;
                slots.setSlotDisabled(msg.slot(), msg.disabled());
                blockEntity.setChanged();
            }
        });
        ctx.get().setPacketHandled(true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToggleableSlotSyncPacket other = (ToggleableSlotSyncPacket) o;
        return java.util.Objects.equals(this.pos, other.pos) && this.slot == other.slot && this.disabled == other.disabled;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.pos, this.slot, this.disabled);
    }

    @Override
    public String toString() {
        return "ToggleableSlotSyncPacket[" + "pos=" + this.pos + ", " + "slot=" + this.slot + ", " + "disabled=" + this.disabled + "]";
    }

}
