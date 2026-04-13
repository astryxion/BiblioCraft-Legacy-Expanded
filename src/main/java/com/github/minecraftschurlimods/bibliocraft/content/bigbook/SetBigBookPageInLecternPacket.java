package com.github.minecraftschurlimods.bibliocraft.content.bigbook;

import com.github.minecraftschurlimods.bibliocraft.init.BCDataComponents;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.CodecUtil;
import com.github.minecraftschurlimods.bibliocraft.util.lectern.LecternUtil;
import com.mojang.datafixers.util.Either;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public record SetBigBookPageInLecternPacket(int page, Either<InteractionHand, BlockPos> target) implements CustomPacketPayload {
    public static final Type<SetBigBookPageInLecternPacket> TYPE = new Type<>(BCUtil.bcLoc("set_big_book_page_in_lectern"));
    public static final StreamCodec<ByteBuf, SetBigBookPageInLecternPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SetBigBookPageInLecternPacket::page,
            ByteBufCodecs.either(CodecUtil.INTERACTION_HAND_STREAM_CODEC, BlockPos.STREAM_CODEC), SetBigBookPageInLecternPacket::target,
            SetBigBookPageInLecternPacket::new);

    public void handleServer(ServerPlayer player) {
        target.ifLeft(left -> updateStack(player.getItemInHand(left), page));
        target.ifRight(right -> LecternUtil.tryGetLecternAndRun(player.level(), right, lectern -> updateStack(lectern.getBook(), page)));
    }

    private static void updateStack(ItemStack stack, int page) {
        if (stack.has(BCDataComponents.BIG_BOOK_CONTENT.get())) {
            stack.update(
                    BCDataComponents.BIG_BOOK_CONTENT.get(),
                    BigBookContent.DEFAULT,
                    data -> new BigBookContent(new ArrayList<>(data.pages()), Math.clamp(page, 0, Math.max(0, data.pages().size() - 1)))
            );
        }
        if (stack.has(BCDataComponents.WRITTEN_BIG_BOOK_CONTENT.get())) {
            stack.update(
                    BCDataComponents.WRITTEN_BIG_BOOK_CONTENT.get(),
                    WrittenBigBookContent.DEFAULT,
                    data -> new WrittenBigBookContent(new ArrayList<>(data.pages()), data.title(), data.author(), data.generation(), Math.clamp(page, 0, Math.max(0, data.pages().size() - 1)))
            );
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
