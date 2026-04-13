package com.github.minecraftschurlimods.bibliocraft.content.bigbook;

import com.github.minecraftschurlimods.bibliocraft.init.BCItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record BigBookSignPacket(WrittenBigBookContent content, InteractionHand hand) {

    public void encode(FriendlyByteBuf buf) {
        content.write(buf);
        buf.writeBoolean(hand == InteractionHand.MAIN_HAND);
    }

    public static BigBookSignPacket decode(FriendlyByteBuf buf) {
        WrittenBigBookContent content = WrittenBigBookContent.read(buf);
        InteractionHand hand = buf.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        return new BigBookSignPacket(content, hand);
    }

    public static void handle(BigBookSignPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() != null) {
                ItemStack stack = new ItemStack(BCItems.WRITTEN_BIG_BOOK.get());
                WrittenBigBookContent.setOnStack(stack, msg.content());
                ctx.get().getSender().setItemInHand(msg.hand(), stack);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
