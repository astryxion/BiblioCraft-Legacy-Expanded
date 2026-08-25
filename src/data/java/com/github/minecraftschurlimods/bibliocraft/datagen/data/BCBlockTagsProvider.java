package com.github.minecraftschurlimods.bibliocraft.datagen.data;

import com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi;
import com.github.minecraftschurlimods.bibliocraft.api.datagen.NonClearingBlockTagsProvider;
import com.github.minecraftschurlimods.bibliocraft.init.BCBlocks;
import com.github.minecraftschurlimods.bibliocraft.init.BCTags;
import com.github.minecraftschurlimods.bibliocraft.util.DatagenUtil;
import net.minecraft.util.registry.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;

public final class BCBlockTagsProvider extends NonClearingBlockTagsProvider {
    public BCBlockTagsProvider(DataGenerator output, ExistingFileHelper existingFileHelper) {
        super(output, BibliocraftApi.MOD_ID, existingFileHelper);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addTags() {
        tag(BCTags.Blocks.FANCY_ARMOR_STANDS).addTag(BCTags.Blocks.FANCY_ARMOR_STANDS_WOOD).add(BCBlocks.IRON_FANCY_ARMOR_STAND.get());
        tag(BCTags.Blocks.FANCY_LAMPS).addTags(BCTags.Blocks.FANCY_LAMPS_GOLD, BCTags.Blocks.FANCY_LAMPS_IRON);
        DatagenUtil.addAll(Registry.BLOCK, BCBlocks.FANCY_GOLD_LAMP.values(), tag(BCTags.Blocks.FANCY_LAMPS_GOLD).add(BCBlocks.CLEAR_FANCY_GOLD_LAMP.get()));
        DatagenUtil.addAll(Registry.BLOCK, BCBlocks.FANCY_IRON_LAMP.values(), tag(BCTags.Blocks.FANCY_LAMPS_IRON).add(BCBlocks.CLEAR_FANCY_IRON_LAMP.get()));
        tag(BCTags.Blocks.FANCY_LANTERNS).addTags(BCTags.Blocks.FANCY_LANTERNS_GOLD, BCTags.Blocks.FANCY_LANTERNS_IRON);
        DatagenUtil.addAll(Registry.BLOCK, BCBlocks.FANCY_GOLD_LANTERN.values(), tag(BCTags.Blocks.FANCY_LANTERNS_GOLD).add(BCBlocks.CLEAR_FANCY_GOLD_LANTERN.get()).addOptional(BCBlocks.SOUL_FANCY_GOLD_LANTERN.getId()));
        DatagenUtil.addAll(Registry.BLOCK, BCBlocks.FANCY_IRON_LANTERN.values(), tag(BCTags.Blocks.FANCY_LANTERNS_IRON).add(BCBlocks.CLEAR_FANCY_IRON_LANTERN.get()).addOptional(BCBlocks.SOUL_FANCY_IRON_LANTERN.getId()));
        tag(BCTags.Blocks.PRINTING_TABLES).add(BCBlocks.PRINTING_TABLE.get(), BCBlocks.IRON_PRINTING_TABLE.get());
        DatagenUtil.addAll(Registry.BLOCK, BCBlocks.TYPEWRITER.values(), tag(BCTags.Blocks.TYPEWRITERS).add(BCBlocks.CLEAR_TYPEWRITER.get()));
        tag(BlockTags.createOptional(new net.minecraft.util.ResourceLocation("minecraft", "mineable/axe"))).addTags(BCTags.Blocks.BOOKCASES, BCTags.Blocks.DISPLAY_CASES, BCTags.Blocks.FANCY_ARMOR_STANDS_WOOD, BCTags.Blocks.FANCY_CLOCKS, BCTags.Blocks.FANCY_CRAFTERS, BCTags.Blocks.FANCY_SIGNS, BCTags.Blocks.GRANDFATHER_CLOCKS, BCTags.Blocks.LABELS, BCTags.Blocks.POTION_SHELVES, BCTags.Blocks.SEATS, BCTags.Blocks.SEAT_BACKS, BCTags.Blocks.SHELVES, BCTags.Blocks.TABLES, BCTags.Blocks.TOOL_RACKS).add(BCBlocks.DISC_RACK.get(), BCBlocks.WALL_DISC_RACK.get(), BCBlocks.PRINTING_TABLE.get());
        tag(BlockTags.createOptional(new net.minecraft.util.ResourceLocation("minecraft", "mineable/pickaxe"))).addTags(BCTags.Blocks.FANCY_LAMPS, BCTags.Blocks.FANCY_LANTERNS, BCTags.Blocks.TYPEWRITERS).add(BCBlocks.COOKIE_JAR.get(), BCBlocks.DESK_BELL.get(), BCBlocks.DINNER_PLATE.get(), BCBlocks.GOLD_CHAIN.get(), BCBlocks.GOLD_LANTERN.get(), BCBlocks.GOLD_SOUL_LANTERN.get(), BCBlocks.IRON_FANCY_ARMOR_STAND.get(), BCBlocks.IRON_PRINTING_TABLE.get(), BCBlocks.SWORD_PEDESTAL.get());
    }
}
