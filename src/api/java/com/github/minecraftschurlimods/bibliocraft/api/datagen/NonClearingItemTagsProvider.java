package com.github.minecraftschurlimods.bibliocraft.api.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.TagBuilder;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * The default {@link ItemTagsProvider} implementation clears the builders before calling {@link ItemTagsProvider#addTags(HolderLookup.Provider)}.
 * We don't want that, so we override {@link ItemTagsProvider#addTags(HolderLookup.Provider)} to not do that.
 */
@SuppressWarnings("unused")
public abstract class NonClearingItemTagsProvider extends ItemTagsProvider {
    // Store the provider here because while the superclass has it, it is private there.
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;
    // Store blockTags for createContentsProvider (parent field is private).
    private final CompletableFuture<TagLookup<Block>> blockTagsFuture;

    /**
     * See super constructor for information.
     */
    public NonClearingItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookupProvider, blockTags);
        this.lookupProvider = lookupProvider;
        this.blockTagsFuture = blockTags;
    }

    /**
     * See super constructor for information.
     */
    @Deprecated
    public NonClearingItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Item>> parentProvider, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookupProvider, parentProvider, blockTags);
        this.lookupProvider = lookupProvider;
        this.blockTagsFuture = blockTags;
    }

    /**
     * See super constructor for information.
     */
    public NonClearingItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, modId, existingFileHelper);
        this.lookupProvider = lookupProvider;
        this.blockTagsFuture = blockTags;
    }

    /**
     * See super constructor for information.
     */
    public NonClearingItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Item>> parentProvider, CompletableFuture<TagLookup<Block>> blockTags, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, parentProvider, blockTags, modId, existingFileHelper);
        this.lookupProvider = lookupProvider;
        this.blockTagsFuture = blockTags;
    }

    /** Public accessor for {@link #tag(TagKey)} for use by datagen helpers. */
    public IntrinsicHolderTagsProvider.IntrinsicTagAppender<Item> getTagAppender(TagKey<Item> key) {
        return tag(key);
    }

    @Override
    protected CompletableFuture<HolderLookup.Provider> createContentsProvider() {
        return lookupProvider.thenApply(provider -> {
            addTags(provider);
            return provider;
        }).thenCombine(blockTagsFuture, (provider, tagLookup) -> {
            Map<TagKey<Block>, TagKey<Item>> tagsToCopy = getTagsToCopy();
            tagsToCopy.forEach((block, item) -> {
                TagBuilder tagBuilder = getOrCreateRawBuilder(item);
                tagLookup.apply(block).orElseThrow(() -> new IllegalStateException("Missing block tag " + item.location())).build().forEach(tagBuilder::add);
            });
            return provider;
        });
    }

    @SuppressWarnings("unchecked")
    private Map<TagKey<Block>, TagKey<Item>> getTagsToCopy() {
        try {
            Field f = ItemTagsProvider.class.getDeclaredField("tagsToCopy");
            f.setAccessible(true);
            return (Map<TagKey<Block>, TagKey<Item>>) f.get(this);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access ItemTagsProvider.tagsToCopy", e);
        }
    }
}
