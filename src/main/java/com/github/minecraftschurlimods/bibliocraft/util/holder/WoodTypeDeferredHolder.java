package com.github.minecraftschurlimods.bibliocraft.util.holder;

import com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi;
import com.github.minecraftschurlimods.bibliocraft.api.woodtype.BibliocraftWoodType;
import com.github.minecraftschurlimods.bibliocraft.fabric.FabricRegistrar;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Registration utility that holds variants for each {@link BibliocraftWoodType}.
 * Uses Fabric's FabricRegistrar; each holder is a RegistryRef.
 */
@SuppressWarnings("unused")
public class WoodTypeDeferredHolder<R, T extends R> implements GroupingDeferredHolder<R, T> {
    private final Map<BibliocraftWoodType, FabricRegistrar.RegistryRef<T>> map = new LinkedHashMap<>();

    public WoodTypeDeferredHolder(FabricRegistrar<R> register, String suffix, Function<BibliocraftWoodType, ? extends T> creator) {
        for (BibliocraftWoodType type : BibliocraftApi.getWoodTypeRegistry().getAll()) {
            @SuppressWarnings("unchecked")
            FabricRegistrar.RegistryRef<T> ref = (FabricRegistrar.RegistryRef<T>) register.register(type.getRegistrationPrefix() + "_" + suffix, () -> creator.apply(type));
            map.put(type, ref);
        }
    }

    public RegistryHolder<T> holder(BibliocraftWoodType type) {
        return map.get(type);
    }

    public T get(BibliocraftWoodType type) {
        RegistryHolder<T> h = holder(type);
        return h == null ? null : h.get();
    }

    public ResourceLocation id(BibliocraftWoodType type) {
        RegistryHolder<T> h = holder(type);
        return h == null ? null : h.getId();
    }

    public Map<BibliocraftWoodType, FabricRegistrar.RegistryRef<T>> map() {
        return Collections.unmodifiableMap(map);
    }

    @Override
    public Collection<RegistryHolder<T>> holders() {
        return Collections.unmodifiableCollection(map.values());
    }

    @Override
    public Collection<T> values() {
        return map.values().stream().map(RegistryHolder::get).toList();
    }

    @Override
    public Collection<ResourceLocation> ids() {
        return map.values().stream().map(RegistryHolder::getId).toList();
    }
}
