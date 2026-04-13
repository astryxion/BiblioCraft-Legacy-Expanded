package com.github.minecraftschurlimods.bibliocraft.content.printingtable;

import com.github.minecraftschurlimods.bibliocraft.init.BCRecipes;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.data.recipes.FinishedRecipe;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class PrintingTableRecipe implements Recipe<PrintingTableRecipeInput> {
    protected final ResourceLocation id;
    protected final ItemStack result;
    protected final int duration;

    public PrintingTableRecipe(ResourceLocation id, ItemStack result, int duration) {
        this.id = id;
        this.result = result;
        this.duration = duration;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public RecipeType<?> getType() {
        return BCRecipes.PRINTING_TABLE.get();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registries) {
        return getResultItem();
    }

    public ItemStack getResultItem() {
        return result.copy();
    }

    public int getDuration() {
        return duration;
    }

    public int getExperienceLevelCost(ItemStack result, ServerLevel level) {
        return 0;
    }

    public boolean canHaveExperienceCost() {
        return false;
    }

    public ItemStack postProcess(ItemStack result, PrintingTableBlockEntity blockEntity) {
        return result;
    }

    public Pair<List<Ingredient>, Ingredient> getDisplayIngredients() {
        return Pair.of(List.of(), Ingredient.EMPTY);
    }

    public abstract PrintingTableMode getMode();

    /** Serialize this recipe to JSON for datagen (1.20.1: RecipeSerializer has no toJson in interface). */
    public abstract void toRecipeJson(JsonObject json);

    public static abstract class Builder implements RecipeBuilder {
        protected final ItemStack result;
        protected final int duration;
        protected final Map<String, CriterionTriggerInstance> criteria = new LinkedHashMap<>();

        public Builder(ItemStack result, int duration) {
            this.result = result;
            this.duration = duration;
        }

        @Override
        public Builder unlockedBy(String name, CriterionTriggerInstance criterion) {
            criteria.put(name, criterion);
            return this;
        }

        public Builder group(@Nullable String group) {
            return this;
        }

        public Item getResult() {
            return result.getItem();
        }

        @Override
        public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
            Advancement.Builder advancementBuilder = Advancement.Builder.advancement()
                    .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                    .rewards(AdvancementRewards.Builder.recipe(id));
            criteria.forEach((name, inst) -> advancementBuilder.addCriterion(name, new Criterion(inst)));
            String[][] req = new String[1][];
            req[0] = criteria.keySet().toArray(new String[0]);
            advancementBuilder.requirements(req);
            ResourceLocation advId = new ResourceLocation(id.getNamespace(), "recipes/" + id.getPath());
            Advancement advancement = advancementBuilder.build(advId);
            PrintingTableRecipe recipe = build();
            consumer.accept(new FinishedRecipe() {
                @Override
                public ResourceLocation getId() { return id; }
                @Override
                public net.minecraft.world.item.crafting.RecipeSerializer<?> getType() { return recipe.getSerializer(); }
                @Override
                public void serializeRecipeData(JsonObject json) {
                    recipe.toRecipeJson(json);
                }
                @Override
                public ResourceLocation getAdvancementId() { return advId; }
                @Override
                public JsonObject serializeAdvancement() {
                    return serializeAdvancementToJson(advancement);
                }
            });
        }

        public abstract PrintingTableRecipe build();
    }

    /** 1.20.1: Advancement has no CODEC; serialize via reflection or manual build. */
    private static JsonObject serializeAdvancementToJson(Advancement advancement) {
        try {
            java.lang.reflect.Field codecField = Advancement.class.getDeclaredField("CODEC");
            codecField.setAccessible(true);
            @SuppressWarnings("unchecked")
            com.mojang.serialization.Codec<Advancement> codec = (com.mojang.serialization.Codec<Advancement>) codecField.get(null);
            return codec.encodeStart(com.mojang.serialization.JsonOps.INSTANCE, advancement)
                    .result()
                    .filter(com.google.gson.JsonElement::isJsonObject)
                    .map(com.google.gson.JsonElement::getAsJsonObject)
                    .orElseGet(com.google.gson.JsonObject::new);
        } catch (NoSuchFieldException e) {
            return buildAdvancementJsonManually(advancement);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize advancement", e);
        }
    }

    private static JsonObject buildAdvancementJsonManually(Advancement advancement) {
        JsonObject json = new JsonObject();
        json.add("parent", null);
        JsonObject criteria = new JsonObject();
        for (String key : advancement.getCriteria().keySet()) {
            JsonObject c = new JsonObject();
            c.addProperty("trigger", "minecraft:recipe_unlocked");
            c.add("conditions", new JsonObject());
            criteria.add(key, c);
        }
        json.add("criteria", criteria);
        JsonArray requirements = new JsonArray();
        for (String[] req : advancement.getRequirements()) {
            JsonArray arr = new JsonArray();
            for (String s : req) arr.add(s);
            requirements.add(arr);
        }
        json.add("requirements", requirements);
        json.add("rewards", new JsonObject());
        return json;
    }
}
