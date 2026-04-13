package com.github.minecraftschurlimods.bibliocraft.util.slot;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ToggleableSlotSyncPacket(BlockPos pos, int slot, boolean disabled) {

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(slot);
        buf.writeBoolean(disabled);
    }

    public static ToggleableSlotSyncPacket decode(FriendlyByteBuf buf) {
        return new ToggleableSlotSyncPacket(buf.readBlockPos(), buf.readInt(), buf.readBoolean());
    }

    public static void handle(ToggleableSlotSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() == null) return;
            var level = ctx.get().getSender().level();
            BlockEntity blockEntity = level.getBlockEntity(msg.pos());
            if (blockEntity instanceof HasToggleableSlots slots) {
                slots.setSlotDisabled(msg.slot(), msg.disabled());
                blockEntity.setChanged();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
