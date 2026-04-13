package com.github.minecraftschurlimods.bibliocraft.datagen;

import com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi;
import com.github.minecraftschurlimods.bibliocraft.api.datagen.BibliocraftDatagenHelper;
import com.github.minecraftschurlimods.bibliocraft.api.datagen.DatagenContext;
import com.github.minecraftschurlimods.bibliocraft.datagen.assets.BCBlockStateProvider;
import com.github.minecraftschurlimods.bibliocraft.datagen.assets.BCEnglishLanguageProvider;
import com.github.minecraftschurlimods.bibliocraft.datagen.assets.BCItemModelProvider;
import com.github.minecraftschurlimods.bibliocraft.datagen.assets.BCSoundDefinitionsProvider;
import com.github.minecraftschurlimods.bibliocraft.datagen.data.BCBlockTagsProvider;
import com.github.minecraftschurlimods.bibliocraft.datagen.data.BCEnchantmentTagsProvider;
import com.github.minecraftschurlimods.bibliocraft.datagen.data.BCItemTagsProvider;
import com.github.minecraftschurlimods.bibliocraft.datagen.data.BCLootTableProvider;
import com.github.minecraftschurlimods.bibliocraft.datagen.data.BCRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.data.DataProvider;

public final class BCDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        BCEnglishLanguageProvider language = pack.addProvider(BCEnglishLanguageProvider::new);
        pack.addProvider(BCBlockStateProvider::new);
        pack.addProvider(BCItemModelProvider::new);
        pack.addProvider(BCSoundDefinitionsProvider::new);

        pack.addProvider(BCLootTableProvider::new);
        pack.addProvider(BCRecipeProvider::new);
        BCBlockTagsProvider blockTags = pack.addProvider((output, lookup) -> new BCBlockTagsProvider(output, lookup));
        BCItemTagsProvider itemTags = pack.addProvider((output, lookup) -> new BCItemTagsProvider(output, lookup, blockTags.contentsGetter()));
        pack.addProvider(BCEnchantmentTagsProvider::new);

        DatagenContext context = new FabricDatagenContext(pack);
        BibliocraftDatagenHelper helper = BibliocraftApi.getDatagenHelper();
        helper.addWoodTypesToGenerateByModid("minecraft");
        helper.generateAll(BibliocraftApi.MOD_ID, context, language::asTranslationProvider, blockTags, itemTags);
    }

    private record FabricDatagenContext(FabricDataGenerator.Pack pack) implements DatagenContext {
        @Override
        public net.minecraft.data.PackOutput getPackOutput() {
            return pack.getOutput();
        }

        @Override
        public java.util.concurrent.CompletableFuture<net.minecraft.core.HolderLookup.Provider> getLookupProvider() {
            return pack.getRegistryLookup();
        }

        @Override
        public void addProvider(DataProvider provider) {
            pack.addProvider(provider);
        }
    }
}
