package com.github.minecraftschurlimods.bibliocraft.content.printingtable;

import com.github.minecraftschurlimods.bibliocraft.init.BCRecipes;
import com.github.minecraftschurlimods.bibliocraft.init.BCTags;
import com.github.minecraftschurlimods.bibliocraft.util.CodecUtil;
import com.google.gson.JsonObject;
import net.minecraft.util.registry.Registry;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.world.World;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.EnchantmentLevelsNumberProvider;
import net.minecraft.loot.IRandomRange;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class PrintingTableCloningWithEnchantmentsRecipe extends PrintingTableCloningRecipe {
    private static final String STORED_ENCHANTMENTS_KEY = "StoredEnchantments";

    private final Optional<IRandomRange> experienceCost;

    public PrintingTableCloningWithEnchantmentsRecipe(ResourceLocation id, List<Ingredient> ingredients, ItemStack result, int duration, Optional<IRandomRange> experienceCost) {
        super(id, java.util.Collections.singletonList(STORED_ENCHANTMENTS_KEY), ingredients, result, duration);
        this.experienceCost = experienceCost;
    }

    @Override
    public boolean matches(PrintingTableRecipeInput input, World level) {
        if (!super.matches(input, level)) return false;
        ItemStack stack = input.right();
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        if (enchantments.isEmpty()) return false;
        return enchantments.keySet().stream()
                .noneMatch(e -> BCTags.Enchantments.PRINTING_TABLE_CLONING_BLACKLIST.contains(e));
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return BCRecipes.PRINTING_TABLE_CLONING_WITH_ENCHANTMENTS.get();
    }

    @Override
    public int getExperienceLevelCost(ItemStack stack, ServerWorld level) {
        return experienceCost.map(provider -> {
            LootContext context = new LootContext.Builder(level)
                    .withParameter(LootParameters.TOOL, stack)
                    .create(LootParameterSets.FISHING);
            if (provider instanceof EnchantmentLevelsNumberProvider) {
                return ((EnchantmentLevelsNumberProvider) provider).getInt(context);
            }
            return provider.getInt(context.getRandom());
        }).orElse(0);
    }

    @Override
    public boolean canHaveExperienceCost() {
        return experienceCost.isPresent();
    }

    @Override
    public void toRecipeJson(JsonObject json) {
        ((Serializer) getSerializer()).toJson(json, this);
    }

    public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<net.minecraft.item.crafting.IRecipeSerializer<?>> implements IRecipeSerializer<PrintingTableCloningWithEnchantmentsRecipe> {
        @Override
        public PrintingTableCloningWithEnchantmentsRecipe fromJson(ResourceLocation id, JsonObject json) {
            List<Ingredient> ingredients = new ArrayList<>();
            for (com.google.gson.JsonElement el : json.getAsJsonArray("ingredients")) ingredients.add(Ingredient.fromJson(el.getAsJsonObject()));
            ItemStack result = net.minecraftforge.common.crafting.CraftingHelper.getItemStack(json.getAsJsonObject("result"), true);
            int duration = json.getAsJsonPrimitive("duration").getAsInt();
            Optional<IRandomRange> experienceCost = json.has("experience_cost")
                    ? Optional.of(EnchantmentLevelsNumberProvider.parse(json.get("experience_cost")))
                    : Optional.empty();
            return new PrintingTableCloningWithEnchantmentsRecipe(id, ingredients, result, duration, experienceCost);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, PrintingTableCloningWithEnchantmentsRecipe recipe) {
            buffer.writeVarInt(recipe.ingredients.size());
            for (Ingredient ing : recipe.ingredients) ing.toNetwork(buffer);
            buffer.writeItem(recipe.result);
            buffer.writeVarInt(recipe.duration);
            buffer.writeBoolean(recipe.experienceCost.isPresent());
            recipe.experienceCost.ifPresent(p -> CodecUtil.encodeToBuffer(buffer, EnchantmentLevelsNumberProvider.NUMBER_PROVIDER_CODEC, p));
        }

        @Override
        public PrintingTableCloningWithEnchantmentsRecipe fromNetwork(ResourceLocation id, PacketBuffer buffer) {
            int m = buffer.readVarInt();
            List<Ingredient> ingredients = new ArrayList<>(m);
            for (int i = 0; i < m; i++) ingredients.add(Ingredient.fromNetwork(buffer));
            ItemStack result = buffer.readItem();
            int duration = buffer.readVarInt();
            Optional<IRandomRange> experienceCost = buffer.readBoolean() ? Optional.of(CodecUtil.decodeFromBuffer(buffer, EnchantmentLevelsNumberProvider.NUMBER_PROVIDER_CODEC)) : Optional.empty();
            return new PrintingTableCloningWithEnchantmentsRecipe(id, ingredients, result, duration, experienceCost);
        }

        public void toJson(JsonObject json, PrintingTableRecipe recipe) {
            PrintingTableCloningWithEnchantmentsRecipe r = (PrintingTableCloningWithEnchantmentsRecipe) recipe;
            com.google.gson.JsonArray ings = new com.google.gson.JsonArray();
            for (Ingredient ing : r.ingredients) ings.add(ing.toJson());
            json.add("ingredients", ings);
            JsonObject resultObj = new JsonObject();
            resultObj.addProperty("item", Registry.ITEM.getKey(r.result.getItem()).toString());
            if (r.result.getCount() != 1) resultObj.addProperty("count", r.result.getCount());
            if (r.result.hasTag()) resultObj.addProperty("nbt", r.result.getTag().toString());
            json.add("result", resultObj);
            json.addProperty("duration", r.duration);
            r.experienceCost.ifPresent(p -> json.add("experience_cost", CodecUtil.encodeJson(EnchantmentLevelsNumberProvider.NUMBER_PROVIDER_CODEC, p)));
        }
    }

    public static class Builder extends PrintingTableRecipe.Builder {
        private final List<Ingredient> ingredients = new ArrayList<>();
        private IRandomRange experienceCost = null;

        public Builder(ItemStack result, int duration) {
            super(result, duration);
        }

        public Builder addIngredient(Ingredient ingredient) {
            ingredients.add(ingredient);
            return this;
        }

        public Builder experienceCost(IRandomRange experienceCost) {
            this.experienceCost = experienceCost;
            return this;
        }

        public PrintingTableCloningWithEnchantmentsRecipe build() {
            return new PrintingTableCloningWithEnchantmentsRecipe(new ResourceLocation(com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi.MOD_ID, "printing_table_cloning_with_enchantments"), ingredients, result, duration, Optional.ofNullable(experienceCost));
        }
    }
}
