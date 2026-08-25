package com.github.minecraftschurlimods.bibliocraft.content.fancysign;

import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.CodecUtil;
import com.github.minecraftschurlimods.bibliocraft.util.FormattedLine;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketBuffer;

import java.util.ArrayList;
import java.util.List;

public final class FancySignContent  {
    private final List<FormattedLine> lines;

    public FancySignContent(List<FormattedLine> lines) {
        this.lines = lines;
    }

    public List<FormattedLine> lines() { return this.lines; }

    public static final Codec<FancySignContent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            FormattedLine.CODEC.listOf().fieldOf("lines").forGetter(FancySignContent::lines)
    ).apply(inst, FancySignContent::new));

    public void write(PacketBuffer buf) {
        CodecUtil.encodeToBuffer(buf, CODEC, this);
    }

    public static FancySignContent read(PacketBuffer buf) {
        return CodecUtil.decodeFromBuffer(buf, CODEC);
    }

    public static FancySignContent withSize(int size) {
        return new FancySignContent(BCUtil.extend(new ArrayList<>(), size, FormattedLine.DEFAULT));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FancySignContent other = (FancySignContent) o;
        return java.util.Objects.equals(this.lines, other.lines);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.lines);
    }

    @Override
    public String toString() {
        return "FancySignContent[" + "lines=" + this.lines + "]";
    }

}
