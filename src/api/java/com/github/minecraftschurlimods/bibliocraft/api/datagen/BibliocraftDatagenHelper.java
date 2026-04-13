package com.github.minecraftschurlimods.bibliocraft.api.datagen;

import com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi;
import com.github.minecraftschurlimods.bibliocraft.api.woodtype.BibliocraftWoodType;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Function;

/**
 * Helper to generate datagen entries for Bibliocraft blocks with your mod's wood type(s).
 * Get via {@link BibliocraftApi#getDatagenHelper()}.
 * Call from your loader's datagen entrypoint (e.g. Fabric DataGeneratorEntrypoint or NeoForge GatherDataEvent).
 */
@SuppressWarnings("unused")
public interface BibliocraftDatagenHelper {
    void addWoodTypeToGenerate(BibliocraftWoodType woodType);
    List<BibliocraftWoodType> getWoodTypesToGenerate();

    void generateEnglishTranslationsFor(TranslationProvider provider, BibliocraftWoodType woodType);

    /**
     * @param provider Loader-specific block state/model provider (e.g. Fabric or NeoForge BlockStateProvider).
     */
    void generateBlockStatesFor(Object provider, BibliocraftWoodType woodType);

    /**
     * @param provider Loader-specific item model provider (e.g. Fabric or NeoForge ItemModelProvider).
     */
    void generateItemModelsFor(Object provider, BibliocraftWoodType woodType);

    void generateBlockTagsFor(Function<TagKey<Block>, TagsProvider.TagAppender<Block>> tagAccessor, BibliocraftWoodType woodType);
    void generateItemTagsFor(Function<TagKey<Item>, TagsProvider.TagAppender<Item>> tagAccessor, BibliocraftWoodType woodType);
    void generateLootTablesFor(BlockLootTableProvider provider, BibliocraftWoodType woodType);
    void generateRecipesFor(RecipeOutput output, BibliocraftWoodType woodType, String modId);

    void generateAllFor(BibliocraftWoodType woodType, String modId, DatagenContext context, TranslationProvider englishLanguageProvider, IntrinsicHolderTagsProvider<Block> blockTagsProvider, ItemTagsProvider itemTagsProvider);

    default void addWoodTypesToGenerateByModid(String modid) {
        BibliocraftApi.getWoodTypeRegistry().getAll().stream()
                .filter(e -> e.getNamespace().equals(modid))
                .forEach(this::addWoodTypeToGenerate);
    }

    default void generateEnglishTranslations(TranslationProvider provider) {
        getWoodTypesToGenerate().forEach(woodType -> generateEnglishTranslationsFor(provider, woodType));
    }

    default void generateBlockStates(Object provider) {
        getWoodTypesToGenerate().forEach(woodType -> generateBlockStatesFor(provider, woodType));
    }

    default void generateItemModels(Object provider) {
        getWoodTypesToGenerate().forEach(woodType -> generateItemModelsFor(provider, woodType));
    }

    default void generateBlockTags(Function<TagKey<Block>, TagsProvider.TagAppender<Block>> tagAccessor) {
        getWoodTypesToGenerate().forEach(woodType -> generateBlockTagsFor(tagAccessor, woodType));
    }

    default void generateItemTags(Function<TagKey<Item>, TagsProvider.TagAppender<Item>> tagAccessor) {
        getWoodTypesToGenerate().forEach(woodType -> generateItemTagsFor(tagAccessor, woodType));
    }

    default void generateLootTables(BlockLootTableProvider provider) {
        getWoodTypesToGenerate().forEach(woodType -> generateLootTablesFor(provider, woodType));
    }

    default void generateRecipes(RecipeOutput output, String modId) {
        getWoodTypesToGenerate().forEach(woodType -> generateRecipesFor(output, woodType, modId));
    }

    default void generateAll(String modId, DatagenContext context, TranslationProvider englishLanguageProvider, IntrinsicHolderTagsProvider<Block> blockTagsProvider, ItemTagsProvider itemTagsProvider) {
        getWoodTypesToGenerate().forEach(woodType -> generateAllFor(woodType, modId, context, englishLanguageProvider, blockTagsProvider, itemTagsProvider));
    }
}
