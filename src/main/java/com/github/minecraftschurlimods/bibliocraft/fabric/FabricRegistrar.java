package com.github.minecraftschurlimods.bibliocraft.fabric;

import com.github.minecraftschurlimods.bibliocraft.util.holder.RegistryHolder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Fabric equivalent of NeoForge's DeferredRegister: collects registrations and applies them in registerAll().
 * Use the returned {@link RegistryRef} as a Supplier; it is populated when registerAll() runs.
 */
public final class FabricRegistrar<T> {
    private final Registry<T> registry;
    private final String namespace;
    private final List<Entry<T>> entries = new ArrayList<>();

    public FabricRegistrar(Registry<T> registry, String namespace) {
        this.registry = registry;
        this.namespace = namespace;
    }

    /**
     * Registers a value. The ref will be populated when {@link #registerAll()} is called.
     *
     * @param path     Registry path (no namespace).
     * @param supplier Supplier of the value to register.
     * @return A ref that will return the registered value after registerAll().
     */
    public RegistryRef<T> register(String path, Supplier<? extends T> supplier) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace, path);
        RegistryRef<T> ref = new RegistryRef<>(id);
        entries.add(new Entry<>(id, supplier, ref));
        return ref;
    }

    /**
     * Applies all collected registrations. Must be called once after all register() calls.
     */
    @SuppressWarnings("unchecked")
    public void registerAll() {
        for (Entry<T> e : entries) {
            T value = (T) e.supplier.get();
            Registry.register(registry, e.id, value);
            e.ref.set(value);
        }
    }

    private record Entry<T>(ResourceLocation id, Supplier<?> supplier, RegistryRef<T> ref) {}

    /**
     * Holds the registered value after registerAll(). Implements RegistryHolder for .get() / .getId() compatibility.
     */
    public static final class RegistryRef<T> implements RegistryHolder<T> {
        private final ResourceLocation id;
        private volatile T value;

        RegistryRef(ResourceLocation id) {
            this.id = id;
        }

        void set(T value) {
            this.value = value;
        }

        @Override
        public T get() {
            return value;
        }

        public ResourceLocation getId() {
            return id;
        }
    }
}
