package com.github.minecraftschurlimods.bibliocraft.client;

import com.github.minecraftschurlimods.bibliocraft.client.EmptyEntityRenderer;
import com.github.minecraftschurlimods.bibliocraft.client.ber.ClipboardBER;
import com.github.minecraftschurlimods.bibliocraft.client.ber.ClockBER;
import com.github.minecraftschurlimods.bibliocraft.client.ber.CookieJarBER;
import com.github.minecraftschurlimods.bibliocraft.client.ber.DinnerPlateBER;
import com.github.minecraftschurlimods.bibliocraft.client.ber.DiscRackBER;
import com.github.minecraftschurlimods.bibliocraft.client.ber.DisplayCaseBER;
import com.github.minecraftschurlimods.bibliocraft.client.ber.FancyArmorStandBER;
import com.github.minecraftschurlimods.bibliocraft.client.ber.FancyCrafterBER;
import com.github.minecraftschurlimods.bibliocraft.client.ber.FancySignBER;
import com.github.minecraftschurlimods.bibliocraft.client.ber.LabelBER;
import com.github.minecraftschurlimods.bibliocraft.client.ber.PotionShelfBER;
import com.github.minecraftschurlimods.bibliocraft.client.ber.ShelfBER;
import com.github.minecraftschurlimods.bibliocraft.client.ber.SwordPedestalBER;
import com.github.minecraftschurlimods.bibliocraft.client.ber.TableBER;
import com.github.minecraftschurlimods.bibliocraft.client.ber.ToolRackBER;
import com.github.minecraftschurlimods.bibliocraft.client.model.BookcaseModel;
import com.github.minecraftschurlimods.bibliocraft.client.model.TableModel;
import com.github.minecraftschurlimods.bibliocraft.client.screen.BCMenuScreens;
import com.github.minecraftschurlimods.bibliocraft.client.screen.FancyCrafterScreen;
import com.github.minecraftschurlimods.bibliocraft.client.screen.PrintingTableScreen;
import com.github.minecraftschurlimods.bibliocraft.client.screen.SlottedBookScreen;
import com.github.minecraftschurlimods.bibliocraft.content.swordpedestal.SwordPedestalBlock;
import com.github.minecraftschurlimods.bibliocraft.content.swordpedestal.SwordPedestalBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.content.table.TableBlock;
import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.init.BCBlocks;
import com.github.minecraftschurlimods.bibliocraft.init.BCEntities;
import com.github.minecraftschurlimods.bibliocraft.init.BCItems;
import com.github.minecraftschurlimods.bibliocraft.init.BCMenus;
import com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.DyeColor;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = BibliocraftApi.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class BCClientEventHandler {
    // @formatter:off
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(BCEntities.FANCY_ARMOR_STAND.get(), ArmorStandRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(BCEntities.SEAT.get(), EmptyEntityRenderer::new);
        event.enqueueWork(() -> {
            ScreenManager.register(BCMenus.BOOKCASE.get(),          BCMenuScreens.Bookcase::new);
            ScreenManager.register(BCMenus.COOKIE_JAR.get(),        BCMenuScreens.CookieJar::new);
            ScreenManager.register(BCMenus.DISC_RACK.get(),         BCMenuScreens.DiscRack::new);
            ScreenManager.register(BCMenus.FANCY_ARMOR_STAND.get(), BCMenuScreens.FancyArmorStand::new);
            ScreenManager.register(BCMenus.LABEL.get(),             BCMenuScreens.Label::new);
            ScreenManager.register(BCMenus.POTION_SHELF.get(),      BCMenuScreens.PotionShelf::new);
            ScreenManager.register(BCMenus.PRINTING_TABLE.get(),    PrintingTableScreen::new);
            ScreenManager.register(BCMenus.SHELF.get(),             BCMenuScreens.Shelf::new);
            ScreenManager.register(BCMenus.TOOL_RACK.get(),         BCMenuScreens.ToolRack::new);
            ScreenManager.register(BCMenus.FANCY_CRAFTER.get(),     FancyCrafterScreen::new);
            ScreenManager.register(BCMenus.SLOTTED_BOOK.get(),      SlottedBookScreen::new);
            // 1.20.1 model render_type is ignored in 1.16.5; set the same layers here.
            BCBlocks.FANCY_CRAFTER.values().forEach(block -> RenderTypeLookup.setRenderLayer(block, RenderType.cutout()));
            BCBlocks.FANCY_CLOCK.values().forEach(block -> RenderTypeLookup.setRenderLayer(block, RenderType.cutout()));
            BCBlocks.WALL_FANCY_CLOCK.values().forEach(block -> RenderTypeLookup.setRenderLayer(block, RenderType.cutout()));
            BCBlocks.GRANDFATHER_CLOCK.values().forEach(block -> RenderTypeLookup.setRenderLayer(block, RenderType.cutout()));
            BCBlocks.DISPLAY_CASE.values().forEach(block -> RenderTypeLookup.setRenderLayer(block, RenderType.cutout()));
            BCBlocks.WALL_DISPLAY_CASE.values().forEach(block -> RenderTypeLookup.setRenderLayer(block, RenderType.cutout()));
            BCBlocks.FANCY_GOLD_LANTERN.values().forEach(block -> RenderTypeLookup.setRenderLayer(block, RenderType.cutout()));
            BCBlocks.FANCY_IRON_LANTERN.values().forEach(block -> RenderTypeLookup.setRenderLayer(block, RenderType.cutout()));
            RenderTypeLookup.setRenderLayer(BCBlocks.CLEAR_FANCY_GOLD_LANTERN.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(BCBlocks.SOUL_FANCY_GOLD_LANTERN.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(BCBlocks.CLEAR_FANCY_IRON_LANTERN.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(BCBlocks.SOUL_FANCY_IRON_LANTERN.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(BCBlocks.COOKIE_JAR.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(BCBlocks.GOLD_CHAIN.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(BCBlocks.GOLD_LANTERN.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(BCBlocks.GOLD_SOUL_LANTERN.get(), RenderType.cutout());
            BCBlocks.FANCY_GOLD_LAMP.values().forEach(block -> RenderTypeLookup.setRenderLayer(block, RenderType.translucent()));
            BCBlocks.FANCY_IRON_LAMP.values().forEach(block -> RenderTypeLookup.setRenderLayer(block, RenderType.translucent()));
            RenderTypeLookup.setRenderLayer(BCBlocks.CLEAR_FANCY_GOLD_LAMP.get(), RenderType.translucent());
            RenderTypeLookup.setRenderLayer(BCBlocks.CLEAR_FANCY_IRON_LAMP.get(), RenderType.translucent());
            ClientRegistry.bindTileEntityRenderer(BCBlockEntities.CLOCK.get(),       ClockBER::new);
            ClientRegistry.bindTileEntityRenderer(BCBlockEntities.CLIPBOARD.get(),   ClipboardBER::new);
            ClientRegistry.bindTileEntityRenderer(BCBlockEntities.COOKIE_JAR.get(),  CookieJarBER::new);
            ClientRegistry.bindTileEntityRenderer(BCBlockEntities.DINNER_PLATE.get(), DinnerPlateBER::new);
            ClientRegistry.bindTileEntityRenderer(BCBlockEntities.DISPLAY_CASE.get(), DisplayCaseBER::new);
            ClientRegistry.bindTileEntityRenderer(BCBlockEntities.DISC_RACK.get(),   DiscRackBER::new);
            ClientRegistry.bindTileEntityRenderer(BCBlockEntities.FANCY_ARMOR_STAND.get(), FancyArmorStandBER::new);
            ClientRegistry.bindTileEntityRenderer(BCBlockEntities.FANCY_CRAFTER.get(), FancyCrafterBER::new);
            ClientRegistry.bindTileEntityRenderer(BCBlockEntities.FANCY_SIGN.get(),  FancySignBER::new);
            ClientRegistry.bindTileEntityRenderer(BCBlockEntities.LABEL.get(),       LabelBER::new);
            ClientRegistry.bindTileEntityRenderer(BCBlockEntities.POTION_SHELF.get(), PotionShelfBER::new);
            ClientRegistry.bindTileEntityRenderer(BCBlockEntities.SHELF.get(),       ShelfBER::new);
            ClientRegistry.bindTileEntityRenderer(BCBlockEntities.SWORD_PEDESTAL.get(), SwordPedestalBER::new);
            ClientRegistry.bindTileEntityRenderer(BCBlockEntities.TABLE.get(),       TableBER::new);
            ClientRegistry.bindTileEntityRenderer(BCBlockEntities.TOOL_RACK.get(),   ToolRackBER::new);
        });
    }
    // @formatter:on

    @SubscribeEvent
    public static void registerAdditional(ModelRegistryEvent event) {
        for (TableBlock.Type type : TableBlock.Type.values()) {
            for (DyeColor color : DyeColor.values()) {
                ResourceLocation loc = BCUtil.bcLoc("block/color/" + color.getSerializedName() + "/table_cloth_" + type.getSerializedName());
                ModelLoader.addSpecialModel(loc);
            }
        }
        net.minecraftforge.client.model.ModelLoaderRegistry.registerLoader(BCUtil.bcLoc("bookcase"), BookcaseModel.LOADER);
        net.minecraftforge.client.model.ModelLoaderRegistry.registerLoader(BCUtil.bcLoc("table"), TableModel.LOADER);
    }

    @SubscribeEvent
    public static void bakingCompleted(ModelBakeEvent event) {
        TableBER.rebuildClothModelCache();
    }

    @SubscribeEvent
    public static void registerColorHandlersBlock(ColorHandlerEvent.Block event) {
        event.getBlockColors().register((state, level, pos, tintIndex) -> {
            if (tintIndex == 0 && level != null && pos != null && level.getBlockEntity(pos) instanceof SwordPedestalBlockEntity) {
                SwordPedestalBlockEntity spbe = (SwordPedestalBlockEntity) level.getBlockEntity(pos);
                return spbe.getColor().rgb();
            }
            return -1;
        }, BCBlocks.SWORD_PEDESTAL.get());
    }

    @SubscribeEvent
    public static void registerColorHandlersItem(ColorHandlerEvent.Item event) {
        event.getItemColors().register((stack, tintIndex) -> tintIndex == 0 ? SwordPedestalBlock.getColorFromStack(stack) : -1, BCItems.SWORD_PEDESTAL.get());
    }
}
