package com.github.minecraftschurlimods.bibliocraft;

import com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi;
import net.minecraftforge.common.ForgeConfigSpec;

public final class BCConfig {
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final ForgeConfigSpec.BooleanValue ENABLE_PRIDE;
    public static final ForgeConfigSpec.BooleanValue ENABLE_PRIDE_ALWAYS;
    public static final ForgeConfigSpec.BooleanValue JEI_SHOW_WOOD_TYPES;
    public static final ForgeConfigSpec.BooleanValue JEI_SHOW_COLOR_TYPES;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder
                .comment("Contains cosmetic options.")
                .push("cosmetic");
        ENABLE_PRIDE = builder
                .comment("Whether to enable pride-themed cosmetics during pride month or not.")
                .translation("config." + BibliocraftApi.MOD_ID + ".cosmetic.enable_pride")
                .worldRestart()
                .define("enable_pride", true);
        ENABLE_PRIDE_ALWAYS = builder
                .comment("Whether to enable pride-themed cosmetics all year or only during pride month. Does nothing if enable_pride is false.")
                .translation("config." + BibliocraftApi.MOD_ID + ".cosmetic.enable_pride_always")
                .worldRestart()
                .define("enable_pride_always", false);
        builder.pop();
        builder
                .comment("Contains compatibility options.")
                .push("compatibility");
        builder
                .comment("Contains compatibility options for the JEI mod.")
                .push("jei");
        JEI_SHOW_WOOD_TYPES = builder
                .comment("Whether to show blocks for all wood types in JEI, or just the default oak.")
                .translation("config." + BibliocraftApi.MOD_ID + ".compatibility.jei.show_wood_types")
                .define("show_wood_types", true);
        JEI_SHOW_COLOR_TYPES = builder
                .comment("Whether to show blocks for all color types in JEI, or just the default white.")
                .translation("config." + BibliocraftApi.MOD_ID + ".compatibility.jei.show_color_types")
                .define("show_color_types", true);
        builder.pop();
        builder.pop();
        CLIENT_SPEC = builder.build();
    }
}
