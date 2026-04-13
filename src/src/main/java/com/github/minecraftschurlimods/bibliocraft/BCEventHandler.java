package com.github.minecraftschurlimods.bibliocraft;

import com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi;
import com.github.minecraftschurlimods.bibliocraft.api.lockandkey.RegisterLockAndKeyBehaviorEvent;
import com.github.minecraftschurlimods.bibliocraft.api.woodtype.RegisterBibliocraftWoodTypesEvent;
import com.github.minecraftschurlimods.bibliocraft.apiimpl.LockAndKeyBehaviorsImpl;
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
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.LockCode;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.jetbrains.annotations.ApiStatus;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;

import java.util.function.Supplier;

public final class BCEventHandler {
    // @formatter:off
    @ApiStatus.Internal
    public static void init(IEventBus modBus) {
        modBus.addListener(EventPriority.LOWEST, BCEventHandler::commonSetup);
        modBus.addListener(BCEventHandler::entityAttributeCreation);
        modBus.addListener(BCEventHandler::registerLockAndKeyBehaviors);
        modBus.addListener(BCEventHandler::registerBibliocraftWoodTypes);
        MinecraftForge.EVENT_BUS.addListener(BCEventHandler::rightClickBlock);
    }
    // @formatter:on

    private static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ((LockAndKeyBehaviorsImpl) BibliocraftApi.getLockAndKeyBehaviors()).register();
            BCPackets.register();
        });
    }

    private static void entityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(BCEntities.FANCY_ARMOR_STAND.get(), LivingEntity.createLivingAttributes().build());
    }

    private static void registerLockAndKeyBehaviors(RegisterLockAndKeyBehaviorEvent event) {
        event.register(BaseContainerBlockEntity.class, BCEventHandler::getLockKey, BCEventHandler::setLockKeyBaseContainer, BaseContainerBlockEntity::getDisplayName);
        event.register(BeaconBlockEntity.class,        BCEventHandler::getLockKeyBeacon, BCEventHandler::setLockKeyBeacon, BeaconBlockEntity::getDisplayName);
        event.register(BCBlockEntity.class,            BCBlockEntity::getLockKey, BCBlockEntity::setLockKey, BCUtil::getNameForBE);
    }

    private static final VarHandle BASE_CONTAINER_LOCK_KEY = lockKeyVarHandle(BaseContainerBlockEntity.class);
    private static final VarHandle BEACON_LOCK_KEY = lockKeyVarHandle(BeaconBlockEntity.class);

    private static VarHandle lockKeyVarHandle(Class<?> clazz) {
        try {
            Class<?> c = clazz;
            while (c != null) {
                for (Field f : c.getDeclaredFields()) {
                    if (f.getType() == LockCode.class) {
                        f.setAccessible(true);
                        return MethodHandles.privateLookupIn(f.getDeclaringClass(), MethodHandles.lookup()).unreflectVarHandle(f);
                    }
                }
                c = c.getSuperclass();
            }
            throw new NoSuchFieldError("LockCode field not found in " + clazz.getName());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static LockCode getLockKey(BaseContainerBlockEntity be) {
        return (LockCode) BASE_CONTAINER_LOCK_KEY.get(be);
    }

    private static void setLockKeyBaseContainer(BaseContainerBlockEntity be, LockCode lock) {
        BASE_CONTAINER_LOCK_KEY.set(be, lock);
    }

    private static LockCode getLockKeyBeacon(BeaconBlockEntity be) {
        return (LockCode) BEACON_LOCK_KEY.get(be);
    }

    private static void setLockKeyBeacon(BeaconBlockEntity be, LockCode lock) {
        BEACON_LOCK_KEY.set(be, lock);
    }

    private static void registerBibliocraftWoodTypes(RegisterBibliocraftWoodTypesEvent event) {
        registerVanilla(event, WoodType.OAK,      Blocks.OAK_PLANKS,      BlockFamilies.OAK_PLANKS);
        registerVanilla(event, WoodType.SPRUCE,   Blocks.SPRUCE_PLANKS,   BlockFamilies.SPRUCE_PLANKS);
        registerVanilla(event, WoodType.BIRCH,    Blocks.BIRCH_PLANKS,    BlockFamilies.BIRCH_PLANKS);
        registerVanilla(event, WoodType.JUNGLE,   Blocks.JUNGLE_PLANKS,   BlockFamilies.JUNGLE_PLANKS);
        registerVanilla(event, WoodType.ACACIA,   Blocks.ACACIA_PLANKS,   BlockFamilies.ACACIA_PLANKS);
        registerVanilla(event, WoodType.DARK_OAK, Blocks.DARK_OAK_PLANKS, BlockFamilies.DARK_OAK_PLANKS);
        registerVanilla(event, WoodType.CRIMSON,  Blocks.CRIMSON_PLANKS,  BlockFamilies.CRIMSON_PLANKS);
        registerVanilla(event, WoodType.WARPED,   Blocks.WARPED_PLANKS,   BlockFamilies.WARPED_PLANKS);
        registerVanilla(event, WoodType.MANGROVE, Blocks.MANGROVE_PLANKS, BlockFamilies.MANGROVE_PLANKS);
        registerVanilla(event, WoodType.BAMBOO,   Blocks.BAMBOO_PLANKS,   BlockFamilies.BAMBOO_PLANKS);
        registerVanilla(event, WoodType.CHERRY,   Blocks.CHERRY_PLANKS,   BlockFamilies.CHERRY_PLANKS);
    }
    // @formatter:on

    /**
     * Private helper for registering the vanilla variants.
     */
    private static void registerVanilla(RegisterBibliocraftWoodTypesEvent event, WoodType woodType, Block planks, BlockFamily family) {
        event.register(BCUtil.mcLoc(woodType.name()), woodType, () -> BlockBehaviour.Properties.copy(planks), BCUtil.mcLoc("block/" + woodType.name() + "_planks"), () -> family);
    }

    /** Returns the mod's network channel for sending packets (e.g. sendToServer, sendToPlayer). */
    public static SimpleChannel getChannel() {
        return BCPackets.CHANNEL;
    }

    private static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        if (level.getBlockEntity(pos) instanceof LecternBlockEntity lectern && LecternUtil.handleLecternUse(level, pos, level.getBlockState(pos), lectern, event.getEntity(), event.getHand())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
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
            CHANNEL.messageBuilder(BigBookSignPacket.class, id++).encoder(BigBookSignPacket::encode).decoder(BigBookSignPacket::decode).consumerMainThread(BigBookSignPacket::handle).add();
            CHANNEL.messageBuilder(BigBookSyncPacket.class, id++).encoder(BigBookSyncPacket::encode).decoder(BigBookSyncPacket::decode).consumerMainThread(BigBookSyncPacket::handle).add();
            CHANNEL.messageBuilder(ClipboardSyncPacket.class, id++).encoder(ClipboardSyncPacket::encode).decoder(ClipboardSyncPacket::decode).consumerMainThread(ClipboardSyncPacket::handle).add();
            CHANNEL.messageBuilder(ClockSyncPacket.class, id++).encoder(ClockSyncPacket::encode).decoder(ClockSyncPacket::decode).consumerMainThread(ClockSyncPacket::handle).add();
            CHANNEL.messageBuilder(FancySignSyncPacket.class, id++).encoder(FancySignSyncPacket::encode).decoder(FancySignSyncPacket::decode).consumerMainThread(FancySignSyncPacket::handle).add();
            CHANNEL.messageBuilder(OpenBookInLecternPacket.class, id++).encoder(OpenBookInLecternPacket::encode).decoder(OpenBookInLecternPacket::decode).consumerMainThread(OpenBookInLecternPacket::handle).add();
            CHANNEL.messageBuilder(PrintingTableInputPacket.class, id++).encoder(PrintingTableInputPacket::encode).decoder(PrintingTableInputPacket::decode).consumerMainThread(PrintingTableInputPacket::handle).add();
            CHANNEL.messageBuilder(PrintingTableSetRecipePacket.class, id++).encoder(PrintingTableSetRecipePacket::encode).decoder(PrintingTableSetRecipePacket::decode).consumerMainThread(PrintingTableSetRecipePacket::handle).add();
            CHANNEL.messageBuilder(PrintingTableTankSyncPacket.class, id++).encoder(PrintingTableTankSyncPacket::encode).decoder(PrintingTableTankSyncPacket::decode).consumerMainThread(PrintingTableTankSyncPacket::handle).add();
            CHANNEL.messageBuilder(SetBigBookPageInLecternPacket.class, id++).encoder(SetBigBookPageInLecternPacket::encode).decoder(SetBigBookPageInLecternPacket::decode).consumerMainThread(SetBigBookPageInLecternPacket::handle).add();
            CHANNEL.messageBuilder(StockroomCatalogSyncPacket.class, id++).encoder(StockroomCatalogSyncPacket::encode).decoder(StockroomCatalogSyncPacket::decode).consumerMainThread(StockroomCatalogSyncPacket::handle).add();
            CHANNEL.messageBuilder(StockroomCatalogRequestListPacket.class, id++).encoder(StockroomCatalogRequestListPacket::encode).decoder(StockroomCatalogRequestListPacket::decode).consumerMainThread(StockroomCatalogRequestListPacket::handle).add();
            CHANNEL.messageBuilder(StockroomCatalogListPacket.class, id++).encoder(StockroomCatalogListPacket::encode).decoder(StockroomCatalogListPacket::decode).consumerMainThread(StockroomCatalogListPacket::handle).add();
            CHANNEL.messageBuilder(TakeLecternBookPacket.class, id++).encoder(TakeLecternBookPacket::encode).decoder(TakeLecternBookPacket::decode).consumerMainThread(TakeLecternBookPacket::handle).add();
            CHANNEL.messageBuilder(ToggleableSlotSyncPacket.class, id++).encoder(ToggleableSlotSyncPacket::encode).decoder(ToggleableSlotSyncPacket::decode).consumerMainThread(ToggleableSlotSyncPacket::handle).add();
            CHANNEL.messageBuilder(TypewriterSyncPacket.class, id++).encoder(TypewriterSyncPacket::encode).decoder(TypewriterSyncPacket::decode).consumerMainThread(TypewriterSyncPacket::handle).add();
        }
    }
}
