package com.github.minecraftschurlimods.bibliocraft.init;

import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import net.minecraft.util.registry.Registry;
import net.minecraft.tags.ITag;
import net.minecraft.item.Item;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.block.Block;

public interface BCTags {
    interface Blocks {
        // @formatter:off
        ITag.INamedTag<Block> BOOKCASES               = tag("bookcases");
        ITag.INamedTag<Block> DISPLAY_CASES           = tag("display_cases");
        ITag.INamedTag<Block> FANCY_ARMOR_STANDS_WOOD = tag("fancy_armor_stands/wood");
        ITag.INamedTag<Block> FANCY_ARMOR_STANDS      = tag("fancy_armor_stands");
        ITag.INamedTag<Block> FANCY_CLOCKS            = tag("fancy_clocks");
        ITag.INamedTag<Block> FANCY_CRAFTERS          = tag("fancy_crafters");
        ITag.INamedTag<Block> FANCY_SIGNS             = tag("fancy_signs");
        ITag.INamedTag<Block> GRANDFATHER_CLOCKS      = tag("grandfather_clocks");
        ITag.INamedTag<Block> LABELS                  = tag("labels");
        ITag.INamedTag<Block> POTION_SHELVES          = tag("potion_shelves");
        ITag.INamedTag<Block> PRINTING_TABLES         = tag("printing_tables");
        ITag.INamedTag<Block> SEATS                   = tag("seats");
        ITag.INamedTag<Block> SEAT_BACKS              = tag("seat_backs");
        ITag.INamedTag<Block> SHELVES                 = tag("shelves");
        ITag.INamedTag<Block> TABLES                  = tag("tables");
        ITag.INamedTag<Block> TOOL_RACKS              = tag("tool_racks");
        ITag.INamedTag<Block> FANCY_LAMPS             = tag("fancy_lamps");
        ITag.INamedTag<Block> FANCY_LAMPS_GOLD        = tag("fancy_lamps/gold");
        ITag.INamedTag<Block> FANCY_LAMPS_IRON        = tag("fancy_lamps/iron");
        ITag.INamedTag<Block> FANCY_LANTERNS          = tag("fancy_lanterns");
        ITag.INamedTag<Block> FANCY_LANTERNS_GOLD     = tag("fancy_lanterns/gold");
        ITag.INamedTag<Block> FANCY_LANTERNS_IRON     = tag("fancy_lanterns/iron");
        ITag.INamedTag<Block> TYPEWRITERS             = tag("typewriters");
        // @formatter:on

        /**
         * @param name The path of the tag.
         * @return A {@link ITag.INamedTag<Block>} with this mod's namespace and the given path.
         */
        static ITag.INamedTag<Block> tag(String name) {
            return net.minecraft.tags.BlockTags.createOptional(BCUtil.bcLoc(name));
        }
    }

    interface Items {
        // @formatter:off
        ITag.INamedTag<Item> BOOKCASES               = tag("bookcases");
        ITag.INamedTag<Item> DISPLAY_CASES           = tag("display_cases");
        ITag.INamedTag<Item> FANCY_ARMOR_STANDS_WOOD = tag("fancy_armor_stands/wood");
        ITag.INamedTag<Item> FANCY_ARMOR_STANDS      = tag("fancy_armor_stands");
        ITag.INamedTag<Item> FANCY_CLOCKS            = tag("fancy_clocks");
        ITag.INamedTag<Item> FANCY_CRAFTERS          = tag("fancy_crafters");
        ITag.INamedTag<Item> FANCY_SIGNS             = tag("fancy_signs");
        ITag.INamedTag<Item> GRANDFATHER_CLOCKS      = tag("grandfather_clocks");
        ITag.INamedTag<Item> LABELS                  = tag("labels");
        ITag.INamedTag<Item> POTION_SHELVES          = tag("potion_shelves");
        ITag.INamedTag<Item> PRINTING_TABLES         = tag("printing_tables");
        ITag.INamedTag<Item> SEATS                   = tag("seats");
        ITag.INamedTag<Item> SEAT_BACKS_SMALL        = tag("seat_backs/small");
        ITag.INamedTag<Item> SEAT_BACKS_RAISED       = tag("seat_backs/raised");
        ITag.INamedTag<Item> SEAT_BACKS_FLAT         = tag("seat_backs/flat");
        ITag.INamedTag<Item> SEAT_BACKS_TALL         = tag("seat_backs/tall");
        ITag.INamedTag<Item> SEAT_BACKS_FANCY        = tag("seat_backs/fancy");
        ITag.INamedTag<Item> SEAT_BACKS              = tag("seat_backs");
        ITag.INamedTag<Item> SHELVES                 = tag("shelves");
        ITag.INamedTag<Item> TABLES                  = tag("tables");
        ITag.INamedTag<Item> TOOL_RACKS              = tag("tool_racks");
        ITag.INamedTag<Item> FANCY_LAMPS             = tag("fancy_lamps");
        ITag.INamedTag<Item> FANCY_LAMPS_GOLD        = tag("fancy_lamps/gold");
        ITag.INamedTag<Item> FANCY_LAMPS_IRON        = tag("fancy_lamps/iron");
        ITag.INamedTag<Item> FANCY_LANTERNS          = tag("fancy_lanterns");
        ITag.INamedTag<Item> FANCY_LANTERNS_GOLD     = tag("fancy_lanterns/gold");
        ITag.INamedTag<Item> FANCY_LANTERNS_IRON     = tag("fancy_lanterns/iron");
        ITag.INamedTag<Item> TYPEWRITERS             = tag("typewriters");
        ITag.INamedTag<Item> BOOKCASE_BOOKS          = tag("bookcase_books");
        ITag.INamedTag<Item> COOKIE_JAR_COOKIES      = tag("cookie_jar_cookies");
        ITag.INamedTag<Item> DISC_RACK_DISCS         = tag("disc_rack_discs");
        ITag.INamedTag<Item> FANCY_SIGN_WAX          = tag("fancy_sign_wax");
        ITag.INamedTag<Item> POTION_SHELF_POTIONS    = tag("potion_shelf_potions");
        ITag.INamedTag<Item> SWORD_PEDESTAL_SWORDS   = tag("sword_pedestal_swords");
        ITag.INamedTag<Item> TOOL_RACK_TOOLS         = tag("tool_rack_tools");
        ITag.INamedTag<Item> TYPEWRITER_PAPER        = tag("typewriter_paper");
        /** 1.20.1: vanilla has no ItemTags.DYEABLE; use this for dyeable items (e.g. sword pedestal). */
        ITag.INamedTag<Item> DYEABLE                 = tag("dyeable");
        /** 1.16.5 vanilla has no copper; empty datapack tag + optional wrapper so recipes can load. */
        ITag.INamedTag<Item> FORGE_INGOTS_COPPER     = net.minecraft.tags.ItemTags.createOptional(new net.minecraft.util.ResourceLocation("forge", "ingots/copper"));
        // @formatter:on

        /**
         * @param name The path of the tag.
         * @return A {@link ITag.INamedTag<Item>} with this mod's namespace and the given path.
         */
        static ITag.INamedTag<Item> tag(String name) {
            return net.minecraft.tags.ItemTags.createOptional(BCUtil.bcLoc(name));
        }

        /**
         * 1.20.1 TagKey.contains is false when a tag is missing. 1.16.5 optional tags throw if they were never bound.
         */
        static boolean contains(ITag.INamedTag<Item> tag, Item item) {
            ITag<Item> resolved = net.minecraft.tags.ItemTags.getAllTags().getTag(tag.getName());
            return resolved != null && resolved.contains(item);
        }

        static java.util.List<Item> values(ITag.INamedTag<Item> tag) {
            ITag<Item> resolved = net.minecraft.tags.ItemTags.getAllTags().getTag(tag.getName());
            return resolved == null ? java.util.Collections.emptyList() : resolved.getValues();
        }
    }

    interface Enchantments {
        ITag.INamedTag<Enchantment> PRINTING_TABLE_CLONING_BLACKLIST = net.minecraftforge.common.ForgeTagHandler.createOptionalTag(net.minecraftforge.registries.ForgeRegistries.ENCHANTMENTS, BCUtil.bcLoc("printing_table_cloning_blacklist"));
    }
}
