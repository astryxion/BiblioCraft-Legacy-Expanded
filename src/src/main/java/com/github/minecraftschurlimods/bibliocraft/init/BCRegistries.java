package com.github.minecraftschurlimods.bibliocraft.init;

import com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public interface BCRegistries {
    // @formatter:off
    DeferredRegister<net.minecraft.world.level.block.Block> BLOCKS             = DeferredRegister.create(ForgeRegistries.BLOCKS, BibliocraftApi.MOD_ID);
    DeferredRegister<net.minecraft.world.item.Item>         ITEMS              = DeferredRegister.create(ForgeRegistries.ITEMS, BibliocraftApi.MOD_ID);
    DeferredRegister<CreativeModeTab>        CREATIVE_TABS      = DeferredRegister.create(Registries.CREATIVE_MODE_TAB,                 BibliocraftApi.MOD_ID);
    DeferredRegister<BlockEntityType<?>>     BLOCK_ENTITIES     = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES,          BibliocraftApi.MOD_ID);
    DeferredRegister<EntityType<?>>          ENTITIES           = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,                BibliocraftApi.MOD_ID);
    DeferredRegister<MenuType<?>>             MENUS              = DeferredRegister.create(ForgeRegistries.MENU_TYPES,                  BibliocraftApi.MOD_ID);
    DeferredRegister<LootNumberProviderType> NUMBER_PROVIDERS   = DeferredRegister.create(Registries.LOOT_NUMBER_PROVIDER_TYPE,       BibliocraftApi.MOD_ID);
    DeferredRegister<RecipeType<?>>          RECIPE_TYPES       = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES,                BibliocraftApi.MOD_ID);
    DeferredRegister<RecipeSerializer<?>>    RECIPE_SERIALIZERS  = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS,          BibliocraftApi.MOD_ID);
    DeferredRegister<SoundEvent>             SOUND_EVENTS       = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS,                BibliocraftApi.MOD_ID);
    // @formatter:on

    /**
     * Central registration method. Classloads the registration classes and registers the registries to the mod bus.
     */
    static void init(IEventBus bus) {
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
        CREATIVE_TABS.register(bus);
        BLOCK_ENTITIES.register(bus);
        ENTITIES.register(bus);
        MENUS.register(bus);
        NUMBER_PROVIDERS.register(bus);
        RECIPE_TYPES.register(bus);
        RECIPE_SERIALIZERS.register(bus);
        SOUND_EVENTS.register(bus);
    }
}
