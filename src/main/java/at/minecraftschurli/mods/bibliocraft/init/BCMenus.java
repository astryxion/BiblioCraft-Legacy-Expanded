package at.minecraftschurli.mods.bibliocraft.init;

import at.minecraftschurli.mods.bibliocraft.content.bookcase.BookcaseBlockEntity;
import at.minecraftschurli.mods.bibliocraft.content.bookcase.BookcaseMenu;
import at.minecraftschurli.mods.bibliocraft.content.cookiejar.CookieJarBlockEntity;
import at.minecraftschurli.mods.bibliocraft.content.cookiejar.CookieJarMenu;
import at.minecraftschurli.mods.bibliocraft.content.discrack.DiscRackBlockEntity;
import at.minecraftschurli.mods.bibliocraft.content.discrack.DiscRackMenu;
import at.minecraftschurli.mods.bibliocraft.content.fancyarmorstand.FancyArmorStandBlockEntity;
import at.minecraftschurli.mods.bibliocraft.content.fancyarmorstand.FancyArmorStandMenu;
import at.minecraftschurli.mods.bibliocraft.content.fancycrafter.FancyCrafterBlockEntity;
import at.minecraftschurli.mods.bibliocraft.content.fancycrafter.FancyCrafterMenu;
import at.minecraftschurli.mods.bibliocraft.content.label.LabelBlockEntity;
import at.minecraftschurli.mods.bibliocraft.content.label.LabelMenu;
import at.minecraftschurli.mods.bibliocraft.content.potionshelf.PotionShelfBlockEntity;
import at.minecraftschurli.mods.bibliocraft.content.potionshelf.PotionShelfMenu;
import at.minecraftschurli.mods.bibliocraft.content.printingtable.PrintingTableBlockEntity;
import at.minecraftschurli.mods.bibliocraft.content.printingtable.PrintingTableMenu;
import at.minecraftschurli.mods.bibliocraft.content.shelf.ShelfBlockEntity;
import at.minecraftschurli.mods.bibliocraft.content.shelf.ShelfMenu;
import at.minecraftschurli.mods.bibliocraft.content.slottedbook.SlottedBookMenu;
import at.minecraftschurli.mods.bibliocraft.content.toolrack.ToolRackBlockEntity;
import at.minecraftschurli.mods.bibliocraft.content.toolrack.ToolRackMenu;
import at.minecraftschurli.mods.bibliocraft.util.BCUtil;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

public interface BCMenus {
    // @formatter:off
    Supplier<MenuType<BookcaseMenu>>        BOOKCASE          = registerBlockEntity("bookcase",          BookcaseMenu::new, BookcaseBlockEntity.class);
    Supplier<MenuType<CookieJarMenu>>       COOKIE_JAR        = registerBlockEntity("cookie_jar",        CookieJarMenu::new, CookieJarBlockEntity.class);
    Supplier<MenuType<DiscRackMenu>>        DISC_RACK         = registerBlockEntity("disc_rack",         DiscRackMenu::new, DiscRackBlockEntity.class);
    Supplier<MenuType<FancyArmorStandMenu>> FANCY_ARMOR_STAND = registerBlockEntity("fancy_armor_stand", FancyArmorStandMenu::new, FancyArmorStandBlockEntity.class);
    Supplier<MenuType<FancyCrafterMenu>>    FANCY_CRAFTER     = registerBlockEntity("fancy_crafter",     (id, inventory, blockEntity) -> new FancyCrafterMenu(id, inventory, blockEntity, new net.minecraft.world.inventory.SimpleContainerData(9)), FancyCrafterBlockEntity.class);
    Supplier<MenuType<LabelMenu>>           LABEL             = registerBlockEntity("label",             LabelMenu::new, LabelBlockEntity.class);
    Supplier<MenuType<PotionShelfMenu>>     POTION_SHELF      = registerBlockEntity("potion_shelf",      PotionShelfMenu::new, PotionShelfBlockEntity.class);
    Supplier<MenuType<PrintingTableMenu>>   PRINTING_TABLE    = registerBlockEntity("printing_table",    PrintingTableMenu::new, PrintingTableBlockEntity.class);
    Supplier<MenuType<ShelfMenu>>           SHELF             = registerBlockEntity("shelf",             ShelfMenu::new, ShelfBlockEntity.class);
    Supplier<MenuType<SlottedBookMenu>>     SLOTTED_BOOK      = register("slotted_book", SlottedBookMenu::new, InteractionHand.STREAM_CODEC);
    Supplier<MenuType<ToolRackMenu>>        TOOL_RACK         = registerBlockEntity("tool_rack",         ToolRackMenu::new, ToolRackBlockEntity.class);
    // @formatter:on

    @FunctionalInterface
    interface BlockEntityMenuFactory<T extends AbstractContainerMenu, B> {
        T create(int id, net.minecraft.world.entity.player.Inventory inventory, B blockEntity);
    }

    private static <T extends AbstractContainerMenu, B extends at.minecraftschurli.mods.bibliocraft.util.block.BCMenuBlockEntity> Supplier<MenuType<T>> registerBlockEntity(String name, BlockEntityMenuFactory<T, B> factory, Class<B> blockEntityClass) {
        return register(name, (id, inventory, pos) -> factory.create(id, inventory, blockEntityClass.cast(BCUtil.nonNull(inventory.player.level().getBlockEntity(pos)))), BlockPos.STREAM_CODEC);
    }

    private static <T extends AbstractContainerMenu, D> Supplier<MenuType<T>> register(String name, ExtendedMenuType.ExtendedFactory<T, D> factory, net.minecraft.network.codec.StreamCodec<? super net.minecraft.network.RegistryFriendlyByteBuf, D> streamCodec) {
        return BCRegistries.MENUS.register(name, () -> new ExtendedMenuType<>(factory, streamCodec));
    }

    /// Empty method, called by [BCRegistries#init()] to classload this class.
    @ApiStatus.Internal
    static void init() {}
}
