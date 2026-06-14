package at.minecraftschurli.mods.bibliocraft.api;

import at.minecraftschurli.mods.bibliocraft.api.datagen.BibliocraftDatagenHelper;
import at.minecraftschurli.mods.bibliocraft.api.lockandkey.LockAndKeyBehaviors;
import at.minecraftschurli.mods.bibliocraft.api.woodtype.BibliocraftWoodTypeRegistry;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;
import java.util.function.Supplier;

/// The main accessor class for Bibliocraft's API. Use this to get references to the singleton instances of various classes.
public final class BibliocraftApi {
    public static final String MOD_ID = "bibliocraft";

    private BibliocraftApi() {
    }

    /// @return The only instance of [BibliocraftDatagenHelper].
    public static BibliocraftDatagenHelper getDatagenHelper() {
        return InstanceHolder.DATAGEN_HELPER.get();
    }

    /// @return The only instance of [BibliocraftWoodTypeRegistry].
    public static BibliocraftWoodTypeRegistry getWoodTypeRegistry() {
        return InstanceHolder.WOOD_TYPE_REGISTRY.get();
    }

    /// @return The only instance of [LockAndKeyBehaviors].
    public static LockAndKeyBehaviors getLockAndKeyBehaviors() {
        return InstanceHolder.LOCK_AND_KEY_BEHAVIORS.get();
    }

    /// The internal class used to hold the instances. DO NOT ACCESS YOURSELF!
    private static class InstanceHolder {
        private static final Supplier<BibliocraftDatagenHelper> DATAGEN_HELPER = memoize(fromServiceLoader(BibliocraftDatagenHelper.class));
        private static final Supplier<BibliocraftWoodTypeRegistry> WOOD_TYPE_REGISTRY = memoize(fromServiceLoader(BibliocraftWoodTypeRegistry.class));
        private static final Supplier<LockAndKeyBehaviors> LOCK_AND_KEY_BEHAVIORS = memoize(fromServiceLoader(LockAndKeyBehaviors.class));

        private InstanceHolder() {
        }

        private static <T> Supplier<T> fromServiceLoader(Class<T> clazz) {
            return () -> ServiceLoader.load(clazz, Thread.currentThread().getContextClassLoader()).findFirst().orElseThrow(() -> {
                IllegalStateException exception = new IllegalStateException("Unable to find implementation for " + clazz.getSimpleName() + "!");
                LoggerFactory.getLogger(MOD_ID).error(exception.getMessage(), exception);
                return exception;
            });
        }

        private static <T> Supplier<T> memoize(Supplier<T> delegate) {
            return new Supplier<>() {
                private T value;

                @Override
                public T get() {
                    if (this.value == null) {
                        this.value = delegate.get();
                    }
                    return this.value;
                }
            };
        }
    }
}
