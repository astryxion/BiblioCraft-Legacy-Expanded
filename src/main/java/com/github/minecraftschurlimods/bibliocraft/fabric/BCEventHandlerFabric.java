package com.github.minecraftschurlimods.bibliocraft.fabric;

import com.github.minecraftschurlimods.bibliocraft.api.lockandkey.RegisterLockAndKeyBehaviorEvent;
import com.github.minecraftschurlimods.bibliocraft.api.woodtype.RegisterBibliocraftWoodTypesEvent;
import com.github.minecraftschurlimods.bibliocraft.apiimpl.LockAndKeyBehaviorsImpl;
import com.github.minecraftschurlimods.bibliocraft.content.fancycrafter.FancyCrafterBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.content.typewriter.TypewriterBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.init.BCBlocks;
import com.github.minecraftschurlimods.bibliocraft.init.BCEntities;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.util.lectern.LecternUtil;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.FilteringStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.ArrayList;
import java.util.List;

/**
 * Fabric replacement for BCEventHandler. Registers events and entity attributes.
 */
public final class BCEventHandlerFabric {

    public static void init() {
        registerCapabilities();
        ((LockAndKeyBehaviorsImpl) com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi.getLockAndKeyBehaviors()).registerForFabric();
        registerEntityAttributes();
        registerUseBlock();
        BibliocraftNetworking.register();
    }

    private static void registerEntityAttributes() {
        FabricDefaultAttributeRegistry.register(BCEntities.FANCY_ARMOR_STAND.get(), LivingEntity.createLivingAttributes().build());
    }

    private static void registerCapabilities() {
        registerBcItemStorage(BCBlockEntities.BOOKCASE.get());
        registerBcItemStorage(BCBlockEntities.COOKIE_JAR.get());
        registerBcItemStorage(BCBlockEntities.DINNER_PLATE.get());
        registerBcItemStorage(BCBlockEntities.DISC_RACK.get());
        registerBcItemStorage(BCBlockEntities.DISPLAY_CASE.get());
        registerBcItemStorage(BCBlockEntities.FANCY_ARMOR_STAND.get());
        registerBcItemStorage(BCBlockEntities.LABEL.get());
        registerBcItemStorage(BCBlockEntities.POTION_SHELF.get());
        registerBcItemStorage(BCBlockEntities.SHELF.get());
        registerBcItemStorage(BCBlockEntities.SWORD_PEDESTAL.get());
        registerBcItemStorage(BCBlockEntities.TABLE.get());
        registerBcItemStorage(BCBlockEntities.TOOL_RACK.get());

        ItemStorage.SIDED.registerForBlockEntity((FancyCrafterBlockEntity be, Direction side) -> {
            if (side == null) return null;
            InventoryStorage storage = InventoryStorage.of(be, side);
            if (side == Direction.DOWN) {
                return Storage.empty();
            }
            List<Storage<ItemVariant>> slots = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                slots.add(FilteringStorage.insertOnlyOf(storage.getSlot(i)));
            }
            for (int i = 10; i < storage.getSlotCount(); i++) {
                slots.add(FilteringStorage.insertOnlyOf(storage.getSlot(i)));
            }
            return new CombinedStorage<>(slots);
        }, BCBlockEntities.FANCY_CRAFTER.get());

        ItemStorage.SIDED.registerForBlockEntity((PrintingTableBlockEntity be, Direction side) -> {
            if (side == null) return null;
            InventoryStorage storage = InventoryStorage.of(be, side);
            if (side == Direction.DOWN) {
                return FilteringStorage.extractOnlyOf(storage.getSlot(10));
            }
            return insertOnlySlots(storage, 0, 10);
        }, BCBlockEntities.PRINTING_TABLE.get());

        ItemStorage.SIDED.registerForBlockEntity((TypewriterBlockEntity be, Direction side) -> {
            if (side == null) return null;
            InventoryStorage storage = InventoryStorage.of(be, side);
            if (side == Direction.DOWN) {
                return FilteringStorage.extractOnlyOf(storage.getSlot(TypewriterBlockEntity.OUTPUT));
            }
            return FilteringStorage.insertOnlyOf(storage.getSlot(TypewriterBlockEntity.INPUT));
        }, BCBlockEntities.TYPEWRITER.get());
    }

    private static <T extends BCBlockEntity> void registerBcItemStorage(BlockEntityType<T> type) {
        ItemStorage.SIDED.registerForBlockEntity((be, side) -> InventoryStorage.of(be, side), type);
    }

    private static Storage<ItemVariant> insertOnlySlots(InventoryStorage storage, int fromInclusive, int toExclusive) {
        List<Storage<ItemVariant>> slots = new ArrayList<>();
        for (int i = fromInclusive; i < toExclusive; i++) {
            slots.add(FilteringStorage.insertOnlyOf(storage.getSlot(i)));
        }
        return new CombinedStorage<>(slots);
    }

    /**
     * Registers default lock-and-key behaviors (vanilla + BC). Called from LockAndKeyBehaviorsImpl.registerForFabric().
     */
    public static void registerLockAndKeyBehaviors(RegisterLockAndKeyBehaviorEvent event) {
        event.register(net.minecraft.world.level.block.entity.BaseContainerBlockEntity.class, be -> be.lockKey, (be, lock) -> be.lockKey = lock, net.minecraft.world.level.block.entity.BaseContainerBlockEntity::getDisplayName);
        event.register(net.minecraft.world.level.block.entity.BeaconBlockEntity.class, be -> be.lockKey, (be, lock) -> be.lockKey = lock, net.minecraft.world.level.block.entity.BeaconBlockEntity::getDisplayName);
        event.register(BCBlockEntity.class, BCBlockEntity::getLockKey, BCBlockEntity::setLockKey, BCUtil::getNameForBE);
    }

    /**
     * Registers vanilla wood types. Called from BibliocraftWoodTypeRegistryImpl.registerForFabric().
     */
    public static void registerBibliocraftWoodTypes(RegisterBibliocraftWoodTypesEvent event) {
        registerVanilla(event, WoodType.OAK,      Blocks.OAK_PLANKS,      BlockFamilies.OAK_PLANKS);
        registerVanilla(event, WoodType.SPRUCE,   Blocks.SPRUCE_PLANKS,   BlockFamilies.SPRUCE_PLANKS);
        registerVanilla(event, WoodType.BIRCH,    Blocks.BIRCH_PLANKS,   BlockFamilies.BIRCH_PLANKS);
        registerVanilla(event, WoodType.JUNGLE,   Blocks.JUNGLE_PLANKS,  BlockFamilies.JUNGLE_PLANKS);
        registerVanilla(event, WoodType.ACACIA,   Blocks.ACACIA_PLANKS,  BlockFamilies.ACACIA_PLANKS);
        registerVanilla(event, WoodType.DARK_OAK, Blocks.DARK_OAK_PLANKS, BlockFamilies.DARK_OAK_PLANKS);
        registerVanilla(event, WoodType.CRIMSON,  Blocks.CRIMSON_PLANKS,  BlockFamilies.CRIMSON_PLANKS);
        registerVanilla(event, WoodType.WARPED,   Blocks.WARPED_PLANKS,   BlockFamilies.WARPED_PLANKS);
        registerVanilla(event, WoodType.MANGROVE, Blocks.MANGROVE_PLANKS, BlockFamilies.MANGROVE_PLANKS);
        registerVanilla(event, WoodType.BAMBOO,   Blocks.BAMBOO_PLANKS,   BlockFamilies.BAMBOO_PLANKS);
        registerVanilla(event, WoodType.CHERRY,   Blocks.CHERRY_PLANKS,   BlockFamilies.CHERRY_PLANKS);
    }

    private static void registerVanilla(RegisterBibliocraftWoodTypesEvent event, WoodType woodType, Block planks, BlockFamily family) {
        event.register(BCUtil.mcLoc(woodType.name()), woodType, () -> BlockBehaviour.Properties.ofFullCopy(planks), BCUtil.mcLoc("block/" + woodType.name() + "_planks"), () -> family);
    }

    private static void registerUseBlock() {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            BlockPos pos = hitResult.getBlockPos();
            if (level.getBlockEntity(pos) instanceof LecternBlockEntity lectern) {
                if (LecternUtil.handleLecternUse(level, pos, level.getBlockState(pos), lectern, player, hand)) {
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.PASS;
        });
    }
}
