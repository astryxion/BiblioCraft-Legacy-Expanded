package com.github.minecraftschurlimods.bibliocraft.init;

import com.github.minecraftschurlimods.bibliocraft.content.bookcase.BookcaseMenu;
import com.github.minecraftschurlimods.bibliocraft.content.cookiejar.CookieJarMenu;
import com.github.minecraftschurlimods.bibliocraft.content.discrack.DiscRackMenu;
import com.github.minecraftschurlimods.bibliocraft.content.fancyarmorstand.FancyArmorStandMenu;
import com.github.minecraftschurlimods.bibliocraft.content.fancycrafter.FancyCrafterMenu;
import com.github.minecraftschurlimods.bibliocraft.content.label.LabelMenu;
import com.github.minecraftschurlimods.bibliocraft.content.potionshelf.PotionShelfMenu;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableMenu;
import com.github.minecraftschurlimods.bibliocraft.content.shelf.ShelfMenu;
import com.github.minecraftschurlimods.bibliocraft.content.slottedbook.SlottedBookMenu;
import com.github.minecraftschurlimods.bibliocraft.content.toolrack.ToolRackMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public interface BCMenus {
    // Block-entity menus use Fabric ExtendedScreenHandlerType to pass BlockPos to client
    // @formatter:off
    Supplier<MenuType<BookcaseMenu>>        BOOKCASE          = cast(BCRegistries.MENUS.register("bookcase",          () -> new ExtendedScreenHandlerType<>((id, inv, pos) -> new BookcaseMenu(id, inv, pos), BlockPos.STREAM_CODEC)));
    Supplier<MenuType<CookieJarMenu>>       COOKIE_JAR        = cast(BCRegistries.MENUS.register("cookie_jar",        () -> new ExtendedScreenHandlerType<>((id, inv, pos) -> new CookieJarMenu(id, inv, pos), BlockPos.STREAM_CODEC)));
    Supplier<MenuType<DiscRackMenu>>        DISC_RACK         = cast(BCRegistries.MENUS.register("disc_rack",         () -> new ExtendedScreenHandlerType<>((id, inv, pos) -> new DiscRackMenu(id, inv, pos), BlockPos.STREAM_CODEC)));
    Supplier<MenuType<FancyArmorStandMenu>> FANCY_ARMOR_STAND = cast(BCRegistries.MENUS.register("fancy_armor_stand", () -> new ExtendedScreenHandlerType<>((id, inv, pos) -> new FancyArmorStandMenu(id, inv, pos), BlockPos.STREAM_CODEC)));
    Supplier<MenuType<FancyCrafterMenu>>    FANCY_CRAFTER     = cast(BCRegistries.MENUS.register("fancy_crafter",     () -> new ExtendedScreenHandlerType<>((id, inv, pos) -> new FancyCrafterMenu(id, inv, pos), BlockPos.STREAM_CODEC)));
    Supplier<MenuType<LabelMenu>>           LABEL             = cast(BCRegistries.MENUS.register("label",             () -> new ExtendedScreenHandlerType<>((id, inv, pos) -> new LabelMenu(id, inv, pos), BlockPos.STREAM_CODEC)));
    Supplier<MenuType<PotionShelfMenu>>     POTION_SHELF      = cast(BCRegistries.MENUS.register("potion_shelf",      () -> new ExtendedScreenHandlerType<>((id, inv, pos) -> new PotionShelfMenu(id, inv, pos), BlockPos.STREAM_CODEC)));
    Supplier<MenuType<PrintingTableMenu>>   PRINTING_TABLE    = cast(BCRegistries.MENUS.register("printing_table",    () -> new ExtendedScreenHandlerType<>((id, inv, pos) -> new PrintingTableMenu(id, inv, pos), BlockPos.STREAM_CODEC)));
    Supplier<MenuType<ShelfMenu>>           SHELF             = cast(BCRegistries.MENUS.register("shelf",             () -> new ExtendedScreenHandlerType<>((id, inv, pos) -> new ShelfMenu(id, inv, pos), BlockPos.STREAM_CODEC)));
    Supplier<MenuType<SlottedBookMenu>>     SLOTTED_BOOK      = cast(BCRegistries.MENUS.register("slotted_book",      () -> new MenuType<>((id, inv) -> new SlottedBookMenu(id, inv, net.minecraft.world.InteractionHand.MAIN_HAND), FeatureFlags.VANILLA_SET)));
    Supplier<MenuType<ToolRackMenu>>        TOOL_RACK         = cast(BCRegistries.MENUS.register("tool_rack",         () -> new ExtendedScreenHandlerType<>((id, inv, pos) -> new ToolRackMenu(id, inv, pos), BlockPos.STREAM_CODEC)));
    // @formatter:on

    @SuppressWarnings("unchecked")
    private static <M extends net.minecraft.world.inventory.AbstractContainerMenu> Supplier<MenuType<M>> cast(Object ref) {
        return (Supplier<MenuType<M>>) ref;
    }

    /**
     * Empty method, called by {@link BCRegistries#init()} to classload this class.
     */
    static void init() {
    }
}
