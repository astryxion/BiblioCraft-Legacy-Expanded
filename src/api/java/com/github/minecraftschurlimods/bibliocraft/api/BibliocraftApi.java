package com.github.minecraftschurlimods.bibliocraft.api;

import com.github.minecraftschurlimods.bibliocraft.api.datagen.BibliocraftDatagenHelper;
import com.github.minecraftschurlimods.bibliocraft.api.lockandkey.LockAndKeyBehaviors;
import com.github.minecraftschurlimods.bibliocraft.api.woodtype.BibliocraftWoodTypeRegistry;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Supplier;

/**
 * The main accessor class for Bibliocraft's API. Use this to get references to the singleton instances of various classes.
 */
public final class BibliocraftApi {
    public static final String MOD_ID = "bibliocraft";

    private BibliocraftApi() {
    }

    /**
     * @return The only instance of {@link BibliocraftDatagenHelper}.
     */
    public static BibliocraftDatagenHelper getDatagenHelper() {
        return InstanceHolder.DATAGEN_HELPER;
    }

    /**
     * @return The only instance of {@link BibliocraftWoodTypeRegistry}.
     */
    public static BibliocraftWoodTypeRegistry getWoodTypeRegistry() {
        return InstanceHolder.WOOD_TYPE_REGISTRY;
    }

    /**
     * @return The only instance of {@link LockAndKeyBehaviors}.
     */
    public static LockAndKeyBehaviors getLockAndKeyBehaviors() {
        return InstanceHolder.LOCK_AND_KEY_BEHAVIORS;
    }

    /**
     * The internal class used to hold the instances. DO NOT ACCESS YOURSELF!
     */
    private static class InstanceHolder {
        private static final BibliocraftDatagenHelper DATAGEN_HELPER = fromServiceLoader(BibliocraftDatagenHelper.class);
        private static final BibliocraftWoodTypeRegistry WOOD_TYPE_REGISTRY = fromServiceLoader(BibliocraftWoodTypeRegistry.class);
        private static final LockAndKeyBehaviors LOCK_AND_KEY_BEHAVIORS = fromServiceLoader(LockAndKeyBehaviors.class);

        private InstanceHolder() {
        }

        private static <T> T fromServiceLoader(Class<T> clazz) {
            java.util.Iterator<T> it = ServiceLoader.load(clazz, Thread.currentThread().getContextClassLoader()).iterator();
            Optional<T> impl = it.hasNext() ? Optional.of(it.next()) : Optional.empty();
            String msg = "Unable to find implementation for " + clazz.getSimpleName() + "!";
            if (!FMLEnvironment.production) {
                return impl.orElseThrow(() -> {
                    IllegalStateException exception = new IllegalStateException(msg);
                    LoggerFactory.getLogger(MOD_ID).error(exception.getMessage(), exception);
                    return exception;
                });
            }
            return impl.orElseGet(() -> {
                LoggerFactory.getLogger(MOD_ID).error(msg);
                return null;
            });
        }
    }
}
