package com.github.minecraftschurlimods.bibliocraft.init;

import com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi;
import com.github.minecraftschurlimods.bibliocraft.apiimpl.BibliocraftWoodTypeRegistryImpl;
import com.github.minecraftschurlimods.bibliocraft.fabric.FabricRegistrar;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import com.github.minecraftschurlimods.bibliocraft.util.holder.RegistryHolder;

import java.util.function.Supplier;

public interface BCRegistries {
    String MOD_ID = BibliocraftApi.MOD_ID;

    // @formatter:off
    FabricRegistrar<Block>                  BLOCKS             = new FabricRegistrar<>(BuiltInRegistries.BLOCK, MOD_ID);
    FabricRegistrar<Item>                   ITEMS              = new FabricRegistrar<>(BuiltInRegistries.ITEM, MOD_ID);
    FabricRegistrar<DataComponentType<?>>   DATA_COMPONENTS    = new FabricRegistrar<>(BuiltInRegistries.DATA_COMPONENT_TYPE, MOD_ID);
    FabricRegistrar<CreativeModeTab>        CREATIVE_TABS      = new FabricRegistrar<>(BuiltInRegistries.CREATIVE_MODE_TAB, MOD_ID);
    FabricRegistrar<BlockEntityType<?>>     BLOCK_ENTITIES     = new FabricRegistrar<>(BuiltInRegistries.BLOCK_ENTITY_TYPE, MOD_ID);
    FabricRegistrar<EntityType<?>>         ENTITIES           = new FabricRegistrar<>(BuiltInRegistries.ENTITY_TYPE, MOD_ID);
    FabricRegistrar<MenuType<?>>           MENUS              = new FabricRegistrar<>(BuiltInRegistries.MENU, MOD_ID);
    FabricRegistrar<LootNumberProviderType> NUMBER_PROVIDERS   = new FabricRegistrar<>(BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE, MOD_ID);
    FabricRegistrar<RecipeType<?>>          RECIPE_TYPES       = new FabricRegistrar<>(BuiltInRegistries.RECIPE_TYPE, MOD_ID);
    FabricRegistrar<RecipeSerializer<?>>   RECIPE_SERIALIZERS = new FabricRegistrar<>(BuiltInRegistries.RECIPE_SERIALIZER, MOD_ID);
    FabricRegistrar<SoundEvent>            SOUND_EVENTS       = new FabricRegistrar<>(BuiltInRegistries.SOUND_EVENT, MOD_ID);
    // @formatter:on

    /**
     * Registers a simple BlockItem for the given block (same path as block, default Item.Properties).
     */
    static FabricRegistrar.RegistryRef<Item> registerSimpleBlockItem(RegistryHolder<Block> block) {
        return ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
    }

    /**
     * Registers a BlockItem for the given path and block with custom properties.
     */
    static FabricRegistrar.RegistryRef<Item> registerSimpleBlockItem(String path, RegistryHolder<Block> block, Item.Properties props) {
        return ITEMS.register(path, () -> new BlockItem(block.get(), props));
    }

    /**
     * Registers a simple Item (no special properties).
     */
    static FabricRegistrar.RegistryRef<Item> registerSimpleItem(String path) {
        return ITEMS.register(path, () -> new Item(new Item.Properties()));
    }

    /**
     * Central registration. Classloads all init classes then applies all registrations.
     * Wood types must be registered first so BCBlocks/BCItems can use them during static init.
     */
    static void init() {
        ((BibliocraftWoodTypeRegistryImpl) BibliocraftApi.getWoodTypeRegistry()).register();
        BCBlocks.init();
        BCItems.init();
        BCCreativeTabs.init();
        BCDataComponents.init();
        BCBlockEntities.init();
        BCEntities.init();
        BCMenus.init();
        BCRecipes.init();
        BCSoundEvents.init();

        BLOCKS.registerAll();
        ITEMS.registerAll();
        CREATIVE_TABS.registerAll();
        DATA_COMPONENTS.registerAll();
        BLOCK_ENTITIES.registerAll();
        ENTITIES.registerAll();
        MENUS.registerAll();
        NUMBER_PROVIDERS.registerAll();
        RECIPE_TYPES.registerAll();
        RECIPE_SERIALIZERS.registerAll();
        SOUND_EVENTS.registerAll();
    }
}
