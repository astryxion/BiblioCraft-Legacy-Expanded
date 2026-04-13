package com.github.minecraftschurlimods.bibliocraft.api.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.RandomSequence;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.apache.commons.lang3.Validate;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/** Same semantics as NeoForge WithConditions: holds conditions + carrier for conditional loot table JSON. */
record WithConditions<T>(List<ICondition> conditions, T carrier) {
    static final String CONDITIONS_KEY = "forge:conditions";
}

/**
 * An adaptation of {@link net.minecraft.data.loot.LootTableProvider} and {@link net.minecraft.data.loot.BlockLootSubProvider} that is optimized to Bibliocraft's needs.
 * Among other features, this class eliminates the sub provider abstraction layer and natively supports data load conditions.
 * If you are an addon developer, you should rarely need to interact with this class outside of the two {@code add()} methods.
 */
public abstract class BlockLootTableProvider implements DataProvider {
    private final CompletableFuture<HolderLookup.Provider> registries;
    private final PackOutput output;
    private final Map<ResourceKey<LootTable>, WithConditionsBuilder<LootTable.Builder>> map = new HashMap<>();

    @SuppressWarnings("unchecked")
    private static JsonElement encodeLootTableWithReflection(RegistryOps ops, LootTable table) {
        Codec<LootTable> codec;
        try {
            Field codecField = LootTable.class.getDeclaredField("CODEC");
            codecField.setAccessible(true);
            codec = (Codec<LootTable>) codecField.get(null);
        } catch (NoSuchFieldException e) {
            try {
                Field mapCodecField = LootTable.class.getDeclaredField("MAP_CODEC");
                mapCodecField.setAccessible(true);
                Object mapCodec = mapCodecField.get(null);
                codec = ((com.mojang.serialization.MapCodec<LootTable>) mapCodec).codec();
            } catch (ReflectiveOperationException e2) {
                throw new IllegalStateException("Could not get LootTable codec", e2);
            }
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not get LootTable codec", e);
        }
        return (JsonElement) codec.encodeStart(ops, table).getOrThrow(false, msg -> {
            throw new IllegalStateException("Loot table encoding failed: " + msg);
        });
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
     * @param output     The {@link PackOutput} to use.
     * @param registries The {@link HolderLookup.Provider} to use.
     */
    public BlockLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.output = output;
        this.registries = registries;
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
    public CompletableFuture<?> run(CachedOutput output) {
        return registries.thenCompose(provider -> run(output, provider));
    }

    private CompletableFuture<?> run(CachedOutput cachedOutput, HolderLookup.Provider provider) {
        generate();
        Map<RandomSupport.Seed128bit, ResourceLocation> seeds = new Object2ObjectOpenHashMap<>();
        return CompletableFuture.allOf(map.entrySet().stream().map(entry -> {
            ResourceLocation location = entry.getKey().location();
            ResourceLocation sequence = seeds.put(RandomSequence.seedForKey(location), location);
            if (sequence != null) {
                Util.logAndPauseIfInIde("Loot table random sequence seed collision on " + sequence + " and " + location);
            }
            WithConditions<LootTable> conditional = entry.getValue()
                    .map(builder -> builder.setRandomSequence(location).setParamSet(LootContextParamSets.BLOCK).build())
                    .build();
            JsonElement json = encodeLootTableWithReflection(RegistryOps.create(JsonOps.INSTANCE, provider), conditional.carrier());
            JsonObject root = json.getAsJsonObject();
            if (!conditional.conditions().isEmpty()) {
                JsonArray conditionsArray = new JsonArray();
                for (ICondition c : conditional.conditions()) {
                    JsonObject condObj = new JsonObject();
                    condObj.addProperty("type", c.getID().toString());
                    if (c instanceof net.minecraftforge.common.crafting.conditions.ModLoadedCondition modLoaded) {
                        String modid = getModLoadedModid(modLoaded);
                        condObj.addProperty("modid", modid);
                    } else {
                        throw new UnsupportedOperationException("BlockLootTableProvider condition type not supported: " + c.getID());
                    }
                    conditionsArray.add(condObj);
                }
                root.add(WithConditions.CONDITIONS_KEY, conditionsArray);
            }
            Path path = this.output.getOutputFolder().resolve("data").resolve(location.getNamespace()).resolve("loot_tables").resolve(location.getPath() + ".json");
            return DataProvider.saveStable(cachedOutput, root, path);
        }).toArray(CompletableFuture[]::new));
    }

    /**
     * Adds a loot table for a block.
     *
     * @param block   The block to add the loot table for.
     * @param builder The builder from which to generate the loot table.
     */
    public void add(Block block, WithConditionsBuilder<LootTable.Builder> builder) {
        map.put(ResourceKey.create(ResourceKey.createRegistryKey(new ResourceLocation("minecraft", "loot_table")), block.getLootTable()), builder);
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
            this.conditions.addAll(List.of(conditions));
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
