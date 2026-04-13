package com.github.minecraftschurlimods.bibliocraft.api.datagen;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Loot table provider for blocks. Supports optional data conditions (e.g. mod loaded);
 * on Fabric, conditions are not serialized but the same API is preserved.
 */
public abstract class BlockLootTableProvider implements DataProvider {
    private final CompletableFuture<HolderLookup.Provider> registries;
    private final PackOutput.PathProvider pathProvider;
    private final Map<ResourceKey<LootTable>, WithConditionsBuilder<LootTable.Builder>> map = new HashMap<>();

    public BlockLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "loot_tables");
        this.registries = registries;
    }

    protected abstract void generate();

    @Override
    public String getName() {
        return "Loot Tables";
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return registries.thenCompose(provider -> run(output, provider));
    }

    private CompletableFuture<?> run(CachedOutput output, HolderLookup.Provider provider) {
        generate();
        Map<RandomSupport.Seed128bit, ResourceLocation> seeds = new Object2ObjectOpenHashMap<>();
        return CompletableFuture.allOf(map.entrySet().stream().map(entry -> {
            ResourceLocation location = entry.getKey().location();
            ResourceLocation sequence = seeds.put(RandomSequence.seedForKey(location), location);
            if (sequence != null) {
                Util.logAndPauseIfInIde("Loot table random sequence seed collision on " + sequence + " and " + location);
            }
            LootTable table = entry.getValue()
                    .map(builder -> builder.setRandomSequence(location).setParamSet(LootContextParamSets.BLOCK).build())
                    .getCarrier();
            return DataProvider.saveStable(output, provider, LootTable.DIRECT_CODEC, table, pathProvider.json(location));
        }).toArray(CompletableFuture[]::new));
    }

    public void add(Block block, WithConditionsBuilder<LootTable.Builder> builder) {
        map.put(block.getLootTable(), builder);
    }

    public void add(Block block, Function<Block, WithConditionsBuilder<LootTable.Builder>> factory) {
        add(block, factory.apply(block));
    }

    public static WithConditionsBuilder<LootTable.Builder> wrapLootTable(LootTable.Builder table) {
        return new WithConditionsBuilder<LootTable.Builder>().withCarrier(table);
    }

    /**
     * Data condition for optional serialization (e.g. mod loaded). On Fabric, not serialized.
     */
    public interface DataCondition {
    }

    /**
     * Builder for a carrier (e.g. LootTable.Builder) with optional conditions.
     */
    public static class WithConditionsBuilder<T> {
        private final List<DataCondition> conditions = new ArrayList<>();
        private T carrier;

        public WithConditionsBuilder() {
        }

        public WithConditionsBuilder(List<DataCondition> conditions) {
            this.conditions.addAll(conditions);
        }

        public <N> WithConditionsBuilder<N> map(Function<T, N> mapper) {
            return new WithConditionsBuilder<N>(new ArrayList<>(conditions)).withCarrier(mapper.apply(carrier));
        }

        public WithConditionsBuilder<T> addCondition(Collection<DataCondition> conditions) {
            this.conditions.addAll(conditions);
            return this;
        }

        public WithConditionsBuilder<T> addCondition(DataCondition... conditions) {
            this.conditions.addAll(List.of(conditions));
            return this;
        }

        public WithConditionsBuilder<T> withCarrier(T carrier) {
            this.carrier = carrier;
            return this;
        }

        /** Returns the carrier. After map(builder -> builder.build()) the carrier is the LootTable. */
        public T getCarrier() {
            if (carrier == null) {
                throw new IllegalStateException("Carrier must be set via withCarrier()");
            }
            return carrier;
        }
    }
}
