package com.github.minecraftschurlimods.bibliocraft;

import com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi;
import com.github.minecraftschurlimods.bibliocraft.api.lockandkey.RegisterLockAndKeyBehaviorEvent;
import com.github.minecraftschurlimods.bibliocraft.api.woodtype.RegisterBibliocraftWoodTypesEvent;
import com.github.minecraftschurlimods.bibliocraft.content.bigbook.BigBookSignPacket;
import com.github.minecraftschurlimods.bibliocraft.content.bigbook.BigBookSyncPacket;
import com.github.minecraftschurlimods.bibliocraft.content.bigbook.SetBigBookPageInLecternPacket;
import com.github.minecraftschurlimods.bibliocraft.content.clipboard.ClipboardSyncPacket;
import com.github.minecraftschurlimods.bibliocraft.content.clock.ClockSyncPacket;
import com.github.minecraftschurlimods.bibliocraft.content.fancysign.FancySignSyncPacket;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableInputPacket;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableSetRecipePacket;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableTankSyncPacket;
import com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog.StockroomCatalogListPacket;
import com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog.StockroomCatalogRequestListPacket;
import com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog.StockroomCatalogSyncPacket;
import com.github.minecraftschurlimods.bibliocraft.content.typewriter.TypewriterSyncPacket;
import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.init.BCBlocks;
import com.github.minecraftschurlimods.bibliocraft.init.BCEntities;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.util.lectern.LecternUtil;
import com.github.minecraftschurlimods.bibliocraft.util.lectern.OpenBookInLecternPacket;
import com.github.minecraftschurlimods.bibliocraft.util.lectern.TakeLecternBookPacket;
import com.github.minecraftschurlimods.bibliocraft.util.slot.ToggleableSlotSyncPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import com.github.minecraftschurlimods.bibliocraft.api.woodtype.BibliocraftWoodType.BlockFamily;
import net.minecraft.util.ActionResultType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.LockCode;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.tileentity.LecternTileEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.WoodType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.lang.reflect.Field;

import java.util.function.Supplier;

public final class BCEventHandler {
    // @formatter:off
    
    public static void init(IEventBus modBus) {
        modBus.addListener(BCEventHandler::entityAttributeCreation);
        modBus.addListener(BCEventHandler::registerLockAndKeyBehaviors);
        modBus.addListener(BCEventHandler::registerBibliocraftWoodTypes);
        MinecraftForge.EVENT_BUS.addListener(BCEventHandler::rightClickBlock);
        BCPackets.register();
    }
    // @formatter:on

    private static void entityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(BCEntities.FANCY_ARMOR_STAND.get(), LivingEntity.createLivingAttributes().build());
    }

    private static void registerLockAndKeyBehaviors(RegisterLockAndKeyBehaviorEvent event) {
        event.register(LockableTileEntity.class, BCEventHandler::getLockKey, BCEventHandler::setLockKeyBaseContainer, LockableTileEntity::getDisplayName);
        event.register(BeaconTileEntity.class,        BCEventHandler::getLockKeyBeacon, BCEventHandler::setLockKeyBeacon, BeaconTileEntity::getDisplayName);
        event.register(BCBlockEntity.class,            BCBlockEntity::getLockKey, BCBlockEntity::setLockKey, BCUtil::getNameForBE);
    }

    private static final Field BASE_CONTAINER_LOCK_KEY = lockKeyField(LockableTileEntity.class);
    private static final Field BEACON_LOCK_KEY = lockKeyField(BeaconTileEntity.class);

    private static Field lockKeyField(Class<?> clazz) {
        try {
            Class<?> c = clazz;
            while (c != null) {
                for (Field f : c.getDeclaredFields()) {
                    if (f.getType() == LockCode.class) {
                        f.setAccessible(true);
                        return f;
                    }
                }
                c = c.getSuperclass();
            }
            throw new NoSuchFieldError("LockCode field not found in " + clazz.getName());
        } catch (RuntimeException e) {
            throw e;
        }
    }

    private static LockCode getLockKey(LockableTileEntity be) {
        try {
            return (LockCode) BASE_CONTAINER_LOCK_KEY.get(be);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setLockKeyBaseContainer(LockableTileEntity be, LockCode lock) {
        try {
            BASE_CONTAINER_LOCK_KEY.set(be, lock);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static LockCode getLockKeyBeacon(BeaconTileEntity be) {
        try {
            return (LockCode) BEACON_LOCK_KEY.get(be);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setLockKeyBeacon(BeaconTileEntity be, LockCode lock) {
        try {
            BEACON_LOCK_KEY.set(be, lock);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void registerBibliocraftWoodTypes(RegisterBibliocraftWoodTypesEvent event) {
        registerVanilla(event, WoodType.OAK,      Blocks.OAK_PLANKS,      Blocks.OAK_SLAB);
        registerVanilla(event, WoodType.SPRUCE,   Blocks.SPRUCE_PLANKS,   Blocks.SPRUCE_SLAB);
        registerVanilla(event, WoodType.BIRCH,    Blocks.BIRCH_PLANKS,    Blocks.BIRCH_SLAB);
        registerVanilla(event, WoodType.JUNGLE,   Blocks.JUNGLE_PLANKS,   Blocks.JUNGLE_SLAB);
        registerVanilla(event, WoodType.ACACIA,   Blocks.ACACIA_PLANKS,   Blocks.ACACIA_SLAB);
        registerVanilla(event, WoodType.DARK_OAK, Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_SLAB);
        registerVanilla(event, WoodType.CRIMSON,  Blocks.CRIMSON_PLANKS,  Blocks.CRIMSON_SLAB);
        registerVanilla(event, WoodType.WARPED,   Blocks.WARPED_PLANKS,   Blocks.WARPED_SLAB);
    }
    // @formatter:on

    /**
     * Private helper for registering the vanilla variants.
     */
    private static void registerVanilla(RegisterBibliocraftWoodTypesEvent event, WoodType woodType, Block planks, Block slab) {
        BlockFamily family = new BlockFamily(planks, slab);
        event.register(BCUtil.mcLoc(woodType.name()), woodType, () -> AbstractBlock.Properties.copy(planks), BCUtil.mcLoc("block/" + woodType.name() + "_planks"), () -> family);
    }

    /** Returns the mod's network channel for sending packets (e.g. sendToServer, sendToPlayer). */
    public static SimpleChannel getChannel() {
        return BCPackets.CHANNEL;
    }

    private static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        World level = event.getWorld();
        BlockPos pos = event.getPos();
        if (level.getBlockEntity(pos) instanceof LecternTileEntity) {
            LecternTileEntity lectern = (LecternTileEntity) level.getBlockEntity(pos);
            if (LecternUtil.handleLecternUse(level, pos, level.getBlockState(pos), lectern, event.getPlayer(), event.getHand())) {
                event.setCanceled(true);
                event.setCancellationResult(ActionResultType.SUCCESS);
            }
        }
    }

    /**
     * Holds the mod's network channel and packet registration. Packets use Forge's SimpleChannel with encode/decode/handle.
     */
    static final class BCPackets {
        private static final String PROTOCOL = "1";
        static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(BibliocraftApi.MOD_ID, "main"),
                () -> PROTOCOL,
                PROTOCOL::equals,
                PROTOCOL::equals);

        private static int id;

        static void register() {
            CHANNEL.messageBuilder(BigBookSignPacket.class, id++).encoder(BigBookSignPacket::encode).decoder(BigBookSignPacket::decode).consumer(BigBookSignPacket::handle).add();
            CHANNEL.messageBuilder(BigBookSyncPacket.class, id++).encoder(BigBookSyncPacket::encode).decoder(BigBookSyncPacket::decode).consumer(BigBookSyncPacket::handle).add();
            CHANNEL.messageBuilder(ClipboardSyncPacket.class, id++).encoder(ClipboardSyncPacket::encode).decoder(ClipboardSyncPacket::decode).consumer(ClipboardSyncPacket::handle).add();
            CHANNEL.messageBuilder(ClockSyncPacket.class, id++).encoder(ClockSyncPacket::encode).decoder(ClockSyncPacket::decode).consumer(ClockSyncPacket::handle).add();
            CHANNEL.messageBuilder(FancySignSyncPacket.class, id++).encoder(FancySignSyncPacket::encode).decoder(FancySignSyncPacket::decode).consumer(FancySignSyncPacket::handle).add();
            CHANNEL.messageBuilder(OpenBookInLecternPacket.class, id++).encoder(OpenBookInLecternPacket::encode).decoder(OpenBookInLecternPacket::decode).consumer(OpenBookInLecternPacket::handle).add();
            CHANNEL.messageBuilder(PrintingTableInputPacket.class, id++).encoder(PrintingTableInputPacket::encode).decoder(PrintingTableInputPacket::decode).consumer(PrintingTableInputPacket::handle).add();
            CHANNEL.messageBuilder(PrintingTableSetRecipePacket.class, id++).encoder(PrintingTableSetRecipePacket::encode).decoder(PrintingTableSetRecipePacket::decode).consumer(PrintingTableSetRecipePacket::handle).add();
            CHANNEL.messageBuilder(PrintingTableTankSyncPacket.class, id++).encoder(PrintingTableTankSyncPacket::encode).decoder(PrintingTableTankSyncPacket::decode).consumer(PrintingTableTankSyncPacket::handle).add();
            CHANNEL.messageBuilder(SetBigBookPageInLecternPacket.class, id++).encoder(SetBigBookPageInLecternPacket::encode).decoder(SetBigBookPageInLecternPacket::decode).consumer(SetBigBookPageInLecternPacket::handle).add();
            CHANNEL.messageBuilder(StockroomCatalogSyncPacket.class, id++).encoder(StockroomCatalogSyncPacket::encode).decoder(StockroomCatalogSyncPacket::decode).consumer(StockroomCatalogSyncPacket::handle).add();
            CHANNEL.messageBuilder(StockroomCatalogRequestListPacket.class, id++).encoder(StockroomCatalogRequestListPacket::encode).decoder(StockroomCatalogRequestListPacket::decode).consumer(StockroomCatalogRequestListPacket::handle).add();
            CHANNEL.messageBuilder(StockroomCatalogListPacket.class, id++).encoder(StockroomCatalogListPacket::encode).decoder(StockroomCatalogListPacket::decode).consumer(StockroomCatalogListPacket::handle).add();
            CHANNEL.messageBuilder(TakeLecternBookPacket.class, id++).encoder(TakeLecternBookPacket::encode).decoder(TakeLecternBookPacket::decode).consumer(TakeLecternBookPacket::handle).add();
            CHANNEL.messageBuilder(ToggleableSlotSyncPacket.class, id++).encoder(ToggleableSlotSyncPacket::encode).decoder(ToggleableSlotSyncPacket::decode).consumer(ToggleableSlotSyncPacket::handle).add();
            CHANNEL.messageBuilder(TypewriterSyncPacket.class, id++).encoder(TypewriterSyncPacket::encode).decoder(TypewriterSyncPacket::decode).consumer(TypewriterSyncPacket::handle).add();
        }
    }
}
