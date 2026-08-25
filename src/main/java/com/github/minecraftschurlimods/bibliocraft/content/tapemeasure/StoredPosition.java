package com.github.minecraftschurlimods.bibliocraft.content.tapemeasure;

import com.github.minecraftschurlimods.bibliocraft.util.CodecUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.item.ItemStack;

public final class StoredPosition  {
    private final BlockPos position;

    public StoredPosition(BlockPos position) {
        this.position = position;
    }

    public BlockPos position() { return this.position; }

    public static final String NBT_KEY = "StoredPosition";
    public static final Codec<StoredPosition> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            BlockPos.CODEC.fieldOf("position").forGetter(StoredPosition::position)
    ).apply(inst, StoredPosition::new));
    public static final StoredPosition DEFAULT = new StoredPosition(BlockPos.ZERO);

    public void write(PacketBuffer buf) {
        buf.writeBlockPos(position);
    }

    public static StoredPosition read(PacketBuffer buf) {
        return new StoredPosition(buf.readBlockPos());
    }

    public static StoredPosition getFromStack(ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTag();
        if (!tag.contains(NBT_KEY)) return DEFAULT;
        return CodecUtil.decodeNbt(CODEC, tag.get(NBT_KEY));
    }

    public static void setOnStack(ItemStack stack, StoredPosition pos) {
        stack.getOrCreateTag().put(NBT_KEY, CodecUtil.encodeNbt(CODEC, pos));
    }

    public static void removeFromStack(ItemStack stack) {
        stack.getOrCreateTag().remove(NBT_KEY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoredPosition other = (StoredPosition) o;
        return java.util.Objects.equals(this.position, other.position);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.position);
    }

    @Override
    public String toString() {
        return "StoredPosition[" + "position=" + this.position + "]";
    }

}
