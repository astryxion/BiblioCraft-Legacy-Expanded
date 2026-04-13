package com.github.minecraftschurlimods.bibliocraft.content.printingtable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public record PrintingTableSetRecipePacket(BlockPos pos, int duration, int maxDuration, int levelCost) {

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(duration);
        buf.writeInt(maxDuration);
        buf.writeInt(levelCost);
    }

    public static PrintingTableSetRecipePacket decode(FriendlyByteBuf buf) {
        return new PrintingTableSetRecipePacket(buf.readBlockPos(), buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static void handle(PrintingTableSetRecipePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() == null) return;
            Player player = ctx.get().getSender();
            Level level = player.level();
            BlockEntity be = level.getBlockEntity(msg.pos());
            if (!(be instanceof PrintingTableBlockEntity blockEntity)) return;
            if (level.isClientSide()) {
                blockEntity.setFromPacket(msg);
            } else if (player instanceof ServerPlayer serverPlayer) {
                com.github.minecraftschurlimods.bibliocraft.BCEventHandler.getChannel().send(PacketDistributor.PLAYER.with(() -> serverPlayer), new PrintingTableSetRecipePacket(msg.pos(), blockEntity.getDuration(), blockEntity.getMaxDuration(), blockEntity.getLevelCost()));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
