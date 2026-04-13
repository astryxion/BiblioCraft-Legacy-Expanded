package com.github.minecraftschurlimods.bibliocraft.init;

import com.github.minecraftschurlimods.bibliocraft.content.bigbook.BigBookCloningRecipe;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableBindingTypewriterPagesRecipe;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableCloningRecipe;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableCloningWithEnchantmentsRecipe;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.EnchantmentLevelsNumberProvider;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableMergingRecipe;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableRecipe;
import com.github.minecraftschurlimods.bibliocraft.content.typewriter.TypewriterPageCloningRecipe;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.RegistryObject;

public interface BCRecipes {
    RegistryObject<RecipeType<PrintingTableRecipe>> PRINTING_TABLE = BCRegistries.RECIPE_TYPES.register("printing_table", () -> RecipeType.simple(BCUtil.bcLoc("printing_table")));

    RegistryObject<RecipeSerializer<BigBookCloningRecipe>> BIG_BOOK_CLONING =
            BCRegistries.RECIPE_SERIALIZERS.register("big_book_cloning", () -> new SimpleCraftingRecipeSerializer<>((id, cat) -> new BigBookCloningRecipe(id, cat)));
    RegistryObject<RecipeSerializer<TypewriterPageCloningRecipe>> TYPEWRITER_PAGE_CLONING =
            BCRegistries.RECIPE_SERIALIZERS.register("typewriter_page_cloning", () -> new SimpleCraftingRecipeSerializer<>((id, cat) -> new TypewriterPageCloningRecipe(id, cat)));
    RegistryObject<RecipeSerializer<PrintingTableBindingTypewriterPagesRecipe>> PRINTING_TABLE_BINDING_TYPEWRITER_PAGES =
            BCRegistries.RECIPE_SERIALIZERS.register("printing_table_binding_typewriter_pages", PrintingTableBindingTypewriterPagesRecipe.Serializer::new);
    RegistryObject<RecipeSerializer<PrintingTableCloningRecipe>> PRINTING_TABLE_CLONING =
            BCRegistries.RECIPE_SERIALIZERS.register("printing_table_cloning", PrintingTableCloningRecipe.Serializer::new);
    RegistryObject<RecipeSerializer<PrintingTableCloningWithEnchantmentsRecipe>> PRINTING_TABLE_CLONING_WITH_ENCHANTMENTS =
            BCRegistries.RECIPE_SERIALIZERS.register("printing_table_cloning_with_enchantments", PrintingTableCloningWithEnchantmentsRecipe.Serializer::new);
    RegistryObject<RecipeSerializer<PrintingTableMergingRecipe>> PRINTING_TABLE_MERGING =
            BCRegistries.RECIPE_SERIALIZERS.register("printing_table_merging", PrintingTableMergingRecipe.Serializer::new);

    RegistryObject<LootNumberProviderType> ENCHANTMENT_LEVELS_NUMBER_PROVIDER =
            BCRegistries.NUMBER_PROVIDERS.register("enchantment_levels", () -> new LootNumberProviderType(new EnchantmentLevelsNumberProvider.LootSerializer()));

    /**
     * Empty method, called by {@link BCRegistries#init(net.minecraftforge.eventbus.api.IEventBus)} to classload this class.
     */
    static void init() {
    }
}
