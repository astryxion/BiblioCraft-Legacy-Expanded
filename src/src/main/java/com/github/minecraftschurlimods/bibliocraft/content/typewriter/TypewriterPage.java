package com.github.minecraftschurlimods.bibliocraft.content.typewriter;

import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.CodecUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record TypewriterPage(List<String> lines, int line) {
    public static final String NBT_KEY = "TypewriterPage";

    public static TypewriterPage getFromStack(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(NBT_KEY)) return DEFAULT;
        return CodecUtil.decodeNbt(CODEC, tag.get(NBT_KEY));
    }

    public static void setOnStack(ItemStack stack, TypewriterPage page) {
        stack.getOrCreateTag().put(NBT_KEY, CodecUtil.encodeNbt(CODEC, page));
    }
    public static final int MAX_LINE_LENGTH = 16;
    public static final int MAX_LINES = 14;
    private static final Codec<String> LINE_CODEC = Codec.STRING.flatXmap(s ->
            s.length() <= MAX_LINE_LENGTH ? DataResult.success(s) : DataResult.error(() -> "Line too long"),
            s -> DataResult.success(s));
    private static final Codec<List<String>> LINES_CODEC = Codec.list(LINE_CODEC).flatXmap(list ->
            list.size() == MAX_LINES ? DataResult.success(list) : DataResult.error(() -> "Must have " + MAX_LINES + " lines"),
            list -> DataResult.success(list));
    public static final Codec<TypewriterPage> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            LINES_CODEC.fieldOf("lines").forGetter(TypewriterPage::lines),
            ExtraCodecs.intRange(0, MAX_LINES).optionalFieldOf("line", 0).forGetter(TypewriterPage::line)
    ).apply(inst, TypewriterPage::new));
    public static final TypewriterPage DEFAULT = new TypewriterPage();

    public void write(FriendlyByteBuf buf) {
        buf.writeCollection(lines, FriendlyByteBuf::writeUtf);
        buf.writeVarInt(line);
    }

    public static TypewriterPage read(FriendlyByteBuf buf) {
        List<String> lines = buf.readList(FriendlyByteBuf::readUtf);
        int line = buf.readVarInt();
        return new TypewriterPage(lines, line);
    }

    public TypewriterPage() {
        this(BCUtil.extend(new ArrayList<>(MAX_LINES), MAX_LINES, ""), 0);
    }

    public TypewriterPage copy() {
        return new TypewriterPage(new ArrayList<>(lines), line);
    }

    public TypewriterPage withLine(int line) {
        return new TypewriterPage(lines, line);
    }
}
