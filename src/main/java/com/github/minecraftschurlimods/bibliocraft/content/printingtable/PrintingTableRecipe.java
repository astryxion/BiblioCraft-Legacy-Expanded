package com.github.minecraftschurlimods.bibliocraft.content.printingtable;

import com.github.minecraftschurlimods.bibliocraft.init.BCRecipes;
import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.item.crafting.IRecipeSerializer;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class PrintingTableRecipe implements IRecipe<PrintingTableRecipeInput> {
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
    public IRecipeType<?> getType() {
        return BCRecipes.PRINTING_TABLE;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public ItemStack getResultItem() {
        return result.copy();
    }

    public int getDuration() {
        return duration;
    }

    public int getExperienceLevelCost(ItemStack result, ServerWorld level) {
        return 0;
    }

    public boolean canHaveExperienceCost() {
        return false;
    }

    public ItemStack postProcess(ItemStack result, PrintingTableBlockEntity blockEntity) {
        return result;
    }

    public Pair<List<Ingredient>, Ingredient> getDisplayIngredients() {
        return Pair.of(java.util.Collections.emptyList(), Ingredient.EMPTY);
    }

    public abstract PrintingTableMode getMode();

    /** Serialize this recipe to JSON for datagen (1.20.1: IRecipeSerializer has no toJson in interface). */
    public abstract void toRecipeJson(JsonObject json);

    public static abstract class Builder {
        protected final ItemStack result;
        protected final int duration;
        protected final Map<String, ICriterionInstance> criteria = new LinkedHashMap<>();

        public Builder(ItemStack result, int duration) {
            this.result = result;
            this.duration = duration;
        }

        public Builder unlockedBy(String name, ICriterionInstance criterion) {
            criteria.put(name, criterion);
            return this;
        }

        public Builder group(@Nullable String group) {
            return this;
        }

        public Item getResult() {
            return result.getItem();
        }

        public void save(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
            Advancement.Builder advancementBuilder = Advancement.Builder.advancement()
                    .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                    .rewards(AdvancementRewards.Builder.recipe(id))
                    .requirements(net.minecraft.advancements.IRequirementsStrategy.AND);
            criteria.forEach(advancementBuilder::addCriterion);
            ResourceLocation advId = new ResourceLocation(id.getNamespace(), "recipes/" + id.getPath());
            Advancement advancement = advancementBuilder.build(advId);
            PrintingTableRecipe recipe = build();
            consumer.accept(new IFinishedRecipe() {
                @Override
                public ResourceLocation getId() { return id; }
                @Override
                public net.minecraft.item.crafting.IRecipeSerializer<?> getType() { return recipe.getSerializer(); }
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
