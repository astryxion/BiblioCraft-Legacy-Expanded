package com.github.minecraftschurlimods.bibliocraft.init;

import com.github.minecraftschurlimods.bibliocraft.content.bigbook.BigBookCloningRecipe;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableBindingTypewriterPagesRecipe;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableCloningRecipe;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableCloningWithEnchantmentsRecipe;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.EnchantmentLevelsNumberProvider;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableMergingRecipe;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableRecipe;
import com.github.minecraftschurlimods.bibliocraft.content.typewriter.TypewriterPageCloningRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;

public interface BCRecipes {
    IRecipeType<PrintingTableRecipe> PRINTING_TABLE = IRecipeType.register("bibliocraft:printing_table");

    RegistryObject<IRecipeSerializer<BigBookCloningRecipe>> BIG_BOOK_CLONING =
            BCRegistries.RECIPE_SERIALIZERS.register("big_book_cloning", () -> new SpecialRecipeSerializer<>(BigBookCloningRecipe::new));
    RegistryObject<IRecipeSerializer<TypewriterPageCloningRecipe>> TYPEWRITER_PAGE_CLONING =
            BCRegistries.RECIPE_SERIALIZERS.register("typewriter_page_cloning", () -> new SpecialRecipeSerializer<>(TypewriterPageCloningRecipe::new));
    RegistryObject<IRecipeSerializer<PrintingTableBindingTypewriterPagesRecipe>> PRINTING_TABLE_BINDING_TYPEWRITER_PAGES =
            BCRegistries.RECIPE_SERIALIZERS.register("printing_table_binding_typewriter_pages", PrintingTableBindingTypewriterPagesRecipe.Serializer::new);
    RegistryObject<IRecipeSerializer<PrintingTableCloningRecipe>> PRINTING_TABLE_CLONING =
            BCRegistries.RECIPE_SERIALIZERS.register("printing_table_cloning", PrintingTableCloningRecipe.Serializer::new);
    RegistryObject<IRecipeSerializer<PrintingTableCloningWithEnchantmentsRecipe>> PRINTING_TABLE_CLONING_WITH_ENCHANTMENTS =
            BCRegistries.RECIPE_SERIALIZERS.register("printing_table_cloning_with_enchantments", PrintingTableCloningWithEnchantmentsRecipe.Serializer::new);
    RegistryObject<IRecipeSerializer<PrintingTableMergingRecipe>> PRINTING_TABLE_MERGING =
            BCRegistries.RECIPE_SERIALIZERS.register("printing_table_merging", PrintingTableMergingRecipe.Serializer::new);

    RegistryObject<EnchantmentLevelsNumberProvider.LootNumberProviderType> ENCHANTMENT_LEVELS_NUMBER_PROVIDER =
            BCRegistries.NUMBER_PROVIDERS.register("enchantment_levels", () -> new EnchantmentLevelsNumberProvider.LootNumberProviderType(new EnchantmentLevelsNumberProvider.LootSerializer()));

    /**
     * Empty method, called by {@link BCRegistries#init(net.minecraftforge.eventbus.api.IEventBus)} to classload this class.
     */
    static void init() {
    }
}
