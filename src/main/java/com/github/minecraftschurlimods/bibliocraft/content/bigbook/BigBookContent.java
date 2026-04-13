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

import java.util.List;

public record BigBookContent(List<List<FormattedLine>> pages, int currentPage) {
    public static final String NBT_KEY = "BigBookContent";
    private static final Codec<List<FormattedLine>> PAGE_CODEC = Codec.list(FormattedLine.CODEC).flatXmap(list ->
            list.size() <= 256 ? DataResult.success(list) : DataResult.error(() -> "Page too large"),
            list -> DataResult.success(list));
    public static final Codec<BigBookContent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.list(PAGE_CODEC).fieldOf("pages").forGetter(BigBookContent::pages),
            ExtraCodecs.intRange(0, 255).optionalFieldOf("current_page", 0).forGetter(BigBookContent::currentPage)
    ).apply(inst, BigBookContent::new));
    public static final BigBookContent DEFAULT = new BigBookContent(List.of(), 0);

    public void write(FriendlyByteBuf buf) {
        CodecUtil.encodeToBuffer(buf, CODEC, this);
    }

    public static BigBookContent read(FriendlyByteBuf buf) {
        return CodecUtil.decodeFromBuffer(buf, CODEC);
    }

    public static BigBookContent getFromStack(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(NBT_KEY)) return DEFAULT;
        return CodecUtil.decodeNbt(CODEC, tag.get(NBT_KEY));
    }

    public static void setOnStack(ItemStack stack, BigBookContent content) {
        stack.getOrCreateTag().put(NBT_KEY, CodecUtil.encodeNbt(CODEC, content));
    }
}
