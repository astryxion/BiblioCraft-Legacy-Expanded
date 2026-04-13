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
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;

import java.util.function.Supplier;

public interface BCMenus {
    // @formatter:off
    Supplier<MenuType<BookcaseMenu>>        BOOKCASE          = BCRegistries.MENUS.register("bookcase",          () -> IForgeMenuType.create(BookcaseMenu::new));
    Supplier<MenuType<CookieJarMenu>>       COOKIE_JAR        = BCRegistries.MENUS.register("cookie_jar",        () -> IForgeMenuType.create(CookieJarMenu::new));
    Supplier<MenuType<DiscRackMenu>>        DISC_RACK         = BCRegistries.MENUS.register("disc_rack",         () -> IForgeMenuType.create(DiscRackMenu::new));
    Supplier<MenuType<FancyArmorStandMenu>> FANCY_ARMOR_STAND = BCRegistries.MENUS.register("fancy_armor_stand", () -> IForgeMenuType.create(FancyArmorStandMenu::new));
    Supplier<MenuType<FancyCrafterMenu>>    FANCY_CRAFTER     = BCRegistries.MENUS.register("fancy_crafter",     () -> IForgeMenuType.create(FancyCrafterMenu::new));
    Supplier<MenuType<LabelMenu>>           LABEL             = BCRegistries.MENUS.register("label",             () -> IForgeMenuType.create(LabelMenu::new));
    Supplier<MenuType<PotionShelfMenu>>     POTION_SHELF      = BCRegistries.MENUS.register("potion_shelf",      () -> IForgeMenuType.create(PotionShelfMenu::new));
    Supplier<MenuType<PrintingTableMenu>>   PRINTING_TABLE    = BCRegistries.MENUS.register("printing_table",    () -> IForgeMenuType.create(PrintingTableMenu::new));
    Supplier<MenuType<ShelfMenu>>           SHELF             = BCRegistries.MENUS.register("shelf",             () -> IForgeMenuType.create(ShelfMenu::new));
    Supplier<MenuType<SlottedBookMenu>>     SLOTTED_BOOK      = BCRegistries.MENUS.register("slotted_book",      () -> IForgeMenuType.create(SlottedBookMenu::new));
    Supplier<MenuType<ToolRackMenu>>        TOOL_RACK         = BCRegistries.MENUS.register("tool_rack",         () -> IForgeMenuType.create(ToolRackMenu::new));
    // @formatter:on

    /**
     * Empty method, called by {@link BCRegistries#init(net.minecraftforge.eventbus.api.IEventBus)} to classload this class.
     */
    static void init() {
    }
}
