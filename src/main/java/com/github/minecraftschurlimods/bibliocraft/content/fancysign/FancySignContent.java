package com.github.minecraftschurlimods.bibliocraft.content.fancysign;

import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.CodecUtil;
import com.github.minecraftschurlimods.bibliocraft.util.FormattedLine;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;

public record FancySignContent(List<FormattedLine> lines) {
    public static final Codec<FancySignContent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            FormattedLine.CODEC.listOf().fieldOf("lines").forGetter(FancySignContent::lines)
    ).apply(inst, FancySignContent::new));

    public void write(FriendlyByteBuf buf) {
        CodecUtil.encodeToBuffer(buf, CODEC, this);
    }

    public static FancySignContent read(FriendlyByteBuf buf) {
        return CodecUtil.decodeFromBuffer(buf, CODEC);
    }

    public static FancySignContent withSize(int size) {
        return new FancySignContent(BCUtil.extend(new ArrayList<>(), size, FormattedLine.DEFAULT));
    }
}
