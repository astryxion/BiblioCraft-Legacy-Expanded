package com.github.minecraftschurlimods.bibliocraft.apiimpl;

import com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi;
import com.github.minecraftschurlimods.bibliocraft.api.datagen.BibliocraftDatagenHelper;
import com.github.minecraftschurlimods.bibliocraft.api.datagen.BlockLootTableProvider;
import com.github.minecraftschurlimods.bibliocraft.api.woodtype.BibliocraftWoodType;
import com.github.minecraftschurlimods.bibliocraft.content.seat.SeatBackBlock;
import com.github.minecraftschurlimods.bibliocraft.content.seat.SeatBackType;
import com.github.minecraftschurlimods.bibliocraft.init.BCBlocks;
import com.github.minecraftschurlimods.bibliocraft.init.BCItems;
import com.github.minecraftschurlimods.bibliocraft.init.BCTags;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.DatagenUtil;
import com.github.minecraftschurlimods.bibliocraft.util.holder.ColoredWoodTypeDeferredHolder;
import com.github.minecraftschurlimods.bibliocraft.util.holder.WoodTypeDeferredHolder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import com.github.minecraftschurlimods.bibliocraft.api.datagen.DatagenContext;
import com.github.minecraftschurlimods.bibliocraft.api.datagen.ModLoadedCondition;
import com.github.minecraftschurlimods.bibliocraft.api.datagen.TranslationProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.ItemTags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@SuppressWarnings({"UnusedReturnValue", "SameParameterValue"})
public final class BibliocraftDatagenHelperImpl implements BibliocraftDatagenHelper {
    private static final TagKey<Item> RODS_WOODEN = TagKey.create(Registries.ITEM, ResourceLocation.parse("minecraft:rods/wooden"));
    private static final TagKey<Item> INGOTS_COPPER = TagKey.create(Registries.ITEM, ResourceLocation.parse("minecraft:ingots/copper"));
    private static final TagKey<Item> INGOTS_IRON = TagKey.create(Registries.ITEM, ResourceLocation.parse("minecraft:ingots/iron"));
    private static final TagKey<Item> DYES_BLACK = TagKey.create(Registries.ITEM, ResourceLocation.parse("minecraft:dyes/black"));
    private static final TagKey<Item> CRAFTING_TABLES = TagKey.create(Registries.ITEM, ResourceLocation.parse("minecraft:crafting_tables"));
    private static final TagKey<Item> FEATHERS = TagKey.create(Registries.ITEM, ResourceLocation.parse("minecraft:feathers"));
    private static final TagKey<Item> GLASS_BLOCKS = TagKey.create(Registries.ITEM, ResourceLocation.parse("minecraft:glass_blocks"));

    private final List<BibliocraftWoodType> WOOD_TYPES = new ArrayList<>();

    @Override
    public synchronized void addWoodTypeToGenerate(BibliocraftWoodType woodType) {
        WOOD_TYPES.add(woodType);
    }

    @Override
    public List<BibliocraftWoodType> getWoodTypesToGenerate() {
        return Collections.unmodifiableList(WOOD_TYPES);
    }

    @Override
    public void generateAllFor(BibliocraftWoodType woodType, String modId, DatagenContext context, TranslationProvider englishLanguageProvider, IntrinsicHolderTagsProvider<Block> blockTagsProvider, ItemTagsProvider itemTagsProvider) {
        PackOutput output = context.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = context.getLookupProvider();

        generateEnglishTranslationsFor(englishLanguageProvider, woodType);
        context.addProvider(new BlockLootTableProvider(output, lookupProvider) {
            @Override
            protected void generate() {
                generateLootTablesFor(this, woodType);
            }

            @Override
            public String getName() {
                return super.getName() + " (Bibliocraft datagen helper for wood type " + woodType.id() + ")";
            }
        });
        context.addProvider(new RecipeProvider(output, lookupProvider) {
            @Override
            public void buildRecipes(RecipeOutput out) {
                generateRecipesFor(out, woodType, modId);
            }

            @Override
            public String getName() {
                return super.getName() + " (Bibliocraft datagen helper for wood type " + woodType.id() + ")";
            }
        });
        // Tag generation is done from BCBlockTagsProvider/BCItemTagsProvider via generateBlockTags(this::tag)/generateItemTags(this::tag).
    }

    @Override
    public void generateEnglishTranslationsFor(TranslationProvider provider, BibliocraftWoodType woodType) {
        woodenBlockTranslation(provider, woodType, BCBlocks.BOOKCASE, "Bookcase");
        woodenBlockTranslation(provider, woodType, BCBlocks.FANCY_ARMOR_STAND, "Fancy Armor Stand");
        woodenBlockTranslation(provider, woodType, BCBlocks.FANCY_CLOCK, "Fancy Clock");
        woodenBlockTranslation(provider, woodType, BCBlocks.WALL_FANCY_CLOCK, "Fancy Clock");
        woodenBlockTranslation(provider, woodType, BCBlocks.FANCY_CRAFTER, "Fancy Crafter");
        woodenBlockTranslation(provider, woodType, BCBlocks.FANCY_SIGN, "Fancy Sign");
        woodenBlockTranslation(provider, woodType, BCBlocks.WALL_FANCY_SIGN, "Fancy Sign");
        woodenBlockTranslation(provider, woodType, BCBlocks.GRANDFATHER_CLOCK, "Grandfather Clock");
        woodenBlockTranslation(provider, woodType, BCBlocks.LABEL, "Label");
        woodenBlockTranslation(provider, woodType, BCBlocks.POTION_SHELF, "Potion Shelf");
        woodenBlockTranslation(provider, woodType, BCBlocks.SHELF, "Shelf");
        woodenBlockTranslation(provider, woodType, BCBlocks.TABLE, "Table");
        woodenBlockTranslation(provider, woodType, BCBlocks.TOOL_RACK, "Tool Rack");
        for (DyeColor color : DyeColor.values()) {
            coloredWoodenBlockTranslation(provider, woodType, color, BCBlocks.DISPLAY_CASE, "Display Case");
            coloredWoodenBlockTranslation(provider, woodType, color, BCBlocks.WALL_DISPLAY_CASE, "Display Case");
            coloredWoodenBlockTranslation(provider, woodType, color, BCBlocks.SEAT, "Seat");
            coloredWoodenBlockTranslation(provider, woodType, color, BCBlocks.SEAT_BACK, "Seat Back");
            coloredWoodenItemTranslation(provider, woodType, color, BCItems.SMALL_SEAT_BACK, "Small Seat Back");
            coloredWoodenItemTranslation(provider, woodType, color, BCItems.RAISED_SEAT_BACK, "Raised Seat Back");
            coloredWoodenItemTranslation(provider, woodType, color, BCItems.FLAT_SEAT_BACK, "Flat Seat Back");
            coloredWoodenItemTranslation(provider, woodType, color, BCItems.TALL_SEAT_BACK, "Tall Seat Back");
            coloredWoodenItemTranslation(provider, woodType, color, BCItems.FANCY_SEAT_BACK, "Fancy Seat Back");
        }
    }

    @Override
    public void generateBlockStatesFor(Object provider, BibliocraftWoodType woodType) {
        // Block state and model generation is loader-specific. Use Fabric datagen entrypoint and pass FabricModelProvider, or NeoForge BlockStateProvider.
        if (provider == null) return;
        // Fabric: when FabricModelProvider / BlockStateModelGenerator is passed, generation can be implemented here.
    }

    @Override
    public void generateItemModelsFor(Object provider, BibliocraftWoodType woodType) {
        // Loader-specific: Fabric/NeoForge datagen calls with their ItemModelProvider; actual generation is done in loader datagen.
    }

    private static ResourceKey<Block> blockKey(Block block) {
        return BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow();
    }

    private static ResourceKey<Item> itemKey(Item item) {
        return BuiltInRegistries.ITEM.getResourceKey(item).orElseThrow();
    }

    @Override
    public void generateBlockTagsFor(Function<TagKey<Block>, TagsProvider.TagAppender<Block>> tagAccessor, BibliocraftWoodType woodType) {
        // @formatter:off
        if (woodType.getNamespace().equals("minecraft")) {
            tagAccessor.apply(BCTags.Blocks.BOOKCASES)              .add(blockKey(BCBlocks.BOOKCASE.get(woodType)));
            tagAccessor.apply(BCTags.Blocks.FANCY_ARMOR_STANDS_WOOD).add(blockKey(BCBlocks.FANCY_ARMOR_STAND.get(woodType)));
            tagAccessor.apply(BCTags.Blocks.FANCY_CLOCKS)           .add(blockKey(BCBlocks.FANCY_CLOCK.get(woodType)));
            tagAccessor.apply(BCTags.Blocks.FANCY_CLOCKS)           .add(blockKey(BCBlocks.WALL_FANCY_CLOCK.get(woodType)));
            tagAccessor.apply(BCTags.Blocks.FANCY_CRAFTERS)         .add(blockKey(BCBlocks.FANCY_CRAFTER.get(woodType)));
            tagAccessor.apply(BCTags.Blocks.FANCY_SIGNS)            .add(blockKey(BCBlocks.FANCY_SIGN.get(woodType)));
            tagAccessor.apply(BCTags.Blocks.FANCY_SIGNS)            .add(blockKey(BCBlocks.WALL_FANCY_SIGN.get(woodType)));
            tagAccessor.apply(BCTags.Blocks.GRANDFATHER_CLOCKS)     .add(blockKey(BCBlocks.GRANDFATHER_CLOCK.get(woodType)));
            tagAccessor.apply(BCTags.Blocks.LABELS)                 .add(blockKey(BCBlocks.LABEL.get(woodType)));
            tagAccessor.apply(BCTags.Blocks.POTION_SHELVES)         .add(blockKey(BCBlocks.POTION_SHELF.get(woodType)));
            tagAccessor.apply(BCTags.Blocks.SHELVES)                .add(blockKey(BCBlocks.SHELF.get(woodType)));
            tagAccessor.apply(BCTags.Blocks.TABLES)                 .add(blockKey(BCBlocks.TABLE.get(woodType)));
            tagAccessor.apply(BCTags.Blocks.TOOL_RACKS)             .add(blockKey(BCBlocks.TOOL_RACK.get(woodType)));
            DatagenUtil.addAll(BuiltInRegistries.BLOCK, BCBlocks.DISPLAY_CASE.element(woodType).values(),      tagAccessor.apply(BCTags.Blocks.DISPLAY_CASES));
            DatagenUtil.addAll(BuiltInRegistries.BLOCK, BCBlocks.WALL_DISPLAY_CASE.element(woodType).values(), tagAccessor.apply(BCTags.Blocks.DISPLAY_CASES));
            DatagenUtil.addAll(BuiltInRegistries.BLOCK, BCBlocks.SEAT.element(woodType).values(),              tagAccessor.apply(BCTags.Blocks.SEATS));
            DatagenUtil.addAll(BuiltInRegistries.BLOCK, BCBlocks.SEAT_BACK.element(woodType).values(),         tagAccessor.apply(BCTags.Blocks.SEAT_BACKS));
        } else {
            tagAccessor.apply(BCTags.Blocks.BOOKCASES)              .addOptional(BCBlocks.BOOKCASE.id(woodType));
            tagAccessor.apply(BCTags.Blocks.FANCY_ARMOR_STANDS_WOOD).addOptional(BCBlocks.FANCY_ARMOR_STAND.id(woodType));
            tagAccessor.apply(BCTags.Blocks.FANCY_CLOCKS)           .addOptional(BCBlocks.FANCY_CLOCK.id(woodType));
            tagAccessor.apply(BCTags.Blocks.FANCY_CLOCKS)           .addOptional(BCBlocks.WALL_FANCY_CLOCK.id(woodType));
            tagAccessor.apply(BCTags.Blocks.FANCY_CRAFTERS)         .addOptional(BCBlocks.FANCY_CRAFTER.id(woodType));
            tagAccessor.apply(BCTags.Blocks.FANCY_SIGNS)            .addOptional(BCBlocks.FANCY_SIGN.id(woodType));
            tagAccessor.apply(BCTags.Blocks.FANCY_SIGNS)            .addOptional(BCBlocks.WALL_FANCY_SIGN.id(woodType));
            tagAccessor.apply(BCTags.Blocks.GRANDFATHER_CLOCKS)     .addOptional(BCBlocks.GRANDFATHER_CLOCK.id(woodType));
            tagAccessor.apply(BCTags.Blocks.LABELS)                 .addOptional(BCBlocks.LABEL.id(woodType));
            tagAccessor.apply(BCTags.Blocks.POTION_SHELVES)         .addOptional(BCBlocks.POTION_SHELF.id(woodType));
            tagAccessor.apply(BCTags.Blocks.SHELVES)                .addOptional(BCBlocks.SHELF.id(woodType));
            tagAccessor.apply(BCTags.Blocks.TABLES)                 .addOptional(BCBlocks.TABLE.id(woodType));
            tagAccessor.apply(BCTags.Blocks.TOOL_RACKS)             .addOptional(BCBlocks.TOOL_RACK.id(woodType));
            DatagenUtil.addAllOptional(BuiltInRegistries.BLOCK, BCBlocks.DISPLAY_CASE.element(woodType).values(),      tagAccessor.apply(BCTags.Blocks.DISPLAY_CASES));
            DatagenUtil.addAllOptional(BuiltInRegistries.BLOCK, BCBlocks.WALL_DISPLAY_CASE.element(woodType).values(), tagAccessor.apply(BCTags.Blocks.DISPLAY_CASES));
            DatagenUtil.addAllOptional(BuiltInRegistries.BLOCK, BCBlocks.SEAT.element(woodType).values(),              tagAccessor.apply(BCTags.Blocks.SEATS));
            DatagenUtil.addAllOptional(BuiltInRegistries.BLOCK, BCBlocks.SEAT_BACK.element(woodType).values(),         tagAccessor.apply(BCTags.Blocks.SEAT_BACKS));
        }
        // @formatter:on
    }

    @Override
    public void generateItemTagsFor(Function<TagKey<Item>, TagsProvider.TagAppender<Item>> tagAccessor, BibliocraftWoodType woodType) {
        // @formatter:off
        if (woodType.getNamespace().equals("minecraft")) {
            tagAccessor.apply(BCTags.Items.BOOKCASES)              .add(itemKey(BCItems.BOOKCASE.get(woodType)));
            tagAccessor.apply(BCTags.Items.FANCY_ARMOR_STANDS_WOOD).add(itemKey(BCItems.FANCY_ARMOR_STAND.get(woodType)));
            tagAccessor.apply(BCTags.Items.FANCY_CLOCKS)           .add(itemKey(BCItems.FANCY_CLOCK.get(woodType)));
            tagAccessor.apply(BCTags.Items.FANCY_CRAFTERS)         .add(itemKey(BCItems.FANCY_CRAFTER.get(woodType)));
            tagAccessor.apply(BCTags.Items.FANCY_SIGNS)            .add(itemKey(BCItems.FANCY_SIGN.get(woodType)));
            tagAccessor.apply(BCTags.Items.GRANDFATHER_CLOCKS)     .add(itemKey(BCItems.GRANDFATHER_CLOCK.get(woodType)));
            tagAccessor.apply(BCTags.Items.LABELS)                 .add(itemKey(BCItems.LABEL.get(woodType)));
            tagAccessor.apply(BCTags.Items.POTION_SHELVES)         .add(itemKey(BCItems.POTION_SHELF.get(woodType)));
            tagAccessor.apply(BCTags.Items.SHELVES)                .add(itemKey(BCItems.SHELF.get(woodType)));
            tagAccessor.apply(BCTags.Items.TABLES)                 .add(itemKey(BCItems.TABLE.get(woodType)));
            tagAccessor.apply(BCTags.Items.TOOL_RACKS)             .add(itemKey(BCItems.TOOL_RACK.get(woodType)));
            DatagenUtil.addAll(BuiltInRegistries.ITEM, BCItems.DISPLAY_CASE.element(woodType).values(),     tagAccessor.apply(BCTags.Items.DISPLAY_CASES));
            DatagenUtil.addAll(BuiltInRegistries.ITEM, BCItems.SEAT.element(woodType).values(),             tagAccessor.apply(BCTags.Items.SEATS));
            DatagenUtil.addAll(BuiltInRegistries.ITEM, BCItems.SMALL_SEAT_BACK.element(woodType).values(),  tagAccessor.apply(BCTags.Items.SEAT_BACKS_SMALL));
            DatagenUtil.addAll(BuiltInRegistries.ITEM, BCItems.RAISED_SEAT_BACK.element(woodType).values(), tagAccessor.apply(BCTags.Items.SEAT_BACKS_RAISED));
            DatagenUtil.addAll(BuiltInRegistries.ITEM, BCItems.FLAT_SEAT_BACK.element(woodType).values(),   tagAccessor.apply(BCTags.Items.SEAT_BACKS_FLAT));
            DatagenUtil.addAll(BuiltInRegistries.ITEM, BCItems.TALL_SEAT_BACK.element(woodType).values(),   tagAccessor.apply(BCTags.Items.SEAT_BACKS_TALL));
            DatagenUtil.addAll(BuiltInRegistries.ITEM, BCItems.FANCY_SEAT_BACK.element(woodType).values(),  tagAccessor.apply(BCTags.Items.SEAT_BACKS_FANCY));
        } else {
            tagAccessor.apply(BCTags.Items.BOOKCASES)              .addOptional(BCItems.BOOKCASE.id(woodType));
            tagAccessor.apply(BCTags.Items.FANCY_ARMOR_STANDS_WOOD).addOptional(BCItems.FANCY_ARMOR_STAND.id(woodType));
            tagAccessor.apply(BCTags.Items.FANCY_CLOCKS)           .addOptional(BCItems.FANCY_CLOCK.id(woodType));
            tagAccessor.apply(BCTags.Items.FANCY_CRAFTERS)         .addOptional(BCItems.FANCY_CRAFTER.id(woodType));
            tagAccessor.apply(BCTags.Items.FANCY_SIGNS)            .addOptional(BCItems.FANCY_SIGN.id(woodType));
            tagAccessor.apply(BCTags.Items.GRANDFATHER_CLOCKS)     .addOptional(BCItems.GRANDFATHER_CLOCK.id(woodType));
            tagAccessor.apply(BCTags.Items.LABELS)                 .addOptional(BCItems.LABEL.id(woodType));
            tagAccessor.apply(BCTags.Items.POTION_SHELVES)         .addOptional(BCItems.POTION_SHELF.id(woodType));
            tagAccessor.apply(BCTags.Items.SHELVES)                .addOptional(BCItems.SHELF.id(woodType));
            tagAccessor.apply(BCTags.Items.TABLES)                 .addOptional(BCItems.TABLE.id(woodType));
            tagAccessor.apply(BCTags.Items.TOOL_RACKS)             .addOptional(BCItems.TOOL_RACK.id(woodType));
            DatagenUtil.addAllOptional(BuiltInRegistries.ITEM, BCItems.DISPLAY_CASE.element(woodType).values(),     tagAccessor.apply(BCTags.Items.DISPLAY_CASES));
            DatagenUtil.addAllOptional(BuiltInRegistries.ITEM, BCItems.SEAT.element(woodType).values(),             tagAccessor.apply(BCTags.Items.SEATS));
            DatagenUtil.addAllOptional(BuiltInRegistries.ITEM, BCItems.SMALL_SEAT_BACK.element(woodType).values(),  tagAccessor.apply(BCTags.Items.SEAT_BACKS_SMALL));
            DatagenUtil.addAllOptional(BuiltInRegistries.ITEM, BCItems.RAISED_SEAT_BACK.element(woodType).values(), tagAccessor.apply(BCTags.Items.SEAT_BACKS_RAISED));
            DatagenUtil.addAllOptional(BuiltInRegistries.ITEM, BCItems.FLAT_SEAT_BACK.element(woodType).values(),   tagAccessor.apply(BCTags.Items.SEAT_BACKS_FLAT));
            DatagenUtil.addAllOptional(BuiltInRegistries.ITEM, BCItems.TALL_SEAT_BACK.element(woodType).values(),   tagAccessor.apply(BCTags.Items.SEAT_BACKS_TALL));
            DatagenUtil.addAllOptional(BuiltInRegistries.ITEM, BCItems.FANCY_SEAT_BACK.element(woodType).values(),  tagAccessor.apply(BCTags.Items.SEAT_BACKS_FANCY));
        }
        // @formatter:on
    }

    @Override
    public void generateLootTablesFor(BlockLootTableProvider provider, BibliocraftWoodType woodType) {
        // @formatter:off
        loot(provider, BCBlocks.BOOKCASE.get(woodType),          woodType, DatagenUtil::createNameableTable);
        loot(provider, BCBlocks.FANCY_ARMOR_STAND.get(woodType), woodType, DatagenUtil::createFancyArmorStandTable);
        loot(provider, BCBlocks.FANCY_CLOCK.get(woodType),       woodType, DatagenUtil::createDefaultTable);
        loot(provider, BCBlocks.WALL_FANCY_CLOCK.get(woodType),  woodType, block -> DatagenUtil.createDefaultTable(BCBlocks.FANCY_CLOCK.get(woodType)));
        loot(provider, BCBlocks.FANCY_CRAFTER.get(woodType),     woodType, DatagenUtil::createNameableTable);
        loot(provider, BCBlocks.FANCY_SIGN.get(woodType),        woodType, DatagenUtil::createDefaultTable);
        loot(provider, BCBlocks.WALL_FANCY_SIGN.get(woodType),   woodType, block -> DatagenUtil.createDefaultTable(BCBlocks.FANCY_SIGN.get(woodType)));
        loot(provider, BCBlocks.GRANDFATHER_CLOCK.get(woodType), woodType, DatagenUtil::createGrandfatherClockTable);
        loot(provider, BCBlocks.LABEL.get(woodType),             woodType, DatagenUtil::createNameableTable);
        loot(provider, BCBlocks.POTION_SHELF.get(woodType),      woodType, DatagenUtil::createNameableTable);
        loot(provider, BCBlocks.SHELF.get(woodType),             woodType, DatagenUtil::createNameableTable);
        loot(provider, BCBlocks.TABLE.get(woodType),             woodType, DatagenUtil::createDefaultTable);
        loot(provider, BCBlocks.TOOL_RACK.get(woodType),         woodType, DatagenUtil::createNameableTable);
        for (DyeColor color : DyeColor.values()) {
            loot(provider, BCBlocks.DISPLAY_CASE.get(woodType, color),      woodType, DatagenUtil::createDefaultTable);
            loot(provider, BCBlocks.WALL_DISPLAY_CASE.get(woodType, color), woodType, block -> DatagenUtil.createDefaultTable(BCBlocks.DISPLAY_CASE.get(woodType, color)));
            loot(provider, BCBlocks.SEAT.get(woodType, color),              woodType, DatagenUtil::createDefaultTable);
            loot(provider, BCBlocks.SEAT_BACK.get(woodType, color), woodType, block -> LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(ExplosionCondition.survivesExplosion())
                    .add(LootItem.lootTableItem(BCItems.SMALL_SEAT_BACK.get(woodType, color)) .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SeatBackBlock.TYPE, SeatBackType.SMALL))))
                    .add(LootItem.lootTableItem(BCItems.RAISED_SEAT_BACK.get(woodType, color)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SeatBackBlock.TYPE, SeatBackType.RAISED))))
                    .add(LootItem.lootTableItem(BCItems.FLAT_SEAT_BACK.get(woodType, color))  .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SeatBackBlock.TYPE, SeatBackType.FLAT))))
                    .add(LootItem.lootTableItem(BCItems.TALL_SEAT_BACK.get(woodType, color))  .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SeatBackBlock.TYPE, SeatBackType.TALL))))
                    .add(LootItem.lootTableItem(BCItems.FANCY_SEAT_BACK.get(woodType, color)) .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SeatBackBlock.TYPE, SeatBackType.FANCY))))));
        }
        // @formatter:on
    }

    @Override
    public void generateRecipesFor(RecipeOutput output, BibliocraftWoodType woodType, String modId) {
        // On Fabric, RecipeOutput has no withConditions; optional-mod recipes are always generated.
        String prefix = "wood/" + woodType.getRegistrationPrefix() + "/";
        Block planks = woodType.family().get().getBaseBlock();
        Block slab = woodType.family().get().get(BlockFamily.Variant.SLAB);
        TagKey<Item> stick = RODS_WOODEN;
        shapedRecipe(BCItems.BOOKCASE.get(woodType), woodType, "bookcases")
                .pattern("PSP")
                .pattern("PSP")
                .pattern("PSP")
                .define('P', planks)
                .define('S', slab)
                .save(output, BCUtil.modLoc(modId, prefix + "bookcase"));
        shapedRecipe(BCItems.FANCY_ARMOR_STAND.get(woodType), woodType, "fancy_armor_stands")
                .pattern(" R ")
                .pattern(" R ")
                .pattern("SSS")
                .define('S', slab)
                .define('R', RODS_WOODEN)
                .save(output, BCUtil.modLoc(modId, prefix + "fancy_armor_stand"));
        shapedRecipe(BCItems.FANCY_CLOCK.get(woodType), woodType, "fancy_clock")
                .pattern("SCS")
                .pattern("SRS")
                .pattern("SIS")
                .define('S', slab)
                .define('C', Items.CLOCK)
                .define('R', RODS_WOODEN)
                .define('I', INGOTS_COPPER)
                .save(output, BCUtil.modLoc(modId, prefix + "fancy_clock"));
        shapedRecipe(BCItems.FANCY_CRAFTER.get(woodType), woodType, "fancy_crafter")
                .pattern("ITF")
                .pattern("PGP")
                .pattern("PCP")
                .define('P', planks)
                .define('I', DYES_BLACK)
                .define('T', CRAFTING_TABLES)
                .define('F', FEATHERS)
                .define('G', GLASS_BLOCKS)
                .define('C', Items.CRAFTER)
                .save(output, BCUtil.modLoc(modId, prefix + "fancy_crafter"));
        shapedRecipe(BCItems.FANCY_SIGN.get(woodType), woodType, "fancy_sign")
                .pattern("P#P")
                .pattern("P#P")
                .pattern(" R ")
                .define('P', planks)
                .define('#', Items.PAPER)
                .define('R', RODS_WOODEN)
                .save(output, BCUtil.modLoc(modId, prefix + "fancy_sign"));
        shapelessRecipe(BCItems.GRANDFATHER_CLOCK.get(woodType), woodType, "grandfather_clock")
                .requires(BCItems.FANCY_CLOCK.get(woodType))
                .requires(BCItems.FANCY_CLOCK.get(woodType))
                .save(output, BCUtil.modLoc(modId, prefix + "grandfather_clock"));
        shapedRecipe(BCItems.LABEL.get(woodType), woodType, "labels")
                .pattern("SSS")
                .pattern("SSS")
                .define('S', slab)
                .save(output, BCUtil.modLoc(modId, prefix + "label"));
        shapedRecipe(BCItems.POTION_SHELF.get(woodType), woodType, "potion_shelves")
                .pattern("SSS")
                .pattern("P#P")
                .pattern("SSS")
                .define('P', planks)
                .define('S', slab)
                .define('#', Items.GLASS_BOTTLE)
                .save(output, BCUtil.modLoc(modId, prefix + "potion_shelf"));
        shapedRecipe(BCItems.SHELF.get(woodType), woodType, "shelves")
                .pattern("SSS")
                .pattern(" P ")
                .pattern("SSS")
                .define('P', planks)
                .define('S', slab)
                .save(output, BCUtil.modLoc(modId, prefix + "shelf"));
        shapedRecipe(BCItems.TABLE.get(woodType), woodType, "tables")
                .pattern("SSS")
                .pattern(" P ")
                .pattern(" P ")
                .define('P', planks)
                .define('S', slab)
                .save(output, BCUtil.modLoc(modId, prefix + "table"));
        shapedRecipe(BCItems.TOOL_RACK.get(woodType), woodType, "tool_racks")
                .pattern("SSS")
                .pattern("S#S")
                .pattern("SSS")
                .define('S', slab)
                .define('#', INGOTS_IRON)
                .save(output, BCUtil.modLoc(modId, prefix + "tool_rack"));
        for (DyeColor color : DyeColor.values()) {
            Item wool = BuiltInRegistries.ITEM.get(BCUtil.mcLoc(color.getName() + "_wool"));
            prefix = "color/" + color.getSerializedName() + "/wood/" + woodType.getRegistrationPrefix() + "/";
            shapedRecipe(BCItems.DISPLAY_CASE.get(woodType, color), woodType, "display_cases")
                    .pattern("SGS")
                    .pattern("SWS")
                    .pattern("SSS")
                    .define('S', slab)
                    .define('W', wool)
                    .define('G', GLASS_BLOCKS)
                    .save(output, BCUtil.modLoc(modId, prefix + "display_case"));
            shapedRecipe(BCItems.SEAT.get(woodType, color), woodType, "seats")
                    .pattern(" W ")
                    .pattern(" S ")
                    .pattern("RSR")
                    .define('S', slab)
                    .define('R', stick)
                    .define('W', wool)
                    .save(output, BCUtil.modLoc(modId, prefix + "seat"));
            shapedRecipe(BCItems.SMALL_SEAT_BACK.get(woodType, color), woodType, "small_seat_backs")
                    .pattern("W")
                    .pattern("S")
                    .define('S', slab)
                    .define('W', wool)
                    .save(output, BCUtil.modLoc(modId, prefix + "small_seat_back"));
            shapedRecipe(BCItems.RAISED_SEAT_BACK.get(woodType, color), woodType, "raised_seat_backs")
                    .pattern(" W ")
                    .pattern(" S ")
                    .pattern("R R")
                    .define('S', slab)
                    .define('R', stick)
                    .define('W', wool)
                    .save(output, BCUtil.modLoc(modId, prefix + "raised_seat_back"));
            shapedRecipe(BCItems.FLAT_SEAT_BACK.get(woodType, color), woodType, "flat_seat_backs")
                    .pattern("RWR")
                    .pattern("RSR")
                    .pattern("R R")
                    .define('S', slab)
                    .define('R', stick)
                    .define('W', wool)
                    .save(output, BCUtil.modLoc(modId, prefix + "flat_seat_back"));
            shapedRecipe(BCItems.TALL_SEAT_BACK.get(woodType, color), woodType, "tall_seat_backs")
                    .pattern("S")
                    .pattern("#")
                    .define('S', slab)
                    .define('#', BCItems.FLAT_SEAT_BACK.get(woodType, color))
                    .save(output, BCUtil.modLoc(modId, prefix + "tall_seat_back"));
            shapedRecipe(BCItems.FANCY_SEAT_BACK.get(woodType, color), woodType, "fancy_seat_backs")
                    .pattern("S#S")
                    .define('S', slab)
                    .define('#', BCItems.FLAT_SEAT_BACK.get(woodType, color))
                    .save(output, BCUtil.modLoc(modId, prefix + "fancy_seat_back"));
        }
    }

    /**
     * @param path The path of the {@link ResourceLocation}.
     * @return A new {@link ResourceLocation} with Bibliocraft's namespace and the given path.
     */
    private static ResourceLocation bcLoc(String path) {
        return BCUtil.bcLoc(path);
    }

    /**
     * Adds an English (en_us) translation to the given {@link TranslationProvider}.
     */
    private static void woodenBlockTranslation(TranslationProvider provider, BibliocraftWoodType woodType, WoodTypeDeferredHolder<Block, ?> holder, String suffix) {
        provider.add(holder.get(woodType), DatagenUtil.toTranslation(woodType.getPath()) + " " + suffix);
    }

    /**
     * Adds an English (en_us) translation to the given {@link TranslationProvider}.
     */
    private static void coloredWoodenBlockTranslation(TranslationProvider provider, BibliocraftWoodType woodType, DyeColor color, ColoredWoodTypeDeferredHolder<Block, ?> holder, String suffix) {
        provider.add(holder.get(woodType, color), DatagenUtil.toTranslation(color.getName()) + " " + DatagenUtil.toTranslation(woodType.getPath()) + " " + suffix);
    }

    /**
     * Adds an English (en_us) translation to the given {@link TranslationProvider}.
     */
    private static void coloredWoodenItemTranslation(TranslationProvider provider, BibliocraftWoodType woodType, DyeColor color, ColoredWoodTypeDeferredHolder<Item, ?> holder, String suffix) {
        provider.add(holder.get(woodType, color), DatagenUtil.toTranslation(color.getName()) + " " + DatagenUtil.toTranslation(woodType.getPath()) + " " + suffix);
    }

    /**
     * Adds a loot table for a block.
     *
     * @param provider The loot table provider.
     * @param block    The block.
     * @param factory  A function that returns a {@link LootTable.Builder} for a given block.
     */
    private static void loot(BlockLootTableProvider provider, Block block, BibliocraftWoodType woodType, Function<Block, LootTable.Builder> factory) {
        BlockLootTableProvider.WithConditionsBuilder<LootTable.Builder> builder = BlockLootTableProvider.wrapLootTable(factory.apply(block));
        if (!woodType.getNamespace().equals("minecraft")) {
            builder.addCondition(new ModLoadedCondition(woodType.getNamespace()));
        }
        provider.add(block, builder);
    }

    /**
     * Adds a shaped recipe for an item.
     *
     * @param item     The item.
     * @param woodType The {@link BibliocraftWoodType}.
     * @return A {@link ShapedRecipeBuilder} with the
     */
    private static ShapedRecipeBuilder shapedRecipe(Item item, BibliocraftWoodType woodType, String group) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, item)
                .group(BibliocraftApi.MOD_ID + ":" + group)
                .unlockedBy("has_planks", CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(ItemPredicate.Builder.item().of(woodType.family().get().getBaseBlock()).build()))));
    }

    /**
     * Adds a shapeless recipe for an item.
     *
     * @param item     The item.
     * @param woodType The {@link BibliocraftWoodType}.
     * @return A {@link ShapelessRecipeBuilder} with the
     */
    private static ShapelessRecipeBuilder shapelessRecipe(Item item, BibliocraftWoodType woodType, String group) {
        return ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, item)
                .group(BibliocraftApi.MOD_ID + ":" + group)
                .unlockedBy("has_planks", CriteriaTriggers.INVENTORY_CHANGED.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(ItemPredicate.Builder.item().of(woodType.family().get().getBaseBlock()).build()))));
    }
}
