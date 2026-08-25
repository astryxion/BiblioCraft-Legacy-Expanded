package com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketBuffer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class StockroomCatalogItemEntry  {
    private final ItemStack item;
    private final int count;
    private final List<BlockPos> containers;

    public StockroomCatalogItemEntry(ItemStack item, int count, List<BlockPos> containers) {
        this.item = item;
        this.count = count;
        this.containers = containers;
    }

    public ItemStack item() { return this.item; }
    public int count() { return this.count; }
    public List<BlockPos> containers() { return this.containers; }

    public static final Codec<StockroomCatalogItemEntry> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ItemStack.CODEC.fieldOf("item").forGetter(StockroomCatalogItemEntry::item),
            Codec.INT.fieldOf("count").forGetter(StockroomCatalogItemEntry::count),
            BlockPos.CODEC.listOf().fieldOf("containers").forGetter(StockroomCatalogItemEntry::containers)
    ).apply(inst, StockroomCatalogItemEntry::new));

    public void write(PacketBuffer buf) {
        buf.writeItem(item);
        buf.writeInt(count);
        buf.writeVarInt(containers.size());
        for (net.minecraft.util.math.BlockPos _e : containers) { buf.writeBlockPos(_e); }
    }

    public static StockroomCatalogItemEntry read(PacketBuffer buf) {
        ItemStack item = buf.readItem();
        int count = buf.readInt();
        int n = buf.readVarInt();
        List<BlockPos> containers = new ArrayList<BlockPos>(n);
        for (int i = 0; i < n; i++) {
            containers.add(buf.readBlockPos());
        }
        return new StockroomCatalogItemEntry(item, count, containers);
    }

    public StockroomCatalogItemEntry(ItemStack item, List<BlockPos> containers) {
        this(item.copy(), item.getCount(), containers);
        this.item.setCount(1);
    }

    public StockroomCatalogItemEntry(ItemStack item) {
        this(item, java.util.Collections.emptyList());
    }

    public StockroomCatalogItemEntry add(int count) {
        return new StockroomCatalogItemEntry(item, this.count + count, new ArrayList<>(containers));
    }

    public StockroomCatalogItemEntry add(BlockPos container) {
        List<BlockPos> list = new ArrayList<>(containers);
        list.add(container);
        return new StockroomCatalogItemEntry(item, count, list);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockroomCatalogItemEntry other = (StockroomCatalogItemEntry) o;
        return java.util.Objects.equals(this.item, other.item) && this.count == other.count && java.util.Objects.equals(this.containers, other.containers);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.item, this.count, this.containers);
    }

    @Override
    public String toString() {
        return "StockroomCatalogItemEntry[" + "item=" + this.item + ", " + "count=" + this.count + ", " + "containers=" + this.containers + "]";
    }

}
