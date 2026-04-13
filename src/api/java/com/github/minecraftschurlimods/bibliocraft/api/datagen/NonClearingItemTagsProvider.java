package com.github.minecraftschurlimods.bibliocraft.api.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

/**
 * ItemTagsProvider that does not clear tag builders before calling {@link #addTags(HolderLookup.Provider)}.
 * Block-to-item tag copies should be done in {@link #addTags(HolderLookup.Provider)} via {@link #copy(net.minecraft.tags.TagKey, net.minecraft.tags.TagKey)}.
 */
@SuppressWarnings("unused")
public abstract class NonClearingItemTagsProvider extends ItemTagsProvider {
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;

    public NonClearingItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<Block>> blockTags) {
        super(output, lookupProvider, blockTags);
        this.lookupProvider = lookupProvider;
    }

    public NonClearingItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<Item>> parentProvider, CompletableFuture<TagsProvider.TagLookup<Block>> blockTags) {
        super(output, lookupProvider, parentProvider, blockTags);
        this.lookupProvider = lookupProvider;
    }

    @Override
    protected CompletableFuture<HolderLookup.Provider> createContentsProvider() {
        return lookupProvider.thenApply(provider -> {
            addTags(provider);
            return provider;
        });
    }
}
