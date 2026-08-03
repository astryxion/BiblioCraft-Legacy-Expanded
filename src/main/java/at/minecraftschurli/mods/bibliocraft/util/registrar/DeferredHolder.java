package at.minecraftschurli.mods.bibliocraft.util.registrar;

import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.Registry;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public class DeferredHolder<R, T extends R> implements Supplier<T> {
    private final ResourceKey<R> key;
    private @Nullable T value;

    protected DeferredHolder(ResourceKey<R> key) {
        this.key = key;
    }

    protected static <R, T extends R> DeferredHolder<R, T> create(ResourceKey<? extends Registry<R>> registryKey, Identifier location) {
        return new DeferredHolder<>(ResourceKey.create(registryKey, location));
    }

    public Identifier getId() {
        return key.identifier();
    }

    public ResourceKey<R> getKey() {
        return key;
    }

    @SuppressWarnings("unchecked")
    public T getValue() {
        if (value == null) {
            throw new IllegalStateException("DeferredHolder " + key.identifier() + " has not been registered yet");
        }
        return value;
    }

    public boolean isBound() {
        return value != null;
    }

    void bind(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return getValue();
    }
}
