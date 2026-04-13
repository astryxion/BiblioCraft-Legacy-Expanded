package com.github.minecraftschurlimods.bibliocraft;

import com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Client-side config (Fabric). Options are loaded from config/bibliocraft.properties.
 */
public final class BCConfig {
    public static boolean ENABLE_PRIDE = true;
    public static boolean ENABLE_PRIDE_ALWAYS = false;
    public static boolean JEI_SHOW_WOOD_TYPES = true;
    public static boolean JEI_SHOW_COLOR_TYPES = true;

    private static final String FILE_NAME = BibliocraftApi.MOD_ID + ".properties";
    private static final String ENABLE_PRIDE_KEY = "cosmetic.enable_pride";
    private static final String ENABLE_PRIDE_ALWAYS_KEY = "cosmetic.enable_pride_always";
    private static final String JEI_SHOW_WOOD_TYPES_KEY = "compatibility.jei.show_wood_types";
    private static final String JEI_SHOW_COLOR_TYPES_KEY = "compatibility.jei.show_color_types";

    public static void load() {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        Path file = configDir.resolve(FILE_NAME);
        if (!Files.exists(file)) {
            save();
            return;
        }
        Properties props = new Properties();
        try (var reader = Files.newBufferedReader(file)) {
            props.load(reader);
            ENABLE_PRIDE = Boolean.parseBoolean(props.getProperty(ENABLE_PRIDE_KEY, "true"));
            ENABLE_PRIDE_ALWAYS = Boolean.parseBoolean(props.getProperty(ENABLE_PRIDE_ALWAYS_KEY, "false"));
            JEI_SHOW_WOOD_TYPES = Boolean.parseBoolean(props.getProperty(JEI_SHOW_WOOD_TYPES_KEY, "true"));
            JEI_SHOW_COLOR_TYPES = Boolean.parseBoolean(props.getProperty(JEI_SHOW_COLOR_TYPES_KEY, "true"));
        } catch (IOException ignored) {
            // Keep defaults
        }
    }

    public static void save() {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        try {
            Files.createDirectories(configDir);
        } catch (IOException ignored) {
            return;
        }
        Path file = configDir.resolve(FILE_NAME);
        Properties props = new Properties();
        props.setProperty(ENABLE_PRIDE_KEY, String.valueOf(ENABLE_PRIDE));
        props.setProperty(ENABLE_PRIDE_ALWAYS_KEY, String.valueOf(ENABLE_PRIDE_ALWAYS));
        props.setProperty(JEI_SHOW_WOOD_TYPES_KEY, String.valueOf(JEI_SHOW_WOOD_TYPES));
        props.setProperty(JEI_SHOW_COLOR_TYPES_KEY, String.valueOf(JEI_SHOW_COLOR_TYPES));
        try (var writer = Files.newBufferedWriter(file)) {
            props.store(writer, "Bibliocraft config");
        } catch (IOException ignored) {
            // Ignore
        }
    }
}
