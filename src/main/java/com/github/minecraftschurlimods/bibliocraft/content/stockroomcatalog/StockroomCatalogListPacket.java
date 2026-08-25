package com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog;

import com.github.minecraftschurlimods.bibliocraft.util.network.PacketClientDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public final class StockroomCatalogListPacket  {
    private final List<BlockPos> containers;
    private final List<StockroomCatalogItemEntry> items;

    public StockroomCatalogListPacket(List<BlockPos> containers, List<StockroomCatalogItemEntry> items) {
        this.containers = containers;
        this.items = items;
    }

    public List<BlockPos> containers() { return this.containers; }
    public List<StockroomCatalogItemEntry> items() { return this.items; }

    public void encode(PacketBuffer buf) {
        buf.writeVarInt(containers.size());
        for (net.minecraft.util.math.BlockPos _e : containers) { buf.writeBlockPos(_e); }
        buf.writeVarInt(items.size());
        for (com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog.StockroomCatalogItemEntry _e : items) { _e.write(buf); }
    }

    public static StockroomCatalogListPacket decode(PacketBuffer buf) {
        int _nPos = buf.readVarInt();
        List<BlockPos> containers = new java.util.ArrayList<BlockPos>(_nPos);
        for (int _i = 0; _i < _nPos; _i++) { containers.add(buf.readBlockPos()); }
        int _nItems = buf.readVarInt();
        List<StockroomCatalogItemEntry> items = new java.util.ArrayList<StockroomCatalogItemEntry>(_nItems);
        for (int _i = 0; _i < _nItems; _i++) { items.add(StockroomCatalogItemEntry.read(buf)); }
        return new StockroomCatalogListPacket(containers, items);
    }

    public static void handle(StockroomCatalogListPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PacketClientDispatcher.stockroomCatalogList(msg)));
        ctx.get().setPacketHandled(true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockroomCatalogListPacket other = (StockroomCatalogListPacket) o;
        return java.util.Objects.equals(this.containers, other.containers) && java.util.Objects.equals(this.items, other.items);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.containers, this.items);
    }

    @Override
    public String toString() {
        return "StockroomCatalogListPacket[" + "containers=" + this.containers + ", " + "items=" + this.items + "]";
    }

}
