package at.minecraftschurli.mods.bibliocraft.init;

import at.minecraftschurli.mods.bibliocraft.api.BibliocraftApi;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import at.minecraftschurli.mods.bibliocraft.util.registrar.DeferredHolder;
import at.minecraftschurli.mods.bibliocraft.util.registrar.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

public interface BCRegistries {
    // @formatter:off
    DeferredRegister.Blocks                              BLOCKS             = DeferredRegister.createBlocks(BibliocraftApi.MOD_ID);
    DeferredRegister.Items                               ITEMS              = DeferredRegister.createItems(BibliocraftApi.MOD_ID);
    DeferredRegister.Entities                            ENTITIES           = DeferredRegister.createEntities(BibliocraftApi.MOD_ID);
    DeferredRegister.DataComponents                      DATA_COMPONENTS    = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, BibliocraftApi.MOD_ID);
    DeferredRegister<Fluid>                              FLUIDS             = DeferredRegister.create(Registries.FLUID, BibliocraftApi.MOD_ID);
    DeferredRegister<CreativeModeTab>                    CREATIVE_TABS      = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BibliocraftApi.MOD_ID);
    DeferredRegister<BlockEntityType<?>>                 BLOCK_ENTITIES     = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, BibliocraftApi.MOD_ID);
    DeferredRegister<MenuType<?>>                        MENUS              = DeferredRegister.create(Registries.MENU, BibliocraftApi.MOD_ID);
    DeferredRegister<MapCodec<? extends NumberProvider>> NUMBER_PROVIDERS   = DeferredRegister.create(Registries.LOOT_NUMBER_PROVIDER_TYPE, BibliocraftApi.MOD_ID);
    DeferredRegister<RecipeType<?>>                      RECIPE_TYPES       = DeferredRegister.create(Registries.RECIPE_TYPE, BibliocraftApi.MOD_ID);
    DeferredRegister<RecipeSerializer<?>>                RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, BibliocraftApi.MOD_ID);
    DeferredRegister<RecipeBookCategory>                 RECIPE_CATEGORIES  = DeferredRegister.create(Registries.RECIPE_BOOK_CATEGORY, BibliocraftApi.MOD_ID);
    DeferredRegister<SoundEvent>                         SOUND_EVENTS       = DeferredRegister.create(Registries.SOUND_EVENT, BibliocraftApi.MOD_ID);
    // @formatter:on

    /// Registers deferred holders directly on Fabric.
    @ApiStatus.Internal
    static void registerFabric() {
        registerDeferred(DATA_COMPONENTS, BuiltInRegistries.DATA_COMPONENT_TYPE);
        registerDeferred(SOUND_EVENTS, BuiltInRegistries.SOUND_EVENT);
        registerDeferred(BLOCKS, BuiltInRegistries.BLOCK);
        registerDeferred(FLUIDS, BuiltInRegistries.FLUID);
        registerDeferred(ITEMS, BuiltInRegistries.ITEM);
        registerDeferred(ENTITIES, BuiltInRegistries.ENTITY_TYPE);
        registerDeferred(BLOCK_ENTITIES, BuiltInRegistries.BLOCK_ENTITY_TYPE);
        registerDeferred(MENUS, BuiltInRegistries.MENU);
        registerDeferred(NUMBER_PROVIDERS, BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE);
        registerDeferred(RECIPE_TYPES, BuiltInRegistries.RECIPE_TYPE);
        registerDeferred(RECIPE_SERIALIZERS, BuiltInRegistries.RECIPE_SERIALIZER);
        registerDeferred(RECIPE_CATEGORIES, BuiltInRegistries.RECIPE_BOOK_CATEGORY);
        registerDeferred(CREATIVE_TABS, BuiltInRegistries.CREATIVE_MODE_TAB);
    }

    private static <T> void registerDeferred(DeferredRegister<T> deferredRegister, Registry<T> registry) {
        deferredRegister.registerAll(registry);
    }

    /// Classloads the registration classes.
    @ApiStatus.Internal
    static void init() {
        BCBlocks.init();
        BCItems.init();
        BCFluids.init();
        BCCreativeTabs.init();
        BCDataComponents.init();
        BCBlockEntities.init();
        BCEntities.init();
        BCMenus.init();
        BCRecipes.init();
        BCSoundEvents.init();
    }
}
