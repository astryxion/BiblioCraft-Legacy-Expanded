package com.github.minecraftschurlimods.bibliocraft.content.tapemeasure;

import com.github.minecraftschurlimods.bibliocraft.util.CodecUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public record StoredPosition(BlockPos position) {
    public static final String NBT_KEY = "StoredPosition";
    public static final Codec<StoredPosition> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            BlockPos.CODEC.fieldOf("position").forGetter(StoredPosition::position)
    ).apply(inst, StoredPosition::new));
    public static final StoredPosition DEFAULT = new StoredPosition(BlockPos.ZERO);

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(position);
    }

    public static StoredPosition read(FriendlyByteBuf buf) {
        return new StoredPosition(buf.readBlockPos());
    }

    public static StoredPosition getFromStack(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(NBT_KEY)) return DEFAULT;
        return CodecUtil.decodeNbt(CODEC, tag.get(NBT_KEY));
    }

    public static void setOnStack(ItemStack stack, StoredPosition pos) {
        stack.getOrCreateTag().put(NBT_KEY, CodecUtil.encodeNbt(CODEC, pos));
    }

    public static void removeFromStack(ItemStack stack) {
        stack.getOrCreateTag().remove(NBT_KEY);
    }
}
