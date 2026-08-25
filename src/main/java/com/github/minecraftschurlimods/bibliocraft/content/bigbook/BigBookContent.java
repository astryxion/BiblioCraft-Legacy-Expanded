package com.github.minecraftschurlimods.bibliocraft.content.bigbook;

import com.github.minecraftschurlimods.bibliocraft.util.CodecUtil;
import com.github.minecraftschurlimods.bibliocraft.util.FormattedLine;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.item.ItemStack;

import java.util.List;

public final class BigBookContent  {
    private final List<List<FormattedLine>> pages;
    private final int currentPage;

    public BigBookContent(List<List<FormattedLine>> pages, int currentPage) {
        this.pages = pages;
        this.currentPage = currentPage;
    }

    public List<List<FormattedLine>> pages() { return this.pages; }
    public int currentPage() { return this.currentPage; }

    public static final String NBT_KEY = "BigBookContent";
    private static final Codec<List<FormattedLine>> PAGE_CODEC = Codec.list(FormattedLine.CODEC).flatXmap(list ->
            list.size() <= 256 ? DataResult.success(list) : DataResult.error("Page too large"),
            list -> DataResult.success(list));
    public static final Codec<BigBookContent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.list(PAGE_CODEC).fieldOf("pages").forGetter(BigBookContent::pages),
            com.mojang.serialization.Codec.INT.flatXmap(v -> v >= 0 && v <= 255 ? com.mojang.serialization.DataResult.success(v) : com.mojang.serialization.DataResult.error("Value " + v + " outside of range [0;255]"), com.mojang.serialization.DataResult::success).optionalFieldOf("current_page", 0).forGetter(BigBookContent::currentPage)
    ).apply(inst, BigBookContent::new));
    public static final BigBookContent DEFAULT = new BigBookContent(java.util.Collections.emptyList(), 0);

    public void write(PacketBuffer buf) {
        CodecUtil.encodeToBuffer(buf, CODEC, this);
    }

    public static BigBookContent read(PacketBuffer buf) {
        return CodecUtil.decodeFromBuffer(buf, CODEC);
    }

    public static BigBookContent getFromStack(ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTag();
        if (!tag.contains(NBT_KEY)) return DEFAULT;
        return CodecUtil.decodeNbt(CODEC, tag.get(NBT_KEY));
    }

    public static void setOnStack(ItemStack stack, BigBookContent content) {
        stack.getOrCreateTag().put(NBT_KEY, CodecUtil.encodeNbt(CODEC, content));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BigBookContent other = (BigBookContent) o;
        return java.util.Objects.equals(this.pages, other.pages) && this.currentPage == other.currentPage;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.pages, this.currentPage);
    }

    @Override
    public String toString() {
        return "BigBookContent[" + "pages=" + this.pages + ", " + "currentPage=" + this.currentPage + "]";
    }

}
