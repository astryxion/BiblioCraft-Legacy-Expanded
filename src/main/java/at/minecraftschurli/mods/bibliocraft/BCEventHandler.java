package at.minecraftschurli.mods.bibliocraft;

import at.minecraftschurli.mods.bibliocraft.api.BibliocraftApi;
import at.minecraftschurli.mods.bibliocraft.api.lockandkey.RegisterLockAndKeyBehaviorEvent;
import at.minecraftschurli.mods.bibliocraft.api.woodtype.RegisterBibliocraftWoodTypesEvent;
import at.minecraftschurli.mods.bibliocraft.apiimpl.BibliocraftWoodTypeRegistryImpl;
import at.minecraftschurli.mods.bibliocraft.apiimpl.LockAndKeyBehaviorsImpl;
import at.minecraftschurli.mods.bibliocraft.content.bigbook.BigBookSignPacket;
import at.minecraftschurli.mods.bibliocraft.content.bigbook.BigBookSyncPacket;
import at.minecraftschurli.mods.bibliocraft.content.bigbook.SetBigBookPageInLecternPacket;
import at.minecraftschurli.mods.bibliocraft.content.clipboard.ClipboardSyncPacket;
import at.minecraftschurli.mods.bibliocraft.content.clock.ClockSyncPacket;
import at.minecraftschurli.mods.bibliocraft.content.fancysign.FancySignSyncPacket;
import at.minecraftschurli.mods.bibliocraft.content.printingtable.PrintingTableBlockEntity;
import at.minecraftschurli.mods.bibliocraft.content.printingtable.PrintingTableInputPacket;
import at.minecraftschurli.mods.bibliocraft.content.printingtable.PrintingTableSetRecipePacket;
import at.minecraftschurli.mods.bibliocraft.content.printingtable.PrintingTableTankSyncPacket;
import at.minecraftschurli.mods.bibliocraft.content.stockroomcatalog.StockroomCatalogListPacket;
import at.minecraftschurli.mods.bibliocraft.content.stockroomcatalog.StockroomCatalogRequestListPacket;
import at.minecraftschurli.mods.bibliocraft.content.stockroomcatalog.StockroomCatalogSyncPacket;
import at.minecraftschurli.mods.bibliocraft.content.typewriter.TypewriterSyncPacket;
import at.minecraftschurli.mods.bibliocraft.init.BCBlockEntities;
import at.minecraftschurli.mods.bibliocraft.init.BCBlocks;
import at.minecraftschurli.mods.bibliocraft.init.BCEntities;
import at.minecraftschurli.mods.bibliocraft.init.BCRegistries;
import at.minecraftschurli.mods.bibliocraft.init.BCRecipes;
import at.minecraftschurli.mods.bibliocraft.util.BCUtil;
import at.minecraftschurli.mods.bibliocraft.content.fancycrafter.FancyCrafterBlockEntity;
import at.minecraftschurli.mods.bibliocraft.content.printingtable.PrintingTableBlockEntity;
import at.minecraftschurli.mods.bibliocraft.content.typewriter.TypewriterBlockEntity;
import at.minecraftschurli.mods.bibliocraft.util.block.BCBlockEntity;
import at.minecraftschurli.mods.bibliocraft.util.lectern.LecternUtil;
import at.minecraftschurli.mods.bibliocraft.util.lectern.OpenBookInLecternPacket;
import at.minecraftschurli.mods.bibliocraft.util.lectern.TakeLecternBookPacket;
import at.minecraftschurli.mods.bibliocraft.util.slot.ToggleableSlotSyncPacket;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.FilteringStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = BibliocraftApi.MOD_ID)
public final class BCEventHandler {
    @SubscribeEvent
    private static void commonSetup(FMLCommonSetupEvent event) {
        ((LockAndKeyBehaviorsImpl) BibliocraftApi.getLockAndKeyBehaviors()).register();
    }

    @SubscribeEvent
    private static void preRegister(NewRegistryEvent event) {
        ((BibliocraftWoodTypeRegistryImpl) BibliocraftApi.getWoodTypeRegistry()).register();
        BCRegistries.init();
    }

    @SubscribeEvent
    private static void entityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(BCEntities.FANCY_ARMOR_STAND.get(), LivingEntity.createLivingAttributes().build());
    }

    // @formatter:off
    @SubscribeEvent
    private static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        event.registrar(BibliocraftApi.MOD_ID)
            .playToServer(BigBookSignPacket.TYPE,                 BigBookSignPacket.STREAM_CODEC,                 (packet, context) -> packet.handle(context.player()))
            .playToServer(BigBookSyncPacket.TYPE,                 BigBookSyncPacket.STREAM_CODEC,                 (packet, context) -> packet.handle(context.player()))
            .playToServer(ClipboardSyncPacket.TYPE,               ClipboardSyncPacket.STREAM_CODEC,               (packet, context) -> packet.handle(context.player()))
            .playBidirectional(ClockSyncPacket.TYPE,              ClockSyncPacket.STREAM_CODEC,                   (packet, context) -> packet.handle(context.player()), (packet, context) -> packet.handle(context.player()))
            .playToServer(FancySignSyncPacket.TYPE,               FancySignSyncPacket.STREAM_CODEC,               (packet, context) -> packet.handle(context.player()))
            .playToClient(OpenBookInLecternPacket.TYPE,           OpenBookInLecternPacket.STREAM_CODEC,           (packet, context) -> packet.handle(context.player()))
            .playToServer(PrintingTableInputPacket.TYPE,          PrintingTableInputPacket.STREAM_CODEC,          (packet, context) -> packet.handle(context.player()))
            .playBidirectional(PrintingTableSetRecipePacket.TYPE, PrintingTableSetRecipePacket.STREAM_CODEC,      (packet, context) -> packet.handle(context.player()), (packet, context) -> packet.handle(context.player()))
            .playToClient(PrintingTableTankSyncPacket.TYPE,       PrintingTableTankSyncPacket.STREAM_CODEC,       (packet, context) -> packet.handle(context.player()))
            .playToServer(SetBigBookPageInLecternPacket.TYPE,     SetBigBookPageInLecternPacket.STREAM_CODEC,     (packet, context) -> packet.handle(context.player()))
            .playToServer(StockroomCatalogSyncPacket.TYPE,        StockroomCatalogSyncPacket.STREAM_CODEC,        (packet, context) -> packet.handle(context.player()))
            .playToServer(StockroomCatalogRequestListPacket.TYPE, StockroomCatalogRequestListPacket.STREAM_CODEC, (packet, context) -> packet.handle(context.player()))
            .playToClient(StockroomCatalogListPacket.TYPE,        StockroomCatalogListPacket.STREAM_CODEC,        (packet, context) -> packet.handle(context.player()))
            .playToServer(TakeLecternBookPacket.TYPE,             TakeLecternBookPacket.STREAM_CODEC,             (packet, context) -> packet.handle(context.player()))
            .playBidirectional(ToggleableSlotSyncPacket.TYPE,     ToggleableSlotSyncPacket.STREAM_CODEC,          (packet, context) -> packet.handle(context.player()), (packet, context) -> packet.handle(context.player()))
            .playBidirectional(TypewriterSyncPacket.TYPE,         TypewriterSyncPacket.STREAM_CODEC,              (packet, context) -> packet.handle(context.player()), (packet, context) -> packet.handle(context.player()));
    }

    /// Fabric entrypoint wiring for common-side handlers.
    public static void registerFabric() {
        FabricDefaultAttributeRegistry.register(BCEntities.FANCY_ARMOR_STAND.get(), LivingEntity.createLivingAttributes().build());
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            BlockPos pos = hitResult.getBlockPos();
            if (level.getBlockEntity(pos) instanceof LecternBlockEntity lectern && LecternUtil.handleLecternUse(level, pos, level.getBlockState(pos), lectern, player, hand)) {
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        });
        registerPayloadTypes();
        registerServerPayloadHandlers();
        registerRecipeSynchronization();
        registerCapabilities();
    }

    private static void registerCapabilities() {
        // @formatter:off
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
            ContainerStorage storage = ContainerStorage.of(be, side);
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
            ContainerStorage storage = ContainerStorage.of(be, side);
            if (side == Direction.DOWN) {
                return FilteringStorage.extractOnlyOf(storage.getSlot(10));
            }
            return insertOnlySlots(storage, 0, 10);
        }, BCBlockEntities.PRINTING_TABLE.get());

        ItemStorage.SIDED.registerForBlockEntity((TypewriterBlockEntity be, Direction side) -> {
            if (side == null) return null;
            ContainerStorage storage = ContainerStorage.of(be, side);
            if (side == Direction.DOWN) {
                return FilteringStorage.extractOnlyOf(storage.getSlot(TypewriterBlockEntity.OUTPUT));
            }
            return FilteringStorage.insertOnlyOf(storage.getSlot(TypewriterBlockEntity.INPUT));
        }, BCBlockEntities.TYPEWRITER.get());

        FluidStorage.SIDED.registerForBlockEntity((PrintingTableBlockEntity be, Direction side) -> be.getBlockState().is(BCBlocks.IRON_PRINTING_TABLE.get()) ? be.getFluidStorage() : null, BCBlockEntities.PRINTING_TABLE.get());
        // @formatter:on
    }

    private static <T extends BCBlockEntity> void registerBcItemStorage(BlockEntityType<T> type) {
        ItemStorage.SIDED.registerForBlockEntity((be, side) -> ContainerStorage.of(be, side), type);
    }

    private static Storage<ItemVariant> insertOnlySlots(ContainerStorage storage, int fromInclusive, int toExclusive) {
        List<Storage<ItemVariant>> slots = new ArrayList<>();
        for (int i = fromInclusive; i < toExclusive; i++) {
            slots.add(FilteringStorage.insertOnlyOf(storage.getSlot(i)));
        }
        return new CombinedStorage<>(slots);
    }

    private static void registerRecipeSynchronization() {
        RecipeSynchronization.synchronizeRecipeSerializer(BCRecipes.PRINTING_TABLE_BINDING_TYPEWRITER_PAGES.get());
        RecipeSynchronization.synchronizeRecipeSerializer(BCRecipes.PRINTING_TABLE_CLONING.get());
        RecipeSynchronization.synchronizeRecipeSerializer(BCRecipes.PRINTING_TABLE_CLONING_WITH_ENCHANTMENTS.get());
        RecipeSynchronization.synchronizeRecipeSerializer(BCRecipes.PRINTING_TABLE_MERGING.get());
    }

    private static void registerPayloadTypes() {
        // @formatter:off
        PayloadTypeRegistry.serverboundPlay().register(BigBookSignPacket.TYPE,                 BigBookSignPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(BigBookSyncPacket.TYPE,                 BigBookSyncPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ClipboardSyncPacket.TYPE,               ClipboardSyncPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ClockSyncPacket.TYPE,                   ClockSyncPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(FancySignSyncPacket.TYPE,               FancySignSyncPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(PrintingTableInputPacket.TYPE,          PrintingTableInputPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(PrintingTableSetRecipePacket.TYPE,      PrintingTableSetRecipePacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SetBigBookPageInLecternPacket.TYPE,     SetBigBookPageInLecternPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(StockroomCatalogSyncPacket.TYPE,        StockroomCatalogSyncPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(StockroomCatalogRequestListPacket.TYPE, StockroomCatalogRequestListPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(TakeLecternBookPacket.TYPE,             TakeLecternBookPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ToggleableSlotSyncPacket.TYPE,          ToggleableSlotSyncPacket.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(TypewriterSyncPacket.TYPE,              TypewriterSyncPacket.STREAM_CODEC);

        PayloadTypeRegistry.clientboundPlay().register(ClockSyncPacket.TYPE,                   ClockSyncPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(OpenBookInLecternPacket.TYPE,           OpenBookInLecternPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(PrintingTableSetRecipePacket.TYPE,      PrintingTableSetRecipePacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(PrintingTableTankSyncPacket.TYPE,       PrintingTableTankSyncPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(StockroomCatalogListPacket.TYPE,        StockroomCatalogListPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ToggleableSlotSyncPacket.TYPE,          ToggleableSlotSyncPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(TypewriterSyncPacket.TYPE,              TypewriterSyncPacket.STREAM_CODEC);
        // @formatter:on
    }

    private static void registerServerPayloadHandlers() {
        // @formatter:off
        ServerPlayNetworking.registerGlobalReceiver(BigBookSignPacket.TYPE,                 (packet, context) -> packet.handle(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(BigBookSyncPacket.TYPE,                 (packet, context) -> packet.handle(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(ClipboardSyncPacket.TYPE,               (packet, context) -> packet.handle(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(ClockSyncPacket.TYPE,                   (packet, context) -> packet.handle(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(FancySignSyncPacket.TYPE,               (packet, context) -> packet.handle(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(PrintingTableInputPacket.TYPE,          (packet, context) -> packet.handle(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(PrintingTableSetRecipePacket.TYPE,      (packet, context) -> packet.handle(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(SetBigBookPageInLecternPacket.TYPE,     (packet, context) -> packet.handle(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(StockroomCatalogSyncPacket.TYPE,        (packet, context) -> packet.handle(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(StockroomCatalogRequestListPacket.TYPE, (packet, context) -> packet.handle(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(TakeLecternBookPacket.TYPE,             (packet, context) -> packet.handle(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(ToggleableSlotSyncPacket.TYPE,          (packet, context) -> packet.handle(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(TypewriterSyncPacket.TYPE,              (packet, context) -> packet.handle(context.player()));
        // @formatter:on
    }

    public static void registerLockAndKeyBehaviors(RegisterLockAndKeyBehaviorEvent event) {
        event.register(BaseContainerBlockEntity.class, be -> be.lockKey, (be, lock) -> be.lockKey = lock,    BaseContainerBlockEntity::getDisplayName);
        event.register(BeaconBlockEntity.class,        be -> be.lockKey, (be, lock) -> be.lockKey = lock,    BeaconBlockEntity::getDisplayName);
        event.register(BCBlockEntity.class,            BCBlockEntity::getLockKey, BCBlockEntity::setLockKey, BCUtil::getNameForBE);
    }

    public static void registerBibliocraftWoodTypes(RegisterBibliocraftWoodTypesEvent event) {
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
        registerVanilla(event, WoodType.PALE_OAK, Blocks.PALE_OAK_PLANKS, BlockFamilies.PALE_OAK_PLANKS);
    }
    // @formatter:on

    @SubscribeEvent
    private static void onDatapackSync(OnDatapackSyncEvent event) {
        event.sendRecipes(BCRecipes.PRINTING_TABLE.get());
    }

    @SubscribeEvent
    private static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        if (level.getBlockEntity(pos) instanceof LecternBlockEntity lectern && LecternUtil.handleLecternUse(level, pos, level.getBlockState(pos), lectern, event.getEntity(), event.getHand())) {
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    /// Private helper for registering the vanilla variants.
    private static void registerVanilla(RegisterBibliocraftWoodTypesEvent event, WoodType woodType, Block planks, BlockFamily family) {
        event.register(Identifier.withDefaultNamespace(woodType.name()), woodType, () -> BlockBehaviour.Properties.ofFullCopy(planks), Identifier.withDefaultNamespace("block/" + woodType.name() + "_planks"), () -> family);
    }
}
