package com.github.minecraftschurlimods.bibliocraft.fabric;

import com.github.minecraftschurlimods.bibliocraft.init.BCRegistries;
import com.github.minecraftschurlimods.bibliocraft.util.block.ContainerSyncScheduler;
import net.fabricmc.api.ModInitializer;

/**
 * Fabric mod initializer. Replaces the NeoForge @Mod constructor.
 */
public final class BibliocraftFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        BCRegistries.init();
        BCEventHandlerFabric.init();
        ContainerSyncScheduler.register();
    }
}
