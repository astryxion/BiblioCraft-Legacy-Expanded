package com.github.minecraftschurlimods.bibliocraft.apiimpl;

import com.github.minecraftschurlimods.bibliocraft.api.woodtype.BibliocraftWoodType;
import com.github.minecraftschurlimods.bibliocraft.api.woodtype.BibliocraftWoodTypeRegistry;
import com.github.minecraftschurlimods.bibliocraft.api.woodtype.RegisterBibliocraftWoodTypesEvent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.SequencedMap;

public final class BibliocraftWoodTypeRegistryImpl implements BibliocraftWoodTypeRegistry {
    private final SequencedMap<ResourceLocation, BibliocraftWoodType> values;
    private boolean loaded = false;

    @ApiStatus.Internal
    public BibliocraftWoodTypeRegistryImpl() {
        values = new LinkedHashMap<>();
    }

    @ApiStatus.Internal
    public void register() {
        // Fabric: run registration directly without event bus.
        registerForFabric();
    }

    /** Called from Fabric init to run registration without the event bus. Idempotent. */
    @ApiStatus.Internal
    public void registerForFabric() {
        if (loaded) return;
        SequencedMap<ResourceLocation, BibliocraftWoodType> registrar = new LinkedHashMap<>();
        RegisterBibliocraftWoodTypesEvent event = new RegisterBibliocraftWoodTypesEvent(registrar);
        com.github.minecraftschurlimods.bibliocraft.fabric.BCEventHandlerFabric.registerBibliocraftWoodTypes(event);
        net.fabricmc.loader.api.FabricLoader.getInstance().getEntrypoints("bibliocraft:wood_types", com.github.minecraftschurlimods.bibliocraft.api.woodtype.BibliocraftWoodTypesEntrypoint.class)
                .forEach(ep -> ep.onRegisterWoodTypes(event));
        registrar.sequencedEntrySet()
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
        return values.sequencedValues();
    }

    private static int compareRLMinecraftFirst(ResourceLocation a, ResourceLocation b) {
        String namespaceA = a.getNamespace(), namespaceB = b.getNamespace();
        if (namespaceA.equals(namespaceB)) return 0;
        if (namespaceA.equals("minecraft")) return -1;
        if (namespaceB.equals("minecraft")) return 1;
        return namespaceA.compareTo(namespaceB);
    }
}
