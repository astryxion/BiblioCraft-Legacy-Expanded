package com.github.minecraftschurlimods.bibliocraft.util;

import net.fabricmc.loader.api.FabricLoader;

/**
 * Contains various compatibility hooks.
 */
public final class CompatUtil {
    /**
     * @return Whether Bibliocraft should register its own config screen or not.
     */
    public static boolean hasConfigScreen() {
        return !FabricLoader.getInstance().isModLoaded("configured");
    }

    /**
     * @return Whether soul candles are expected to be present in the game.
     */
    public static boolean hasSoulCandles() {
        return FabricLoader.getInstance().isModLoaded("buzzier_bees");
    }
}
