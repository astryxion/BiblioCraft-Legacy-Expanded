package com.github.minecraftschurlimods.bibliocraft.content.printingtable;

import com.github.minecraftschurlimods.bibliocraft.init.BCRecipes;
import com.github.minecraftschurlimods.bibliocraft.init.BCTags;
import com.github.minecraftschurlimods.bibliocraft.util.CodecUtil;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.EnchantmentLevelsNumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class PrintingTableCloningWithEnchantmentsRecipe extends PrintingTableCloningRecipe {
    private static final String STORED_ENCHANTMENTS_KEY = "StoredEnchantments";

    private final Optional<NumberProvider> experienceCost;

    public PrintingTableCloningWithEnchantmentsRecipe(ResourceLocation id, List<Ingredient> ingredients, ItemStack result, int duration, Optional<NumberProvider> experienceCost) {
        super(id, List.of(STORED_ENCHANTMENTS_KEY), ingredients, result, duration);
        this.experienceCost = experienceCost;
    }

    @Override
    public boolean matches(PrintingTableRecipeInput input, Level level) {
        if (!super.matches(input, level)) return false;
        ItemStack stack = input.right();
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        if (enchantments.isEmpty()) return false;
        var registry = level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT);
        return enchantments.keySet().stream()
                .noneMatch(e -> registry.getResourceKey(e).flatMap(registry::getHolder).map(h -> h.is(BCTags.Enchantments.PRINTING_TABLE_CLONING_BLACKLIST)).orElse(false));
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BCRecipes.PRINTING_TABLE_CLONING_WITH_ENCHANTMENTS.get();
    }

    @Override
    public int getExperienceLevelCost(ItemStack stack, ServerLevel level) {
        return experienceCost.map(provider -> provider.getInt(new LootContext.Builder(new LootParams(level, Map.of(LootContextParams.TOOL, stack), Map.of(), 0)).create(null))).orElse(0);
    }

    @Override
    public boolean canHaveExperienceCost() {
        return experienceCost.isPresent();
    }

    @Override
    public void toRecipeJson(JsonObject json) {
        ((Serializer) getSerializer()).toJson(json, this);
    }

    public static class Serializer implements RecipeSerializer<PrintingTableCloningWithEnchantmentsRecipe> {
        @Override
        public PrintingTableCloningWithEnchantmentsRecipe fromJson(ResourceLocation id, JsonObject json) {
            List<Ingredient> ingredients = new ArrayList<>();
            for (var el : json.getAsJsonArray("ingredients")) ingredients.add(Ingredient.fromJson(el.getAsJsonObject(), false));
            ItemStack result = net.minecraftforge.common.crafting.CraftingHelper.getItemStack(json.getAsJsonObject("result"), true);
            int duration = json.getAsJsonPrimitive("duration").getAsInt();
            Optional<NumberProvider> experienceCost = json.has("experience_cost")
                    ? Optional.of(CodecUtil.decodeJson(EnchantmentLevelsNumberProvider.NUMBER_PROVIDER_CODEC, json.get("experience_cost")))
                    : Optional.empty();
            return new PrintingTableCloningWithEnchantmentsRecipe(id, ingredients, result, duration, experienceCost);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, PrintingTableCloningWithEnchantmentsRecipe recipe) {
            buffer.writeVarInt(recipe.ingredients.size());
            for (Ingredient ing : recipe.ingredients) ing.toNetwork(buffer);
            buffer.writeItem(recipe.result);
            buffer.writeVarInt(recipe.duration);
            buffer.writeBoolean(recipe.experienceCost.isPresent());
            recipe.experienceCost.ifPresent(p -> CodecUtil.encodeToBuffer(buffer, EnchantmentLevelsNumberProvider.NUMBER_PROVIDER_CODEC, p));
        }

        @Override
        public PrintingTableCloningWithEnchantmentsRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            int m = buffer.readVarInt();
            List<Ingredient> ingredients = new ArrayList<>(m);
            for (int i = 0; i < m; i++) ingredients.add(Ingredient.fromNetwork(buffer));
            ItemStack result = buffer.readItem();
            int duration = buffer.readVarInt();
            Optional<NumberProvider> experienceCost = buffer.readBoolean() ? Optional.of(CodecUtil.decodeFromBuffer(buffer, EnchantmentLevelsNumberProvider.NUMBER_PROVIDER_CODEC)) : Optional.empty();
            return new PrintingTableCloningWithEnchantmentsRecipe(id, ingredients, result, duration, experienceCost);
        }

        public void toJson(JsonObject json, PrintingTableRecipe recipe) {
            PrintingTableCloningWithEnchantmentsRecipe r = (PrintingTableCloningWithEnchantmentsRecipe) recipe;
            com.google.gson.JsonArray ings = new com.google.gson.JsonArray();
            for (Ingredient ing : r.ingredients) ings.add(ing.toJson());
            json.add("ingredients", ings);
            JsonObject resultObj = new JsonObject();
            resultObj.addProperty("item", BuiltInRegistries.ITEM.getKey(r.result.getItem()).toString());
            if (r.result.getCount() != 1) resultObj.addProperty("count", r.result.getCount());
            if (r.result.hasTag()) resultObj.addProperty("nbt", r.result.getTag().toString());
            json.add("result", resultObj);
            json.addProperty("duration", r.duration);
            r.experienceCost.ifPresent(p -> json.add("experience_cost", CodecUtil.encodeJson(EnchantmentLevelsNumberProvider.NUMBER_PROVIDER_CODEC, p)));
        }
    }

    public static class Builder extends PrintingTableRecipe.Builder {
        private final List<Ingredient> ingredients = new ArrayList<>();
        private NumberProvider experienceCost = null;

        public Builder(ItemStack result, int duration) {
            super(result, duration);
        }

        public Builder addIngredient(Ingredient ingredient) {
            ingredients.add(ingredient);
            return this;
        }

        public Builder experienceCost(NumberProvider experienceCost) {
            this.experienceCost = experienceCost;
            return this;
        }

        public PrintingTableCloningWithEnchantmentsRecipe build() {
            return new PrintingTableCloningWithEnchantmentsRecipe(new ResourceLocation(com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi.MOD_ID, "printing_table_cloning_with_enchantments"), ingredients, result, duration, Optional.ofNullable(experienceCost));
        }
    }
}
