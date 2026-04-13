package com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog;

import com.github.minecraftschurlimods.bibliocraft.util.lectern.LecternUtil;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record StockroomCatalogSyncPacket(StockroomCatalogContent content, Either<InteractionHand, BlockPos> target) {

    public void encode(FriendlyByteBuf buf) {
        content.write(buf);
        buf.writeBoolean(target.left().isPresent());
        if (target.left().isPresent()) {
            buf.writeBoolean(target.left().get() == InteractionHand.MAIN_HAND);
        } else {
            buf.writeBlockPos(target.right().get());
        }
    }

    public static StockroomCatalogSyncPacket decode(FriendlyByteBuf buf) {
        StockroomCatalogContent content = StockroomCatalogContent.read(buf);
        Either<InteractionHand, BlockPos> target = buf.readBoolean()
                ? Either.left(buf.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND)
                : Either.right(buf.readBlockPos());
        return new StockroomCatalogSyncPacket(content, target);
    }

    public static void handle(StockroomCatalogSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() == null) return;
            Player player = ctx.get().getSender();
            msg.target().ifLeft(hand -> StockroomCatalogContent.setOnStack(player.getItemInHand(hand), msg.content()));
            msg.target().ifRight(pos -> LecternUtil.tryGetLecternAndRun(player.level(), pos, lectern -> StockroomCatalogContent.setOnStack(lectern.getBook(), msg.content())));
        });
        ctx.get().setPacketHandled(true);
    }
}
