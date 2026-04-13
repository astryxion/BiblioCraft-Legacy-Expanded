package com.github.minecraftschurlimods.bibliocraft.datagen.data;

import com.github.minecraftschurlimods.bibliocraft.content.bigbook.BigBookContent;
import com.github.minecraftschurlimods.bibliocraft.content.bigbook.BigBookCloningRecipe;
import com.github.minecraftschurlimods.bibliocraft.content.bigbook.WrittenBigBookContent;
import com.github.minecraftschurlimods.bibliocraft.content.clipboard.ClipboardContent;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.EnchantmentLevelsNumberProvider;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableBindingTypewriterPagesRecipe;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableCloningRecipe;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableCloningWithEnchantmentsRecipe;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableMergingRecipe;
import com.github.minecraftschurlimods.bibliocraft.content.typewriter.TypewriterPageCloningRecipe;
import com.github.minecraftschurlimods.bibliocraft.content.swordpedestal.SwordPedestalBlock;
import com.github.minecraftschurlimods.bibliocraft.init.BCItems;
import com.github.minecraftschurlimods.bibliocraft.init.BCRecipes;
import com.github.minecraftschurlimods.bibliocraft.init.BCTags;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class BCRecipeProvider extends RecipeProvider {
    public BCRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> output) {
        for (DyeColor color : DyeColor.values()) {
            String name = color.getSerializedName();
            ItemStack swordPedestal = new ItemStack(BCItems.SWORD_PEDESTAL.get());
            SwordPedestalBlock.DyedColor.putOnStack(swordPedestal, new SwordPedestalBlock.DyedColor(color.getTextColor(), true));
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, swordPedestal.getItem())
                    .pattern(" S ")
                    .pattern("SWS")
                    .define('S', Items.SMOOTH_STONE_SLAB)
                    .define('W', BuiltInRegistries.ITEM.get(BCUtil.mcLoc(name + "_wool")))
                    .group("bibliocraft:sword_pedestal")
                    .unlockedBy("has_smooth_stone_slab", has(Items.SMOOTH_STONE_SLAB))
                    .save(output, BCUtil.bcLoc("color/" + name + "/sword_pedestal"));
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BCItems.FANCY_GOLD_LAMP.get(color))
                    .pattern("CGC")
                    .pattern(" I ")
                    .pattern("NIN")
                    .define('C', TagKey.create(Registries.ITEM, BCUtil.cLoc("dyed/" + name)))
                    .define('G', Items.GLOWSTONE)
                    .define('I', Tags.Items.INGOTS_GOLD)
                    .define('N', Tags.Items.NUGGETS_GOLD)
                    .group("bibliocraft:fancy_lamp")
                    .unlockedBy("has_gold_ingot", has(Tags.Items.INGOTS_GOLD))
                    .save(output, BCUtil.bcLoc("color/" + name + "/fancy_gold_lamp"));
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BCItems.FANCY_IRON_LAMP.get(color))
                    .pattern("CGC")
                    .pattern(" I ")
                    .pattern("NIN")
                    .define('C', TagKey.create(Registries.ITEM, BCUtil.cLoc("dyed/" + name)))
                    .define('G', Items.GLOWSTONE)
                    .define('I', Tags.Items.INGOTS_IRON)
                    .define('N', Tags.Items.NUGGETS_IRON)
                    .group("bibliocraft:fancy_lamp")
                    .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                    .save(output, BCUtil.bcLoc("color/" + name + "/fancy_iron_lamp"));
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BCItems.FANCY_GOLD_LANTERN.get(color))
                    .pattern("GIG")
                    .pattern("ICI")
                    .pattern("GIG")
                    .define('G', Ingredient.of(Items.GLASS_PANE))
                    .define('I', Tags.Items.INGOTS_GOLD)
                    .define('C', Ingredient.of(BuiltInRegistries.ITEM.get(BCUtil.mcLoc(name + "_candle"))))
                    .group("bibliocraft:fancy_lantern")
                    .unlockedBy("has_gold_ingot", has(Tags.Items.INGOTS_GOLD))
                    .save(output, BCUtil.bcLoc("color/" + name + "/fancy_gold_lantern"));
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BCItems.FANCY_IRON_LANTERN.get(color))
                    .pattern("GIG")
                    .pattern("ICI")
                    .pattern("GIG")
                    .define('G', Ingredient.of(Items.GLASS_PANE))
                    .define('I', Tags.Items.INGOTS_IRON)
                    .define('C', Ingredient.of(BuiltInRegistries.ITEM.get(BCUtil.mcLoc(name + "_candle"))))
                    .group("bibliocraft:fancy_lantern")
                    .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                    .save(output, BCUtil.bcLoc("color/" + name + "/fancy_iron_lantern"));
            ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BCItems.TYPEWRITER.get(color))
                    .pattern("IPI")
                    .pattern("BDB")
                    .pattern("CCC")
                    .define('I', Tags.Items.INGOTS_IRON)
                    .define('P', Items.PAPER)
                    .define('B', Tags.Items.STORAGE_BLOCKS_IRON)
                    .define('D', Tags.Items.DYES_BLACK)
                    .define('C', Ingredient.of(BuiltInRegistries.ITEM.get(BCUtil.mcLoc(name + "_terracotta"))))
                    .group("bibliocraft:typewriter")
                    .unlockedBy("has_terracotta", has(BuiltInRegistries.ITEM.get(BCUtil.mcLoc(name + "_terracotta"))))
                    .save(output, BCUtil.bcLoc("color/" + name + "/typewriter"));
        }
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BCItems.CLEAR_FANCY_GOLD_LAMP.get())
                .pattern("CGC")
                .pattern(" I ")
                .pattern("NIN")
                .define('C', Tags.Items.GLASS)
                .define('G', Items.GLOWSTONE)
                .define('I', Tags.Items.INGOTS_GOLD)
                .define('N', Tags.Items.NUGGETS_GOLD)
                .group("bibliocraft:fancy_lamp")
                .unlockedBy("has_gold_ingot", has(Tags.Items.INGOTS_GOLD))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BCItems.CLEAR_FANCY_IRON_LAMP.get())
                .pattern("CGC")
                .pattern(" I ")
                .pattern("NIN")
                .define('C', Tags.Items.GLASS)
                .define('G', Items.GLOWSTONE)
                .define('I', Tags.Items.INGOTS_IRON)
                .define('N', Tags.Items.NUGGETS_IRON)
                .group("bibliocraft:fancy_lamp")
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BCItems.CLEAR_FANCY_GOLD_LANTERN.get())
                .pattern("GIG")
                .pattern("ICI")
                .pattern("GIG")
                .define('G', Ingredient.of(Items.GLASS_PANE))
                .define('I', Tags.Items.INGOTS_GOLD)
                .define('C', Ingredient.of(Items.CANDLE))
                .group("bibliocraft:fancy_lantern")
                .unlockedBy("has_gold_ingot", has(Tags.Items.INGOTS_GOLD))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BCItems.CLEAR_FANCY_IRON_LANTERN.get())
                .pattern("GIG")
                .pattern("ICI")
                .pattern("GIG")
                .define('G', Ingredient.of(Items.GLASS_PANE))
                .define('I', Tags.Items.INGOTS_IRON)
                .define('C', Ingredient.of(Items.CANDLE))
                .group("bibliocraft:fancy_lantern")
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BCItems.CLEAR_TYPEWRITER.get())
                .pattern("IPI")
                .pattern("BDB")
                .pattern("CCC")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('P', Items.PAPER)
                .define('B', Tags.Items.STORAGE_BLOCKS_IRON)
                .define('D', Tags.Items.DYES_BLACK)
                .define('C', Ingredient.of(Items.TERRACOTTA))
                .group("bibliocraft:typewriter")
                .unlockedBy("has_terracotta", has(Items.TERRACOTTA))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BCItems.SOUL_FANCY_GOLD_LANTERN.get())
                .pattern("GIG")
                .pattern("ICI")
                .pattern("GIG")
                .define('G', Ingredient.of(Items.GLASS_PANE))
                .define('I', Tags.Items.INGOTS_GOLD)
                .define('C', Ingredient.of(BuiltInRegistries.ITEM.get(BCUtil.modLoc("buzzier_bees", "soul_candle"))))
                .group("bibliocraft:fancy_lantern")
                .unlockedBy("has_gold_ingot", has(Tags.Items.INGOTS_GOLD))
                .save(r -> net.minecraftforge.common.crafting.ConditionalRecipe.builder().addCondition(new ModLoadedCondition("buzzier_bees")).addRecipe(r).build(output, r.getId()), BCUtil.bcLoc("soul_fancy_gold_lantern"));
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BCItems.SOUL_FANCY_IRON_LANTERN.get())
                .pattern("GIG")
                .pattern("ICI")
                .pattern("GIG")
                .define('G', Ingredient.of(Items.GLASS_PANE))
                .define('I', Tags.Items.INGOTS_IRON)
                .define('C', Ingredient.of(BuiltInRegistries.ITEM.get(BCUtil.modLoc("buzzier_bees", "soul_candle"))))
                .group("bibliocraft:fancy_lantern")
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .save(r -> net.minecraftforge.common.crafting.ConditionalRecipe.builder().addCondition(new ModLoadedCondition("buzzier_bees")).addRecipe(r).build(output, r.getId()), BCUtil.bcLoc("soul_fancy_iron_lantern"));
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BCItems.CLIPBOARD.get())
                .pattern("I F")
                .pattern("PPP")
                .pattern(" L ")
                .define('I', Tags.Items.DYES_BLACK)
                .define('F', Tags.Items.FEATHERS)
                .define('P', Items.PAPER)
                .define('L', ItemTags.WOODEN_PRESSURE_PLATES)
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BCItems.COOKIE_JAR.get())
                .pattern(" I ")
                .pattern("GCG")
                .pattern("GRG")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('G', Items.GLASS_PANE)
                .define('C', Items.COOKIE)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BCItems.DESK_BELL.get())
                .pattern(" B ")
                .pattern(" I ")
                .pattern("IRI")
                .define('B', Items.STONE_BUTTON)
                .define('I', Tags.Items.INGOTS_IRON)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BCItems.DINNER_PLATE.get())
                .pattern("SSS")
                .define('S', Items.SMOOTH_QUARTZ_SLAB)
                .unlockedBy("has_smooth_quartz", has(Items.SMOOTH_QUARTZ_SLAB))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BCItems.DISC_RACK.get())
                .pattern("RRR")
                .pattern("SSS")
                .define('R', Tags.Items.RODS_WOODEN)
                .define('S', ItemTags.WOODEN_SLABS)
                .unlockedBy("has_wooden_slab", has(ItemTags.WOODEN_SLABS))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BCItems.IRON_FANCY_ARMOR_STAND.get())
                .pattern(" I ")
                .pattern(" I ")
                .pattern("SSS")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('S', Items.SMOOTH_STONE_SLAB)
                .unlockedBy("has_smooth_stone_slab", has(Items.SMOOTH_STONE_SLAB))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BCItems.GOLD_CHAIN.get())
                .pattern("N")
                .pattern("I")
                .pattern("N")
                .define('I', Tags.Items.INGOTS_GOLD)
                .define('N', Tags.Items.NUGGETS_GOLD)
                .unlockedBy("has_gold_ingot", has(Tags.Items.INGOTS_GOLD))
                .unlockedBy("has_gold_nugget", has(Tags.Items.NUGGETS_GOLD))
                .unlockedBy("has_gold_chain", has(BCItems.GOLD_CHAIN.get()))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BCItems.GOLD_LANTERN.get())
                .pattern("NNN")
                .pattern("NTN")
                .pattern("NNN")
                .define('T', Items.TORCH)
                .define('N', Tags.Items.NUGGETS_GOLD)
                .unlockedBy("has_gold_nugget", has(Tags.Items.NUGGETS_GOLD))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BCItems.GOLD_SOUL_LANTERN.get())
                .pattern("NNN")
                .pattern("NTN")
                .pattern("NNN")
                .define('T', Items.SOUL_TORCH)
                .define('N', Tags.Items.NUGGETS_GOLD)
                .unlockedBy("has_gold_nugget", has(Tags.Items.NUGGETS_GOLD))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BCItems.PRINTING_TABLE.get())
                .pattern("CCC")
                .pattern("PPP")
                .pattern("BRB")
                .define('C', Tags.Items.INGOTS_COPPER)
                .define('P', ItemTags.PLANKS)
                .define('B', Tags.Items.STORAGE_BLOCKS_IRON)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .unlockedBy("has_copper_ingot", has(Tags.Items.INGOTS_COPPER))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BCItems.IRON_PRINTING_TABLE.get())
                .pattern("CCC")
                .pattern("III")
                .pattern("BRB")
                .define('C', Tags.Items.INGOTS_COPPER)
                .define('I', Tags.Items.INGOTS_IRON)
                .define('B', Tags.Items.STORAGE_BLOCKS_IRON)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .unlockedBy("has_copper_ingot", has(Tags.Items.INGOTS_COPPER))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BCItems.BIG_BOOK.get())
                .pattern("PPP")
                .pattern("PBP")
                .pattern("PPP")
                .define('P', Items.PAPER)
                .define('B', Items.WRITABLE_BOOK)
                .unlockedBy("has_writable_book", has(Items.WRITABLE_BOOK))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BCItems.LOCK_AND_KEY.get())
                .pattern("NI")
                .pattern("NI")
                .pattern(" I")
                .define('N', Tags.Items.NUGGETS_GOLD)
                .define('I', Tags.Items.INGOTS_GOLD)
                .unlockedBy("has_gold_ingot", has(Tags.Items.INGOTS_GOLD))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BCItems.PLUMB_LINE.get())
                .pattern("SSS")
                .pattern("S S")
                .pattern("I S")
                .define('S', Items.STRING)
                .define('I', Tags.Items.INGOTS_GOLD)
                .unlockedBy("has_gold_ingot", has(Tags.Items.INGOTS_GOLD))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BCItems.STOCKROOM_CATALOG.get())
                .pattern("PDP")
                .pattern("PBP")
                .pattern("PPP")
                .define('P', Items.PAPER)
                .define('D', Tags.Items.DYES_GREEN)
                .define('B', Items.BOOK)
                .unlockedBy("has_book", has(Items.BOOK))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BCItems.TAPE_MEASURE.get())
                .pattern(" I ")
                .pattern("IRI")
                .pattern(" I ")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('R', BCItems.TAPE_REEL.get())
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .save(output);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BCItems.TAPE_REEL.get())
                .pattern("SSS")
                .pattern("SDS")
                .pattern("SSS")
                .define('S', Items.STRING)
                .define('D', Tags.Items.DYES_YELLOW)
                .unlockedBy("has_yellow_dye", has(Tags.Items.DYES_YELLOW))
                .save(output);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, BCItems.REDSTONE_BOOK.get())
                .requires(Items.BOOK)
                .requires(Items.REDSTONE_TORCH)
                .unlockedBy("has_book", has(Items.BOOK))
                .save(output);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, BCItems.SLOTTED_BOOK.get())
                .requires(Items.BOOK)
                .requires(BCTags.Items.LABELS)
                .unlockedBy("has_book", has(Items.BOOK))
                .save(output);
        SpecialRecipeBuilder.special(BCRecipes.BIG_BOOK_CLONING.get()).save(output, "big_book_cloning");
        SpecialRecipeBuilder.special(BCRecipes.TYPEWRITER_PAGE_CLONING.get()).save(output, "typewriter_page_cloning");
        new PrintingTableCloningRecipe.Builder(new ItemStack(BCItems.CLIPBOARD.get()), 100)
                .addNbtKey(ClipboardContent.NBT_KEY)
                .addIngredient(Ingredient.of(BCItems.CLIPBOARD.get()))
                .unlockedBy("has_clipboard", has(BCItems.CLIPBOARD.get()))
                .save(output, BCUtil.bcLoc("clipboard_cloning_in_printing_table"));
        new PrintingTableCloningRecipe.Builder(new ItemStack(BCItems.TYPEWRITER_PAGE.get()), 100)
                .addNbtKey(com.github.minecraftschurlimods.bibliocraft.content.typewriter.TypewriterPage.NBT_KEY)
                .addIngredient(Ingredient.of(BCTags.Items.TYPEWRITER_PAPER))
                .unlockedBy("has_paper", has(BCTags.Items.TYPEWRITER_PAPER))
                .save(output, BCUtil.bcLoc("typewriter_page_cloning_in_printing_table"));
        new PrintingTableCloningRecipe.Builder(new ItemStack(Items.WRITABLE_BOOK), 100)
                .addNbtKey("pages")
                .addIngredient(Ingredient.of(Items.WRITABLE_BOOK))
                .unlockedBy("has_writable_book", has(Items.WRITABLE_BOOK))
                .save(output, BCUtil.bcLoc("writable_book_cloning_in_printing_table"));
        new PrintingTableCloningRecipe.Builder(new ItemStack(Items.WRITTEN_BOOK), 100)
                .addNbtKey("title").addNbtKey("author").addNbtKey("pages").addNbtKey("generation").addNbtKey("resolved")
                .addIngredient(Ingredient.of(Items.WRITABLE_BOOK))
                .unlockedBy("has_writable_book", has(Items.WRITABLE_BOOK))
                .save(output, BCUtil.bcLoc("written_book_cloning_in_printing_table"));
        new PrintingTableCloningRecipe.Builder(new ItemStack(BCItems.BIG_BOOK.get()), 100)
                .addNbtKey(BigBookContent.NBT_KEY)
                .addIngredient(Ingredient.of(BCItems.BIG_BOOK.get()))
                .unlockedBy("has_big_book", has(BCItems.BIG_BOOK.get()))
                .save(output, BCUtil.bcLoc("big_book_cloning_in_printing_table"));
        new PrintingTableCloningRecipe.Builder(new ItemStack(BCItems.WRITTEN_BIG_BOOK.get()), 100)
                .addNbtKey(WrittenBigBookContent.NBT_KEY)
                .addIngredient(Ingredient.of(BCItems.BIG_BOOK.get()))
                .unlockedBy("has_big_book", has(BCItems.BIG_BOOK.get()))
                .save(output, BCUtil.bcLoc("written_big_book_cloning_in_printing_table"));
        new PrintingTableCloningWithEnchantmentsRecipe.Builder(new ItemStack(Items.ENCHANTED_BOOK), 600)
                .addIngredient(Ingredient.of(Items.BOOK))
                .experienceCost(new EnchantmentLevelsNumberProvider(ConstantValue.exactly(1), ConstantValue.exactly(2)))
                .unlockedBy("has_enchanted_book", has(Items.ENCHANTED_BOOK))
                .save(output, BCUtil.bcLoc("enchanted_book_cloning_in_printing_table"));
        new PrintingTableMergingRecipe.Builder(Ingredient.of(BCItems.CLIPBOARD.get()), new ItemStack(BCItems.CLIPBOARD.get()), 200)
                .addMerger(ClipboardContent.NBT_KEY, "title", PrintingTableMergingRecipe.MergeMethod.FIRST)
                .addMerger(ClipboardContent.NBT_KEY, "active", PrintingTableMergingRecipe.MergeMethod.FIRST)
                .addMerger(ClipboardContent.NBT_KEY, "pages", PrintingTableMergingRecipe.MergeMethod.APPEND)
                .unlockedBy("has_clipboard", has(BCItems.CLIPBOARD.get()))
                .save(output, BCUtil.bcLoc("clipboard_merging"));
        new PrintingTableMergingRecipe.Builder(Ingredient.of(Items.WRITABLE_BOOK), new ItemStack(Items.WRITABLE_BOOK), 200)
                .addMerger("pages", "pages", PrintingTableMergingRecipe.MergeMethod.APPEND)
                .unlockedBy("has_writable_book", has(Items.WRITABLE_BOOK))
                .save(output, BCUtil.bcLoc("writable_book_merging"));
        new PrintingTableMergingRecipe.Builder(Ingredient.of(Items.WRITABLE_BOOK), new ItemStack(Items.WRITTEN_BOOK), 200)
                .addMerger("WrittenBook", "title", PrintingTableMergingRecipe.MergeMethod.FIRST)
                .addMerger("WrittenBook", "author", PrintingTableMergingRecipe.MergeMethod.FIRST)
                .addMerger("WrittenBook", "generation", PrintingTableMergingRecipe.MergeMethod.MIN)
                .addMerger("WrittenBook", "pages", PrintingTableMergingRecipe.MergeMethod.APPEND)
                .unlockedBy("has_writable_book", has(Items.WRITABLE_BOOK))
                .save(output, BCUtil.bcLoc("written_book_merging"));
        new PrintingTableMergingRecipe.Builder(Ingredient.of(BCItems.BIG_BOOK.get()), new ItemStack(BCItems.BIG_BOOK.get()), 200)
                .addMerger(BigBookContent.NBT_KEY, "pages", PrintingTableMergingRecipe.MergeMethod.APPEND)
                .addMerger(BigBookContent.NBT_KEY, "current_page", PrintingTableMergingRecipe.MergeMethod.FIRST)
                .unlockedBy("has_big_book", has(BCItems.BIG_BOOK.get()))
                .save(output, BCUtil.bcLoc("big_book_merging"));
        new PrintingTableMergingRecipe.Builder(Ingredient.of(BCItems.BIG_BOOK.get()), new ItemStack(BCItems.WRITTEN_BIG_BOOK.get()), 200)
                .addMerger(WrittenBigBookContent.NBT_KEY, "pages", PrintingTableMergingRecipe.MergeMethod.APPEND)
                .addMerger(WrittenBigBookContent.NBT_KEY, "title", PrintingTableMergingRecipe.MergeMethod.FIRST)
                .addMerger(WrittenBigBookContent.NBT_KEY, "author", PrintingTableMergingRecipe.MergeMethod.FIRST)
                .addMerger(WrittenBigBookContent.NBT_KEY, "generation", PrintingTableMergingRecipe.MergeMethod.MIN)
                .addMerger(WrittenBigBookContent.NBT_KEY, "current_page", PrintingTableMergingRecipe.MergeMethod.FIRST)
                .unlockedBy("has_big_book", has(BCItems.BIG_BOOK.get()))
                .save(output, BCUtil.bcLoc("written_big_book_merging"));
        new PrintingTableBindingTypewriterPagesRecipe.Builder(Ingredient.of(Tags.Items.LEATHER), 200)
                .unlockedBy("has_leather", has(Tags.Items.LEATHER))
                .save(output, BCUtil.bcLoc("typewriter_pages_binding"));
    }
}
