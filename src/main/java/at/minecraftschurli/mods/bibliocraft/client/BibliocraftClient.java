package at.minecraftschurli.mods.bibliocraft.client;

import at.minecraftschurli.mods.bibliocraft.BCConfig;
import net.fabricmc.api.ClientModInitializer;

public final class BibliocraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BCConfig.load();
        BCClientEventHandler.registerFabric();
    }
}
