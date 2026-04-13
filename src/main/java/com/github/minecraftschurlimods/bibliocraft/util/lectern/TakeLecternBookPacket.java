package com.github.minecraftschurlimods.bibliocraft.util.lectern;

import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.LecternBlock;

public record TakeLecternBookPacket(BlockPos pos) implements CustomPacketPayload {
    public static final Type<TakeLecternBookPacket> TYPE = new Type<>(BCUtil.bcLoc("take_lectern_book"));
    public static final StreamCodec<ByteBuf, TakeLecternBookPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, TakeLecternBookPacket::pos,
            TakeLecternBookPacket::new);

    public void handleServer(ServerPlayer player) {
        Level level = player.level();
        if (level.getBlockState(pos).getValue(LecternBlock.HAS_BOOK)) {
            LecternUtil.takeLecternBook(player, level, pos);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
