package com.github.minecraftschurlimods.bibliocraft.content.fancysign;

import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public final class FancySignSyncPacket  {
    private final FancySignContent list;
    private final BlockPos pos;
    private final boolean back;

    public FancySignSyncPacket(FancySignContent list, BlockPos pos, boolean back) {
        this.list = list;
        this.pos = pos;
        this.back = back;
    }

    public FancySignContent list() { return this.list; }
    public BlockPos pos() { return this.pos; }
    public boolean back() { return this.back; }

    public void encode(PacketBuffer buf) {
        list.write(buf);
        buf.writeBlockPos(pos);
        buf.writeBoolean(back);
    }

    public static FancySignSyncPacket decode(PacketBuffer buf) {
        FancySignContent list = FancySignContent.read(buf);
        BlockPos pos = buf.readBlockPos();
        boolean back = buf.readBoolean();
        return new FancySignSyncPacket(list, pos, back);
    }

    public static void handle(FancySignSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() == null) return;
            TileEntity be = ctx.get().getSender().level.getBlockEntity(msg.pos());
            if (!(be instanceof FancySignBlockEntity)) return;
            FancySignBlockEntity sign = (FancySignBlockEntity) be;
            if (msg.back()) {
                sign.setBackContent(msg.list());
            } else {
                sign.setFrontContent(msg.list());
            }
        });
        ctx.get().setPacketHandled(true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FancySignSyncPacket other = (FancySignSyncPacket) o;
        return java.util.Objects.equals(this.list, other.list) && java.util.Objects.equals(this.pos, other.pos) && this.back == other.back;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.list, this.pos, this.back);
    }

    @Override
    public String toString() {
        return "FancySignSyncPacket[" + "list=" + this.list + ", " + "pos=" + this.pos + ", " + "back=" + this.back + "]";
    }

}
