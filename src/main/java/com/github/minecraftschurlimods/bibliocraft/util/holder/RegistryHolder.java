package com.github.minecraftschurlimods.bibliocraft.util.holder;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * Minimal holder interface for registry entries. Provides get() and getId() for compatibility
 * with code that expected NeoForge's DeferredHolder. Fabric's RegistryRef implements this.
 */
public interface RegistryHolder<T> extends Supplier<T> {
    ResourceLocation getId();
}
