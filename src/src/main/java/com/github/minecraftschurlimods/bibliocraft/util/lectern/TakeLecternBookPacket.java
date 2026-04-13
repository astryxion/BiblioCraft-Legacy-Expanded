package com.github.minecraftschurlimods.bibliocraft.util.lectern;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record TakeLecternBookPacket(BlockPos pos) {

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public static TakeLecternBookPacket decode(FriendlyByteBuf buf) {
        return new TakeLecternBookPacket(buf.readBlockPos());
    }

    public static void handle(TakeLecternBookPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() == null) return;
            var player = ctx.get().getSender();
            var level = player.level();
            if (level.getBlockState(msg.pos()).getValue(LecternBlock.HAS_BOOK)) {
                LecternUtil.takeLecternBook(player, level, msg.pos());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
