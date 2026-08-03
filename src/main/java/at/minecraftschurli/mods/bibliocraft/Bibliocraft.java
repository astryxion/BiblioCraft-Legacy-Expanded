package at.minecraftschurli.mods.bibliocraft;

import at.minecraftschurli.mods.bibliocraft.api.BibliocraftApi;
import at.minecraftschurli.mods.bibliocraft.apiimpl.BibliocraftWoodTypeRegistryImpl;
import at.minecraftschurli.mods.bibliocraft.apiimpl.LockAndKeyBehaviorsImpl;
import at.minecraftschurli.mods.bibliocraft.init.BCRegistries;
import net.fabricmc.api.ModInitializer;

public final class Bibliocraft implements ModInitializer {
    @Override
    public void onInitialize() {
        ((BibliocraftWoodTypeRegistryImpl) BibliocraftApi.getWoodTypeRegistry()).register();
        BCRegistries.init();
        BCRegistries.registerFabric();
        ((LockAndKeyBehaviorsImpl) BibliocraftApi.getLockAndKeyBehaviors()).register();
        BCEventHandler.registerFabric();
    }
}
