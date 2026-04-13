package com.github.minecraftschurlimods.bibliocraft.util.holder;

import net.minecraft.resources.ResourceLocation;

import java.util.Collection;

/**
 * Represents a group of registry holders (Fabric: RegistryRef; was NeoForge DeferredHolder).
 */
public interface GroupingDeferredHolder<R, T extends R> {
    /**
     * @return An immutable collection of all holders in this object.
     */
    Collection<RegistryHolder<T>> holders();

    /**
     * @return An immutable collection of values of all holders in this object.
     */
    Collection<T> values();

    /**
     * @return An immutable collection of ids of all holders in this object.
     */
    Collection<ResourceLocation> ids();
}
