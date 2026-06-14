package at.minecraftschurli.mods.bibliocraft.test;

import at.minecraftschurli.mods.bibliocraft.api.BibliocraftApi;
import at.minecraftschurli.mods.bibliocraft.api.datagen.BibliocraftDatagenHelper;
import at.minecraftschurli.mods.bibliocraft.api.woodtype.BibliocraftWoodTypeRegistry;
import at.minecraftschurli.mods.bibliocraft.apiimpl.BibliocraftDatagenHelperImpl;
import at.minecraftschurli.mods.bibliocraft.apiimpl.BibliocraftWoodTypeRegistryImpl;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

@SuppressWarnings("ConstantValue")
public class BibliocraftApiTest {
    @GameTest
    public void testDatagenHelperAvailable(GameTestHelper helper) {
        BibliocraftDatagenHelper datagenHelper = BibliocraftApi.getDatagenHelper();
        helper.assertTrue(datagenHelper != null, "BibliocraftDatagenHelper is not available");
        helper.assertTrue(datagenHelper.getClass() == BibliocraftDatagenHelperImpl.class, "BibliocraftDatagenHelper implementation is replaced");
        helper.succeed();
    }

    @GameTest
    public void testWoodTypeRegistryAvailable(GameTestHelper helper) {
        BibliocraftWoodTypeRegistry woodTypeRegistry = BibliocraftApi.getWoodTypeRegistry();
        helper.assertTrue(woodTypeRegistry != null, "BibliocraftWoodTypeRegistry is not available");
        helper.assertTrue(woodTypeRegistry.getClass() == BibliocraftWoodTypeRegistryImpl.class, "BibliocraftWoodTypeRegistry implementation is replaced");
        helper.assertTrue(woodTypeRegistry.get("oak") != null, "Oak WoodType is not registered");
        helper.assertTrue(woodTypeRegistry.get("spruce") != null, "Spruce WoodType is not registered");
        helper.assertTrue(woodTypeRegistry.get("birch") != null, "Birch WoodType is not registered");
        helper.assertTrue(woodTypeRegistry.get("jungle") != null, "Jungle WoodType is not registered");
        helper.assertTrue(woodTypeRegistry.get("acacia") != null, "Acacia WoodType is not registered");
        helper.assertTrue(woodTypeRegistry.get("dark_oak") != null, "Dark Oak WoodType is not registered");
        helper.assertTrue(woodTypeRegistry.get("crimson") != null, "Crimson WoodType is not registered");
        helper.assertTrue(woodTypeRegistry.get("warped") != null, "Warped WoodType is not registered");
        helper.assertTrue(woodTypeRegistry.get("mangrove") != null, "Mangrove WoodType is not registered");
        helper.assertTrue(woodTypeRegistry.get("bamboo") != null, "Bamboo WoodType is not registered");
        helper.assertTrue(woodTypeRegistry.get("cherry") != null, "Cherry WoodType is not registered");
        helper.succeed();
    }
}
