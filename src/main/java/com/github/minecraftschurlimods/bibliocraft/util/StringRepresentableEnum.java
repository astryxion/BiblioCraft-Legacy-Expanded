package com.github.minecraftschurlimods.bibliocraft.util;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

/**
 * Helper interface that automatically implements {@link IStringSerializable#getSerializedName()} using {@link Enum#name()}.
 */
public interface StringRepresentableEnum extends IStringSerializable {
    String name();

    @Override
    default String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
