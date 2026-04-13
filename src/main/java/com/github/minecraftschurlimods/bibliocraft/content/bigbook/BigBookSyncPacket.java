package com.github.minecraftschurlimods.bibliocraft.content.bigbook;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record BigBookSyncPacket(BigBookContent content, InteractionHand hand) {

    public void encode(FriendlyByteBuf buf) {
        content.write(buf);
        buf.writeBoolean(hand == InteractionHand.MAIN_HAND);
    }

    public static BigBookSyncPacket decode(FriendlyByteBuf buf) {
        BigBookContent content = BigBookContent.read(buf);
        InteractionHand hand = buf.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        return new BigBookSyncPacket(content, hand);
    }

    public static void handle(BigBookSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() != null) {
                BigBookContent.setOnStack(ctx.get().getSender().getItemInHand(msg.hand()), msg.content());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
