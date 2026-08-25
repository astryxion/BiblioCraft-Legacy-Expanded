package com.github.minecraftschurlimods.bibliocraft.content.clock;

import com.github.minecraftschurlimods.bibliocraft.util.network.PacketClientDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public final class ClockSyncPacket  {
    private final BlockPos pos;
    private final boolean tickSound;
    private final List<ClockTrigger> triggers;

    public ClockSyncPacket(BlockPos pos, boolean tickSound, List<ClockTrigger> triggers) {
        this.pos = pos;
        this.tickSound = tickSound;
        this.triggers = triggers;
    }

    public BlockPos pos() { return this.pos; }
    public boolean tickSound() { return this.tickSound; }
    public List<ClockTrigger> triggers() { return this.triggers; }

    public void encode(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeBoolean(tickSound);
        buf.writeVarInt(triggers.size());
        for (com.github.minecraftschurlimods.bibliocraft.content.clock.ClockTrigger _e : triggers) { _e.write(buf); }
    }

    public static ClockSyncPacket decode(PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        boolean tickSound = buf.readBoolean();
        int _nTrig = buf.readVarInt();
        List<ClockTrigger> triggers = new java.util.ArrayList<ClockTrigger>(_nTrig);
        for (int _i = 0; _i < _nTrig; _i++) { triggers.add(ClockTrigger.read(buf)); }
        return new ClockSyncPacket(pos, tickSound, triggers);
    }

    @SuppressWarnings("deprecation")
    public static void handle(ClockSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            NetworkEvent.Context context = ctx.get();
            if (context.getSender() != null) {
                // Server: client sent packet, update server BE and broadcast is done in setFromPacket
                World level = context.getSender().level;
                BlockPos pos = msg.pos();
                if (!level.hasChunkAt(pos)) return;
                TileEntity blockEntity = level.getBlockEntity(pos);
                if (!(blockEntity instanceof ClockBlockEntity)) return;
                ClockBlockEntity clock = (ClockBlockEntity) blockEntity;
                clock.setFromPacket(msg);
            } else {
                // Client: server broadcast sync, update client BE so reopening GUI shows triggers (1.21.1 parity)
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PacketClientDispatcher.clockSync(msg));
            }
        });
        ctx.get().setPacketHandled(true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClockSyncPacket other = (ClockSyncPacket) o;
        return java.util.Objects.equals(this.pos, other.pos) && this.tickSound == other.tickSound && java.util.Objects.equals(this.triggers, other.triggers);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.pos, this.tickSound, this.triggers);
    }

    @Override
    public String toString() {
        return "ClockSyncPacket[" + "pos=" + this.pos + ", " + "tickSound=" + this.tickSound + ", " + "triggers=" + this.triggers + "]";
    }

}
