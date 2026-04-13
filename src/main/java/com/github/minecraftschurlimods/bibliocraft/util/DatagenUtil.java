package com.github.minecraftschurlimods.bibliocraft.util;

import net.minecraft.Util;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Registry;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Vanilla-only datagen helpers. Block/item model generation is loader-specific and handled by the datagen impl.
 */
@SuppressWarnings("unused")
public final class DatagenUtil {
    public static final Map<DyeColor, ResourceLocation> CANDLE_TEXTURES = Util.make(new HashMap<>(), map -> Arrays.stream(DyeColor.values()).forEach(color -> map.put(color, BCUtil.mcLoc("block/" + color.getName() + "_candle_lit"))));
    public static final Map<DyeColor, ResourceLocation> GLASS_TEXTURES = Util.make(new HashMap<>(), map -> Arrays.stream(DyeColor.values()).forEach(color -> map.put(color, BCUtil.mcLoc("block/" + color.getName() + "_stained_glass"))));
    public static final Map<DyeColor, ResourceLocation> WOOL_TEXTURES = Util.make(new HashMap<>(), map -> Arrays.stream(DyeColor.values()).forEach(color -> map.put(color, BCUtil.mcLoc("block/" + color.getName() + "_wool"))));

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

    public static LootTable.Builder createStandardTable(LootPoolSingletonContainer.Builder<?> builder) {
        return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(builder).when(ExplosionCondition.survivesExplosion()));
    }

    public static LootTable.Builder createDefaultTable(Block block) {
        return createStandardTable(LootItem.lootTableItem(block));
    }

    public static LootTable.Builder createNameableTable(Block block) {
        return createStandardTable(LootItem.lootTableItem(block).apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY)));
    }

    public static LootTable.Builder createFancyArmorStandTable(Block block) {
        return createStandardTable(LootItem.lootTableItem(block)
                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER)))
                .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY)));
    }

    public static LootTable.Builder createGrandfatherClockTable(Block block) {
        return createStandardTable(LootItem.lootTableItem(block)
                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER))));
    }

    public static <T> void addAll(Registry<T> registry, Collection<? extends T> collection, TagsProvider.TagAppender<T> tag) {
        collection.stream().map(e -> ResourceKey.create(registry.key(), registry.getKey(e))).forEach(tag::add);
    }

    public static <T> void addAllOptional(Registry<T> registry, Collection<? extends T> collection, TagsProvider.TagAppender<T> tag) {
        collection.stream().map(registry::getKey).forEach(tag::addOptional);
    }
}
