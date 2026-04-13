package com.github.minecraftschurlimods.bibliocraft.content.fancysign;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record FancySignSyncPacket(FancySignContent list, BlockPos pos, boolean back) {

    public void encode(FriendlyByteBuf buf) {
        list.write(buf);
        buf.writeBlockPos(pos);
        buf.writeBoolean(back);
    }

    public static FancySignSyncPacket decode(FriendlyByteBuf buf) {
        FancySignContent list = FancySignContent.read(buf);
        BlockPos pos = buf.readBlockPos();
        boolean back = buf.readBoolean();
        return new FancySignSyncPacket(list, pos, back);
    }

    public static void handle(FancySignSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() == null) return;
            BlockEntity be = ctx.get().getSender().level().getBlockEntity(msg.pos());
            if (!(be instanceof FancySignBlockEntity sign)) return;
            if (msg.back()) {
                sign.setBackContent(msg.list());
            } else {
                sign.setFrontContent(msg.list());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
