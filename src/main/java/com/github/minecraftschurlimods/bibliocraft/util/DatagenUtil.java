package com.github.minecraftschurlimods.bibliocraft.util;

import com.github.minecraftschurlimods.bibliocraft.content.fancylight.AbstractFancyLightBlock;
import net.minecraft.util.Util;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.util.registry.Registry;
import net.minecraft.data.TagsProvider;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.tags.ITag;
import net.minecraft.item.DyeColor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.StandaloneLootEntry;
import net.minecraft.loot.functions.CopyName;
import net.minecraft.loot.functions.CopyNbt;
import net.minecraft.loot.conditions.SurvivesExplosion;
import net.minecraft.loot.conditions.BlockStateProperty;
import net.minecraft.loot.ConstantRange;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Utility class holding various helper methods specifically for datagen.
 */
@SuppressWarnings("unused")
public final class DatagenUtil {
    /** 1.16 has no candles; colored wool is the existing-block stand-in for lantern candle faces (same pattern as copper → gold). */
    public static final Map<DyeColor, ResourceLocation> CANDLE_TEXTURES = Util.make(new HashMap<>(), map -> Arrays.stream(DyeColor.values()).forEach(color -> map.put(color, BCUtil.mcLoc("block/" + color.getName() + "_wool"))));
    public static final Map<DyeColor, ResourceLocation> GLASS_TEXTURES = Util.make(new HashMap<>(), map -> Arrays.stream(DyeColor.values()).forEach(color -> map.put(color, BCUtil.mcLoc("block/" + color.getName() + "_stained_glass"))));
    public static final Map<DyeColor, ResourceLocation> WOOL_TEXTURES = Util.make(new HashMap<>(), map -> Arrays.stream(DyeColor.values()).forEach(color -> map.put(color, BCUtil.mcLoc("block/" + color.getName() + "_wool"))));

    /**
     * @param s The string to create a translation for.
     * @return A translated form of the given string.
     */
    public static String toTranslation(String s) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (char c : s.toCharArray()) {
            if (c == '_') {
                builder.append(' ');
                first = true;
            } else if (first) {
                builder.append(Character.toUpperCase(c));
                first = false;
            } else {
                builder.append(Character.toLowerCase(c));
            }
        }
        return builder.toString();
    }

    /**
     * Adds a block with horizontal rotations and a parent model. Enables UV-locking.
     *
     * @param provider Your mod's {@link BlockStateProvider}.
     * @param block    A {@link Supplier} for the {@link Block} to add the model for.
     * @param name     The name of the model file.
     * @param parent   The parent id of the model file.
     * @param texture  The texture to apply.
     */
    public static void horizontalBlockModel(BlockStateProvider provider, Supplier<? extends Block> block, String name, ResourceLocation parent, ResourceLocation texture) {
        horizontalBlockModel(provider, block, $ -> provider.models().withExistingParent(name, parent).texture("texture", texture), true);
    }

    /**
     * Adds a block with horizontal rotations and a parent model.
     *
     * @param provider Your mod's {@link BlockStateProvider}.
     * @param block    A {@link Supplier} for the {@link Block} to add the model for.
     * @param name     The name of the model file.
     * @param parent   The parent id of the model file.
     * @param texture  The texture to apply.
     * @param uvLock   Whether to UV-lock the models or not.
     */
    public static void horizontalBlockModel(BlockStateProvider provider, Supplier<? extends Block> block, String name, ResourceLocation parent, ResourceLocation texture, boolean uvLock) {
        horizontalBlockModel(provider, block, $ -> provider.models().withExistingParent(name, parent).texture("texture", texture), uvLock);
    }

    /**
     * Adds a block with horizontal rotations. Enables UV-locking.
     *
     * @param provider      Your mod's {@link BlockStateProvider}.
     * @param block         A {@link Supplier} for the {@link Block} to add the model for.
     * @param modelFunction A {@link Function} determining which {@link ModelFile} to use for which {@link BlockState}.
     */
    public static void horizontalBlockModel(BlockStateProvider provider, Supplier<? extends Block> block, Function<BlockState, ModelFile> modelFunction) {
        horizontalBlockModel(provider, block, modelFunction, true);
    }

    /**
     * Adds a block with horizontal rotations.
     *
     * @param provider      Your mod's {@link BlockStateProvider}.
     * @param block         A {@link Supplier} for the {@link Block} to add the model for.
     * @param modelFunction A {@link Function} determining which {@link ModelFile} to use for which {@link BlockState}.
     * @param uvLock        Whether to UV-lock the block models or not.
     */
    public static void horizontalBlockModel(BlockStateProvider provider, Supplier<? extends Block> block, Function<BlockState, ModelFile> modelFunction, boolean uvLock) {
        provider.getVariantBuilder(block.get()).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(modelFunction.apply(state))
                .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                .uvLock(uvLock)
                .build());
    }

    /**
     * Adds a double-high block with a bottom and top model file.
     *
     * @param provider Your mod's {@link BlockStateProvider}.
     * @param block    The block to add the model for.
     * @param bottom   The bottom model file.
     * @param top      The top model file.
     * @param uvLock   Whether to UV-lock the models or not.
     */
    public static void doubleHighHorizontalBlockModel(BlockStateProvider provider, Supplier<? extends Block> block, ModelFile bottom, ModelFile top, boolean uvLock) {
        horizontalBlockModel(provider, block, state -> state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? bottom : top, uvLock);
    }

    /**
     * Adds a block with an open/closed property.
     *
     * @param provider Your mod's {@link BlockStateProvider}.
     * @param block    The block to add the model for.
     * @param open     The open model file.
     * @param closed   The closed model file.
     * @param uvLock   Whether to UV-lock the models or not.
     */
    public static void openClosedHorizontalBlockModel(BlockStateProvider provider, Supplier<? extends Block> block, ModelFile open, ModelFile closed, boolean uvLock) {
        horizontalBlockModel(provider, block, state -> state.getValue(BlockStateProperties.OPEN) ? open : closed, uvLock);
    }

    /**
     * Adds a block with a fancy light type property.
     *
     * @param provider Your mod's {@link BlockStateProvider}.
     * @param block    The block to add the model for.
     * @param standing The standing model file.
     * @param hanging  The hanging model file.
     * @param wall     The wall model file.
     * @param uvLock   Whether to UV-lock the models or not.
     */
    public static void fancyLightBlockModel(BlockStateProvider provider, Supplier<? extends Block> block, ModelFile standing, ModelFile hanging, ModelFile wall, boolean uvLock) {
        horizontalBlockModel(provider, block, state -> {
            switch (state.getValue(AbstractFancyLightBlock.TYPE)) {
                case STANDING:
                    return standing;
                case HANGING:
                    return hanging;
                case WALL:
                    return wall;
            }
            return standing;
        }, uvLock);
    }

    /**
     * Adds a fancy lamp model.
     *
     * @param provider     Your mod's {@link BlockStateProvider}.
     * @param block        The block to add the model for.
     * @param folderPrefix The folder prefix of the model.
     * @param material     The material of the lamp. E.g. gold, iron.
     * @param texture      The glass texture to use.
     */
    public static void fancyLampModel(BlockStateProvider provider, Supplier<? extends Block> block, String folderPrefix, String material, ResourceLocation texture) {
        fancyLightBlockModel(provider, block,
                provider.models().withExistingParent(folderPrefix + "fancy_" + material + "_lamp_standing", BCUtil.bcLoc("block/template/fancy_lamp/standing_" + material)).texture("color", texture),
                provider.models().withExistingParent(folderPrefix + "fancy_" + material + "_lamp_hanging", BCUtil.bcLoc("block/template/fancy_lamp/hanging_" + material)).texture("color", texture),
                provider.models().withExistingParent(folderPrefix + "fancy_" + material + "_lamp_wall", BCUtil.bcLoc("block/template/fancy_lamp/wall_" + material)).texture("color", texture),
                false);
    }

    /**
     * Adds a fancy lantern model.
     *
     * @param provider     Your mod's {@link BlockStateProvider}.
     * @param block        The block to add the model for.
     * @param folderPrefix The folder prefix of the model.
     * @param material     The material of the lantern. E.g. gold, iron.
     * @param texture      The candle texture to use.
     */
    public static void fancyLanternModel(BlockStateProvider provider, Supplier<? extends Block> block, String folderPrefix, String material, ResourceLocation texture) {
        fancyLightBlockModel(provider, block,
                provider.models().withExistingParent(folderPrefix + "fancy_" + material + "_lantern_standing", BCUtil.bcLoc("block/template/fancy_lantern/standing_" + material)).texture("color", texture),
                provider.models().withExistingParent(folderPrefix + "fancy_" + material + "_lantern_hanging", BCUtil.bcLoc("block/template/fancy_lantern/hanging_" + material)).texture("color", texture),
                provider.models().withExistingParent(folderPrefix + "fancy_" + material + "_lantern_wall", BCUtil.bcLoc("block/template/fancy_lantern/wall_" + material)).texture("color", texture),
                false);
    }

    /**
     * Creates a standard loot table with the given entry builder.
     *
     * @param builder The entry builder to use.
     * @return A standard loot table with the given entry builder.
     */
    public static LootTable.Builder createStandardTable(StandaloneLootEntry.Builder<?> builder) {
        return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(builder).when(SurvivesExplosion.survivesExplosion()));
    }

    /**
     * Creates a standard loot table that drops the given block.
     *
     * @param block The block to drop.
     * @return A standard loot table that drops the given block.
     */
    public static LootTable.Builder createDefaultTable(Block block) {
        return createStandardTable(ItemLootEntry.lootTableItem(block));
    }

    /**
     * Creates a standard loot table that drops the given nameable block.
     *
     * @param block The block to drop.
     * @return A standard loot table that drops the given nameable block.
     */
    public static LootTable.Builder createNameableTable(Block block) {
        return createStandardTable(ItemLootEntry.lootTableItem(block).apply(CopyName.copyName(CopyName.Source.BLOCK_ENTITY)));
    }

    /**
     * Creates a standard loot table that drops the given block with full block entity NBT copied to the item.
     *
     * @param block The block to drop.
     * @return A standard loot table that drops the block with BlockEntityTag.
     */
    public static LootTable.Builder createCopyNbtBlockEntityTable(Block block) {
        // 1.16 NBT paths cannot be "." (that is 1.20+). Copy the BE keys this block actually stores.
        return createStandardTable(ItemLootEntry.lootTableItem(block).apply(CopyNbt.copyData(CopyNbt.Source.BLOCK_ENTITY)
                .copy("color", "BlockEntityTag.color")
                .copy("items", "BlockEntityTag.items")
                .copy("Lock", "BlockEntityTag.Lock")
                .copy("color.rgb", "rgb")
                .copy("color.show_in_tooltip", "show_in_tooltip")));
    }

    /**
     * Creates a loot table for the clipboard block: copies block entity "clipboard_content" to item "ClipboardContent"
     * so the dropped item has clipboard data at the key used by {@link com.github.minecraftschurlimods.bibliocraft.content.clipboard.ClipboardContent#getFromStack}.
     */
    public static LootTable.Builder createClipboardLootTable(Block block) {
        return createStandardTable(ItemLootEntry.lootTableItem(block).apply(CopyNbt.copyData(CopyNbt.Source.BLOCK_ENTITY).copy("clipboard_content", "ClipboardContent")));
    }

    /**
     * Creates a loot table for a fancy armor stand.
     *
     * @param block The block to create the loot table for.
     * @return A loot table for a fancy armor stand, dropping the given block.
     */
    public static LootTable.Builder createFancyArmorStandTable(Block block) {
        return createStandardTable(ItemLootEntry.lootTableItem(block)
                .when(BlockStateProperty.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER)))
                .apply(CopyName.copyName(CopyName.Source.BLOCK_ENTITY)));
    }

    /**
     * Creates a loot table for a grandfather clock.
     *
     * @param block The block to create the loot table for.
     * @return A loot table for a grandfather clock, dropping the given block.
     */
    public static LootTable.Builder createGrandfatherClockTable(Block block) {
        return createStandardTable(ItemLootEntry.lootTableItem(block)
                .when(BlockStateProperty.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER))));
    }

    /**
     * Adds all elements of the given collection to the {@link TagsProvider.Builder}
     *
     * @param registry   The {@link Registry} associated with the collection elements.
     * @param collection The collection containing the elements to add.
     * @param tag        The given {@link TagsProvider.Builder}, obtainable through {@link TagsProvider#tag(ITag.INamedTag)}.
     * @param <T>        The type of the collection elements.
     */
    @SuppressWarnings("DataFlowIssue")
    public static <T> void addAll(Registry<T> registry, Collection<? extends T> collection, TagsProvider.Builder<T> tag) {
        collection.forEach(tag::add);
    }

    /**
     * Adds all elements of the given collection to the {@link TagsProvider.Builder}
     *
     * @param registry   The {@link Registry} associated with the collection elements.
     * @param collection The collection containing the elements to add.
     * @param tag        The given {@link TagsProvider.Builder}, obtainable through {@link TagsProvider#tag(ITag.INamedTag)}.
     * @param <T>        The type of the collection elements.
     */
    @SuppressWarnings("DataFlowIssue")
    public static <T> void addAllOptional(Registry<T> registry, Collection<? extends T> collection, TagsProvider.Builder<T> tag) {
        collection.stream().map(registry::getKey).forEach(tag::addOptional);
    }
}
