package com.github.minecraftschurlimods.bibliocraft.init;

import com.github.minecraftschurlimods.bibliocraft.api.woodtype.BibliocraftWoodType;
import com.github.minecraftschurlimods.bibliocraft.content.bigbook.BigBookItem;
import com.github.minecraftschurlimods.bibliocraft.content.clipboard.ClipboardItem;
import com.github.minecraftschurlimods.bibliocraft.content.clock.FancyClockItem;
import com.github.minecraftschurlimods.bibliocraft.content.discrack.DiscRackItem;
import com.github.minecraftschurlimods.bibliocraft.content.displaycase.DisplayCaseItem;
import com.github.minecraftschurlimods.bibliocraft.content.fancysign.FancySignItem;
import com.github.minecraftschurlimods.bibliocraft.content.lockandkey.LockAndKeyItem;
import com.github.minecraftschurlimods.bibliocraft.content.plumbline.PlumbLineItem;
import com.github.minecraftschurlimods.bibliocraft.content.redstonebook.RedstoneBookItem;
import com.github.minecraftschurlimods.bibliocraft.content.seat.SeatBackItem;
import com.github.minecraftschurlimods.bibliocraft.content.seat.SeatBackType;
import com.github.minecraftschurlimods.bibliocraft.content.slottedbook.SlottedBookItem;
import com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog.StockroomCatalogItem;
import com.github.minecraftschurlimods.bibliocraft.content.swordpedestal.SwordPedestalBlock;
import com.github.minecraftschurlimods.bibliocraft.content.tapemeasure.TapeMeasureItem;
import com.github.minecraftschurlimods.bibliocraft.content.typewriter.TypewriterPageItem;
import com.github.minecraftschurlimods.bibliocraft.util.block.ColoredWoodTypeBlockItem;
import com.github.minecraftschurlimods.bibliocraft.util.holder.ColoredDeferredHolder;
import com.github.minecraftschurlimods.bibliocraft.util.holder.ColoredWoodTypeDeferredHolder;
import com.github.minecraftschurlimods.bibliocraft.util.holder.WoodTypeDeferredHolder;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;
import java.util.function.Supplier;

public interface BCItems {
    Item.Properties PROPERTIES = new Item.Properties();

    // @formatter:off
    WoodTypeDeferredHolder<Item, BlockItem>           BOOKCASE          = woodenBlock("bookcase",          BCBlocks.BOOKCASE);
    WoodTypeDeferredHolder<Item, DoubleHighBlockItem> FANCY_ARMOR_STAND = woodenBlock("fancy_armor_stand", wood -> new DoubleHighBlockItem(BCBlocks.FANCY_ARMOR_STAND.get(wood), PROPERTIES));
    WoodTypeDeferredHolder<Item, FancyClockItem>      FANCY_CLOCK       = woodenBlock("fancy_clock",       FancyClockItem::new);
    WoodTypeDeferredHolder<Item, FancySignItem>       FANCY_SIGN        = woodenBlock("fancy_sign",        FancySignItem::new);
    WoodTypeDeferredHolder<Item, BlockItem>           FANCY_CRAFTER     = woodenBlock("fancy_crafter",     BCBlocks.FANCY_CRAFTER);
    WoodTypeDeferredHolder<Item, DoubleHighBlockItem> GRANDFATHER_CLOCK = woodenBlock("grandfather_clock", wood -> new DoubleHighBlockItem(BCBlocks.GRANDFATHER_CLOCK.get(wood), PROPERTIES));
    WoodTypeDeferredHolder<Item, BlockItem>           LABEL             = woodenBlock("label",             BCBlocks.LABEL);
    WoodTypeDeferredHolder<Item, BlockItem>           POTION_SHELF      = woodenBlock("potion_shelf",      BCBlocks.POTION_SHELF);
    WoodTypeDeferredHolder<Item, BlockItem>           SHELF             = woodenBlock("shelf",             BCBlocks.SHELF);
    WoodTypeDeferredHolder<Item, BlockItem>           TABLE             = woodenBlock("table",             BCBlocks.TABLE);
    WoodTypeDeferredHolder<Item, BlockItem>           TOOL_RACK         = woodenBlock("tool_rack",         BCBlocks.TOOL_RACK);
    ColoredWoodTypeDeferredHolder<Item, BlockItem>    DISPLAY_CASE      = new ColoredWoodTypeDeferredHolder<>(BCRegistries.ITEMS, "display_case",     DisplayCaseItem::new);
    ColoredWoodTypeDeferredHolder<Item, BlockItem>    SEAT              = new ColoredWoodTypeDeferredHolder<>(BCRegistries.ITEMS, "seat",             (wood, color) -> new ColoredWoodTypeBlockItem(BCBlocks.SEAT, wood, color));
    ColoredWoodTypeDeferredHolder<Item, SeatBackItem> SMALL_SEAT_BACK   = new ColoredWoodTypeDeferredHolder<>(BCRegistries.ITEMS, "small_seat_back",  (wood, color) -> new SeatBackItem(BCBlocks.SEAT_BACK, wood, color, SeatBackType.SMALL));
    ColoredWoodTypeDeferredHolder<Item, SeatBackItem> RAISED_SEAT_BACK  = new ColoredWoodTypeDeferredHolder<>(BCRegistries.ITEMS, "raised_seat_back", (wood, color) -> new SeatBackItem(BCBlocks.SEAT_BACK, wood, color, SeatBackType.RAISED));
    ColoredWoodTypeDeferredHolder<Item, SeatBackItem> FLAT_SEAT_BACK    = new ColoredWoodTypeDeferredHolder<>(BCRegistries.ITEMS, "flat_seat_back",   (wood, color) -> new SeatBackItem(BCBlocks.SEAT_BACK, wood, color, SeatBackType.FLAT));
    ColoredWoodTypeDeferredHolder<Item, SeatBackItem> TALL_SEAT_BACK    = new ColoredWoodTypeDeferredHolder<>(BCRegistries.ITEMS, "tall_seat_back",   (wood, color) -> new SeatBackItem(BCBlocks.SEAT_BACK, wood, color, SeatBackType.TALL));
    ColoredWoodTypeDeferredHolder<Item, SeatBackItem> FANCY_SEAT_BACK   = new ColoredWoodTypeDeferredHolder<>(BCRegistries.ITEMS, "fancy_seat_back",  (wood, color) -> new SeatBackItem(BCBlocks.SEAT_BACK, wood, color, SeatBackType.FANCY));
    RegistryObject<BlockItem>          CLEAR_FANCY_GOLD_LAMP = BCRegistries.ITEMS.register(                    "fancy_gold_lamp",    () -> new BlockItem(BCBlocks.CLEAR_FANCY_GOLD_LAMP.get(), PROPERTIES));
    ColoredDeferredHolder<Item, BlockItem> FANCY_GOLD_LAMP = new ColoredDeferredHolder<>(BCRegistries.ITEMS, "fancy_gold_lamp",    color -> new BlockItem(BCBlocks.FANCY_GOLD_LAMP.get(color), PROPERTIES));
    RegistryObject<BlockItem>          CLEAR_FANCY_IRON_LAMP = BCRegistries.ITEMS.register(                    "fancy_iron_lamp",    () -> new BlockItem(BCBlocks.CLEAR_FANCY_IRON_LAMP.get(), PROPERTIES));
    ColoredDeferredHolder<Item, BlockItem> FANCY_IRON_LAMP = new ColoredDeferredHolder<>(BCRegistries.ITEMS, "fancy_iron_lamp",    color -> new BlockItem(BCBlocks.FANCY_IRON_LAMP.get(color), PROPERTIES));
    RegistryObject<BlockItem>          CLEAR_FANCY_GOLD_LANTERN = BCRegistries.ITEMS.register(                    "fancy_gold_lantern", () -> new BlockItem(BCBlocks.CLEAR_FANCY_GOLD_LANTERN.get(), PROPERTIES));
    ColoredDeferredHolder<Item, BlockItem> FANCY_GOLD_LANTERN = new ColoredDeferredHolder<>(BCRegistries.ITEMS, "fancy_gold_lantern", color -> new BlockItem(BCBlocks.FANCY_GOLD_LANTERN.get(color), PROPERTIES));
    RegistryObject<BlockItem>           SOUL_FANCY_GOLD_LANTERN = BCRegistries.ITEMS.register(               "soul_fancy_gold_lantern", () -> new BlockItem(BCBlocks.SOUL_FANCY_GOLD_LANTERN.get(), PROPERTIES));
    RegistryObject<BlockItem>          CLEAR_FANCY_IRON_LANTERN = BCRegistries.ITEMS.register(                    "fancy_iron_lantern", () -> new BlockItem(BCBlocks.CLEAR_FANCY_IRON_LANTERN.get(), PROPERTIES));
    ColoredDeferredHolder<Item, BlockItem> FANCY_IRON_LANTERN = new ColoredDeferredHolder<>(BCRegistries.ITEMS, "fancy_iron_lantern", color -> new BlockItem(BCBlocks.FANCY_IRON_LANTERN.get(color), PROPERTIES));
    RegistryObject<BlockItem>           SOUL_FANCY_IRON_LANTERN = BCRegistries.ITEMS.register(               "soul_fancy_iron_lantern", () -> new BlockItem(BCBlocks.SOUL_FANCY_IRON_LANTERN.get(), PROPERTIES));
    RegistryObject<BlockItem>          CLEAR_TYPEWRITER = BCRegistries.ITEMS.register(                    "typewriter", () -> new BlockItem(BCBlocks.CLEAR_TYPEWRITER.get(), PROPERTIES));
    ColoredDeferredHolder<Item, BlockItem> TYPEWRITER = new ColoredDeferredHolder<>(BCRegistries.ITEMS, "typewriter", color -> new BlockItem(BCBlocks.TYPEWRITER.get(color), PROPERTIES));
    RegistryObject<ClipboardItem>        CLIPBOARD              = BCRegistries.ITEMS.register("clipboard", ClipboardItem::new);
    RegistryObject<BlockItem>            COOKIE_JAR             = registerSimpleBlockItem(BCBlocks.COOKIE_JAR);
    RegistryObject<BlockItem>            DESK_BELL              = registerSimpleBlockItem(BCBlocks.DESK_BELL);
    RegistryObject<BlockItem>            DINNER_PLATE           = registerSimpleBlockItem(BCBlocks.DINNER_PLATE);
    RegistryObject<DiscRackItem>         DISC_RACK              = registerItem("disc_rack", () -> new DiscRackItem(PROPERTIES));
    RegistryObject<DoubleHighBlockItem>  IRON_FANCY_ARMOR_STAND = BCRegistries.ITEMS.register("iron_fancy_armor_stand", () -> new DoubleHighBlockItem(BCBlocks.IRON_FANCY_ARMOR_STAND.get(), PROPERTIES));
    RegistryObject<BlockItem>            GOLD_CHAIN             = registerSimpleBlockItem(BCBlocks.GOLD_CHAIN);
    RegistryObject<BlockItem>            GOLD_LANTERN           = registerSimpleBlockItem(BCBlocks.GOLD_LANTERN);
    RegistryObject<BlockItem>            GOLD_SOUL_LANTERN      = registerSimpleBlockItem(BCBlocks.GOLD_SOUL_LANTERN);
    RegistryObject<BlockItem>            PRINTING_TABLE         = registerSimpleBlockItem(BCBlocks.PRINTING_TABLE);
    RegistryObject<BlockItem>            IRON_PRINTING_TABLE    = registerSimpleBlockItem(BCBlocks.IRON_PRINTING_TABLE);
    RegistryObject<BlockItem>            SWORD_PEDESTAL         = registerSimpleBlockItem("sword_pedestal", BCBlocks.SWORD_PEDESTAL, new Item.Properties().stacksTo(1));
    RegistryObject<BigBookItem>          BIG_BOOK               = BCRegistries.ITEMS.register("big_book", () -> new BigBookItem(false));
    RegistryObject<BigBookItem>          WRITTEN_BIG_BOOK       = BCRegistries.ITEMS.register("written_big_book", () -> new BigBookItem(true));
    RegistryObject<LockAndKeyItem>       LOCK_AND_KEY           = registerItem("lock_and_key", () -> new LockAndKeyItem(PROPERTIES));
    RegistryObject<PlumbLineItem>        PLUMB_LINE             = registerItem("plumb_line", () -> new PlumbLineItem(PROPERTIES));
    RegistryObject<RedstoneBookItem>     REDSTONE_BOOK          = BCRegistries.ITEMS.register("redstone_book", () -> new RedstoneBookItem(new Item.Properties().stacksTo(1)));
    RegistryObject<SlottedBookItem>      SLOTTED_BOOK           = BCRegistries.ITEMS.register("slotted_book", () -> new SlottedBookItem(new Item.Properties().stacksTo(1)));
    RegistryObject<StockroomCatalogItem> STOCKROOM_CATALOG      = BCRegistries.ITEMS.register("stockroom_catalog", () -> new StockroomCatalogItem(new Item.Properties().stacksTo(1)));
    RegistryObject<TapeMeasureItem>      TAPE_MEASURE           = registerItem("tape_measure", () -> new TapeMeasureItem(PROPERTIES));
    RegistryObject<Item>                 TAPE_REEL              = registerSimpleItem("tape_reel");
    RegistryObject<TypewriterPageItem>   TYPEWRITER_PAGE        = BCRegistries.ITEMS.register("typewriter_page", () -> new TypewriterPageItem(new Item.Properties().stacksTo(1)));
    //TODO Hand Drill
    //TODO Screw Gun
    //TODO Monocle
    //TODO Reading Glasses
    //TODO Tinted Glasses
    //TODO Atlas
    //TODO Drafting Compass
    //TODO Painting Canvas
    //TODO Recipe Book
    // @formatter:on

    /**
     * Helper method to register a {@code WoodTypeDeferredHolder<Item, BlockItem>} for a {@code WoodTypeDeferredHolder<Block, ?>}.
     *
     * @param name  The name of the {@link WoodTypeDeferredHolder}.
     * @param block The {@code WoodTypeDeferredHolder<Block, ?>} to use as a base.
     * @return A {@code WoodTypeDeferredHolder<Item, BlockItem>} that represents the blocks in the given {@code WoodTypeDeferredHolder<Block, ?>}.
     */
    static WoodTypeDeferredHolder<Item, BlockItem> woodenBlock(String name, WoodTypeDeferredHolder<Block, ?> block) {
        return woodenBlock(name, wood -> new BlockItem(block.get(wood), PROPERTIES));
    }

    /**
     * Helper method to register a {@code WoodTypeDeferredHolder<Item, BlockItem>}.
     *
     * @param name    The name of the {@link WoodTypeDeferredHolder}.
     * @param creator A function of {@link BibliocraftWoodType} to {@code T} to pass into the {@link WoodTypeDeferredHolder}.
     * @return A {@code WoodTypeDeferredHolder<Item, BlockItem>} that represents the blocks in the given {@code WoodTypeDeferredHolder<Block, ?>}.
     */
    static <T extends Item> WoodTypeDeferredHolder<Item, T> woodenBlock(String name, Function<BibliocraftWoodType, T> creator) {
        return new WoodTypeDeferredHolder<>(BCRegistries.ITEMS, name, creator);
    }

    static RegistryObject<BlockItem> registerSimpleBlockItem(RegistryObject<? extends Block> block) {
        return BCRegistries.ITEMS.register(block.getId().getPath(), () -> new BlockItem(block.get(), PROPERTIES));
    }

    static RegistryObject<BlockItem> registerSimpleBlockItem(String name, RegistryObject<? extends Block> block, Item.Properties props) {
        return BCRegistries.ITEMS.register(name, () -> new BlockItem(block.get(), props));
    }

    static <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> supplier) {
        return BCRegistries.ITEMS.register(name, supplier);
    }

    static RegistryObject<Item> registerSimpleItem(String name) {
        return BCRegistries.ITEMS.register(name, () -> new Item(PROPERTIES));
    }

    /**
     * Empty method, called by {@link BCRegistries#init(net.minecraftforge.eventbus.api.IEventBus)} to classload this class.
     */
    static void init() {
    }
}
