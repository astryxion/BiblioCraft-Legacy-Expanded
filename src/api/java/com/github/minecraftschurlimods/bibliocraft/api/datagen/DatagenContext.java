package com.github.minecraftschurlimods.bibliocraft.api.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

/**
 * Loader-agnostic context for data generation. Supplies output, registry lookup and a way to register providers.
 */
public interface DatagenContext {
    PackOutput getPackOutput();
    CompletableFuture<HolderLookup.Provider> getLookupProvider();
    /** Register a data provider (e.g. for client-only or server-only generation). */
    void addProvider(DataProvider provider);
}
