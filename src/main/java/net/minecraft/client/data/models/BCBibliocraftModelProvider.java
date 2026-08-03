package net.minecraft.client.data.models;

import net.minecraft.core.Holder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public abstract class BCBibliocraftModelProvider extends ModelProvider {
    private final PackOutput output;
    private final String modId;

    protected BCBibliocraftModelProvider(PackOutput output, String modId) {
        super(output);
        this.output = output;
        this.modId = modId;
    }

    protected String modId() {
        return modId;
    }

    protected abstract void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels);

    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.empty();
    }

    protected Stream<? extends Holder<Item>> getKnownItems() {
        return Stream.empty();
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        ItemInfoCollector itemInfoCollector = new ItemInfoCollector();
        BlockStateGeneratorCollector blockStateCollector = new BlockStateGeneratorCollector();
        SimpleModelCollector modelCollector = new SimpleModelCollector();
        BlockModelGenerators blockModels = new BlockModelGenerators(blockStateCollector, itemInfoCollector, modelCollector);
        ItemModelGenerators itemModels = new ItemModelGenerators(itemInfoCollector, modelCollector);
        registerModels(blockModels, itemModels);
        blockStateCollector.validate();
        itemInfoCollector.finalizeAndValidate();
        PackOutput.PathProvider blockStatePathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        PackOutput.PathProvider itemInfoPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items");
        PackOutput.PathProvider modelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
        return CompletableFuture.allOf(
                blockStateCollector.save(cachedOutput, blockStatePathProvider),
                modelCollector.save(cachedOutput, modelPathProvider),
                itemInfoCollector.save(cachedOutput, itemInfoPathProvider)
        );
    }
}