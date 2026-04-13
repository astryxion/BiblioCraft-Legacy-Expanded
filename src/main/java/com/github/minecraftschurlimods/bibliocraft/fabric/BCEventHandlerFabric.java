package com.github.minecraftschurlimods.bibliocraft.fabric;

import com.github.minecraftschurlimods.bibliocraft.api.lockandkey.RegisterLockAndKeyBehaviorEvent;
import com.github.minecraftschurlimods.bibliocraft.api.woodtype.RegisterBibliocraftWoodTypesEvent;
import com.github.minecraftschurlimods.bibliocraft.apiimpl.LockAndKeyBehaviorsImpl;
import com.github.minecraftschurlimods.bibliocraft.init.BCEntities;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.util.lectern.LecternUtil;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;

/**
 * Fabric replacement for BCEventHandler. Registers events and entity attributes.
 */
public final class BCEventHandlerFabric {

    public static void init() {
        ((LockAndKeyBehaviorsImpl) com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi.getLockAndKeyBehaviors()).registerForFabric();
        registerEntityAttributes();
        registerUseBlock();
        BibliocraftNetworking.register();
    }

    private static void registerEntityAttributes() {
        FabricDefaultAttributeRegistry.register(BCEntities.FANCY_ARMOR_STAND.get(), LivingEntity.createLivingAttributes().build());
    }

    /**
     * Registers default lock-and-key behaviors (vanilla + BC). Called from LockAndKeyBehaviorsImpl.registerForFabric().
     */
    public static void registerLockAndKeyBehaviors(RegisterLockAndKeyBehaviorEvent event) {
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
