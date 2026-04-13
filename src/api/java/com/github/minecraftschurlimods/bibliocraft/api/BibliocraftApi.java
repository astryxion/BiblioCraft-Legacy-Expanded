package com.github.minecraftschurlimods.bibliocraft.api;

import com.github.minecraftschurlimods.bibliocraft.api.datagen.BibliocraftDatagenHelper;
import com.github.minecraftschurlimods.bibliocraft.api.lockandkey.LockAndKeyBehaviors;
import com.github.minecraftschurlimods.bibliocraft.api.woodtype.BibliocraftWoodTypeRegistry;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Supplier;

/**
 * The main accessor class for Bibliocraft's API. Use this to get references to the singleton instances of various classes.
 * Uses ServiceLoader with the API module class loader (Fabric-compatible).
 */
public final class BibliocraftApi {
    public static final String MOD_ID = "bibliocraft";

    private BibliocraftApi() {
    }

    public static BibliocraftDatagenHelper getDatagenHelper() {
        return InstanceHolder.DATAGEN_HELPER.get();
    }

    public static BibliocraftWoodTypeRegistry getWoodTypeRegistry() {
        return InstanceHolder.woodTypeRegistry();
    }

    public static LockAndKeyBehaviors getLockAndKeyBehaviors() {
        return InstanceHolder.LOCK_AND_KEY_BEHAVIORS.get();
    }

    private static class InstanceHolder {
        private static final Supplier<BibliocraftDatagenHelper> DATAGEN_HELPER = fromServiceLoader(BibliocraftDatagenHelper.class);
        private static BibliocraftWoodTypeRegistry woodTypeRegistryCache;
        private static BibliocraftWoodTypeRegistry woodTypeRegistry() {
            if (woodTypeRegistryCache == null) {
                woodTypeRegistryCache = fromServiceLoader(BibliocraftWoodTypeRegistry.class).get();
            }
            return woodTypeRegistryCache;
        }
        private static final Supplier<LockAndKeyBehaviors> LOCK_AND_KEY_BEHAVIORS = fromServiceLoader(LockAndKeyBehaviors.class);

        private static <T> Supplier<T> fromServiceLoader(Class<T> clazz) {
            return () -> {
                Optional<T> impl = ServiceLoader.load(clazz, BibliocraftApi.class.getClassLoader()).findFirst();
                String msg = "Unable to find implementation for " + clazz.getSimpleName() + "!";
                return impl.orElseThrow(() -> {
                    IllegalStateException exception = new IllegalStateException(msg);
                    LoggerFactory.getLogger(MOD_ID).error(exception.getMessage(), exception);
                    return exception;
                });
            };
        }
    }
}
