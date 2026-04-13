package com.github.minecraftschurlimods.bibliocraft.api.datagen;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * Loader-agnostic provider for adding translations (e.g. en_us).
 */
public interface TranslationProvider {
    void add(Item item, String translation);
    void add(Block block, String translation);
}
