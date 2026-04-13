package com.github.minecraftschurlimods.bibliocraft.content.clipboard;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ClipboardSyncPacket(ClipboardContent content, InteractionHand hand) {

    public void encode(FriendlyByteBuf buf) {
        content.write(buf);
        buf.writeBoolean(hand == InteractionHand.MAIN_HAND);
    }

    public static ClipboardSyncPacket decode(FriendlyByteBuf buf) {
        ClipboardContent content = ClipboardContent.read(buf);
        InteractionHand hand = buf.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        return new ClipboardSyncPacket(content, hand);
    }

    public static void handle(ClipboardSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() != null) {
                ClipboardContent.setOnStack(ctx.get().getSender().getItemInHand(msg.hand()), msg.content());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
