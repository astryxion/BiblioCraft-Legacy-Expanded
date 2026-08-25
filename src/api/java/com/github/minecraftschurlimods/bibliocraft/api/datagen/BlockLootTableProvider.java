package com.github.minecraftschurlimods.bibliocraft.api.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.block.Block;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTableManager;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.apache.commons.lang3.Validate;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/** Same semantics as NeoForge WithConditions: holds conditions + carrier for conditional loot table JSON. */
 final class WithConditions<T>  {
    private final List<ICondition> conditions;
    private final T carrier;

    public WithConditions(List<ICondition> conditions, T carrier) {
        this.conditions = conditions;
        this.carrier = carrier;
    }

    public List<ICondition> conditions() { return this.conditions; }
    public T carrier() { return this.carrier; }

    static final String CONDITIONS_KEY = "forge:conditions";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WithConditions other = (WithConditions) o;
        return java.util.Objects.equals(this.conditions, other.conditions) && java.util.Objects.equals(this.carrier, other.carrier);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.conditions, this.carrier);
    }

    @Override
    public String toString() {
        return "WithConditions[" + "conditions=" + this.conditions + ", " + "carrier=" + this.carrier + "]";
    }

}

/**
 * An adaptation of {@link net.minecraft.data.LootTableProvider} and {@link net.minecraft.data.loot.BlockLootTables} that is optimized to Bibliocraft's needs.
 * Among other features, this class eliminates the sub provider abstraction layer and natively supports data load conditions.
 * If you are an addon developer, you should rarely need to interact with this class outside of the two {@code add()} methods.
 */
public abstract class BlockLootTableProvider implements IDataProvider {
    private final DataGenerator output;
    private final Map<ResourceLocation, WithConditionsBuilder<LootTable.Builder>> map = new HashMap<>();

    private static JsonElement encodeLootTable(LootTable table) {
        return LootTableManager.serialize(table);
    }

    private static String getModLoadedModid(net.minecraftforge.common.crafting.conditions.ModLoadedCondition c) {
        try {
            try {
                Method m = c.getClass().getMethod("getModid");
                return (String) m.invoke(c);
            } catch (NoSuchMethodException e) {
                Method m = c.getClass().getMethod("getModId");
                return (String) m.invoke(c);
            }
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not get modid from ModLoadedCondition", e);
        }
    }

    /**
     * @param output The {@link DataGenerator} to use.
     */
    public BlockLootTableProvider(DataGenerator output) {
        this.output = output;
    }

    /**
     * Override this method to add your loot tables.
     */
    protected abstract void generate();

    @Override
    public String getName() {
        return "Loot Tables";
    }

    @Override
    public void run(DirectoryCache cachedOutput) throws java.io.IOException {
        generate();
        com.google.gson.Gson gson = new com.google.gson.GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        Path folder = this.output.getOutputFolder();
        for (Map.Entry<ResourceLocation, WithConditionsBuilder<LootTable.Builder>> entry : map.entrySet()) {
            ResourceLocation location = entry.getKey();
            WithConditions<LootTable> conditional = entry.getValue()
                    .map(builder -> builder.setParamSet(LootParameterSets.BLOCK).build())
                    .build();
            JsonElement json = encodeLootTable(conditional.carrier());
            JsonObject root = json.getAsJsonObject();
            if (!conditional.conditions().isEmpty()) {
                JsonArray conditionsArray = new JsonArray();
                for (ICondition c : conditional.conditions()) {
                    JsonObject condObj = new JsonObject();
                    condObj.addProperty("type", c.getID().toString());
                    if (c instanceof net.minecraftforge.common.crafting.conditions.ModLoadedCondition) {
                        net.minecraftforge.common.crafting.conditions.ModLoadedCondition modLoaded = (net.minecraftforge.common.crafting.conditions.ModLoadedCondition) c;
                        String modid = getModLoadedModid(modLoaded);
                        condObj.addProperty("modid", modid);
                    } else {
                        throw new UnsupportedOperationException("BlockLootTableProvider condition type not supported: " + c.getID());
                    }
                    conditionsArray.add(condObj);
                }
                root.add(WithConditions.CONDITIONS_KEY, conditionsArray);
            }
            Path path = folder.resolve("data").resolve(location.getNamespace()).resolve("loot_tables").resolve(location.getPath() + ".json");
            IDataProvider.save(gson, cachedOutput, root, path);
        }
    }

    /**
     * Adds a loot table for a block.
     *
     * @param block   The block to add the loot table for.
     * @param builder The builder from which to generate the loot table.
     */
    public void add(Block block, WithConditionsBuilder<LootTable.Builder> builder) {
        map.put(block.getLootTable(), builder);
    }

    /**
     * Adds a loot table for a block.
     *
     * @param block   The block to add the loot table for.
     * @param factory A function for the builder from which to generate the loot table.
     */
    public void add(Block block, Function<Block, WithConditionsBuilder<LootTable.Builder>> factory) {
        add(block, factory.apply(block));
    }

    /**
     * @param table The loot table builder to wrap.
     * @return The given loot table, wrapped as a {@link WithConditionsBuilder}.
     */
    public static WithConditionsBuilder<LootTable.Builder> wrapLootTable(LootTable.Builder table) {
        return new WithConditionsBuilder<LootTable.Builder>().withCarrier(table);
    }

    /**
     * Builder for {@link WithConditions} with map operation and no validation on conditions.
     *
     * @param <T> The wrapped builder's type.
     */
    public static class WithConditionsBuilder<T> {
        private final List<ICondition> conditions = new ArrayList<>();
        private T carrier;

        /**
         * Constructs a new {@link WithConditionsBuilder} using the provided existing list of conditions.
         *
         * @param conditions The existing list of conditions to use.
         */
        public WithConditionsBuilder(List<ICondition> conditions) {
            this.conditions.addAll(conditions);
        }

        /**
         * Constructs a new {@link WithConditionsBuilder} using the provided existing list of conditions.
         */
        public WithConditionsBuilder() {
            this(new ArrayList<>());
        }

        /**
         * Transforms this {@code WithConditionsBuilder<T>} to a {@code WithConditionsBuilder<N>} using the provided mapper.
         *
         * @param mapper The function to use for transforming.
         * @param <N>    The new generic type of the {@link WithConditionsBuilder}.
         * @return A transformed variant of this {@link WithConditionsBuilder}.
         */
        public <N> WithConditionsBuilder<N> map(Function<T, N> mapper) {
            return new WithConditionsBuilder<N>(conditions).withCarrier(mapper.apply(carrier));
        }

        /**
         * Adds a condition to the builder.
         *
         * @param conditions The condition to add to the builder.
         * @return This builder, for chaining.
         */
        public WithConditionsBuilder<T> addCondition(Collection<ICondition> conditions) {
            this.conditions.addAll(conditions);
            return this;
        }

        /**
         * Adds one or multiple conditions to the builder.
         *
         * @param conditions The condition(s) to add to the builder.
         * @return This builder, for chaining.
         */
        public WithConditionsBuilder<T> addCondition(ICondition... conditions) {
            this.conditions.addAll(java.util.Arrays.asList(conditions));
            return this;
        }

        /**
         * Sets the carrier of the conditions, i.e. the underlying object the conditions will be applied to.
         *
         * @param carrier The carrier to set.
         * @return This builder, for chaining.
         */
        public WithConditionsBuilder<T> withCarrier(T carrier) {
            this.carrier = carrier;
            return this;
        }

        /**
         * @return A {@link WithConditions} constructed from this builder.
         */
        public WithConditions<T> build() {
            Validate.notNull(carrier, "You need to supply a carrier to create a WithConditions");
            return new WithConditions<>(conditions, carrier);
        }
    }
}
