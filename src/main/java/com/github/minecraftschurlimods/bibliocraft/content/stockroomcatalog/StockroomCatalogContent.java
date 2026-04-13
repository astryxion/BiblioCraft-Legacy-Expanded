package com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog;

import com.github.minecraftschurlimods.bibliocraft.util.CodecUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record StockroomCatalogContent(List<GlobalPos> positions) {
    public static final String NBT_KEY = "StockroomCatalogContent";
    public static final Codec<StockroomCatalogContent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            GlobalPos.CODEC.listOf().fieldOf("positions").forGetter(StockroomCatalogContent::positions)
    ).apply(inst, StockroomCatalogContent::new));
    public static final StockroomCatalogContent DEFAULT = new StockroomCatalogContent(List.of());

    public void write(FriendlyByteBuf buf) {
        CodecUtil.encodeToBuffer(buf, CODEC, this);
    }

    public static StockroomCatalogContent read(FriendlyByteBuf buf) {
        return CodecUtil.decodeFromBuffer(buf, CODEC);
    }

    public static StockroomCatalogContent getFromStack(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(NBT_KEY)) return DEFAULT;
        return CodecUtil.decodeNbt(CODEC, tag.get(NBT_KEY));
    }

    public static void setOnStack(ItemStack stack, StockroomCatalogContent content) {
        stack.getOrCreateTag().put(NBT_KEY, CodecUtil.encodeNbt(CODEC, content));
    }

    public StockroomCatalogContent add(GlobalPos pos) {
        List<GlobalPos> list = new ArrayList<>(positions);
        list.add(pos);
        return new StockroomCatalogContent(list);
    }

    public StockroomCatalogContent remove(GlobalPos pos) {
        List<GlobalPos> list = new ArrayList<>(positions);
        list.remove(pos);
        return new StockroomCatalogContent(list);
    }
}
