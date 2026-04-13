package com.github.minecraftschurlimods.bibliocraft.api.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

/**
 * Block tag provider that does not clear tag builders before calling {@link #addTags(HolderLookup.Provider)}.
 * Extends {@link IntrinsicHolderTagsProvider} for blocks (1.21.1 has no separate BlockTagsProvider).
 */
public abstract class NonClearingBlockTagsProvider extends IntrinsicHolderTagsProvider<Block> {
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;

    public NonClearingBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId) {
        super(output, Registries.BLOCK, lookupProvider, block -> ResourceKey.create(Registries.BLOCK, BuiltInRegistries.BLOCK.getKey(block)));
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
