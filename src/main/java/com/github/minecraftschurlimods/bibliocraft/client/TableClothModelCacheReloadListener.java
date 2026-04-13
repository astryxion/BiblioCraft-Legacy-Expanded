package com.github.minecraftschurlimods.bibliocraft.client;

import com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi;
import com.github.minecraftschurlimods.bibliocraft.client.ber.BookcaseBER;
import com.github.minecraftschurlimods.bibliocraft.client.ber.TableBER;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

/**
 * Clears the table cloth model cache on resource reload so it is rebuilt on next use
 * (after ModelManager has finished loading).
 */
public final class TableClothModelCacheReloadListener implements SimpleSynchronousResourceReloadListener {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(BibliocraftApi.MOD_ID, "table_cloth_model_cache");

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        TableBER.clearClothModelCache();
        BookcaseBER.clearBookModelCache();
    }
}
