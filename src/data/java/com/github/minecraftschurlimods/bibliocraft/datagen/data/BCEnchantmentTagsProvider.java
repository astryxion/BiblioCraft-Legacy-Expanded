package com.github.minecraftschurlimods.bibliocraft.datagen.data;

import com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi;
import com.github.minecraftschurlimods.bibliocraft.init.BCTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class BCEnchantmentTagsProvider extends net.minecraft.data.tags.TagsProvider<Enchantment> {
    public BCEnchantmentTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, Registries.ENCHANTMENT, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BCTags.Enchantments.PRINTING_TABLE_CLONING_BLACKLIST);
    }
}
