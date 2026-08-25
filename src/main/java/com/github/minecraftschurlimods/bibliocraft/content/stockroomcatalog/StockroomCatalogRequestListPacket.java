package com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog;

import com.github.minecraftschurlimods.bibliocraft.util.lectern.LecternUtil;
import com.mojang.datafixers.util.Either;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.LecternTileEntity;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.List;
import java.util.function.Supplier;

public final class StockroomCatalogRequestListPacket  {
    private final StockroomCatalogSorting.Container containerSorting;
    private final StockroomCatalogSorting.Item itemSorting;
    private final Either<Hand, BlockPos> target;

    public StockroomCatalogRequestListPacket(StockroomCatalogSorting.Container containerSorting, StockroomCatalogSorting.Item itemSorting, Either<Hand, BlockPos> target) {
        this.containerSorting = containerSorting;
        this.itemSorting = itemSorting;
        this.target = target;
    }

    public StockroomCatalogSorting.Container containerSorting() { return this.containerSorting; }
    public StockroomCatalogSorting.Item itemSorting() { return this.itemSorting; }
    public Either<Hand, BlockPos> target() { return this.target; }

    public void encode(PacketBuffer buf) {
        StockroomCatalogSorting.Container.write(buf, containerSorting);
        StockroomCatalogSorting.Item.write(buf, itemSorting);
        buf.writeBoolean(target.left().isPresent());
        if (target.left().isPresent()) {
            buf.writeBoolean(target.left().get() == Hand.MAIN_HAND);
        } else {
            buf.writeBlockPos(target.right().get());
        }
    }

    public static StockroomCatalogRequestListPacket decode(PacketBuffer buf) {
        StockroomCatalogSorting.Container containerSorting = StockroomCatalogSorting.Container.read(buf);
        StockroomCatalogSorting.Item itemSorting = StockroomCatalogSorting.Item.read(buf);
        Either<Hand, BlockPos> target = buf.readBoolean()
                ? Either.left(buf.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND)
                : Either.right(buf.readBlockPos());
        return new StockroomCatalogRequestListPacket(containerSorting, itemSorting, target);
    }

    public static void handle(StockroomCatalogRequestListPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() == null) return;
            PlayerEntity player = ctx.get().getSender();
            ItemStack stack = msg.target().map(player::getItemInHand, pos -> LecternUtil.tryGetLecternAndApply(player.level, pos, LecternTileEntity::getBook));
            List<BlockPos> containers = StockroomCatalogItem.calculatePositions(stack, player.level, player, msg.containerSorting());
            List<StockroomCatalogItemEntry> items = StockroomCatalogItem.calculateItems(containers, player.level, msg.itemSorting());
            com.github.minecraftschurlimods.bibliocraft.BCEventHandler.getChannel().send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new StockroomCatalogListPacket(containers, items));
        });
        ctx.get().setPacketHandled(true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockroomCatalogRequestListPacket other = (StockroomCatalogRequestListPacket) o;
        return java.util.Objects.equals(this.containerSorting, other.containerSorting) && java.util.Objects.equals(this.itemSorting, other.itemSorting) && java.util.Objects.equals(this.target, other.target);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.containerSorting, this.itemSorting, this.target);
    }

    @Override
    public String toString() {
        return "StockroomCatalogRequestListPacket[" + "containerSorting=" + this.containerSorting + ", " + "itemSorting=" + this.itemSorting + ", " + "target=" + this.target + "]";
    }

}
