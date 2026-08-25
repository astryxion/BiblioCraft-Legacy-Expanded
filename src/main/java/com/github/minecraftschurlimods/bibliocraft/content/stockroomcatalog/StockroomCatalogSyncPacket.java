package com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog;

import com.github.minecraftschurlimods.bibliocraft.util.lectern.LecternUtil;
import com.mojang.datafixers.util.Either;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public final class StockroomCatalogSyncPacket  {
    private final StockroomCatalogContent content;
    private final Either<Hand, BlockPos> target;

    public StockroomCatalogSyncPacket(StockroomCatalogContent content, Either<Hand, BlockPos> target) {
        this.content = content;
        this.target = target;
    }

    public StockroomCatalogContent content() { return this.content; }
    public Either<Hand, BlockPos> target() { return this.target; }

    public void encode(PacketBuffer buf) {
        content.write(buf);
        buf.writeBoolean(target.left().isPresent());
        if (target.left().isPresent()) {
            buf.writeBoolean(target.left().get() == Hand.MAIN_HAND);
        } else {
            buf.writeBlockPos(target.right().get());
        }
    }

    public static StockroomCatalogSyncPacket decode(PacketBuffer buf) {
        StockroomCatalogContent content = StockroomCatalogContent.read(buf);
        Either<Hand, BlockPos> target = buf.readBoolean()
                ? Either.left(buf.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND)
                : Either.right(buf.readBlockPos());
        return new StockroomCatalogSyncPacket(content, target);
    }

    public static void handle(StockroomCatalogSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() == null) return;
            PlayerEntity player = ctx.get().getSender();
            msg.target().ifLeft(hand -> StockroomCatalogContent.setOnStack(player.getItemInHand(hand), msg.content()));
            msg.target().ifRight(pos -> LecternUtil.tryGetLecternAndRun(player.level, pos, lectern -> StockroomCatalogContent.setOnStack(lectern.getBook(), msg.content())));
        });
        ctx.get().setPacketHandled(true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockroomCatalogSyncPacket other = (StockroomCatalogSyncPacket) o;
        return java.util.Objects.equals(this.content, other.content) && java.util.Objects.equals(this.target, other.target);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.content, this.target);
    }

    @Override
    public String toString() {
        return "StockroomCatalogSyncPacket[" + "content=" + this.content + ", " + "target=" + this.target + "]";
    }

}
