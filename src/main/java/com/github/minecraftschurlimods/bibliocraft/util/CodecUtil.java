package com.github.minecraftschurlimods.bibliocraft.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IStringSerializable;

import java.util.function.Supplier;

/**
 * Utility class holding various codec-related helper methods.
 */
public final class CodecUtil {
    /**
     * @param valuesSupplier The enum's {@code values()} method.
     * @param <E>            The enum type.
     * @return An enum {@link Codec}.
     */
    public static <E extends Enum<E> & IStringSerializable> Codec<E> enumCodec(Supplier<E[]> valuesSupplier) {
        return IStringSerializable.fromEnum(valuesSupplier, name -> {
            for (E e : valuesSupplier.get()) {
                if (e.getSerializedName().equals(name)) {
                    return e;
                }
            }
            return null;
        });
    }

    /**
     * @param codec A {@link Codec}.
     * @param <T>   The type of the {@link Codec}.
     * @return Encodes the value to JSON and writes as UTF string to the buffer.
     */
    public static <T> void encodeToBuffer(PacketBuffer buffer, Codec<T> codec, T value) {
        buffer.writeUtf(encodeJson(codec, value).toString());
    }

    /**
     * @param codec A {@link Codec}.
     * @param <T>   The type of the {@link Codec}.
     * @return Decodes from UTF string in the buffer using the given codec.
     */
    public static <T> T decodeFromBuffer(PacketBuffer buffer, Codec<T> codec) {
        return decodeJson(codec, new com.google.gson.JsonParser().parse(buffer.readUtf()));
    }

    /**
     * Encodes the given value to NBT using the given {@link Codec}.
     *
     * @param codec The {@link Codec} to use for encoding.
     * @param value The value to encode to NBT.
     * @param <T>   The type of the value and the {@link Codec}.
     * @return The NBT representation of the given value.
     */
    public static <T> INBT encodeNbt(Codec<T> codec, T value) {
        return codec.encodeStart(NBTDynamicOps.INSTANCE, value).result().orElseThrow(() -> new IllegalArgumentException("Failed to encode to NBT"));
    }

    /**
     * Decodes the given value from NBT using the given {@link Codec}.
     *
     * @param codec The {@link Codec} to use for decoding.
     * @param tag   The NBT representation to decode.
     * @param <T>   The type of the value and the {@link Codec}.
     * @return The decoded value.
     */
    public static <T> T decodeNbt(Codec<T> codec, INBT tag) {
        return codec.decode(NBTDynamicOps.INSTANCE, tag).result().orElseThrow(() -> new IllegalArgumentException("Failed to decode from NBT")).getFirst();
    }

    /**
     * Encodes the given value to JSON using the given {@link Codec}.
     *
     * @param codec The {@link Codec} to use for encoding.
     * @param value The value to encode to NBT.
     * @param <T>   The type of the value and the {@link Codec}.
     * @return The JSON representation of the given value.
     */
    public static <T> JsonElement encodeJson(Codec<T> codec, T value) {
        return codec.encodeStart(JsonOps.INSTANCE, value).result().orElseThrow(() -> new IllegalArgumentException("Failed to encode to JSON"));
    }

    /**
     * Decodes the given value from JSON using the given {@link Codec}.
     *
     * @param codec The {@link Codec} to use for decoding.
     * @param json  The JSON representation to decode.
     * @param <T>   The type of the value and the {@link Codec}.
     * @return The decoded value.
     */
    public static <T> T decodeJson(Codec<T> codec, JsonElement json) {
        return codec.decode(JsonOps.INSTANCE, json).result().orElseThrow(() -> new IllegalArgumentException("Failed to decode from JSON")).getFirst();
    }
}
