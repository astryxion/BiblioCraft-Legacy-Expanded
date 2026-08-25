package com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog;

import com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi;
import com.github.minecraftschurlimods.bibliocraft.util.StringRepresentableEnum;
import net.minecraft.network.PacketBuffer;

public interface StockroomCatalogSorting extends StringRepresentableEnum {
    String getTranslationKey();

    enum Item implements StockroomCatalogSorting {
        ALPHABETICAL_ASC, ALPHABETICAL_DESC, COUNT_ASC, COUNT_DESC;

        public static void write(PacketBuffer buf, Item item) { buf.writeByte(item.ordinal()); }
        public static Item read(PacketBuffer buf) { return values()[buf.readByte()]; }

        @Override
        public String getTranslationKey() {
            return "gui." + BibliocraftApi.MOD_ID + ".stockroom_catalog.sorting.item." + getSerializedName();
        }
    }

    enum Container implements StockroomCatalogSorting {
        ALPHABETICAL_ASC, ALPHABETICAL_DESC, DISTANCE_ASC, DISTANCE_DESC;

        public static void write(PacketBuffer buf, Container c) { buf.writeByte(c.ordinal()); }
        public static Container read(PacketBuffer buf) { return values()[buf.readByte()]; }

        @Override
        public String getTranslationKey() {
            return "gui." + BibliocraftApi.MOD_ID + ".stockroom_catalog.sorting.container." + getSerializedName();
        }
    }
}
