package com.github.minecraftschurlimods.bibliocraft.util.holder;

import com.github.minecraftschurlimods.bibliocraft.fabric.FabricRegistrar;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Registration utility that holds variants for each {@link DyeColor}.
 * Uses Fabric's FabricRegistrar; each holder is a RegistryRef.
 */
@SuppressWarnings("unused")
public class ColoredDeferredHolder<R, T extends R> implements GroupingDeferredHolder<R, T> {
    private final Map<DyeColor, FabricRegistrar.RegistryRef<T>> map = new LinkedHashMap<>();

    public ColoredDeferredHolder(FabricRegistrar<R> register, String suffix, Function<DyeColor, ? extends T> creator) {
        for (DyeColor color : DyeColor.values()) {
            @SuppressWarnings("unchecked")
            FabricRegistrar.RegistryRef<T> ref = (FabricRegistrar.RegistryRef<T>) register.register(color.getName() + "_" + suffix, () -> creator.apply(color));
            map.put(color, ref);
        }
    }

    public RegistryHolder<T> holder(DyeColor color) {
        return map.get(color);
    }

    public T get(DyeColor color) {
        return map.get(color).get();
    }

    public ResourceLocation id(DyeColor color) {
        return map.get(color).getId();
    }

    public Map<DyeColor, FabricRegistrar.RegistryRef<T>> map() {
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
