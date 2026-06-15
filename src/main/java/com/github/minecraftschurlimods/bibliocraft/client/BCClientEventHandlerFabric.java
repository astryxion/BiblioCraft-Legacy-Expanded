package com.github.minecraftschurlimods.bibliocraft.client;

import com.github.minecraftschurlimods.bibliocraft.BCConfig;
import com.github.minecraftschurlimods.bibliocraft.client.screen.BCMenuScreens;
import com.github.minecraftschurlimods.bibliocraft.client.screen.FancyCrafterScreen;
import com.github.minecraftschurlimods.bibliocraft.client.screen.PrintingTableScreen;
import com.github.minecraftschurlimods.bibliocraft.client.screen.SlottedBookScreen;
import com.github.minecraftschurlimods.bibliocraft.content.swordpedestal.SwordPedestalBlock;
import com.github.minecraftschurlimods.bibliocraft.content.swordpedestal.SwordPedestalBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.init.BCBlocks;
import com.github.minecraftschurlimods.bibliocraft.init.BCEntities;
import com.github.minecraftschurlimods.bibliocraft.init.BCItems;
import com.github.minecraftschurlimods.bibliocraft.init.BCMenus;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.world.level.block.Block;

/**
 * Fabric client registration. Replaces NeoForge's BCClientEventHandler for client-only registrations.
 */
public final class BCClientEventHandlerFabric {

    public static void init() {
        BCConfig.load();
        com.github.minecraftschurlimods.bibliocraft.fabric.BibliocraftClientNetworking.register();
        registerBlockRenderLayers();
        registerMenuScreens();
        registerRenderers();
        registerModelLoading();
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new TableClothModelCacheReloadListener());
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> registerColorHandlers());
    }

    /**
     * Register block render layers for transparency (Fabric requires this; NeoForge may auto-apply).
     * Without CUTOUT/TRANSLUCENT, glass and alpha textures render as solid.
     */
    private static void registerBlockRenderLayers() {
        BlockRenderLayerMap.INSTANCE.putBlock(BCBlocks.COOKIE_JAR.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BCBlocks.CLEAR_FANCY_GOLD_LAMP.get(), RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(BCBlocks.CLEAR_FANCY_IRON_LAMP.get(), RenderType.translucent());
        BlockRenderLayerMap.INSTANCE.putBlock(BCBlocks.CLEAR_FANCY_GOLD_LANTERN.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BCBlocks.CLEAR_FANCY_IRON_LANTERN.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BCBlocks.SOUL_FANCY_GOLD_LANTERN.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BCBlocks.SOUL_FANCY_IRON_LANTERN.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BCBlocks.GOLD_LANTERN.get(), RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BCBlocks.GOLD_SOUL_LANTERN.get(), RenderType.cutout());
        for (Block b : BCBlocks.FANCY_GOLD_LAMP.values()) BlockRenderLayerMap.INSTANCE.putBlock(b, RenderType.translucent());
        for (Block b : BCBlocks.FANCY_IRON_LAMP.values()) BlockRenderLayerMap.INSTANCE.putBlock(b, RenderType.translucent());
        for (Block b : BCBlocks.FANCY_GOLD_LANTERN.values()) BlockRenderLayerMap.INSTANCE.putBlock(b, RenderType.cutout());
        for (Block b : BCBlocks.FANCY_IRON_LANTERN.values()) BlockRenderLayerMap.INSTANCE.putBlock(b, RenderType.cutout());
        for (Block b : BCBlocks.DISPLAY_CASE.values()) BlockRenderLayerMap.INSTANCE.putBlock(b, RenderType.cutout());
        for (Block b : BCBlocks.WALL_DISPLAY_CASE.values()) BlockRenderLayerMap.INSTANCE.putBlock(b, RenderType.cutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BCBlocks.GOLD_CHAIN.get(), RenderType.cutout());
        for (Block b : BCBlocks.FANCY_CRAFTER.values()) BlockRenderLayerMap.INSTANCE.putBlock(b, RenderType.cutout());
    }

    private static void registerColorHandlers() {
        net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry.BLOCK.register((state, level, pos, tintIndex) ->
            tintIndex == 0 && level != null && pos != null && level.getBlockEntity(pos) instanceof SwordPedestalBlockEntity spbe
                ? spbe.getColor().rgb() : -1, BCBlocks.SWORD_PEDESTAL.get());
        net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry.ITEM.register((stack, tintIndex) ->
            tintIndex == 0 ? stack.getOrDefault(DataComponents.DYED_COLOR, SwordPedestalBlock.DEFAULT_COLOR).rgb() : -1,
            BCItems.SWORD_PEDESTAL.get());
    }

    private static void registerModelLoading() {
        net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin.register(
            (resourceManager, executor) -> java.util.concurrent.CompletableFuture.completedFuture(resourceManager),
            new com.github.minecraftschurlimods.bibliocraft.client.model.BCModelLoadingPlugin()
        );
    }

    private static void registerMenuScreens() {
        MenuScreens.register(BCMenus.BOOKCASE.get(), BCMenuScreens.Bookcase::new);
        MenuScreens.register(BCMenus.COOKIE_JAR.get(), BCMenuScreens.CookieJar::new);
        MenuScreens.register(BCMenus.DISC_RACK.get(), BCMenuScreens.DiscRack::new);
        MenuScreens.register(BCMenus.FANCY_ARMOR_STAND.get(), BCMenuScreens.FancyArmorStand::new);
        MenuScreens.register(BCMenus.LABEL.get(), BCMenuScreens.Label::new);
        MenuScreens.register(BCMenus.POTION_SHELF.get(), BCMenuScreens.PotionShelf::new);
        MenuScreens.register(BCMenus.PRINTING_TABLE.get(), PrintingTableScreen::new);
        MenuScreens.register(BCMenus.SHELF.get(), BCMenuScreens.Shelf::new);
        MenuScreens.register(BCMenus.TOOL_RACK.get(), BCMenuScreens.ToolRack::new);
        MenuScreens.register(BCMenus.FANCY_CRAFTER.get(), FancyCrafterScreen::new);
        MenuScreens.register(BCMenus.SLOTTED_BOOK.get(), SlottedBookScreen::new);
    }

    private static void registerRenderers() {
        EntityModelLayerRegistry.registerModelLayer(com.github.minecraftschurlimods.bibliocraft.client.ber.ClockBER.LOCATION, com.github.minecraftschurlimods.bibliocraft.client.ber.ClockBER::createLayerDefinition);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(BCEntities.FANCY_ARMOR_STAND.get(), ArmorStandRenderer::new);
        net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(BCEntities.SEAT.get(), EmptyEntityRenderer::new);
        BlockEntityRendererRegistry.register(BCBlockEntities.BOOKCASE.get(), ctx -> new com.github.minecraftschurlimods.bibliocraft.client.ber.BookcaseBER());
        BlockEntityRendererRegistry.register(BCBlockEntities.CLOCK.get(), com.github.minecraftschurlimods.bibliocraft.client.ber.ClockBER::new);
        BlockEntityRendererRegistry.register(BCBlockEntities.CLIPBOARD.get(), ctx -> new com.github.minecraftschurlimods.bibliocraft.client.ber.ClipboardBER());
        BlockEntityRendererRegistry.register(BCBlockEntities.COOKIE_JAR.get(), ctx -> new com.github.minecraftschurlimods.bibliocraft.client.ber.CookieJarBER());
        BlockEntityRendererRegistry.register(BCBlockEntities.DINNER_PLATE.get(), ctx -> new com.github.minecraftschurlimods.bibliocraft.client.ber.DinnerPlateBER());
        BlockEntityRendererRegistry.register(BCBlockEntities.DISPLAY_CASE.get(), ctx -> new com.github.minecraftschurlimods.bibliocraft.client.ber.DisplayCaseBER());
        BlockEntityRendererRegistry.register(BCBlockEntities.DISC_RACK.get(), ctx -> new com.github.minecraftschurlimods.bibliocraft.client.ber.DiscRackBER());
        BlockEntityRendererRegistry.register(BCBlockEntities.FANCY_ARMOR_STAND.get(), ctx -> new com.github.minecraftschurlimods.bibliocraft.client.ber.FancyArmorStandBER());
        BlockEntityRendererRegistry.register(BCBlockEntities.FANCY_CRAFTER.get(), ctx -> new com.github.minecraftschurlimods.bibliocraft.client.ber.FancyCrafterBER());
        BlockEntityRendererRegistry.register(BCBlockEntities.FANCY_SIGN.get(), ctx -> new com.github.minecraftschurlimods.bibliocraft.client.ber.FancySignBER());
        BlockEntityRendererRegistry.register(BCBlockEntities.LABEL.get(), ctx -> new com.github.minecraftschurlimods.bibliocraft.client.ber.LabelBER());
        BlockEntityRendererRegistry.register(BCBlockEntities.POTION_SHELF.get(), ctx -> new com.github.minecraftschurlimods.bibliocraft.client.ber.PotionShelfBER());
        BlockEntityRendererRegistry.register(BCBlockEntities.SHELF.get(), ctx -> new com.github.minecraftschurlimods.bibliocraft.client.ber.ShelfBER());
        BlockEntityRendererRegistry.register(BCBlockEntities.SWORD_PEDESTAL.get(), ctx -> new com.github.minecraftschurlimods.bibliocraft.client.ber.SwordPedestalBER());
        BlockEntityRendererRegistry.register(BCBlockEntities.TABLE.get(), ctx -> new com.github.minecraftschurlimods.bibliocraft.client.ber.TableBER(ctx));
        BlockEntityRendererRegistry.register(BCBlockEntities.TOOL_RACK.get(), ctx -> new com.github.minecraftschurlimods.bibliocraft.client.ber.ToolRackBER());
    }
}
