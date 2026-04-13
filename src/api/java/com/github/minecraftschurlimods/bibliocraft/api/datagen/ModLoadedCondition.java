package com.github.minecraftschurlimods.bibliocraft.api.datagen;

/**
 * Data condition indicating that a loot table (or recipe) should only be loaded when the given mod is present.
 * Used with {@link BlockLootTableProvider.WithConditionsBuilder#addCondition(BlockLootTableProvider.DataCondition...)}.
 * On Fabric, this condition is not serialized into the loot table JSON; the same API is preserved for compatibility.
 */
public record ModLoadedCondition(String modId) implements BlockLootTableProvider.DataCondition {
}
