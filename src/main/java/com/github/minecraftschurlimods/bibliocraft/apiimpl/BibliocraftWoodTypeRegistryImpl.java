package com.github.minecraftschurlimods.bibliocraft.apiimpl;

import com.github.minecraftschurlimods.bibliocraft.api.woodtype.BibliocraftWoodType;
import com.github.minecraftschurlimods.bibliocraft.api.woodtype.BibliocraftWoodTypeRegistry;
import com.github.minecraftschurlimods.bibliocraft.api.woodtype.RegisterBibliocraftWoodTypesEvent;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class BibliocraftWoodTypeRegistryImpl implements BibliocraftWoodTypeRegistry {
    private final Map<ResourceLocation, BibliocraftWoodType> values;
    private boolean loaded = false;

    
    public BibliocraftWoodTypeRegistryImpl() {
        values = new LinkedHashMap<>();
    }

    
    public void register() {
        Map<ResourceLocation, BibliocraftWoodType> registrar = new LinkedHashMap<>();
        FMLJavaModLoadingContext.get().getModEventBus().post(new RegisterBibliocraftWoodTypesEvent(registrar));
        new ArrayList<>(registrar.entrySet())
                .stream()
                .sorted((a, b) -> compareRLMinecraftFirst(a.getKey(), b.getKey()))
                .forEach(e -> values.put(e.getKey(), e.getValue()));
        loaded = true;
    }

    @Override
    @Nullable
    public BibliocraftWoodType get(ResourceLocation id) {
        if (!loaded)
            throw new IllegalStateException("Tried to access BibliocraftWoodTypeRegistry#get() before registration was done!");
        return values.get(id);
    }

    @Override
    public Collection<BibliocraftWoodType> getAll() {
        if (!loaded)
            throw new IllegalStateException("Tried to access BibliocraftWoodTypeRegistry#getAll() before registration was done!");
        return values.values();
    }

    private static int compareRLMinecraftFirst(ResourceLocation a, ResourceLocation b) {
        String namespaceA = a.getNamespace(), namespaceB = b.getNamespace();
        if (namespaceA.equals(namespaceB)) return 0;
        if (namespaceA.equals("minecraft")) return -1;
        if (namespaceB.equals("minecraft")) return 1;
        return namespaceA.compareTo(namespaceB);
    }
}
