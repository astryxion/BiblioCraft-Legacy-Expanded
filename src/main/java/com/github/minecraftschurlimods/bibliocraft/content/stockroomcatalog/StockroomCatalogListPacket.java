package com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog;

import com.github.minecraftschurlimods.bibliocraft.util.network.PacketClientDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public record StockroomCatalogListPacket(List<BlockPos> containers, List<StockroomCatalogItemEntry> items) {

    public void encode(FriendlyByteBuf buf) {
        buf.writeCollection(containers, (b, p) -> b.writeBlockPos(p));
        buf.writeCollection(items, (b, e) -> e.write(b));
    }

    public static StockroomCatalogListPacket decode(FriendlyByteBuf buf) {
        List<BlockPos> containers = buf.readList(FriendlyByteBuf::readBlockPos);
        List<StockroomCatalogItemEntry> items = buf.readList(StockroomCatalogItemEntry::read);
        return new StockroomCatalogListPacket(containers, items);
    }

    public static void handle(StockroomCatalogListPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PacketClientDispatcher.stockroomCatalogList(msg)));
        ctx.get().setPacketHandled(true);
    }
}
