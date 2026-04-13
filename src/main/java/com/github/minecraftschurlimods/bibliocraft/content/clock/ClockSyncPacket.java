package com.github.minecraftschurlimods.bibliocraft.content.clock;

import com.github.minecraftschurlimods.bibliocraft.util.network.PacketClientDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public record ClockSyncPacket(BlockPos pos, boolean tickSound, List<ClockTrigger> triggers) {

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeBoolean(tickSound);
        buf.writeCollection(triggers, (b, t) -> t.write(b));
    }

    public static ClockSyncPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        boolean tickSound = buf.readBoolean();
        List<ClockTrigger> triggers = buf.readList(ClockTrigger::read);
        return new ClockSyncPacket(pos, tickSound, triggers);
    }

    @SuppressWarnings("deprecation")
    public static void handle(ClockSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            NetworkEvent.Context context = ctx.get();
            if (context.getSender() != null) {
                // Server: client sent packet, update server BE and broadcast is done in setFromPacket
                Level level = context.getSender().level();
                BlockPos pos = msg.pos();
                if (!level.hasChunkAt(pos)) return;
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (!(blockEntity instanceof ClockBlockEntity clock)) return;
                clock.setFromPacket(msg);
            } else {
                // Client: server broadcast sync, update client BE so reopening GUI shows triggers (1.21.1 parity)
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PacketClientDispatcher.clockSync(msg));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
