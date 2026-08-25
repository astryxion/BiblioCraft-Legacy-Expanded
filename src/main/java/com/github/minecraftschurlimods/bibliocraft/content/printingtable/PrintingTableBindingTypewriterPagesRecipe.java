package com.github.minecraftschurlimods.bibliocraft.content.printingtable;

import net.minecraft.util.text.StringTextComponent;
import com.github.minecraftschurlimods.bibliocraft.content.typewriter.TypewriterPage;
import com.github.minecraftschurlimods.bibliocraft.init.BCItems;
import com.github.minecraftschurlimods.bibliocraft.init.BCRecipes;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.world.World;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;

import java.util.ArrayList;
import java.util.List;

public class PrintingTableBindingTypewriterPagesRecipe extends PrintingTableBindingRecipe {
    private final Ingredient ingredient;

    public PrintingTableBindingTypewriterPagesRecipe(ResourceLocation id, Ingredient ingredient, int duration) {
        super(id, new ItemStack(Items.WRITTEN_BOOK), duration);
        this.ingredient = ingredient;
    }

    @Override
    public boolean matches(PrintingTableRecipeInput input, World level) {
        if (input.left().isEmpty()) return false;
        if (input.right().isEmpty()) return false;
        if (!ingredient.test(input.right())) return false;
        for (ItemStack stack : input.left()) {
            if (!stack.isEmpty() && stack.getItem() != BCItems.TYPEWRITER_PAGE.get()) return false;
            if (!stack.isEmpty()) {
                TypewriterPage page = TypewriterPage.getFromStack(stack);
                if (page.lines().stream().allMatch(String::isEmpty)) return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(PrintingTableRecipeInput input) {
        ItemStack result = getResultItem().copy();
        List<ITextComponent> pageComponents = new ArrayList<>();
        for (ItemStack stack : input.left()) {
            if (stack.isEmpty()) continue;
            if (stack.getItem() != BCItems.TYPEWRITER_PAGE.get()) continue;
            TypewriterPage page = TypewriterPage.getFromStack(stack);
            pageComponents.add(concatTypewriterPageText(page));
        }
        if (pageComponents.isEmpty()) return result;
        ListNBT pagesTag = new ListNBT();
        for (ITextComponent comp : pageComponents) {
            pagesTag.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(comp)));
        }
        result.getOrCreateTag().put("pages", pagesTag);
        result.getOrCreateTag().putString("title", "");
        result.getOrCreateTag().putString("author", "");
        result.getOrCreateTag().putBoolean("resolved", false);
        return result;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return BCRecipes.PRINTING_TABLE_BINDING_TYPEWRITER_PAGES.get();
    }

    @Override
    public ItemStack postProcess(ItemStack result, PrintingTableBlockEntity blockEntity) {
        ITextComponent name = blockEntity.getPlayerName();
        if (name != null && result.hasTag() && result.getTag().contains("author")) {
            result.getOrCreateTag().putString("author", name.getString());
        }
        return result;
    }

    @Override
    public Pair<List<Ingredient>, Ingredient> getDisplayIngredients() {
        return Pair.of(java.util.Collections.singletonList(Ingredient.of(BCItems.TYPEWRITER_PAGE.get())), ingredient);
    }

    @Override
    public void toRecipeJson(com.google.gson.JsonObject json) {
        ((Serializer) getSerializer()).toJson(json, this);
    }

    private static ITextComponent concatTypewriterPageText(TypewriterPage page) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < page.lines().size(); i++) {
            String line = page.lines().get(i);
            if (!line.isEmpty()) {
                builder.append(line);
                if (i < page.lines().size() - 1) builder.append('\n');
            } else {
                builder.append('\n');
            }
        }
        return new StringTextComponent(builder.toString());
    }

    public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<net.minecraft.item.crafting.IRecipeSerializer<?>> implements IRecipeSerializer<PrintingTableBindingTypewriterPagesRecipe> {
        @Override
        public PrintingTableBindingTypewriterPagesRecipe fromJson(ResourceLocation id, JsonObject json) {
            Ingredient ingredient = Ingredient.fromJson(json.getAsJsonObject("ingredient"));
            int duration = json.getAsJsonPrimitive("duration").getAsInt();
            return new PrintingTableBindingTypewriterPagesRecipe(id, ingredient, duration);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, PrintingTableBindingTypewriterPagesRecipe recipe) {
            recipe.ingredient.toNetwork(buffer);
            buffer.writeVarInt(recipe.duration);
        }

        @Override
        public PrintingTableBindingTypewriterPagesRecipe fromNetwork(ResourceLocation id, PacketBuffer buffer) {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            int duration = buffer.readVarInt();
            return new PrintingTableBindingTypewriterPagesRecipe(id, ingredient, duration);
        }

        public void toJson(JsonObject json, PrintingTableRecipe recipe) {
            PrintingTableBindingTypewriterPagesRecipe r = (PrintingTableBindingTypewriterPagesRecipe) recipe;
            json.add("ingredient", r.ingredient.toJson());
            json.addProperty("duration", r.duration);
        }
    }

    public static class Builder extends PrintingTableRecipe.Builder {
        private final Ingredient ingredient;

        public Builder(Ingredient ingredient, int duration) {
            super(ItemStack.EMPTY, duration);
            this.ingredient = ingredient;
        }

        @Override
        public PrintingTableRecipe build() {
            return new PrintingTableBindingTypewriterPagesRecipe(new ResourceLocation(com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi.MOD_ID, "printing_table_binding_typewriter_pages"), ingredient, duration);
        }
    }
}
