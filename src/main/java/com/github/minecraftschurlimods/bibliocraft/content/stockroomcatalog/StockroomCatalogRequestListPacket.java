package com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog;

import com.github.minecraftschurlimods.bibliocraft.util.lectern.LecternUtil;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;
import java.util.function.Supplier;

public record StockroomCatalogRequestListPacket(StockroomCatalogSorting.Container containerSorting, StockroomCatalogSorting.Item itemSorting, Either<InteractionHand, BlockPos> target) {

    public void encode(FriendlyByteBuf buf) {
        StockroomCatalogSorting.Container.write(buf, containerSorting);
        StockroomCatalogSorting.Item.write(buf, itemSorting);
        buf.writeBoolean(target.left().isPresent());
        if (target.left().isPresent()) {
            buf.writeBoolean(target.left().get() == InteractionHand.MAIN_HAND);
        } else {
            buf.writeBlockPos(target.right().get());
        }
    }

    public static StockroomCatalogRequestListPacket decode(FriendlyByteBuf buf) {
        StockroomCatalogSorting.Container containerSorting = StockroomCatalogSorting.Container.read(buf);
        StockroomCatalogSorting.Item itemSorting = StockroomCatalogSorting.Item.read(buf);
        Either<InteractionHand, BlockPos> target = buf.readBoolean()
                ? Either.left(buf.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND)
                : Either.right(buf.readBlockPos());
        return new StockroomCatalogRequestListPacket(containerSorting, itemSorting, target);
    }

    public static void handle(StockroomCatalogRequestListPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() == null) return;
            Player player = ctx.get().getSender();
            ItemStack stack = msg.target().map(player::getItemInHand, pos -> LecternUtil.tryGetLecternAndApply(player.level(), pos, LecternBlockEntity::getBook));
            List<BlockPos> containers = StockroomCatalogItem.calculatePositions(stack, player.level(), player, msg.containerSorting());
            List<StockroomCatalogItemEntry> items = StockroomCatalogItem.calculateItems(containers, player.level(), msg.itemSorting());
            com.github.minecraftschurlimods.bibliocraft.BCEventHandler.getChannel().send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new StockroomCatalogListPacket(containers, items));
        });
        ctx.get().setPacketHandled(true);
    }
}
