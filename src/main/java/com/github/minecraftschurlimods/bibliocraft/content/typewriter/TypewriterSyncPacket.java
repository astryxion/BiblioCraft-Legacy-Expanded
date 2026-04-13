package com.github.minecraftschurlimods.bibliocraft.content.typewriter;

import com.github.minecraftschurlimods.bibliocraft.init.BCSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record TypewriterSyncPacket(BlockPos pos, TypewriterPage page, boolean playSound) {

    public TypewriterSyncPacket(BlockPos pos, TypewriterPage page) {
        this(pos, page, false);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        page.write(buf);
        buf.writeBoolean(playSound);
    }

    public static TypewriterSyncPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        TypewriterPage page = TypewriterPage.read(buf);
        boolean playSound = buf.readBoolean();
        return new TypewriterSyncPacket(pos, page, playSound);
    }

    public static void handle(TypewriterSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() == null) return;
            BlockEntity be = ctx.get().getSender().level().getBlockEntity(msg.pos());
            if (be instanceof TypewriterBlockEntity typewriter) {
                typewriter.setPage(msg.page());
                if (msg.playSound()) {
                    ctx.get().getSender().playSound(BCSoundEvents.TYPEWRITER_CHIME.get());
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
