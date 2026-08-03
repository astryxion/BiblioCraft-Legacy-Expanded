package at.minecraftschurli.mods.bibliocraft.datagen.data;

import at.minecraftschurli.mods.bibliocraft.api.BibliocraftApi;
import at.minecraftschurli.mods.bibliocraft.api.datagen.NonClearingBlockTagsProvider;
import at.minecraftschurli.mods.bibliocraft.init.BCBlocks;
import at.minecraftschurli.mods.bibliocraft.init.BCTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;

import java.util.concurrent.CompletableFuture;

public final class BCBlockTagsProvider extends NonClearingBlockTagsProvider {
    public BCBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, BibliocraftApi.MOD_ID);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        BCBlockItemTagsProvider.addBlockTags(this::tag);
        tag(BlockTags.MINEABLE_WITH_AXE)
                .addTag(BCTags.Blocks.BOOKCASES)
                .addTag(BCTags.Blocks.DISPLAY_CASES)
                .addTag(BCTags.Blocks.FANCY_ARMOR_STANDS_WOOD)
                .addTag(BCTags.Blocks.FANCY_CLOCKS)
                .addTag(BCTags.Blocks.FANCY_CRAFTERS)
                .addTag(BCTags.Blocks.FANCY_SIGNS)
                .addTag(BCTags.Blocks.GRANDFATHER_CLOCKS)
                .addTag(BCTags.Blocks.LABELS)
                .addTag(BCTags.Blocks.POTION_SHELVES)
                .addTag(BCTags.Blocks.SEATS)
                .addTag(BCTags.Blocks.SEAT_BACKS)
                .addTag(BCTags.Blocks.SHELVES)
                .addTag(BCTags.Blocks.TABLES)
                .addTag(BCTags.Blocks.TOOL_RACKS)
                .add(BCBlocks.DISC_RACK.get().builtInRegistryHolder().key(),
                        BCBlocks.WALL_DISC_RACK.get().builtInRegistryHolder().key(),
                        BCBlocks.PRINTING_TABLE.get().builtInRegistryHolder().key());
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .addTag(BCTags.Blocks.FANCY_LAMPS)
                .addTag(BCTags.Blocks.FANCY_LANTERNS)
                .addTag(BCTags.Blocks.TYPEWRITERS)
                .add(BCBlocks.COOKIE_JAR.get().builtInRegistryHolder().key(),
                        BCBlocks.DESK_BELL.get().builtInRegistryHolder().key(),
                        BCBlocks.DINNER_PLATE.get().builtInRegistryHolder().key(),
                        BCBlocks.GOLD_CHAIN.get().builtInRegistryHolder().key(),
                        BCBlocks.GOLD_LANTERN.get().builtInRegistryHolder().key(),
                        BCBlocks.GOLD_SOUL_LANTERN.get().builtInRegistryHolder().key(),
                        BCBlocks.IRON_FANCY_ARMOR_STAND.get().builtInRegistryHolder().key(),
                        BCBlocks.IRON_PRINTING_TABLE.get().builtInRegistryHolder().key(),
                        BCBlocks.SWORD_PEDESTAL.get().builtInRegistryHolder().key());
    }

}
