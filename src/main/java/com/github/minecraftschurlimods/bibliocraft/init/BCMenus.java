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
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;

import java.util.function.Supplier;

public interface BCMenus {
    // @formatter:off
    Supplier<ContainerType<BookcaseMenu>>        BOOKCASE          = BCRegistries.MENUS.register("bookcase",          () -> IForgeContainerType.create(BookcaseMenu::new));
    Supplier<ContainerType<CookieJarMenu>>       COOKIE_JAR        = BCRegistries.MENUS.register("cookie_jar",        () -> IForgeContainerType.create(CookieJarMenu::new));
    Supplier<ContainerType<DiscRackMenu>>        DISC_RACK         = BCRegistries.MENUS.register("disc_rack",         () -> IForgeContainerType.create(DiscRackMenu::new));
    Supplier<ContainerType<FancyArmorStandMenu>> FANCY_ARMOR_STAND = BCRegistries.MENUS.register("fancy_armor_stand", () -> IForgeContainerType.create(FancyArmorStandMenu::new));
    Supplier<ContainerType<FancyCrafterMenu>>    FANCY_CRAFTER     = BCRegistries.MENUS.register("fancy_crafter",     () -> IForgeContainerType.create(FancyCrafterMenu::new));
    Supplier<ContainerType<LabelMenu>>           LABEL             = BCRegistries.MENUS.register("label",             () -> IForgeContainerType.create(LabelMenu::new));
    Supplier<ContainerType<PotionShelfMenu>>     POTION_SHELF      = BCRegistries.MENUS.register("potion_shelf",      () -> IForgeContainerType.create(PotionShelfMenu::new));
    Supplier<ContainerType<PrintingTableMenu>>   PRINTING_TABLE    = BCRegistries.MENUS.register("printing_table",    () -> IForgeContainerType.create(PrintingTableMenu::new));
    Supplier<ContainerType<ShelfMenu>>           SHELF             = BCRegistries.MENUS.register("shelf",             () -> IForgeContainerType.create(ShelfMenu::new));
    Supplier<ContainerType<SlottedBookMenu>>     SLOTTED_BOOK      = BCRegistries.MENUS.register("slotted_book",      () -> IForgeContainerType.create(SlottedBookMenu::new));
    Supplier<ContainerType<ToolRackMenu>>        TOOL_RACK         = BCRegistries.MENUS.register("tool_rack",         () -> IForgeContainerType.create(ToolRackMenu::new));
    // @formatter:on

    /**
     * Empty method, called by {@link BCRegistries#init(net.minecraftforge.eventbus.api.IEventBus)} to classload this class.
     */
    static void init() {
    }
}
