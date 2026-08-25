package com.github.minecraftschurlimods.bibliocraft.datagen;

import com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi;
import com.github.minecraftschurlimods.bibliocraft.api.datagen.BibliocraftDatagenHelper;
import com.github.minecraftschurlimods.bibliocraft.datagen.assets.BCBlockStateProvider;
import com.github.minecraftschurlimods.bibliocraft.datagen.assets.BCEnglishLanguageProvider;
import com.github.minecraftschurlimods.bibliocraft.datagen.assets.BCItemModelProvider;
import com.github.minecraftschurlimods.bibliocraft.datagen.assets.BCSoundDefinitionsProvider;
import com.github.minecraftschurlimods.bibliocraft.datagen.data.BCBlockTagsProvider;
import com.github.minecraftschurlimods.bibliocraft.datagen.data.BCEnchantmentTagsProvider;
import com.github.minecraftschurlimods.bibliocraft.datagen.data.BCItemTagsProvider;
import com.github.minecraftschurlimods.bibliocraft.datagen.data.BCLootTableProvider;
import com.github.minecraftschurlimods.bibliocraft.datagen.data.BCRecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = BibliocraftApi.MOD_ID)
public final class BCDatagen {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        BCEnglishLanguageProvider language = new BCEnglishLanguageProvider(generator);
        if (event.includeClient()) {
            generator.addProvider(language);
            generator.addProvider(new BCBlockStateProvider(generator, existingFileHelper));
            generator.addProvider(new BCItemModelProvider(generator, existingFileHelper));
            generator.addProvider(new BCSoundDefinitionsProvider(generator, existingFileHelper));
        }

        BCBlockTagsProvider blockTags = new BCBlockTagsProvider(generator, existingFileHelper);
        BCItemTagsProvider itemTags = new BCItemTagsProvider(generator, blockTags, existingFileHelper);
        if (event.includeServer()) {
            generator.addProvider(new BCLootTableProvider(generator));
            generator.addProvider(new BCRecipeProvider(generator));
            generator.addProvider(blockTags);
            generator.addProvider(itemTags);
            generator.addProvider(new BCEnchantmentTagsProvider(generator, existingFileHelper));
        }

        BibliocraftDatagenHelper helper = BibliocraftApi.getDatagenHelper();
        helper.addWoodTypesToGenerateByModid("minecraft");
        helper.generateAll(BibliocraftApi.MOD_ID, event, language, blockTags, itemTags);
    }
}
