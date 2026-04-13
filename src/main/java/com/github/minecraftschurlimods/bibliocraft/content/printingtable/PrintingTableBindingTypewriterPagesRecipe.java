package com.github.minecraftschurlimods.bibliocraft.content.printingtable;

import com.github.minecraftschurlimods.bibliocraft.content.typewriter.TypewriterPage;
import com.github.minecraftschurlimods.bibliocraft.init.BCItems;
import com.github.minecraftschurlimods.bibliocraft.init.BCRecipes;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;

import java.util.ArrayList;
import java.util.List;

public class PrintingTableBindingTypewriterPagesRecipe extends PrintingTableBindingRecipe {
    private final Ingredient ingredient;

    public PrintingTableBindingTypewriterPagesRecipe(ResourceLocation id, Ingredient ingredient, int duration) {
        super(id, new ItemStack(Items.WRITTEN_BOOK), duration);
        this.ingredient = ingredient;
    }

    @Override
    public boolean matches(PrintingTableRecipeInput input, Level level) {
        if (input.left().isEmpty()) return false;
        if (input.right().isEmpty()) return false;
        if (!ingredient.test(input.right())) return false;
        for (ItemStack stack : input.left()) {
            if (!stack.isEmpty() && !stack.is(BCItems.TYPEWRITER_PAGE.get())) return false;
            if (!stack.isEmpty()) {
                TypewriterPage page = TypewriterPage.getFromStack(stack);
                if (page.lines().stream().allMatch(String::isEmpty)) return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(PrintingTableRecipeInput input, RegistryAccess registries) {
        ItemStack result = getResultItem(registries).copy();
        List<Component> pageComponents = new ArrayList<>();
        for (ItemStack stack : input.left()) {
            if (stack.isEmpty()) continue;
            if (!stack.is(BCItems.TYPEWRITER_PAGE.get())) continue;
            TypewriterPage page = TypewriterPage.getFromStack(stack);
            pageComponents.add(concatTypewriterPageText(page));
        }
        if (pageComponents.isEmpty()) return result;
        ListTag pagesTag = new ListTag();
        for (Component comp : pageComponents) {
            pagesTag.add(StringTag.valueOf(Component.Serializer.toJson(comp)));
        }
        result.getOrCreateTag().put("pages", pagesTag);
        result.getOrCreateTag().putString("title", "");
        result.getOrCreateTag().putString("author", "");
        result.getOrCreateTag().putBoolean("resolved", false);
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BCRecipes.PRINTING_TABLE_BINDING_TYPEWRITER_PAGES.get();
    }

    @Override
    public ItemStack postProcess(ItemStack result, PrintingTableBlockEntity blockEntity) {
        Component name = blockEntity.getPlayerName();
        if (name != null && result.hasTag() && result.getTag().contains("author")) {
            result.getOrCreateTag().putString("author", name.getString());
        }
        return result;
    }

    @Override
    public Pair<List<Ingredient>, Ingredient> getDisplayIngredients() {
        return Pair.of(List.of(Ingredient.of(BCItems.TYPEWRITER_PAGE.get())), ingredient);
    }

    @Override
    public void toRecipeJson(com.google.gson.JsonObject json) {
        ((Serializer) getSerializer()).toJson(json, this);
    }

    private static Component concatTypewriterPageText(TypewriterPage page) {
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
        return Component.literal(builder.toString());
    }

    public static class Serializer implements RecipeSerializer<PrintingTableBindingTypewriterPagesRecipe> {
        @Override
        public PrintingTableBindingTypewriterPagesRecipe fromJson(ResourceLocation id, JsonObject json) {
            Ingredient ingredient = Ingredient.fromJson(json.getAsJsonObject("ingredient"), false);
            int duration = json.getAsJsonPrimitive("duration").getAsInt();
            return new PrintingTableBindingTypewriterPagesRecipe(id, ingredient, duration);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, PrintingTableBindingTypewriterPagesRecipe recipe) {
            recipe.ingredient.toNetwork(buffer);
            buffer.writeVarInt(recipe.duration);
        }

        @Override
        public PrintingTableBindingTypewriterPagesRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
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
