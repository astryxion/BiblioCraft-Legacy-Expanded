package com.github.minecraftschurlimods.bibliocraft.init;

import com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.SoundEvent;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public interface BCRegistries {
    // @formatter:off
    DeferredRegister<net.minecraft.block.Block> BLOCKS             = DeferredRegister.create(ForgeRegistries.BLOCKS, BibliocraftApi.MOD_ID);
    DeferredRegister<net.minecraft.item.Item>         ITEMS              = DeferredRegister.create(ForgeRegistries.ITEMS, BibliocraftApi.MOD_ID);
    DeferredRegister<TileEntityType<?>>     BLOCK_ENTITIES     = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES,          BibliocraftApi.MOD_ID);
    DeferredRegister<EntityType<?>>          ENTITIES           = DeferredRegister.create(ForgeRegistries.ENTITIES,                BibliocraftApi.MOD_ID);
    DeferredRegister<ContainerType<?>>             MENUS              = DeferredRegister.create(ForgeRegistries.CONTAINERS,                  BibliocraftApi.MOD_ID);
    DeferredRegister<IRecipeSerializer<?>>    RECIPE_SERIALIZERS  = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS,          BibliocraftApi.MOD_ID);
    DeferredRegister<com.github.minecraftschurlimods.bibliocraft.content.printingtable.EnchantmentLevelsNumberProvider.LootNumberProviderType> NUMBER_PROVIDERS = DeferredRegister.create(com.github.minecraftschurlimods.bibliocraft.content.printingtable.EnchantmentLevelsNumberProvider.LootNumberProviderType.class, BibliocraftApi.MOD_ID);
    DeferredRegister<SoundEvent>             SOUND_EVENTS       = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS,                BibliocraftApi.MOD_ID);
    // @formatter:on

    /**
     * Central registration method. Classloads the registration classes and registers the registries to the mod bus.
     */
    static void init(IEventBus bus) {
        BCTags.Items.FORGE_INGOTS_COPPER.getName();
        BCBlocks.init();
        BCItems.init();
        BCCreativeTabs.init();
        BCBlockEntities.init();
        BCEntities.init();
        BCMenus.init();
        BCRecipes.init();
        BCSoundEvents.init();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        ENTITIES.register(bus);
        MENUS.register(bus);
        RECIPE_SERIALIZERS.register(bus);
        NUMBER_PROVIDERS.makeRegistry("number_providers", net.minecraftforge.registries.RegistryBuilder::new);
        NUMBER_PROVIDERS.register(bus);
        SOUND_EVENTS.register(bus);
    }
}
