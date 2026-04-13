package com.github.minecraftschurlimods.bibliocraft.content.clipboard;

import com.github.minecraftschurlimods.bibliocraft.util.CodecUtil;
import com.github.minecraftschurlimods.bibliocraft.util.StringRepresentableEnum;
import com.mojang.serialization.Codec;

public enum CheckboxState implements StringRepresentableEnum {
    EMPTY, CHECK, X;
    public static final Codec<CheckboxState> CODEC = CodecUtil.enumCodec(CheckboxState::values);
}
