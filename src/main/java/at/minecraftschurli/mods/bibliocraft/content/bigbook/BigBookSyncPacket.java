package at.minecraftschurli.mods.bibliocraft.content.bigbook;

import at.minecraftschurli.mods.bibliocraft.init.BCDataComponents;
import at.minecraftschurli.mods.bibliocraft.util.BCUtil;
import at.minecraftschurli.mods.bibliocraft.util.CodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public record BigBookSyncPacket(BigBookContent content, InteractionHand hand) implements CustomPacketPayload {
    public static final Type<BigBookSyncPacket> TYPE = new Type<>(BCUtil.bcLoc("big_book_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, BigBookSyncPacket> STREAM_CODEC = StreamCodec.composite(
            BigBookContent.STREAM_CODEC, BigBookSyncPacket::content,
            CodecUtil.INTERACTION_HAND_STREAM_CODEC, BigBookSyncPacket::hand,
            BigBookSyncPacket::new);

    public void handle(Player player) {
        player.getItemInHand(hand).set(BCDataComponents.BIG_BOOK_CONTENT.get(), content);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
