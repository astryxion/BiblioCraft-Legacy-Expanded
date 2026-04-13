package com.github.minecraftschurlimods.bibliocraft.content.bigbook;

import com.github.minecraftschurlimods.bibliocraft.util.CodecUtil;
import com.github.minecraftschurlimods.bibliocraft.util.FormattedLine;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record WrittenBigBookContent(List<List<FormattedLine>> pages, String title, String author, int generation, int currentPage) {
    public static final int TITLE_MAX_LENGTH = 16;
    public static final int MAX_GENERATION = 2;
    public static final int MAX_CRAFTABLE_GENERATION = 1;
    public static final String NBT_KEY = "WrittenBigBookContent";
    private static final Codec<List<FormattedLine>> PAGE_CODEC = Codec.list(FormattedLine.CODEC).flatXmap(list ->
            list.size() <= 256 ? DataResult.success(list) : DataResult.error(() -> "Page too large"),
            list -> DataResult.success(list));
    private static final Codec<String> TITLE_CODEC = Codec.STRING.flatXmap(s ->
            s.length() <= TITLE_MAX_LENGTH ? DataResult.success(s) : DataResult.error(() -> "Title too long"),
            s -> DataResult.success(s));
    public static final Codec<WrittenBigBookContent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.list(PAGE_CODEC).fieldOf("pages").forGetter(WrittenBigBookContent::pages),
            TITLE_CODEC.fieldOf("title").forGetter(WrittenBigBookContent::title),
            Codec.STRING.fieldOf("author").forGetter(WrittenBigBookContent::author),
            ExtraCodecs.intRange(0, MAX_GENERATION).optionalFieldOf("generation", 0).forGetter(WrittenBigBookContent::generation),
            ExtraCodecs.intRange(0, 255).optionalFieldOf("current_page", 0).forGetter(WrittenBigBookContent::currentPage)
    ).apply(inst, WrittenBigBookContent::new));
    public static final WrittenBigBookContent DEFAULT = new WrittenBigBookContent(List.of(), "", "", 0, 0);

    public void write(FriendlyByteBuf buf) {
        CodecUtil.encodeToBuffer(buf, CODEC, this);
    }

    public static WrittenBigBookContent read(FriendlyByteBuf buf) {
        return CodecUtil.decodeFromBuffer(buf, CODEC);
    }

    public static WrittenBigBookContent getFromStack(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
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
}
