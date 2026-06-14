package at.minecraftschurli.mods.bibliocraft.content.typewriter;

import at.minecraftschurli.mods.bibliocraft.init.BCSoundEvents;
import at.minecraftschurli.mods.bibliocraft.util.BCUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public record TypewriterSyncPacket(BlockPos pos, TypewriterPage page, boolean playSound) implements CustomPacketPayload {
    public static final Type<TypewriterSyncPacket> TYPE = new Type<>(BCUtil.bcLoc("typewriter_sync"));
    public static final StreamCodec<ByteBuf, TypewriterSyncPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, TypewriterSyncPacket::pos,
            TypewriterPage.STREAM_CODEC, TypewriterSyncPacket::page,
            ByteBufCodecs.BOOL, TypewriterSyncPacket::playSound,
            TypewriterSyncPacket::new);

    @SuppressWarnings("unused")
    public TypewriterSyncPacket(BlockPos pos, TypewriterPage page) {
        this(pos, page, false);
    }

    public void handle(Player player) {
        if (player.level().getBlockEntity(pos) instanceof TypewriterBlockEntity typewriter) {
            typewriter.setPage(page);
            if (playSound) {
                player.playSound(BCSoundEvents.TYPEWRITER_CHIME.get());
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
