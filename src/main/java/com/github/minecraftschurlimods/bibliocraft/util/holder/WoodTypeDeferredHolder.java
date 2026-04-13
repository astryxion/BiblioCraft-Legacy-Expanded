package com.github.minecraftschurlimods.bibliocraft.util.holder;

import com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi;
import com.github.minecraftschurlimods.bibliocraft.api.woodtype.BibliocraftWoodType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Registration utility that holds variants of a {@link DeferredHolder} for a given list of {@link BibliocraftWoodType}s.
 *
 * @param <R> The first type of the {@link DeferredHolder} to use. For example, for wrapping a {@code DeferredHolder<Block, DoorBlock>}, this would be {@code Block}.
 * @param <T> The second type of the {@link DeferredHolder} to use. For example, for wrapping a {@code DeferredHolder<Block, DoorBlock>}, this would be {@code DoorBlock}.
 */
@SuppressWarnings("unused")
public class WoodTypeDeferredHolder<R, T extends R> implements GroupingDeferredHolder<R, T> {
    private final Map<BibliocraftWoodType, RegistryObject<T>> map = new LinkedHashMap<>();

    /**
     * Creates a new instance of this class.
     *
     * @param register The registry to use.
     * @param suffix   The suffix to use for the registry names to use. Will be combined with the wood type for the final registry name.
     * @param creator  A function of {@link BibliocraftWoodType} to {@code T}, responsible for actually creating the {@link RegistryObject}.
     */
    public WoodTypeDeferredHolder(DeferredRegister<R> register, String suffix, Function<BibliocraftWoodType, ? extends T> creator) {
        for (BibliocraftWoodType type : BibliocraftApi.getWoodTypeRegistry().getAll()) {
            map.put(type, register.register(type.getRegistrationPrefix() + "_" + suffix, () -> creator.apply(type)));
        }
    }

    /**
     * @param type The {@link BibliocraftWoodType} to get the {@link RegistryObject} for.
     * @return The {@link RegistryObject} for the given {@link BibliocraftWoodType}.
     */
    public RegistryObject<T> holder(BibliocraftWoodType type) {
        return map.get(type);
    }

    /**
     * @param type The {@link BibliocraftWoodType} to get the value of the {@link RegistryObject} for.
     * @return The value of the {@link RegistryObject} for the given {@link BibliocraftWoodType}. This is equivalent to calling {@code holder(type).get()}.
     */
    public T get(BibliocraftWoodType type) {
        RegistryObject<T> holder = holder(type);
        if (holder == null) return null;
        return holder.get();
    }

    /**
     * @param type The {@link BibliocraftWoodType} to get the id of the {@link RegistryObject} for.
     * @return The id of the {@link RegistryObject} for the given {@link BibliocraftWoodType}. This is equivalent to calling {@code holder(type).getId()}.
     */
    public ResourceLocation id(BibliocraftWoodType type) {
        RegistryObject<T> holder = holder(type);
        if (holder == null) return null;
        return holder.getId();
    }

    /**
     * @return An immutable map of all {@link BibliocraftWoodType} to {@link RegistryObject} associations in this object.
     */
    public Map<BibliocraftWoodType, RegistryObject<T>> map() {
        return Collections.unmodifiableMap(map);
    }

    @Override
    public Collection<RegistryObject<T>> holders() {
        return map.values();
    }

    @Override
    public Collection<T> values() {
        return map.values().stream().map(RegistryObject::get).toList();
    }

    @Override
    public Collection<ResourceLocation> ids() {
        return map.values().stream().map(RegistryObject::getId).toList();
    }
}
