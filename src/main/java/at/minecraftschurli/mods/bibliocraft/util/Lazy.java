package at.minecraftschurli.mods.bibliocraft.util;

import java.util.function.Supplier;

public final class Lazy<T> implements Supplier<T> {
    private final Supplier<T> delegate;
    private T value;
    private boolean initialized;

    private Lazy(Supplier<T> delegate) {
        this.delegate = delegate;
    }

    public static <T> Lazy<T> of(Supplier<T> delegate) {
        return new Lazy<>(delegate);
    }

    @Override
    public T get() {
        if (!initialized) {
            value = delegate.get();
            initialized = true;
        }
        return value;
    }
}
