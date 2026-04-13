package com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog;

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
import com.github.minecraftschurlimods.bibliocraft.util.BCPackets;
import net.minecraft.world.level.block.entity.LecternBlockEntity;

import java.util.List;

public record StockroomCatalogRequestListPacket(StockroomCatalogSorting.Container containerSorting, StockroomCatalogSorting.Item itemSorting, Either<InteractionHand, BlockPos> target) implements CustomPacketPayload {
    public static final Type<StockroomCatalogRequestListPacket> TYPE = new Type<>(BCUtil.bcLoc("stockroom_catalog_request_list"));
    public static final StreamCodec<ByteBuf, StockroomCatalogRequestListPacket> STREAM_CODEC = StreamCodec.composite(
            StockroomCatalogSorting.Container.STREAM_CODEC, StockroomCatalogRequestListPacket::containerSorting,
            StockroomCatalogSorting.Item.STREAM_CODEC, StockroomCatalogRequestListPacket::itemSorting,
            ByteBufCodecs.either(CodecUtil.INTERACTION_HAND_STREAM_CODEC, BlockPos.STREAM_CODEC), StockroomCatalogRequestListPacket::target,
            StockroomCatalogRequestListPacket::new);

    public void handleServer(ServerPlayer player) {
        ItemStack stack = target.map(player::getItemInHand, pos -> LecternUtil.tryGetLecternAndApply(player.level(), pos, LecternBlockEntity::getBook));
        List<BlockPos> containers = StockroomCatalogItem.calculatePositions(stack, player.level(), player, containerSorting);
        List<StockroomCatalogItemEntry> items = StockroomCatalogItem.calculateItems(containers, player.level(), itemSorting);
        BCPackets.sendToPlayer(player, new StockroomCatalogListPacket(containers, items));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
