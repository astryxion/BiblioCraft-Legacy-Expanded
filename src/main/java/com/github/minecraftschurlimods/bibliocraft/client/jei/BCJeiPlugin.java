package com.github.minecraftschurlimods.bibliocraft.client.jei;

import com.github.minecraftschurlimods.bibliocraft.BCConfig;
import com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi;
import com.github.minecraftschurlimods.bibliocraft.api.woodtype.BibliocraftWoodType;
import com.github.minecraftschurlimods.bibliocraft.client.screen.FancyCrafterScreen;
import com.github.minecraftschurlimods.bibliocraft.client.screen.PrintingTableScreen;
import com.github.minecraftschurlimods.bibliocraft.content.fancycrafter.FancyCrafterMenu;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableMenu;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableRecipe;
import com.github.minecraftschurlimods.bibliocraft.init.BCItems;
import com.github.minecraftschurlimods.bibliocraft.init.BCMenus;
import com.github.minecraftschurlimods.bibliocraft.init.BCRecipes;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.github.minecraftschurlimods.bibliocraft.util.CompatUtil;
import com.github.minecraftschurlimods.bibliocraft.util.Translations;
import com.github.minecraftschurlimods.bibliocraft.util.holder.ColoredDeferredHolder;
import com.github.minecraftschurlimods.bibliocraft.util.holder.ColoredWoodTypeDeferredHolder;
import com.github.minecraftschurlimods.bibliocraft.util.holder.GroupingDeferredHolder;
import com.github.minecraftschurlimods.bibliocraft.util.holder.WoodTypeDeferredHolder;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

@JeiPlugin
public final class BCJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID = BCUtil.bcLoc("jei_plugin");
    private static final DyeColor WHITE = DyeColor.WHITE;
    private static BibliocraftWoodType oak;
    private static List<WoodTypeDeferredHolder<Item, ?>> woodTypeDeferredHolders;
    private static List<ColoredDeferredHolder<Item, ?>> coloredDeferredHolders;
    private static List<ColoredWoodTypeDeferredHolder<Item, ?>> coloredWoodTypeDeferredHolders;

    private static BibliocraftWoodType getOak() {
        if (oak == null) oak = BibliocraftApi.getWoodTypeRegistry().get(BCUtil.mcLoc("oak"));
        return oak;
    }
    private static List<WoodTypeDeferredHolder<Item, ?>> getWoodTypeDeferredHolders() {
        if (woodTypeDeferredHolders == null) woodTypeDeferredHolders = List.of(BCItems.BOOKCASE, BCItems.FANCY_ARMOR_STAND, BCItems.FANCY_CLOCK, BCItems.FANCY_CRAFTER, BCItems.GRANDFATHER_CLOCK, BCItems.LABEL, BCItems.POTION_SHELF, BCItems.SHELF, BCItems.TABLE, BCItems.TOOL_RACK);
        return woodTypeDeferredHolders;
    }
    private static List<ColoredDeferredHolder<Item, ?>> getColoredDeferredHolders() {
        if (coloredDeferredHolders == null) coloredDeferredHolders = List.of(BCItems.FANCY_GOLD_LAMP, BCItems.FANCY_IRON_LAMP, BCItems.FANCY_GOLD_LANTERN, BCItems.FANCY_IRON_LANTERN);
        return coloredDeferredHolders;
    }
    private static List<ColoredWoodTypeDeferredHolder<Item, ?>> getColoredWoodTypeDeferredHolders() {
        if (coloredWoodTypeDeferredHolders == null) coloredWoodTypeDeferredHolders = List.of(BCItems.DISPLAY_CASE, BCItems.SEAT, BCItems.SMALL_SEAT_BACK, BCItems.RAISED_SEAT_BACK, BCItems.FLAT_SEAT_BACK, BCItems.TALL_SEAT_BACK, BCItems.FANCY_SEAT_BACK);
        return coloredWoodTypeDeferredHolders;
    }

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        try {
            Class<?> interpClass = Class.forName("mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter");
            Object proxy = java.lang.reflect.Proxy.newProxyInstance(
                interpClass.getClassLoader(),
                new Class<?>[] { interpClass },
                (proxy1, method, args) -> {
                    String name = method.getName();
                    if (args != null && args.length >= 1 && args[0] instanceof ItemStack stack) {
                        String subtype = DyedColorSubtypeInterpreter.INSTANCE.getSubtypeInfo(stack);
                        if ("getSubtypeInfo".equals(name) || "getLegacyStringSubtypeInfo".equals(name)) return subtype;
                        if ("getSubtypeData".equals(name)) return subtype.isEmpty() ? null : subtype;
                    }
                    return null;
                });
            registration.getClass().getMethod("registerSubtypeInterpreter", Item.class, interpClass).invoke(registration, BCItems.SWORD_PEDESTAL.get(), proxy);
        } catch (Throwable ignored) {
            // JEI API may not expose ISubtypeInterpreter in this version
        }
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new PrintingTableRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (!BCConfig.JEI_SHOW_WOOD_TYPES.get()) {
            for (WoodTypeDeferredHolder<Item, ?> holder : getWoodTypeDeferredHolders()) {
                registration.addIngredientInfo(new ItemStack(holder.get(getOak())), VanillaTypes.ITEM_STACK, Translations.ALL_WOOD_TYPES);
            }
        }
        if (!BCConfig.JEI_SHOW_COLOR_TYPES.get()) {
            if (!BCConfig.JEI_SHOW_WOOD_TYPES.get()) {
                for (ColoredWoodTypeDeferredHolder<Item, ?> holder : getColoredWoodTypeDeferredHolders()) {
                    registration.addIngredientInfo(new ItemStack(holder.get(getOak(), WHITE)), VanillaTypes.ITEM_STACK, Translations.ALL_COLORS_AND_WOOD_TYPES);
                }
            } else {
                for (ColoredWoodTypeDeferredHolder<Item, ?> holder : getColoredWoodTypeDeferredHolders()) {
                    for (BibliocraftWoodType woodType : BibliocraftApi.getWoodTypeRegistry().getAll()) {
                        registration.addIngredientInfo(new ItemStack(holder.get(woodType, WHITE)), VanillaTypes.ITEM_STACK, Translations.ALL_COLORS);
                    }
                }
            }
            for (ColoredDeferredHolder<Item, ?> holder : getColoredDeferredHolders()) {
                registration.addIngredientInfo(new ItemStack(holder.get(WHITE)), VanillaTypes.ITEM_STACK, Translations.ALL_COLORS);
            }
        }
        net.minecraft.world.item.crafting.RecipeManager recipeManager = ClientUtil.getLevel().getRecipeManager();
        List<PrintingTableRecipe> printingRecipes = recipeManager.getRecipes().stream()
                .filter(r -> r.getType() == BCRecipes.PRINTING_TABLE.get())
                .map(r -> (PrintingTableRecipe) r)
                .toList();
        registration.addRecipes(PrintingTableRecipeCategory.TYPE, printingRecipes);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(FancyCrafterMenu.class, BCMenus.FANCY_CRAFTER.get(), RecipeTypes.CRAFTING, 0, 9, 10, 44);
        registration.addRecipeTransferHandler(PrintingTableMenu.class, BCMenus.PRINTING_TABLE.get(), PrintingTableRecipeCategory.TYPE, 0, 10, 11, 36);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        if (BCConfig.JEI_SHOW_WOOD_TYPES.get()) {
            for (BibliocraftWoodType woodType : BibliocraftApi.getWoodTypeRegistry().getAll()) {
                registration.addRecipeCatalyst(new ItemStack(BCItems.FANCY_CRAFTER.get(woodType)), RecipeTypes.CRAFTING);
            }
        } else {
            registration.addRecipeCatalyst(new ItemStack(BCItems.FANCY_CRAFTER.get(getOak())), RecipeTypes.CRAFTING);
        }
        registration.addRecipeCatalyst(new ItemStack(BCItems.PRINTING_TABLE.get()), PrintingTableRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(BCItems.IRON_PRINTING_TABLE.get()), PrintingTableRecipeCategory.TYPE);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(FancyCrafterScreen.class, 88, 32, 28, 23, RecipeTypes.CRAFTING);
        registration.addRecipeClickArea(PrintingTableScreen.class, 108, 28, 28, 23, PrintingTableRecipeCategory.TYPE);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        if (!BCConfig.JEI_SHOW_WOOD_TYPES.get()) {
            for (WoodTypeDeferredHolder<Item, ?> holder : getWoodTypeDeferredHolders()) {
                removeAllExcept(runtime, holder, holder.get(getOak()));
            }
        }
        if (!BCConfig.JEI_SHOW_COLOR_TYPES.get()) {
            if (!BCConfig.JEI_SHOW_WOOD_TYPES.get()) {
                for (ColoredWoodTypeDeferredHolder<Item, ?> holder : getColoredWoodTypeDeferredHolders()) {
                    removeAllExcept(runtime, holder, holder.get(getOak(), WHITE));
                }
            } else {
                for (ColoredWoodTypeDeferredHolder<Item, ?> holder : getColoredWoodTypeDeferredHolders()) {
                    for (BibliocraftWoodType woodType : BibliocraftApi.getWoodTypeRegistry().getAll()) {
                        removeAllExcept(runtime, holder, holder.get(woodType, WHITE));
                    }
                }
            }
            for (ColoredDeferredHolder<Item, ?> holder : getColoredDeferredHolders()) {
                removeAllExcept(runtime, holder, holder.get(WHITE));
            }
        }
        if (!CompatUtil.hasSoulCandles()) {
            remove(runtime, Stream.of(BCItems.SOUL_FANCY_GOLD_LANTERN, BCItems.SOUL_FANCY_IRON_LANTERN).map(Supplier::get).map(ItemStack::new).toList());
        }
    }

    private void remove(IJeiRuntime runtime, List<ItemStack> list) {
        runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, list);
    }

    private void removeAllExcept(IJeiRuntime runtime, GroupingDeferredHolder<Item, ?> holder, Item except) {
        remove(runtime, holder.values().stream().filter(e -> e != except).map(ItemStack::new).toList());
    }
}
