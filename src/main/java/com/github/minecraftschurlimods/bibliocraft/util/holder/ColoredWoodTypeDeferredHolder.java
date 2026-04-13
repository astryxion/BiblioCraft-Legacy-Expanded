package com.github.minecraftschurlimods.bibliocraft.util.holder;

import com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi;
import com.github.minecraftschurlimods.bibliocraft.api.woodtype.BibliocraftWoodType;
import com.github.minecraftschurlimods.bibliocraft.fabric.FabricRegistrar;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Registration utility that holds colored variants for each {@link BibliocraftWoodType} and {@link DyeColor}.
 * Uses Fabric's FabricRegistrar; each holder is a RegistryRef.
 */
@SuppressWarnings("unused")
public class ColoredWoodTypeDeferredHolder<R, T extends R> implements GroupingDeferredHolder<R, T> {
    private final Map<BibliocraftWoodType, ColoredDeferredHolder<R, T>> map = new LinkedHashMap<>();

    public ColoredWoodTypeDeferredHolder(FabricRegistrar<R> register, String suffix, BiFunction<BibliocraftWoodType, DyeColor, ? extends T> creator) {
        for (BibliocraftWoodType type : BibliocraftApi.getWoodTypeRegistry().getAll()) {
            map.put(type, new ColoredDeferredHolder<>(register, type.getRegistrationPrefix() + "_" + suffix, color -> creator.apply(type, color)));
        }
    }

    public ColoredDeferredHolder<R, T> element(BibliocraftWoodType type) {
        return map.get(type);
    }

    public RegistryHolder<T> holder(BibliocraftWoodType type, DyeColor color) {
        return map.get(type).holder(color);
    }

    public T get(BibliocraftWoodType type, DyeColor color) {
        return map.get(type).get(color);
    }

    public ResourceLocation id(BibliocraftWoodType type, DyeColor color) {
        return map.get(type).id(color);
    }

    public Collection<ColoredDeferredHolder<R, T>> elements() {
        return Collections.unmodifiableCollection(map.values());
    }

    public Map<BibliocraftWoodType, ColoredDeferredHolder<R, T>> map() {
        return Collections.unmodifiableMap(map);
    }

    @Override
    public Collection<RegistryHolder<T>> holders() {
        return map.values().stream().flatMap(holder -> holder.holders().stream()).toList();
    }

    @Override
    public Collection<T> values() {
        return map.values().stream().flatMap(holder -> holder.values().stream()).toList();
    }

    @Override
    public Collection<ResourceLocation> ids() {
        return map.values().stream().flatMap(holder -> holder.ids().stream()).toList();
    }
}
