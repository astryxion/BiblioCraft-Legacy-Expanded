package com.github.minecraftschurlimods.bibliocraft;

import com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi;
import com.github.minecraftschurlimods.bibliocraft.apiimpl.BibliocraftWoodTypeRegistryImpl;
import com.github.minecraftschurlimods.bibliocraft.apiimpl.LockAndKeyBehaviorsImpl;
import com.github.minecraftschurlimods.bibliocraft.init.BCRegistries;
import net.minecraftforge.fml.ModLoadingContext;
import com.github.minecraftschurlimods.bibliocraft.BCConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(value = BibliocraftApi.MOD_ID)
public final class Bibliocraft {
    public Bibliocraft() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, BCConfig.CLIENT_SPEC);
        net.minecraftforge.eventbus.api.IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BCEventHandler.init(bus);
        ((BibliocraftWoodTypeRegistryImpl) BibliocraftApi.getWoodTypeRegistry()).register();
        ((LockAndKeyBehaviorsImpl) BibliocraftApi.getLockAndKeyBehaviors()).register();
        BCRegistries.init(bus);
    }
}
