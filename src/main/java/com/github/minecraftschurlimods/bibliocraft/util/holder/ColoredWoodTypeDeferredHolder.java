package com.github.minecraftschurlimods.bibliocraft.util.holder;

import com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi;
import com.github.minecraftschurlimods.bibliocraft.api.woodtype.BibliocraftWoodType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.DyeColor;
import net.minecraft.block.WoodType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.RegistryObject;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Registration utility that holds variants of a {@link RegistryObject} for a given list of {@link WoodType}s and each {@link DyeColor}.
 *
 * @param <R> The first type of the {@link RegistryObject} to use. For example, for wrapping a {@code RegistryObject<DoorBlock>}, this would be {@code Block}.
 * @param <T> The second type of the {@link RegistryObject} to use. For example, for wrapping a {@code RegistryObject<DoorBlock>}, this would be {@code DoorBlock}.
 */
@SuppressWarnings("unused")
public class ColoredWoodTypeDeferredHolder<R extends net.minecraftforge.registries.IForgeRegistryEntry<R>, T extends R> implements GroupingDeferredHolder<R, T> {
    private final Map<BibliocraftWoodType, ColoredDeferredHolder<R, T>> map = new LinkedHashMap<>();

    /**
     * Creates a new instance of this class.
     *
     * @param register The registry to use.
     * @param suffix   The suffix to use for the registry names to use. Will be combined with the wood type and the color for the final registry name.
     * @param creator  A function of {@link BibliocraftWoodType} and {@link DyeColor} to {@code T}, responsible for actually creating the {@link RegistryObject}.
     */
    public ColoredWoodTypeDeferredHolder(DeferredRegister<R> register, String suffix, BiFunction<BibliocraftWoodType, DyeColor, ? extends T> creator) {
        for (BibliocraftWoodType type : BibliocraftApi.getWoodTypeRegistry().getAll()) {
            map.put(type, new ColoredDeferredHolder<>(register, type.getRegistrationPrefix() + "_" + suffix, color -> creator.apply(type, color)));
        }
    }

    /**
     * @param type The {@link BibliocraftWoodType} to get the {@link ColoredDeferredHolder} for.
     * @return The {@link ColoredDeferredHolder} for the given {@link BibliocraftWoodType}.
     */
    public ColoredDeferredHolder<R, T> element(BibliocraftWoodType type) {
        return map.get(type);
    }

    /**
     * @param type  The {@link BibliocraftWoodType} to get the {@link RegistryObject} for.
     * @param color The {@link DyeColor} to get the {@link RegistryObject} for.
     * @return The {@link RegistryObject} for the given {@link BibliocraftWoodType}.
     */
    public RegistryObject<T> holder(BibliocraftWoodType type, DyeColor color) {
        return map.get(type).holder(color);
    }

    /**
     * @param type  The {@link BibliocraftWoodType} to get the value of the {@link RegistryObject} for.
     * @param color The {@link DyeColor} to get the value of the {@link RegistryObject} for.
     * @return The value of the {@link DeferredHolder} for the given {@link BibliocraftWoodType}. This is equivalent to calling {@code holder(type).get()}.
     */
    public T get(BibliocraftWoodType type, DyeColor color) {
        return map.get(type).get(color);
    }

    /**
     * @param type  The {@link BibliocraftWoodType} to get the id of the {@link DeferredHolder} for.
     * @param color The {@link DyeColor} to get the id of the {@link DeferredHolder} for.
     * @return The id of the {@link RegistryObject} for the given {@link BibliocraftWoodType}. This is equivalent to calling {@code holder(type).getId()}.
     */
    public ResourceLocation id(BibliocraftWoodType type, DyeColor color) {
        return map.get(type).id(color);
    }

    /**
     * @return An immutable collection of all {@link ColoredDeferredHolder}s in this object.
     */
    public Collection<ColoredDeferredHolder<R, T>> elements() {
        return map.values();
    }

    /**
     * @return An immutable map of all {@link BibliocraftWoodType} to {@link RegistryObject} associations in this object.
     */
    public Map<BibliocraftWoodType, ColoredDeferredHolder<R, T>> map() {
        return Collections.unmodifiableMap(map);
    }

    @Override
    public Collection<RegistryObject<T>> holders() {
        return elements().stream().flatMap(holder -> holder.holders().stream()).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public Collection<T> values() {
        return elements().stream().flatMap(holder -> holder.values().stream()).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public Collection<ResourceLocation> ids() {
        return elements().stream().flatMap(holder -> holder.ids().stream()).collect(java.util.stream.Collectors.toList());
    }
}
