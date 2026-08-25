package com.github.minecraftschurlimods.bibliocraft.content.typewriter;

import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.CodecUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class TypewriterPage  {
    private final List<String> lines;
    private final int line;

    public TypewriterPage(List<String> lines, int line) {
        this.lines = lines;
        this.line = line;
    }

    public List<String> lines() { return this.lines; }
    public int line() { return this.line; }

    public static final String NBT_KEY = "TypewriterPage";

    public static TypewriterPage getFromStack(ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTag();
        if (!tag.contains(NBT_KEY)) return DEFAULT;
        return CodecUtil.decodeNbt(CODEC, tag.get(NBT_KEY));
    }

    public static void setOnStack(ItemStack stack, TypewriterPage page) {
        stack.getOrCreateTag().put(NBT_KEY, CodecUtil.encodeNbt(CODEC, page));
    }
    public static final int MAX_LINE_LENGTH = 16;
    public static final int MAX_LINES = 14;
    private static final Codec<String> LINE_CODEC = Codec.STRING.flatXmap(s ->
            s.length() <= MAX_LINE_LENGTH ? DataResult.success(s) : DataResult.error("Line too long"),
            s -> DataResult.success(s));
    private static final Codec<List<String>> LINES_CODEC = Codec.list(LINE_CODEC).flatXmap(list ->
            list.size() == MAX_LINES ? DataResult.success(list) : DataResult.error("Must have " + MAX_LINES + " lines"),
            list -> DataResult.success(list));
    public static final Codec<TypewriterPage> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            LINES_CODEC.fieldOf("lines").forGetter(TypewriterPage::lines),
            com.mojang.serialization.Codec.INT.flatXmap(v -> v >= 0 && v <= MAX_LINES ? com.mojang.serialization.DataResult.success(v) : com.mojang.serialization.DataResult.error("Value " + v + " outside of range [0;MAX_LINES]"), com.mojang.serialization.DataResult::success).optionalFieldOf("line", 0).forGetter(TypewriterPage::line)
    ).apply(inst, TypewriterPage::new));
    public static final TypewriterPage DEFAULT = new TypewriterPage();

    public void write(PacketBuffer buf) {
        buf.writeVarInt(lines.size());
        for (String _e : lines) { buf.writeUtf(_e); }
        buf.writeVarInt(line);
    }

    public static TypewriterPage read(PacketBuffer buf) {
        int _nLines = buf.readVarInt();
        List<String> lines = new java.util.ArrayList<String>(_nLines);
        for (int _i = 0; _i < _nLines; _i++) { lines.add(buf.readUtf()); }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypewriterPage other = (TypewriterPage) o;
        return java.util.Objects.equals(this.lines, other.lines) && this.line == other.line;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.lines, this.line);
    }

    @Override
    public String toString() {
        return "TypewriterPage[" + "lines=" + this.lines + ", " + "line=" + this.line + "]";
    }

}
