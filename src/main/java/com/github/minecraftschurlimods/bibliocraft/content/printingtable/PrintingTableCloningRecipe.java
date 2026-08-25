package com.github.minecraftschurlimods.bibliocraft.content.printingtable;

import com.github.minecraftschurlimods.bibliocraft.init.BCRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.NonNullList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.world.World;
import net.minecraft.nbt.CompoundNBT;

import java.util.ArrayList;
import java.util.List;

public class PrintingTableCloningRecipe extends PrintingTableRecipe {
    protected final List<String> nbtKeysToCopy;
    protected final List<Ingredient> ingredients;

    public PrintingTableCloningRecipe(ResourceLocation id, List<String> nbtKeysToCopy, List<Ingredient> ingredients, ItemStack result, int duration) {
        super(id, result, duration);
        this.nbtKeysToCopy = nbtKeysToCopy;
        this.ingredients = ingredients;
    }

    @Override
    public boolean matches(PrintingTableRecipeInput input, World level) {
        if (input.left().isEmpty()) return false;
        if (input.right().isEmpty()) return false;
        if (input.right().getItem() != result.getItem()) return false;
        CompoundNBT rightTag = input.right().getTag();
        if (rightTag != null) {
            for (String key : nbtKeysToCopy) {
                if (!rightTag.contains(key)) return false;
            }
        } else if (!nbtKeysToCopy.isEmpty()) return false;
        if (input.left().stream().filter(e -> !e.isEmpty()).count() != ingredients.size()) return false;
        List<Ingredient> copy = new ArrayList<>(ingredients);
        outer:
        for (int i = 0; i < input.left().size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;
            for (Ingredient ingredient : copy) {
                if (ingredient.test(stack)) {
                    copy.remove(ingredient);
                    continue outer;
                }
            }
            return false;
        }
        return copy.isEmpty();
    }

    @Override
    public ItemStack assemble(PrintingTableRecipeInput input) {
        ItemStack stack = result.copy();
        CompoundNBT rightTag = input.right().getTag();
        if (rightTag != null) {
            CompoundNBT resultTag = stack.getOrCreateTag();
            for (String key : nbtKeysToCopy) {
                if (rightTag.contains(key)) {
                    resultTag.put(key, rightTag.get(key).copy());
                }
            }
        }
        return stack;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return BCRecipes.PRINTING_TABLE_CLONING.get();
    }

    @Override
    public PrintingTableMode getMode() {
        return PrintingTableMode.CLONE;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(PrintingTableRecipeInput input) {
        NonNullList<ItemStack> remainingItems = super.getRemainingItems(input);
        remainingItems.set(9, input.right().copy());
        return remainingItems;
    }

    @Override
    public Pair<List<Ingredient>, Ingredient> getDisplayIngredients() {
        return Pair.of(ingredients, Ingredient.of(result));
    }

    @Override
    public void toRecipeJson(JsonObject json) {
        ((Serializer) getSerializer()).toJson(json, this);
    }

    public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<net.minecraft.item.crafting.IRecipeSerializer<?>> implements IRecipeSerializer<PrintingTableCloningRecipe> {
        /** Maps 1.21-style data component IDs to 1.20.1 NBT tag keys used for copying. */
        private static List<String> dataComponentsToNbtKeys(JsonArray dataComponents) {
            List<String> nbtKeys = new ArrayList<>();
            for (com.google.gson.JsonElement el : dataComponents) {
                String comp = el.getAsString();
                switch (comp) {
case "minecraft:written_book_content": nbtKeys.addAll(java.util.Collections.unmodifiableList(java.util.Arrays.asList("pages", "author", "title", "resolved", "generation"))); break;
case "minecraft:writable_book_content": nbtKeys.add("pages"); break;
case "minecraft:stored_enchantments": nbtKeys.add("StoredEnchantments"); break;
case "bibliocraft:typewriter_page": nbtKeys.add("TypewriterPage"); break;
case "bibliocraft:clipboard_content": nbtKeys.add("ClipboardContent"); break;
case "bibliocraft:written_big_book_content": nbtKeys.add("WrittenBigBookContent"); break;
case "bibliocraft:big_book_content": nbtKeys.add("BigBookContent"); break;
default: nbtKeys.add(comp); break;
}
            }
            return nbtKeys;
        }

        @Override
        public PrintingTableCloningRecipe fromJson(ResourceLocation id, JsonObject json) {
            List<String> nbtKeys;
            if (json.has("nbt_keys") && json.get("nbt_keys").isJsonArray()) {
                nbtKeys = new ArrayList<>();
                for (com.google.gson.JsonElement el : json.getAsJsonArray("nbt_keys")) nbtKeys.add(el.getAsString());
            } else if (json.has("data_components") && json.get("data_components").isJsonArray()) {
                nbtKeys = dataComponentsToNbtKeys(json.getAsJsonArray("data_components"));
            } else {
                nbtKeys = new ArrayList<>();
            }
            List<Ingredient> ingredients = new ArrayList<>();
            JsonArray ingredientsJson = json.has("ingredients") && json.get("ingredients").isJsonArray() ? json.getAsJsonArray("ingredients") : new JsonArray();
            for (com.google.gson.JsonElement el : ingredientsJson) ingredients.add(Ingredient.fromJson(el.getAsJsonObject()));
            ItemStack result = net.minecraftforge.common.crafting.CraftingHelper.getItemStack(json.getAsJsonObject("result"), true);
            int duration = json.getAsJsonPrimitive("duration").getAsInt();
            return new PrintingTableCloningRecipe(id, nbtKeys, ingredients, result, duration);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, PrintingTableCloningRecipe recipe) {
            buffer.writeVarInt(recipe.nbtKeysToCopy.size());
            for (String key : recipe.nbtKeysToCopy) buffer.writeUtf(key);
            buffer.writeVarInt(recipe.ingredients.size());
            for (Ingredient ing : recipe.ingredients) ing.toNetwork(buffer);
            buffer.writeItem(recipe.result);
            buffer.writeVarInt(recipe.duration);
        }

        @Override
        public PrintingTableCloningRecipe fromNetwork(ResourceLocation id, PacketBuffer buffer) {
            int n = buffer.readVarInt();
            List<String> nbtKeys = new ArrayList<>(n);
            for (int i = 0; i < n; i++) nbtKeys.add(buffer.readUtf());
            int m = buffer.readVarInt();
            List<Ingredient> ingredients = new ArrayList<>(m);
            for (int i = 0; i < m; i++) ingredients.add(Ingredient.fromNetwork(buffer));
            ItemStack result = buffer.readItem();
            int duration = buffer.readVarInt();
            return new PrintingTableCloningRecipe(id, nbtKeys, ingredients, result, duration);
        }

        public void toJson(JsonObject json, PrintingTableRecipe recipe) {
            PrintingTableCloningRecipe r = (PrintingTableCloningRecipe) recipe;
            JsonArray keys = new JsonArray();
            for (String key : r.nbtKeysToCopy) keys.add(key);
            json.add("nbt_keys", keys);
            JsonArray ings = new JsonArray();
            for (Ingredient ing : r.ingredients) ings.add(ing.toJson());
            json.add("ingredients", ings);
            JsonObject resultObj = new JsonObject();
            resultObj.addProperty("item", net.minecraft.util.registry.Registry.ITEM.getKey(r.result.getItem()).toString());
            if (r.result.getCount() != 1) resultObj.addProperty("count", r.result.getCount());
            if (r.result.hasTag()) resultObj.addProperty("nbt", r.result.getTag().toString());
            json.add("result", resultObj);
            json.addProperty("duration", r.duration);
        }
    }

    public static class Builder extends PrintingTableRecipe.Builder {
        private final List<String> nbtKeysToCopy = new ArrayList<>();
        private final List<Ingredient> ingredients = new ArrayList<>();

        public Builder(ItemStack result, int duration) {
            super(result, duration);
        }

        public Builder addNbtKey(String key) {
            nbtKeysToCopy.add(key);
            return this;
        }

        public Builder addIngredient(Ingredient ingredient) {
            ingredients.add(ingredient);
            return this;
        }

        @Override
        public PrintingTableRecipe build() {
            return new PrintingTableCloningRecipe(new ResourceLocation(com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi.MOD_ID, "printing_table_cloning"), java.util.Collections.unmodifiableList(new java.util.ArrayList<>(nbtKeysToCopy)), java.util.Collections.unmodifiableList(new java.util.ArrayList<>(ingredients)), result, duration);
        }
    }
}
