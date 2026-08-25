package com.github.minecraftschurlimods.bibliocraft.util.lectern;

import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketBuffer;
import net.minecraft.block.LecternBlock;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public final class TakeLecternBookPacket  {
    private final BlockPos pos;

    public TakeLecternBookPacket(BlockPos pos) {
        this.pos = pos;
    }

    public BlockPos pos() { return this.pos; }

    public void encode(PacketBuffer buf) {
        buf.writeBlockPos(pos);
    }

    public static TakeLecternBookPacket decode(PacketBuffer buf) {
        return new TakeLecternBookPacket(buf.readBlockPos());
    }

    public static void handle(TakeLecternBookPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() == null) return;
            net.minecraft.entity.player.ServerPlayerEntity player = ctx.get().getSender();
            net.minecraft.world.World level = player.level;
            if (level.getBlockState(msg.pos()).getValue(LecternBlock.HAS_BOOK)) {
                LecternUtil.takeLecternBook(player, level, msg.pos());
            }
        });
        ctx.get().setPacketHandled(true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TakeLecternBookPacket other = (TakeLecternBookPacket) o;
        return java.util.Objects.equals(this.pos, other.pos);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.pos);
    }

    @Override
    public String toString() {
        return "TakeLecternBookPacket[" + "pos=" + this.pos + "]";
    }

}
