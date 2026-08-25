package com.github.minecraftschurlimods.bibliocraft.datagen.data;

import com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi;
import com.github.minecraftschurlimods.bibliocraft.init.BCTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BCEnchantmentTagsProvider extends net.minecraft.data.TagsProvider<Enchantment> {
    public BCEnchantmentTagsProvider(DataGenerator output, ExistingFileHelper existingFileHelper) {
        super(output, Registry.ENCHANTMENT, BibliocraftApi.MOD_ID, existingFileHelper, "enchantment");
    }

    @Override
    protected java.nio.file.Path getPath(net.minecraft.util.ResourceLocation id) {
        return this.generator.getOutputFolder().resolve("data/" + id.getNamespace() + "/tags/" + this.folder + "/" + id.getPath() + ".json");
    }

    @Override
    public String getName() {
        return "Enchantment Tags: " + BibliocraftApi.MOD_ID;
    }

    @Override
    protected void addTags() {
        tag(BCTags.Enchantments.PRINTING_TABLE_CLONING_BLACKLIST);
    }
}
