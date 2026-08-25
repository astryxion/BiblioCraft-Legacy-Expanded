package com.github.minecraftschurlimods.bibliocraft.content.printingtable;

import com.github.minecraftschurlimods.bibliocraft.content.bigbook.BigBookContent;
import com.github.minecraftschurlimods.bibliocraft.content.bigbook.WrittenBigBookContent;
import com.github.minecraftschurlimods.bibliocraft.content.clipboard.ClipboardContent;
import com.github.minecraftschurlimods.bibliocraft.init.BCRecipes;
import com.github.minecraftschurlimods.bibliocraft.util.CodecUtil;
import com.github.minecraftschurlimods.bibliocraft.util.StringRepresentableEnum;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.NonNullList;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.world.World;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PrintingTableMergingRecipe extends PrintingTableRecipe {
    /** Writable book stores only "pages" (ListNBT). This type encodes to JSON object {"pages": [...]} for merging. 1.20.1: withAlternative -> either; object form via MapCodec. */
    private static final Codec<WritableBookPages> PAGES_CODEC = Codec.either(
            Codec.STRING.listOf().xmap(WritableBookPages::new, WritableBookPages::pages),
            Codec.STRING.listOf().optionalFieldOf("pages", java.util.Collections.emptyList()).xmap(WritableBookPages::new, WritableBookPages::pages).codec())
            .xmap(e -> e.map(l -> l, r -> r), Either::right);
    /** Written book tag (title, author, pages, generation, resolved). */
    private static final Codec<WrittenBookTag> WRITTEN_BOOK_CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.optionalFieldOf("title", "").forGetter(WrittenBookTag::title),
            Codec.STRING.optionalFieldOf("author", "").forGetter(WrittenBookTag::author),
            Codec.STRING.listOf().optionalFieldOf("pages", java.util.Collections.emptyList()).forGetter(WrittenBookTag::pages),
            Codec.INT.optionalFieldOf("generation", 0).forGetter(WrittenBookTag::generation),
            Codec.BOOL.optionalFieldOf("resolved", true).forGetter(WrittenBookTag::resolved)
    ).apply(inst, WrittenBookTag::new));

    private static final Map<String, Codec<?>> NBT_KEY_CODECS = (new java.util.function.Supplier<java.util.Map>() { public java.util.Map get() { java.util.Map m = new java.util.LinkedHashMap(); m.put(ClipboardContent.NBT_KEY, ClipboardContent.CODEC); m.put(BigBookContent.NBT_KEY, BigBookContent.CODEC); m.put(WrittenBigBookContent.NBT_KEY, WrittenBigBookContent.CODEC); m.put("pages", PAGES_CODEC); m.put("WrittenBook", WRITTEN_BOOK_CODEC); return java.util.Collections.unmodifiableMap(m); } }).get();

    private final Map<String, Map<String, MergeMethod>> mergers;
    private final Ingredient ingredient;

    public PrintingTableMergingRecipe(ResourceLocation id, Map<String, Map<String, MergeMethod>> mergers, Ingredient ingredient, ItemStack result, int duration) {
        super(id, result, duration);
        this.mergers = mergers;
        this.ingredient = ingredient;
    }

    @Override
    public PrintingTableMode getMode() {
        return PrintingTableMode.MERGE;
    }

    @Override
    public boolean matches(PrintingTableRecipeInput input, World level) {
        ItemStack right = input.right();
        if (!ingredient.test(right)) return false;
        List<ItemStack> left = new ArrayList<>(input.left());
        left.removeIf(ItemStack::isEmpty);
        if (left.size() < 2) return false;
        for (ItemStack stack : left) {
            if (!ItemStack.isSame(stack, result)) return false;
            CompoundNBT tag = stack.getTag();
            if (tag == null) return false;
            for (String nbtKey : mergers.keySet()) {
                if ("WrittenBook".equals(nbtKey)) {
                    if (!tag.contains("pages")) return false;
                } else if (!tag.contains(nbtKey)) return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(PrintingTableRecipeInput input) {
        ItemStack resultStack = input.right().copy();
        CompoundNBT resultTag = resultStack.getOrCreateTag();
        List<Map<String, JsonObject>> jsonsByKey = new ArrayList<>();
        for (ItemStack stack : input.left()) {
            if (stack.isEmpty()) continue;
            Map<String, JsonObject> keyToJson = new HashMap<>();
            CompoundNBT tag = stack.getTag();
            if (tag == null) continue;
            for (String nbtKey : mergers.keySet()) {
                Codec<?> codec = NBT_KEY_CODECS.get(nbtKey);
                if (codec == null) continue;
                INBT nbt = "WrittenBook".equals(nbtKey) ? tag : tag.get(nbtKey);
                if (nbt == null && !"WrittenBook".equals(nbtKey)) continue;
                if (nbt == null && "WrittenBook".equals(nbtKey) && tag.isEmpty()) continue;
                Object value = "WrittenBook".equals(nbtKey) ? CodecUtil.decodeNbt(codec, tag) : CodecUtil.decodeNbt(codec, nbt);
                JsonElement el = CodecUtil.encodeJson((Codec<Object>) codec, value);
                if (el.isJsonObject()) keyToJson.put(nbtKey, el.getAsJsonObject());
            }
            jsonsByKey.add(keyToJson);
        }
        for (String nbtKey : mergers.keySet()) {
            Codec<?> codec = NBT_KEY_CODECS.get(nbtKey);
            if (codec == null) continue;
            List<JsonObject> jsons = new ArrayList<>();
            for (Map<String, JsonObject> m : jsonsByKey) {
                JsonObject jo = m.get(nbtKey);
                if (jo != null) jsons.add(jo);
            }
            if (jsons.isEmpty()) continue;
            JsonObject merged = new JsonObject();
            for (String field : mergers.get(nbtKey).keySet()) {
                MergeMethod method = getMerger(nbtKey, field, jsons);
                JsonElement mergedValue;
                switch (method) {
                    case FIRST:
                        mergedValue = jsons.get(0).get(field);
                        break;
                    case LAST:
                        mergedValue = jsons.get(jsons.size() - 1).get(field);
                        break;
                    case MIN:
                        mergedValue = new JsonPrimitive(jsons.stream().map(j -> j.get(field)).filter(Objects::nonNull).filter(JsonElement::isJsonPrimitive).mapToInt(JsonElement::getAsInt).min().orElseThrow(java.util.NoSuchElementException::new));
                        break;
                    case MAX:
                        mergedValue = new JsonPrimitive(jsons.stream().map(j -> j.get(field)).filter(Objects::nonNull).filter(JsonElement::isJsonPrimitive).mapToInt(JsonElement::getAsInt).max().orElseThrow(java.util.NoSuchElementException::new));
                        break;
                    case APPEND:
                    default: {
                        JsonArray arr = new JsonArray();
                        jsons.stream().map(j -> j.get(field)).filter(Objects::nonNull).filter(e -> e.isJsonArray()).map(JsonElement::getAsJsonArray).flatMap(a -> {
                            java.util.List<JsonElement> list = new java.util.ArrayList<>();
                            for (int ai = 0; ai < a.size(); ai++) list.add(a.get(ai));
                            return list.stream();
                        }).forEach(arr::add);
                        mergedValue = arr;
                        break;
                    }
                }
                merged.add(field, mergedValue);
            }
            Object decoded = CodecUtil.decodeJson(codec, merged);
            INBT encoded = CodecUtil.encodeNbt((Codec<Object>) codec, decoded);
            if ("WrittenBook".equals(nbtKey) && encoded instanceof CompoundNBT) {
                CompoundNBT ct = (CompoundNBT) encoded;
                resultTag.merge(ct);
            } else {
                resultTag.put(nbtKey, encoded);
            }
        }
        return resultStack;
    }

    private static final class WritableBookPages  {
    private final List<String> pages;

    public WritableBookPages(List<String> pages) {
        this.pages = pages;
    }

    public List<String> pages() { return this.pages; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WritableBookPages other = (WritableBookPages) o;
        return java.util.Objects.equals(this.pages, other.pages);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.pages);
    }

    @Override
    public String toString() {
        return "WritableBookPages[" + "pages=" + this.pages + "]";
    }

}
    private static final class WrittenBookTag  {
    private final String title;
    private final String author;
    private final List<String> pages;
    private final int generation;
    private final boolean resolved;

    public WrittenBookTag(String title, String author, List<String> pages, int generation, boolean resolved) {
        this.title = title;
        this.author = author;
        this.pages = pages;
        this.generation = generation;
        this.resolved = resolved;
    }

    public String title() { return this.title; }
    public String author() { return this.author; }
    public List<String> pages() { return this.pages; }
    public int generation() { return this.generation; }
    public boolean resolved() { return this.resolved; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WrittenBookTag other = (WrittenBookTag) o;
        return java.util.Objects.equals(this.title, other.title) && java.util.Objects.equals(this.author, other.author) && java.util.Objects.equals(this.pages, other.pages) && this.generation == other.generation && this.resolved == other.resolved;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.title, this.author, this.pages, this.generation, this.resolved);
    }

    @Override
    public String toString() {
        return "WrittenBookTag[" + "title=" + this.title + ", " + "author=" + this.author + ", " + "pages=" + this.pages + ", " + "generation=" + this.generation + ", " + "resolved=" + this.resolved + "]";
    }

}

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return BCRecipes.PRINTING_TABLE_MERGING.get();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(PrintingTableRecipeInput input) {
        NonNullList<ItemStack> remainingItems = super.getRemainingItems(input);
        for (int i = 0; i < 9; i++) {
            ItemStack stack = input.left().get(i);
            if (!stack.isEmpty()) remainingItems.set(i, stack.copy());
        }
        return remainingItems;
    }

    @Override
    public Pair<List<Ingredient>, Ingredient> getDisplayIngredients() {
        return Pair.of(java.util.Collections.unmodifiableList(java.util.Arrays.asList(Ingredient.of(result.copy()), Ingredient.of(result.copy()))), ingredient);
    }

    private MergeMethod getMerger(String nbtKey, String field, List<JsonObject> jsons) {
        MergeMethod merger = mergers.get(nbtKey).get(field);
        if (merger == null)
            return jsons.stream().allMatch(j -> j.has(field) && j.get(field).isJsonArray()) ? MergeMethod.APPEND : MergeMethod.FIRST;
        if (merger == MergeMethod.MIN || merger == MergeMethod.MAX)
            return jsons.stream().allMatch(j -> isJsonNumber(j.get(field))) ? merger : MergeMethod.FIRST;
        return merger;
    }

    private static boolean isJsonNumber(JsonElement json) {
        return json != null && json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber();
    }

    public enum MergeMethod implements StringRepresentableEnum {
        FIRST, LAST, MIN, MAX, APPEND;
        public static final Codec<MergeMethod> CODEC = CodecUtil.enumCodec(MergeMethod::values);
    }

    /** Maps 1.21 component IDs to 1.20.1 NBT keys used in NBT_KEY_CODECS / mergers. */
    private static String componentIdToNbtKey(String componentId) {
        switch (componentId) {
case "minecraft:written_book_content": return "WrittenBook";
case "minecraft:writable_book_content": return "pages";
case "bibliocraft:written_big_book_content": return WrittenBigBookContent.NBT_KEY;
case "bibliocraft:big_book_content": return BigBookContent.NBT_KEY;
case "bibliocraft:clipboard_content": return ClipboardContent.NBT_KEY;
default: return componentId;
}
    }

    public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<net.minecraft.item.crafting.IRecipeSerializer<?>> implements IRecipeSerializer<PrintingTableMergingRecipe> {
        @Override
        public PrintingTableMergingRecipe fromJson(ResourceLocation id, JsonObject json) {
            Map<String, Map<String, MergeMethod>> mergers = new HashMap<>();
            JsonObject mergersObj = json.has("mergers") && json.get("mergers").isJsonObject() ? json.getAsJsonObject("mergers") : null;
            if (mergersObj != null) {
                for (java.util.Map.Entry<String, com.google.gson.JsonElement> nbtEntry : mergersObj.entrySet()) {
                    String nbtKey = nbtEntry.getKey();
                    Map<String, MergeMethod> fieldMap = new HashMap<>();
                    JsonObject fieldObj = mergersObj.getAsJsonObject(nbtKey);
                    for (java.util.Map.Entry<String, com.google.gson.JsonElement> fieldEntry : fieldObj.entrySet()) {
                        String field = fieldEntry.getKey();
                        fieldMap.put(field, MergeMethod.valueOf(fieldObj.get(field).getAsString().toUpperCase(java.util.Locale.ROOT)));
                    }
                    mergers.put(nbtKey, fieldMap);
                }
            } else if (json.has("component_mergers") && json.get("component_mergers").isJsonObject()) {
                JsonObject componentMergers = json.getAsJsonObject("component_mergers");
                for (java.util.Map.Entry<String, com.google.gson.JsonElement> componentEntry : componentMergers.entrySet()) {
                    String componentId = componentEntry.getKey();
                    String nbtKey = componentIdToNbtKey(componentId);
                    Map<String, MergeMethod> fieldMap = new HashMap<>();
                    JsonObject fieldObj = componentMergers.getAsJsonObject(componentId);
                    for (java.util.Map.Entry<String, com.google.gson.JsonElement> fieldEntry : fieldObj.entrySet()) {
                        String field = fieldEntry.getKey();
                        fieldMap.put(field, MergeMethod.valueOf(fieldObj.get(field).getAsString().toUpperCase(java.util.Locale.ROOT)));
                    }
                    mergers.put(nbtKey, fieldMap);
                }
            }
            Ingredient ingredient = Ingredient.fromJson(json.getAsJsonObject("ingredient"));
            ItemStack result = net.minecraftforge.common.crafting.CraftingHelper.getItemStack(json.getAsJsonObject("result"), true);
            int duration = json.getAsJsonPrimitive("duration").getAsInt();
            return new PrintingTableMergingRecipe(id, mergers, ingredient, result, duration);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, PrintingTableMergingRecipe recipe) {
            buffer.writeVarInt(recipe.mergers.size());
            for (java.util.Map.Entry<String, Map<String, MergeMethod>> e : recipe.mergers.entrySet()) {
                buffer.writeUtf((String) e.getKey());
                buffer.writeVarInt(e.getValue().size());
                for (java.util.Map.Entry<String, MergeMethod> f : e.getValue().entrySet()) {
                    buffer.writeUtf((String) f.getKey());
                    buffer.writeEnum(f.getValue());
                }
            }
            recipe.ingredient.toNetwork(buffer);
            buffer.writeItem(recipe.result);
            buffer.writeVarInt(recipe.duration);
        }

        @Override
        public PrintingTableMergingRecipe fromNetwork(ResourceLocation id, PacketBuffer buffer) {
            int n = buffer.readVarInt();
            Map<String, Map<String, MergeMethod>> mergers = new HashMap<>();
            for (int i = 0; i < n; i++) {
                String nbtKey = buffer.readUtf();
                int m = buffer.readVarInt();
                Map<String, MergeMethod> fieldMap = new HashMap<>();
                for (int j = 0; j < m; j++) {
                    fieldMap.put(buffer.readUtf(), buffer.readEnum(MergeMethod.class));
                }
                mergers.put(nbtKey, fieldMap);
            }
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            ItemStack result = buffer.readItem();
            int duration = buffer.readVarInt();
            return new PrintingTableMergingRecipe(id, mergers, ingredient, result, duration);
        }

        public void toJson(JsonObject json, PrintingTableRecipe recipe) {
            PrintingTableMergingRecipe r = (PrintingTableMergingRecipe) recipe;
            JsonObject mergersObj = new JsonObject();
            for (java.util.Map.Entry<String, Map<String, MergeMethod>> e : r.mergers.entrySet()) {
                JsonObject fieldObj = new JsonObject();
                for (java.util.Map.Entry<String, MergeMethod> f : e.getValue().entrySet()) {
                    fieldObj.addProperty(f.getKey(), f.getValue().name());
                }
                mergersObj.add(e.getKey(), fieldObj);
            }
            json.add("mergers", mergersObj);
            json.add("ingredient", r.ingredient.toJson());
            JsonObject resultObj = new JsonObject();
            resultObj.addProperty("item", net.minecraft.util.registry.Registry.ITEM.getKey(r.result.getItem()).toString());
            if (r.result.getCount() != 1) resultObj.addProperty("count", r.result.getCount());
            if (r.result.hasTag()) resultObj.addProperty("nbt", r.result.getTag().toString());
            json.add("result", resultObj);
            json.addProperty("duration", r.duration);
        }
    }

    @Override
    public void toRecipeJson(JsonObject json) {
        ((Serializer) getSerializer()).toJson(json, this);
    }

    public static class Builder extends PrintingTableRecipe.Builder {
        private final Map<String, Map<String, MergeMethod>> mergers = new HashMap<>();
        private final Ingredient ingredient;

        public Builder(Ingredient ingredient, ItemStack result, int duration) {
            super(result, duration);
            this.ingredient = ingredient;
        }

        public Builder addMerger(String nbtKey, String field, MergeMethod method) {
            mergers.computeIfAbsent(nbtKey, k -> new HashMap<>()).put(field, method);
            return this;
        }

        @Override
        public PrintingTableRecipe build() {
            return new PrintingTableMergingRecipe(new ResourceLocation(com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi.MOD_ID, "printing_table_merging"), mergers, ingredient, result, duration);
        }
    }
}
