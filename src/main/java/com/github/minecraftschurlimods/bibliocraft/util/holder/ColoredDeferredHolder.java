package com.github.minecraftschurlimods.bibliocraft.util.holder;

import net.minecraft.util.ResourceLocation;
import net.minecraft.item.DyeColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.RegistryObject;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Registration utility that holds variants of a {@link RegistryObject} for each {@link DyeColor}.
 *
 * @param <R> The first type of the {@link RegistryObject} to use. For example, for wrapping a {@code RegistryObject<DoorBlock>}, this would be {@code Block}.
 * @param <T> The second type of the {@link RegistryObject} to use. For example, for wrapping a {@code RegistryObject<DoorBlock>}, this would be {@code DoorBlock}.
 */
@SuppressWarnings("unused")
public class ColoredDeferredHolder<R extends net.minecraftforge.registries.IForgeRegistryEntry<R>, T extends R> implements GroupingDeferredHolder<R, T> {
    private final Map<DyeColor, RegistryObject<T>> map = new LinkedHashMap<>();

    /**
     * Creates a new instance of this class.
     *
     * @param register The registry to use.
     * @param suffix   The suffix to use for the registry names to use. Will be combined with the wood type for the final registry name.
     * @param creator  A function of {@link DyeColor} to {@code T}, responsible for actually creating the {@link RegistryObject}.
     */
    public ColoredDeferredHolder(DeferredRegister<R> register, String suffix, Function<DyeColor, ? extends T> creator) {
        for (DyeColor type : DyeColor.values()) {
            map.put(type, register.register(type.getName() + "_" + suffix, () -> creator.apply(type)));
        }
    }

    /**
     * @param color The {@link DyeColor} to get the {@link RegistryObject} for.
     * @return The {@link RegistryObject} for the given {@link DyeColor}.
     */
    public RegistryObject<T> holder(DyeColor color) {
        return map.get(color);
    }

    /**
     * @param color The {@link DyeColor} to get the value of the {@link RegistryObject} for.
     * @return The value of the {@link RegistryObject} for the given {@link DyeColor}. This is equivalent to calling {@code holder(color).get()}.
     */
    public T get(DyeColor color) {
        return map.get(color).get();
    }

    /**
     * @param color The {@link DyeColor} to get the id of the {@link RegistryObject} for.
     * @return The id of the {@link RegistryObject} for the given {@link DyeColor}. This is equivalent to calling {@code holder(color).getId()}.
     */
    public ResourceLocation id(DyeColor color) {
        return map.get(color).getId();
    }

    /**
     * @return An immutable map of all {@link DyeColor} to {@link RegistryObject} associations in this object.
     */
    public Map<DyeColor, RegistryObject<T>> map() {
        return Collections.unmodifiableMap(map);
    }

    @Override
    public Collection<RegistryObject<T>> holders() {
        return map.values();
    }

    @Override
    public Collection<T> values() {
        return map.values().stream().map(RegistryObject::get).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public Collection<ResourceLocation> ids() {
        return map.values().stream().map(RegistryObject::getId).collect(java.util.stream.Collectors.toList());
    }
}
