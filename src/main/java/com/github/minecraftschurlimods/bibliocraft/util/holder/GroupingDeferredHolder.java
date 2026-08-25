package com.github.minecraftschurlimods.bibliocraft.util.holder;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

import java.util.Collection;

/**
 * Represents a group of {@link RegistryObject}s.
 */
public interface GroupingDeferredHolder<R extends net.minecraftforge.registries.IForgeRegistryEntry<R>, T extends R> {
    /**
     * @return An immutable collection of all {@link RegistryObject}s in this object.
     */
    Collection<RegistryObject<T>> holders();

    /**
     * @return An immutable collection of values of all {@link RegistryObject}s in this object.
     */
    Collection<T> values();

    /**
     * @return An immutable collection of ids of all {@link RegistryObject}s in this object.
     */
    Collection<ResourceLocation> ids();
}
