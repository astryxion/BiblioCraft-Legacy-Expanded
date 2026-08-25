package com.github.minecraftschurlimods.bibliocraft.content.bigbook;

import com.github.minecraftschurlimods.bibliocraft.util.CodecUtil;
import com.github.minecraftschurlimods.bibliocraft.util.FormattedLine;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.item.ItemStack;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class WrittenBigBookContent  {
    private final List<List<FormattedLine>> pages;
    private final String title;
    private final String author;
    private final int generation;
    private final int currentPage;

    public WrittenBigBookContent(List<List<FormattedLine>> pages, String title, String author, int generation, int currentPage) {
        this.pages = pages;
        this.title = title;
        this.author = author;
        this.generation = generation;
        this.currentPage = currentPage;
    }

    public List<List<FormattedLine>> pages() { return this.pages; }
    public String title() { return this.title; }
    public String author() { return this.author; }
    public int generation() { return this.generation; }
    public int currentPage() { return this.currentPage; }

    public static final int TITLE_MAX_LENGTH = 16;
    public static final int MAX_GENERATION = 2;
    public static final int MAX_CRAFTABLE_GENERATION = 1;
    public static final String NBT_KEY = "WrittenBigBookContent";
    private static final Codec<List<FormattedLine>> PAGE_CODEC = Codec.list(FormattedLine.CODEC).flatXmap(list ->
            list.size() <= 256 ? DataResult.success(list) : DataResult.error("Page too large"),
            list -> DataResult.success(list));
    private static final Codec<String> TITLE_CODEC = Codec.STRING.flatXmap(s ->
            s.length() <= TITLE_MAX_LENGTH ? DataResult.success(s) : DataResult.error("Title too long"),
            s -> DataResult.success(s));
    public static final Codec<WrittenBigBookContent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.list(PAGE_CODEC).fieldOf("pages").forGetter(WrittenBigBookContent::pages),
            TITLE_CODEC.fieldOf("title").forGetter(WrittenBigBookContent::title),
            Codec.STRING.fieldOf("author").forGetter(WrittenBigBookContent::author),
            com.mojang.serialization.Codec.INT.flatXmap(v -> v >= 0 && v <= MAX_GENERATION ? com.mojang.serialization.DataResult.success(v) : com.mojang.serialization.DataResult.error("Value " + v + " outside of range [0;MAX_GENERATION]"), com.mojang.serialization.DataResult::success).optionalFieldOf("generation", 0).forGetter(WrittenBigBookContent::generation),
            com.mojang.serialization.Codec.INT.flatXmap(v -> v >= 0 && v <= 255 ? com.mojang.serialization.DataResult.success(v) : com.mojang.serialization.DataResult.error("Value " + v + " outside of range [0;255]"), com.mojang.serialization.DataResult::success).optionalFieldOf("current_page", 0).forGetter(WrittenBigBookContent::currentPage)
    ).apply(inst, WrittenBigBookContent::new));
    public static final WrittenBigBookContent DEFAULT = new WrittenBigBookContent(java.util.Collections.emptyList(), "", "", 0, 0);

    public void write(PacketBuffer buf) {
        CodecUtil.encodeToBuffer(buf, CODEC, this);
    }

    public static WrittenBigBookContent read(PacketBuffer buf) {
        return CodecUtil.decodeFromBuffer(buf, CODEC);
    }

    public static WrittenBigBookContent getFromStack(ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTag();
        if (!tag.contains(NBT_KEY)) return DEFAULT;
        return CodecUtil.decodeNbt(CODEC, tag.get(NBT_KEY));
    }

    public static void setOnStack(ItemStack stack, WrittenBigBookContent content) {
        stack.getOrCreateTag().put(NBT_KEY, CodecUtil.encodeNbt(CODEC, content));
    }

    @Nullable
    public WrittenBigBookContent tryCraftCopy() {
        return generation >= MAX_CRAFTABLE_GENERATION ? null : new WrittenBigBookContent(new ArrayList<>(pages), title, author, generation + 1, currentPage);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WrittenBigBookContent other = (WrittenBigBookContent) o;
        return java.util.Objects.equals(this.pages, other.pages) && java.util.Objects.equals(this.title, other.title) && java.util.Objects.equals(this.author, other.author) && this.generation == other.generation && this.currentPage == other.currentPage;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.pages, this.title, this.author, this.generation, this.currentPage);
    }

    @Override
    public String toString() {
        return "WrittenBigBookContent[" + "pages=" + this.pages + ", " + "title=" + this.title + ", " + "author=" + this.author + ", " + "generation=" + this.generation + ", " + "currentPage=" + this.currentPage + "]";
    }

}
