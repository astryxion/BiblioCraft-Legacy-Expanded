package com.github.minecraftschurlimods.bibliocraft.content.bigbook;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public final class BigBookSyncPacket  {
    private final BigBookContent content;
    private final Hand hand;

    public BigBookSyncPacket(BigBookContent content, Hand hand) {
        this.content = content;
        this.hand = hand;
    }

    public BigBookContent content() { return this.content; }
    public Hand hand() { return this.hand; }

    public void encode(PacketBuffer buf) {
        content.write(buf);
        buf.writeBoolean(hand == Hand.MAIN_HAND);
    }

    public static BigBookSyncPacket decode(PacketBuffer buf) {
        BigBookContent content = BigBookContent.read(buf);
        Hand hand = buf.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BigBookSyncPacket other = (BigBookSyncPacket) o;
        return java.util.Objects.equals(this.content, other.content) && java.util.Objects.equals(this.hand, other.hand);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.content, this.hand);
    }

    @Override
    public String toString() {
        return "BigBookSyncPacket[" + "content=" + this.content + ", " + "hand=" + this.hand + "]";
    }

}
