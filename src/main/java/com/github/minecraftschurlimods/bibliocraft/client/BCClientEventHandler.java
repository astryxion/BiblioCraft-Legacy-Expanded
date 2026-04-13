package com.github.minecraftschurlimods.bibliocraft.client;

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
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = BibliocraftApi.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class BCClientEventHandler {
    // @formatter:off
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(BCMenus.BOOKCASE.get(),          BCMenuScreens.Bookcase::new);
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
        });
    }
    // @formatter:on

    @SubscribeEvent
    public static void registerAdditional(ModelEvent.RegisterAdditional event) {
        for (TableBlock.Type type : TableBlock.Type.values()) {
            for (DyeColor color : DyeColor.values()) {
                ResourceLocation loc = BCUtil.bcLoc("block/color/" + color.getSerializedName() + "/table_cloth_" + type.getSerializedName());
                event.register(new ModelResourceLocation(loc.getNamespace(), loc.getPath(), "inventory"));
            }
        }
    }

    @SubscribeEvent
    public static void bakingCompleted(ModelEvent.BakingCompleted event) {
        TableBER.rebuildClothModelCache();
    }

    @SubscribeEvent
    public static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        // Forge 1.20.1 register(String, ...) treats first arg as path and prepends mod namespace
        event.register(BCUtil.bcLoc("bookcase").getPath(), BookcaseModel.LOADER);
        event.register(BCUtil.bcLoc("table").getPath(), TableModel.LOADER);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ClockBER.LOCATION, ClockBER::createLayerDefinition);
    }

    // @formatter:off
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(BCEntities.FANCY_ARMOR_STAND.get(), ArmorStandRenderer::new);
        event.registerEntityRenderer(BCEntities.SEAT.get(),              EmptyEntityRenderer::new);
        event.registerBlockEntityRenderer(BCBlockEntities.CLOCK.get(),       ClockBER::new);
        event.registerBlockEntityRenderer(BCBlockEntities.CLIPBOARD.get(),         $ -> new ClipboardBER());
        event.registerBlockEntityRenderer(BCBlockEntities.COOKIE_JAR.get(),        $ -> new CookieJarBER());
        event.registerBlockEntityRenderer(BCBlockEntities.DINNER_PLATE.get(),      $ -> new DinnerPlateBER());
        event.registerBlockEntityRenderer(BCBlockEntities.DISPLAY_CASE.get(),      $ -> new DisplayCaseBER());
        event.registerBlockEntityRenderer(BCBlockEntities.DISC_RACK.get(),         $ -> new DiscRackBER());
        event.registerBlockEntityRenderer(BCBlockEntities.FANCY_ARMOR_STAND.get(), $ -> new FancyArmorStandBER());
        event.registerBlockEntityRenderer(BCBlockEntities.FANCY_CRAFTER.get(),     $ -> new FancyCrafterBER());
        event.registerBlockEntityRenderer(BCBlockEntities.FANCY_SIGN.get(),        $ -> new FancySignBER());
        event.registerBlockEntityRenderer(BCBlockEntities.LABEL.get(),             $ -> new LabelBER());
        event.registerBlockEntityRenderer(BCBlockEntities.POTION_SHELF.get(),      $ -> new PotionShelfBER());
        event.registerBlockEntityRenderer(BCBlockEntities.SHELF.get(),             $ -> new ShelfBER());
        event.registerBlockEntityRenderer(BCBlockEntities.SWORD_PEDESTAL.get(),    $ -> new SwordPedestalBER());
        event.registerBlockEntityRenderer(BCBlockEntities.TABLE.get(),             $ -> new TableBER());
        event.registerBlockEntityRenderer(BCBlockEntities.TOOL_RACK.get(),         $ -> new ToolRackBER());
    }
    // @formatter:on

    @SubscribeEvent
    public static void registerColorHandlersBlock(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> tintIndex == 0 && level != null && pos != null && level.getBlockEntity(pos) instanceof SwordPedestalBlockEntity spbe ? spbe.getColor().rgb() : -1, BCBlocks.SWORD_PEDESTAL.get());
    }

    @SubscribeEvent
    public static void registerColorHandlersItem(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> tintIndex == 0 ? SwordPedestalBlock.getColorFromStack(stack) : -1, BCItems.SWORD_PEDESTAL.get());
    }
}
