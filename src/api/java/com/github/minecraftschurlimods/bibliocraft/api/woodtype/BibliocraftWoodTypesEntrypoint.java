package com.github.minecraftschurlimods.bibliocraft.api.woodtype;

/**
 * Fabric entrypoint for addon mods to register custom wood types.
 * Implement this interface and expose it via fabric.mod.json entrypoints "bibliocraft:wood_types".
 */
@FunctionalInterface
public interface BibliocraftWoodTypesEntrypoint {
    void onRegisterWoodTypes(RegisterBibliocraftWoodTypesEvent event);
}
