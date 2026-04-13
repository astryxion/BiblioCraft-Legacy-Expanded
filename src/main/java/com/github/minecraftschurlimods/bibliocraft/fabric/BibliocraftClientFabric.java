package com.github.minecraftschurlimods.bibliocraft.fabric;

import net.fabricmc.api.ClientModInitializer;

/**
 * Fabric client initializer. Registers client-only event handlers (screens, renderers, etc.).
 */
public final class BibliocraftClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        com.github.minecraftschurlimods.bibliocraft.client.BCClientEventHandlerFabric.init();
    }
}
