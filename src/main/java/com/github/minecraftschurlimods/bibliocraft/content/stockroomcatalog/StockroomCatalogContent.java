package com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog;

import com.github.minecraftschurlimods.bibliocraft.util.CodecUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class StockroomCatalogContent  {
    private final List<GlobalPos> positions;

    public StockroomCatalogContent(List<GlobalPos> positions) {
        this.positions = positions;
    }

    public List<GlobalPos> positions() { return this.positions; }

    public static final String NBT_KEY = "StockroomCatalogContent";
    public static final Codec<StockroomCatalogContent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            GlobalPos.CODEC.listOf().fieldOf("positions").forGetter(StockroomCatalogContent::positions)
    ).apply(inst, StockroomCatalogContent::new));
    public static final StockroomCatalogContent DEFAULT = new StockroomCatalogContent(java.util.Collections.emptyList());

    public void write(PacketBuffer buf) {
        CodecUtil.encodeToBuffer(buf, CODEC, this);
    }

    public static StockroomCatalogContent read(PacketBuffer buf) {
        return CodecUtil.decodeFromBuffer(buf, CODEC);
    }

    public static StockroomCatalogContent getFromStack(ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTag();
        if (!tag.contains(NBT_KEY)) return DEFAULT;
        return CodecUtil.decodeNbt(CODEC, tag.get(NBT_KEY));
    }

    public static void setOnStack(ItemStack stack, StockroomCatalogContent content) {
        stack.getOrCreateTag().put(NBT_KEY, CodecUtil.encodeNbt(CODEC, content));
    }

    public StockroomCatalogContent add(GlobalPos pos) {
        List<GlobalPos> list = new ArrayList<>(positions);
        list.add(pos);
        return new StockroomCatalogContent(list);
    }

    public StockroomCatalogContent remove(GlobalPos pos) {
        List<GlobalPos> list = new ArrayList<>(positions);
        list.remove(pos);
        return new StockroomCatalogContent(list);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockroomCatalogContent other = (StockroomCatalogContent) o;
        return java.util.Objects.equals(this.positions, other.positions);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.positions);
    }

    @Override
    public String toString() {
        return "StockroomCatalogContent[" + "positions=" + this.positions + "]";
    }

}
