package com.github.minecraftschurlimods.bibliocraft.content.typewriter;

import com.github.minecraftschurlimods.bibliocraft.init.BCSoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public final class TypewriterSyncPacket  {
    private final BlockPos pos;
    private final TypewriterPage page;
    private final boolean playSound;

    public TypewriterSyncPacket(BlockPos pos, TypewriterPage page, boolean playSound) {
        this.pos = pos;
        this.page = page;
        this.playSound = playSound;
    }

    public BlockPos pos() { return this.pos; }
    public TypewriterPage page() { return this.page; }
    public boolean playSound() { return this.playSound; }

    public TypewriterSyncPacket(BlockPos pos, TypewriterPage page) {
        this(pos, page, false);
    }

    public void encode(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        page.write(buf);
        buf.writeBoolean(playSound);
    }

    public static TypewriterSyncPacket decode(PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        TypewriterPage page = TypewriterPage.read(buf);
        boolean playSound = buf.readBoolean();
        return new TypewriterSyncPacket(pos, page, playSound);
    }

    public static void handle(TypewriterSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() == null) return;
            TileEntity be = ctx.get().getSender().level.getBlockEntity(msg.pos());
            if (be instanceof TypewriterBlockEntity) {
                TypewriterBlockEntity typewriter = (TypewriterBlockEntity) be;
                typewriter.setPage(msg.page());
                if (msg.playSound()) {
                    ctx.get().getSender().playSound(BCSoundEvents.TYPEWRITER_CHIME.get(), 1f, 1f);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypewriterSyncPacket other = (TypewriterSyncPacket) o;
        return java.util.Objects.equals(this.pos, other.pos) && java.util.Objects.equals(this.page, other.page) && this.playSound == other.playSound;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.pos, this.page, this.playSound);
    }

    @Override
    public String toString() {
        return "TypewriterSyncPacket[" + "pos=" + this.pos + ", " + "page=" + this.page + ", " + "playSound=" + this.playSound + "]";
    }

}
