package com.github.minecraftschurlimods.bibliocraft.content.printingtable;

import com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi;
import com.github.minecraftschurlimods.bibliocraft.util.CodecUtil;
import com.github.minecraftschurlimods.bibliocraft.util.StringRepresentableEnum;
import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;

public enum PrintingTableMode implements StringRepresentableEnum {
    BIND, CLONE, MERGE;
    public static final Codec<PrintingTableMode> CODEC = CodecUtil.enumCodec(PrintingTableMode::values);

    public static void write(FriendlyByteBuf buf, PrintingTableMode mode) {
        buf.writeByte(mode.ordinal());
    }

    public static PrintingTableMode read(FriendlyByteBuf buf) {
        return values()[buf.readByte()];
    }

    public String getTranslationKey() {
        return "gui." + BibliocraftApi.MOD_ID + ".printing_table.mode." + getSerializedName();
    }
}
