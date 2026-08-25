package com.github.minecraftschurlimods.bibliocraft.datagen.data;

import com.github.minecraftschurlimods.bibliocraft.init.BCBlocks;
import com.github.minecraftschurlimods.bibliocraft.util.DatagenUtil;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.data.LootTableProvider;
import net.minecraft.item.DyeColor;
import net.minecraft.block.Block;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootParameterSet;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTableManager;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class BCLootTableProvider extends LootTableProvider {
    public BCLootTableProvider(DataGenerator output) {
        super(output);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
        return ImmutableList.of(Pair.of(BCBlockLootProvider::new, LootParameterSets.BLOCK));
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker) {
        map.forEach((id, table) -> LootTableManager.validate(validationtracker, id, table));
    }

    private static final class BCBlockLootProvider extends BlockLootTables {
        private final List<Block> blocks = new ArrayList<>();

        @Override
        protected void addTables() {
            // @formatter:off
            add(BCBlocks.CLEAR_FANCY_GOLD_LAMP.get(),    DatagenUtil.createDefaultTable(BCBlocks.CLEAR_FANCY_GOLD_LAMP.get()));
            add(BCBlocks.CLEAR_FANCY_IRON_LAMP.get(),    DatagenUtil.createDefaultTable(BCBlocks.CLEAR_FANCY_IRON_LAMP.get()));
            add(BCBlocks.CLEAR_FANCY_GOLD_LANTERN.get(), DatagenUtil.createDefaultTable(BCBlocks.CLEAR_FANCY_GOLD_LANTERN.get()));
            add(BCBlocks.CLEAR_FANCY_IRON_LANTERN.get(), DatagenUtil.createDefaultTable(BCBlocks.CLEAR_FANCY_IRON_LANTERN.get()));
            add(BCBlocks.SOUL_FANCY_GOLD_LANTERN.get(),  DatagenUtil.createDefaultTable(BCBlocks.SOUL_FANCY_GOLD_LANTERN.get()));
            add(BCBlocks.SOUL_FANCY_IRON_LANTERN.get(),  DatagenUtil.createDefaultTable(BCBlocks.SOUL_FANCY_IRON_LANTERN.get()));
            add(BCBlocks.CLEAR_TYPEWRITER.get(),         DatagenUtil.createDefaultTable(BCBlocks.CLEAR_TYPEWRITER.get()));
            for (DyeColor color : DyeColor.values()) {
                add(BCBlocks.FANCY_GOLD_LAMP.get(color),    DatagenUtil.createDefaultTable(BCBlocks.FANCY_GOLD_LAMP.get(color)));
                add(BCBlocks.FANCY_IRON_LAMP.get(color),    DatagenUtil.createDefaultTable(BCBlocks.FANCY_IRON_LAMP.get(color)));
                add(BCBlocks.FANCY_GOLD_LANTERN.get(color), DatagenUtil.createDefaultTable(BCBlocks.FANCY_GOLD_LANTERN.get(color)));
                add(BCBlocks.FANCY_IRON_LANTERN.get(color), DatagenUtil.createDefaultTable(BCBlocks.FANCY_IRON_LANTERN.get(color)));
                add(BCBlocks.TYPEWRITER.get(color),         DatagenUtil.createDefaultTable(BCBlocks.TYPEWRITER.get(color)));
            }
            add(BCBlocks.CLIPBOARD.get(),              DatagenUtil.createClipboardLootTable(BCBlocks.CLIPBOARD.get()));
            add(BCBlocks.COOKIE_JAR.get(),             DatagenUtil.createNameableTable(BCBlocks.COOKIE_JAR.get()));
            add(BCBlocks.DESK_BELL.get(),              DatagenUtil.createDefaultTable(BCBlocks.DESK_BELL.get()));
            add(BCBlocks.DINNER_PLATE.get(),           DatagenUtil.createDefaultTable(BCBlocks.DINNER_PLATE.get()));
            add(BCBlocks.DISC_RACK.get(),              DatagenUtil.createNameableTable(BCBlocks.DISC_RACK.get()));
            add(BCBlocks.WALL_DISC_RACK.get(),         DatagenUtil.createNameableTable(BCBlocks.DISC_RACK.get()));
            add(BCBlocks.IRON_FANCY_ARMOR_STAND.get(), DatagenUtil.createFancyArmorStandTable(BCBlocks.IRON_FANCY_ARMOR_STAND.get()));
            add(BCBlocks.GOLD_CHAIN.get(),             DatagenUtil.createDefaultTable(BCBlocks.GOLD_CHAIN.get()));
            add(BCBlocks.GOLD_LANTERN.get(),           DatagenUtil.createDefaultTable(BCBlocks.GOLD_LANTERN.get()));
            add(BCBlocks.GOLD_SOUL_LANTERN.get(),      DatagenUtil.createDefaultTable(BCBlocks.GOLD_SOUL_LANTERN.get()));
            add(BCBlocks.PRINTING_TABLE.get(),         DatagenUtil.createNameableTable(BCBlocks.PRINTING_TABLE.get()));
            add(BCBlocks.IRON_PRINTING_TABLE.get(),    DatagenUtil.createNameableTable(BCBlocks.IRON_PRINTING_TABLE.get()));
            add(BCBlocks.SWORD_PEDESTAL.get(),         DatagenUtil.createCopyNbtBlockEntityTable(BCBlocks.SWORD_PEDESTAL.get()));
            // @formatter:on
        }

        @Override
        protected void add(Block block, LootTable.Builder builder) {
            super.add(block, builder);
            blocks.add(block);
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return blocks;
        }
    }
}
