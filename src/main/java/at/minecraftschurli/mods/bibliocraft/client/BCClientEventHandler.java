package at.minecraftschurli.mods.bibliocraft.client;

import at.minecraftschurli.mods.bibliocraft.client.ber.BookcaseBER;
import at.minecraftschurli.mods.bibliocraft.client.ber.ClipboardBER;
import at.minecraftschurli.mods.bibliocraft.client.ber.ClockBER;
import at.minecraftschurli.mods.bibliocraft.client.ber.CookieJarBER;
import at.minecraftschurli.mods.bibliocraft.client.ber.DinnerPlateBER;
import at.minecraftschurli.mods.bibliocraft.client.ber.DiscRackBER;
import at.minecraftschurli.mods.bibliocraft.client.ber.DisplayCaseBER;
import at.minecraftschurli.mods.bibliocraft.client.ber.FancyArmorStandBER;
import at.minecraftschurli.mods.bibliocraft.client.ber.FancyCrafterBER;
import at.minecraftschurli.mods.bibliocraft.client.ber.FancySignBER;
import at.minecraftschurli.mods.bibliocraft.client.ber.LabelBER;
import at.minecraftschurli.mods.bibliocraft.client.ber.PotionShelfBER;
import at.minecraftschurli.mods.bibliocraft.client.ber.ShelfBER;
import at.minecraftschurli.mods.bibliocraft.client.ber.SwordPedestalBER;
import at.minecraftschurli.mods.bibliocraft.client.ber.TableBER;
import at.minecraftschurli.mods.bibliocraft.client.ber.ToolRackBER;
import at.minecraftschurli.mods.bibliocraft.client.model.BookcaseBookModels;
import at.minecraftschurli.mods.bibliocraft.client.model.ConditionalModelLoader;
import at.minecraftschurli.mods.bibliocraft.client.model.SwordPedestalTintSource;
import at.minecraftschurli.mods.bibliocraft.client.screen.BCMenuScreens;
import at.minecraftschurli.mods.bibliocraft.client.screen.FancyCrafterScreen;
import at.minecraftschurli.mods.bibliocraft.client.screen.PrintingTableScreen;
import at.minecraftschurli.mods.bibliocraft.client.screen.SlottedBookScreen;
import at.minecraftschurli.mods.bibliocraft.init.BCBlockEntities;
import at.minecraftschurli.mods.bibliocraft.init.BCBlocks;
import at.minecraftschurli.mods.bibliocraft.init.BCEntities;
import at.minecraftschurli.mods.bibliocraft.init.BCMenus;
import at.minecraftschurli.mods.bibliocraft.util.BCUtil;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import at.minecraftschurli.mods.bibliocraft.client.jei.BCJeiPlugin;
import at.minecraftschurli.mods.bibliocraft.content.clock.ClockSyncPacket;
import at.minecraftschurli.mods.bibliocraft.content.printingtable.PrintingTableSetRecipePacket;
import at.minecraftschurli.mods.bibliocraft.content.printingtable.PrintingTableTankSyncPacket;
import at.minecraftschurli.mods.bibliocraft.content.stockroomcatalog.StockroomCatalogListPacket;
import at.minecraftschurli.mods.bibliocraft.content.typewriter.TypewriterSyncPacket;
import at.minecraftschurli.mods.bibliocraft.util.lectern.OpenBookInLecternPacket;
import at.minecraftschurli.mods.bibliocraft.util.slot.ToggleableSlotSyncPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.recipe.v1.sync.ClientRecipeSynchronizedEvent;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.crafting.RecipeMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.resources.Identifier;

import java.util.List;

public final class BCClientEventHandler {
    private BCClientEventHandler() {}

    public static void registerFabric() {
        registerMenuScreens();
        registerModelLoaders();
        registerBookcaseModels();
        registerLayerDefinitions();
        registerRenderers();
        registerColorHandlersBlock();
        registerClientPayloadHandlers();
        registerRecipeSynchronization();
    }

    private static void registerBookcaseModels() {
        ModelLoadingPlugin.register(BookcaseBookModels::register);
    }

    private static void registerRecipeSynchronization() {
        ClientRecipeSynchronizedEvent.EVENT.register((client, recipes) -> {
            if (FabricLoader.getInstance().isModLoaded("jei")) {
                BCJeiPlugin.setRecipeMap(RecipeMap.create(recipes.recipes()));
            }
        });
    }

    private static void registerClientPayloadHandlers() {
        // @formatter:off
        ClientPlayNetworking.registerGlobalReceiver(ClockSyncPacket.TYPE,                   (packet, context) -> packet.handle(context.player()));
        ClientPlayNetworking.registerGlobalReceiver(OpenBookInLecternPacket.TYPE,           (packet, context) -> packet.handle(context.player()));
        ClientPlayNetworking.registerGlobalReceiver(PrintingTableSetRecipePacket.TYPE,      (packet, context) -> packet.handle(context.player()));
        ClientPlayNetworking.registerGlobalReceiver(PrintingTableTankSyncPacket.TYPE,       (packet, context) -> packet.handle(context.player()));
        ClientPlayNetworking.registerGlobalReceiver(StockroomCatalogListPacket.TYPE,        (packet, context) -> packet.handle(context.player()));
        ClientPlayNetworking.registerGlobalReceiver(ToggleableSlotSyncPacket.TYPE,          (packet, context) -> packet.handle(context.player()));
        ClientPlayNetworking.registerGlobalReceiver(TypewriterSyncPacket.TYPE,              (packet, context) -> packet.handle(context.player()));
        // @formatter:on
    }

    private static void registerMenuScreens() {
        // @formatter:off
        MenuScreens.register(BCMenus.BOOKCASE.get(),           BCMenuScreens.Bookcase::new);
        MenuScreens.register(BCMenus.COOKIE_JAR.get(),        BCMenuScreens.CookieJar::new);
        MenuScreens.register(BCMenus.DISC_RACK.get(),         BCMenuScreens.DiscRack::new);
        MenuScreens.register(BCMenus.FANCY_ARMOR_STAND.get(), BCMenuScreens.FancyArmorStand::new);
        MenuScreens.register(BCMenus.LABEL.get(),             BCMenuScreens.Label::new);
        MenuScreens.register(BCMenus.POTION_SHELF.get(),      BCMenuScreens.PotionShelf::new);
        MenuScreens.register(BCMenus.PRINTING_TABLE.get(),    PrintingTableScreen::new);
        MenuScreens.register(BCMenus.SHELF.get(),             BCMenuScreens.Shelf::new);
        MenuScreens.register(BCMenus.TOOL_RACK.get(),         BCMenuScreens.ToolRack::new);
        MenuScreens.register(BCMenus.FANCY_CRAFTER.get(),     FancyCrafterScreen::new);
        MenuScreens.register(BCMenus.SLOTTED_BOOK.get(),      SlottedBookScreen::new);
        // @formatter:on
    }

    private static void registerModelLoaders() {
        // Conditional block models are resolved through vanilla model deserialization hooks during resource reload.
    }

    private static void registerLayerDefinitions() {
        ModelLayerRegistry.registerModelLayer(ClockBER.LOCATION, ClockBER::createLayerDefinition);
    }

    private static void registerRenderers() {
        // @formatter:off
        EntityRendererRegistry.register(BCEntities.FANCY_ARMOR_STAND.get(), ArmorStandRenderer::new);
        EntityRendererRegistry.register(BCEntities.SEAT.get(),              NoopRenderer::new);
        BlockEntityRendererRegistry.register(BCBlockEntities.BOOKCASE.get(), BookcaseBER::new);
        BlockEntityRendererRegistry.register(BCBlockEntities.CLOCK.get(),            ClockBER::new);
        BlockEntityRendererRegistry.register(BCBlockEntities.CLIPBOARD.get(),        ClipboardBER::new);
        BlockEntityRendererRegistry.register(BCBlockEntities.COOKIE_JAR.get(),       CookieJarBER::new);
        BlockEntityRendererRegistry.register(BCBlockEntities.DINNER_PLATE.get(),     DinnerPlateBER::new);
        BlockEntityRendererRegistry.register(BCBlockEntities.DISPLAY_CASE.get(),     DisplayCaseBER::new);
        BlockEntityRendererRegistry.register(BCBlockEntities.DISC_RACK.get(),        DiscRackBER::new);
        BlockEntityRendererRegistry.register(BCBlockEntities.FANCY_ARMOR_STAND.get(), FancyArmorStandBER::new);
        BlockEntityRendererRegistry.register(BCBlockEntities.FANCY_CRAFTER.get(),    FancyCrafterBER::new);
        BlockEntityRendererRegistry.register(BCBlockEntities.FANCY_SIGN.get(),       FancySignBER::new);
        BlockEntityRendererRegistry.register(BCBlockEntities.LABEL.get(),            LabelBER::new);
        BlockEntityRendererRegistry.register(BCBlockEntities.POTION_SHELF.get(),     PotionShelfBER::new);
        BlockEntityRendererRegistry.register(BCBlockEntities.SHELF.get(),            ShelfBER::new);
        BlockEntityRendererRegistry.register(BCBlockEntities.SWORD_PEDESTAL.get(), SwordPedestalBER::new);
        BlockEntityRendererRegistry.register(BCBlockEntities.TABLE.get(),            TableBER::new);
        BlockEntityRendererRegistry.register(BCBlockEntities.TOOL_RACK.get(),        ToolRackBER::new);
        // @formatter:on
    }

    private static void registerColorHandlersBlock() {
        BlockColorRegistry.register(List.of(SwordPedestalTintSource.INSTANCE), BCBlocks.SWORD_PEDESTAL.get());
    }
}
