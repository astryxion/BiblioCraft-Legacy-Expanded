package com.github.minecraftschurlimods.bibliocraft.content.clipboard;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public final class ClipboardSyncPacket  {
    private final ClipboardContent content;
    private final Hand hand;

    public ClipboardSyncPacket(ClipboardContent content, Hand hand) {
        this.content = content;
        this.hand = hand;
    }

    public ClipboardContent content() { return this.content; }
    public Hand hand() { return this.hand; }

    public void encode(PacketBuffer buf) {
        content.write(buf);
        buf.writeBoolean(hand == Hand.MAIN_HAND);
    }

    public static ClipboardSyncPacket decode(PacketBuffer buf) {
        ClipboardContent content = ClipboardContent.read(buf);
        Hand hand = buf.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClipboardSyncPacket other = (ClipboardSyncPacket) o;
        return java.util.Objects.equals(this.content, other.content) && java.util.Objects.equals(this.hand, other.hand);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.content, this.hand);
    }

    @Override
    public String toString() {
        return "ClipboardSyncPacket[" + "content=" + this.content + ", " + "hand=" + this.hand + "]";
    }

}
