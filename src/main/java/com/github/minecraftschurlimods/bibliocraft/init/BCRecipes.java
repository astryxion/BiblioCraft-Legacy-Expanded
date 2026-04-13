package com.github.minecraftschurlimods.bibliocraft.init;

import com.github.minecraftschurlimods.bibliocraft.content.bigbook.BigBookCloningRecipe;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.EnchantmentLevelsNumberProvider;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableBindingTypewriterPagesRecipe;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableCloningRecipe;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableCloningWithEnchantmentsRecipe;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableMergingRecipe;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableRecipe;
import com.github.minecraftschurlimods.bibliocraft.content.typewriter.TypewriterPageCloningRecipe;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import com.github.minecraftschurlimods.bibliocraft.fabric.FabricRegistrar;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;

import java.util.function.Supplier;

public interface BCRecipes {
    FabricRegistrar.RegistryRef<RecipeType<?>> PRINTING_TABLE = BCRegistries.RECIPE_TYPES.register("printing_table", () -> new RecipeType<>() {});

    Supplier<RecipeSerializer<BigBookCloningRecipe>> BIG_BOOK_CLONING =
            castSerializer(BCRegistries.RECIPE_SERIALIZERS.register("big_book_cloning", () -> new SimpleCraftingRecipeSerializer<>(BigBookCloningRecipe::new)));
    Supplier<RecipeSerializer<TypewriterPageCloningRecipe>> TYPEWRITER_PAGE_CLONING =
            castSerializer(BCRegistries.RECIPE_SERIALIZERS.register("typewriter_page_cloning", () -> new SimpleCraftingRecipeSerializer<>(TypewriterPageCloningRecipe::new)));
    Supplier<RecipeSerializer<PrintingTableBindingTypewriterPagesRecipe>> PRINTING_TABLE_BINDING_TYPEWRITER_PAGES =
            castSerializer(BCRegistries.RECIPE_SERIALIZERS.register("printing_table_binding_typewriter_pages", serializer(PrintingTableBindingTypewriterPagesRecipe.CODEC, PrintingTableBindingTypewriterPagesRecipe.STREAM_CODEC)));
    Supplier<RecipeSerializer<PrintingTableCloningRecipe>> PRINTING_TABLE_CLONING =
            castSerializer(BCRegistries.RECIPE_SERIALIZERS.register("printing_table_cloning", serializer(PrintingTableCloningRecipe.CODEC, PrintingTableCloningRecipe.STREAM_CODEC)));
    Supplier<RecipeSerializer<PrintingTableCloningWithEnchantmentsRecipe>> PRINTING_TABLE_CLONING_WITH_ENCHANTMENTS =
            castSerializer(BCRegistries.RECIPE_SERIALIZERS.register("printing_table_cloning_with_enchantments", serializer(PrintingTableCloningWithEnchantmentsRecipe.CODEC, PrintingTableCloningWithEnchantmentsRecipe.STREAM_CODEC)));
    Supplier<RecipeSerializer<PrintingTableMergingRecipe>> PRINTING_TABLE_MERGING =
            castSerializer(BCRegistries.RECIPE_SERIALIZERS.register("printing_table_merging", serializer(PrintingTableMergingRecipe.CODEC, PrintingTableMergingRecipe.STREAM_CODEC)));

    Supplier<LootNumberProviderType> ENCHANTMENT_LEVELS_NUMBER_PROVIDER =
            BCRegistries.NUMBER_PROVIDERS.register("enchantment_levels", () -> new LootNumberProviderType(EnchantmentLevelsNumberProvider.CODEC));

    /**
     * Returns a {@link Supplier} for a {@link RecipeSerializer} created using the given {@link MapCodec} and {@link StreamCodec}.
     *
     * @param codec       The {@link MapCodec} to use.
     * @param streamCodec The {@link StreamCodec} to use.
     * @param <T>         The generic type of the {@link RecipeSerializer}.
     * @return A {@link Supplier} for a {@link RecipeSerializer}.
     */
    @SuppressWarnings("unchecked")
    private static <T extends Recipe<?>> Supplier<RecipeSerializer<T>> castSerializer(Object ref) {
        return (Supplier<RecipeSerializer<T>>) ref;
    }

    static <T extends Recipe<?>> Supplier<RecipeSerializer<T>> serializer(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        return () -> new RecipeSerializer<>() {
            @Override
            public MapCodec<T> codec() {
                return codec;
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
                return streamCodec;
            }
        };
    }

    /**
     * Empty method, called by {@link BCRegistries#init()} to classload this class.
     */
    static void init() {
    }
}
