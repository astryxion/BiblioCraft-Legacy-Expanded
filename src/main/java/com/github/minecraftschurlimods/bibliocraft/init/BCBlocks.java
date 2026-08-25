package com.github.minecraftschurlimods.bibliocraft.init;

import com.github.minecraftschurlimods.bibliocraft.content.bookcase.BookcaseBlock;
import com.github.minecraftschurlimods.bibliocraft.content.clipboard.ClipboardBlock;
import com.github.minecraftschurlimods.bibliocraft.content.clock.FancyClockBlock;
import com.github.minecraftschurlimods.bibliocraft.content.clock.GrandfatherClockBlock;
import com.github.minecraftschurlimods.bibliocraft.content.clock.WallFancyClockBlock;
import com.github.minecraftschurlimods.bibliocraft.content.cookiejar.CookieJarBlock;
import com.github.minecraftschurlimods.bibliocraft.content.deskbell.DeskBellBlock;
import com.github.minecraftschurlimods.bibliocraft.content.dinnerplate.DinnerPlateBlock;
import com.github.minecraftschurlimods.bibliocraft.content.discrack.DiscRackBlock;
import com.github.minecraftschurlimods.bibliocraft.content.discrack.WallDiscRackBlock;
import com.github.minecraftschurlimods.bibliocraft.content.displaycase.DisplayCaseBlock;
import com.github.minecraftschurlimods.bibliocraft.content.displaycase.WallDisplayCaseBlock;
import com.github.minecraftschurlimods.bibliocraft.content.fancyarmorstand.FancyArmorStandBlock;
import com.github.minecraftschurlimods.bibliocraft.content.fancycrafter.FancyCrafterBlock;
import com.github.minecraftschurlimods.bibliocraft.content.fancylight.AbstractFancyLightBlock;
import com.github.minecraftschurlimods.bibliocraft.content.fancylight.FancyLampBlock;
import com.github.minecraftschurlimods.bibliocraft.content.fancylight.FancyLanternBlock;
import com.github.minecraftschurlimods.bibliocraft.content.fancysign.FancySignBlock;
import com.github.minecraftschurlimods.bibliocraft.content.fancysign.WallFancySignBlock;
import com.github.minecraftschurlimods.bibliocraft.content.label.LabelBlock;
import com.github.minecraftschurlimods.bibliocraft.content.potionshelf.PotionShelfBlock;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableBlock;
import com.github.minecraftschurlimods.bibliocraft.content.seat.SeatBackBlock;
import com.github.minecraftschurlimods.bibliocraft.content.seat.SeatBlock;
import com.github.minecraftschurlimods.bibliocraft.content.shelf.ShelfBlock;
import com.github.minecraftschurlimods.bibliocraft.content.swordpedestal.SwordPedestalBlock;
import com.github.minecraftschurlimods.bibliocraft.content.table.TableBlock;
import com.github.minecraftschurlimods.bibliocraft.content.toolrack.ToolRackBlock;
import com.github.minecraftschurlimods.bibliocraft.content.typewriter.TypewriterBlock;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.holder.ColoredDeferredHolder;
import com.github.minecraftschurlimods.bibliocraft.util.holder.ColoredWoodTypeDeferredHolder;
import com.github.minecraftschurlimods.bibliocraft.util.holder.WoodTypeDeferredHolder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChainBlock;
import net.minecraft.block.LanternBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.AbstractBlock;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Function;
import java.util.function.Supplier;

public interface BCBlocks {
    // @formatter:off
    WoodTypeDeferredHolder<Block, BookcaseBlock>         BOOKCASE          = woodenBlock("bookcase",          BookcaseBlock::new);
    WoodTypeDeferredHolder<Block, FancyArmorStandBlock>  FANCY_ARMOR_STAND = woodenBlock("fancy_armor_stand", FancyArmorStandBlock::new);
    WoodTypeDeferredHolder<Block, FancyClockBlock>       FANCY_CLOCK       = woodenBlock("fancy_clock",       FancyClockBlock::new);
    WoodTypeDeferredHolder<Block, WallFancyClockBlock>   WALL_FANCY_CLOCK  = woodenBlock("wall_fancy_clock",  WallFancyClockBlock::new);
    WoodTypeDeferredHolder<Block, FancyCrafterBlock>     FANCY_CRAFTER     = woodenBlock("fancy_crafter",     FancyCrafterBlock::new);
    WoodTypeDeferredHolder<Block, FancySignBlock>        FANCY_SIGN        = woodenBlock("fancy_sign",        FancySignBlock::new);
    WoodTypeDeferredHolder<Block, WallFancySignBlock>    WALL_FANCY_SIGN   = woodenBlock("wall_fancy_sign",   WallFancySignBlock::new);
    WoodTypeDeferredHolder<Block, GrandfatherClockBlock> GRANDFATHER_CLOCK = woodenBlock("grandfather_clock", GrandfatherClockBlock::new);
    WoodTypeDeferredHolder<Block, LabelBlock>            LABEL             = woodenBlock("label",             LabelBlock::new);
    WoodTypeDeferredHolder<Block, PotionShelfBlock>      POTION_SHELF      = woodenBlock("potion_shelf",      PotionShelfBlock::new);
    WoodTypeDeferredHolder<Block, ShelfBlock>            SHELF             = woodenBlock("shelf",             ShelfBlock::new);
    WoodTypeDeferredHolder<Block, TableBlock>            TABLE             = woodenBlock("table",             TableBlock::new);
    WoodTypeDeferredHolder<Block, ToolRackBlock>         TOOL_RACK         = woodenBlock("tool_rack",         ToolRackBlock::new);
    ColoredWoodTypeDeferredHolder<Block, DisplayCaseBlock>     DISPLAY_CASE      = coloredWoodenBlock("display_case",      DisplayCaseBlock::new);
    ColoredWoodTypeDeferredHolder<Block, WallDisplayCaseBlock> WALL_DISPLAY_CASE = coloredWoodenBlock("wall_display_case", WallDisplayCaseBlock::new);
    ColoredWoodTypeDeferredHolder<Block, SeatBlock>            SEAT              = coloredWoodenBlock("seat",              SeatBlock::new);
    ColoredWoodTypeDeferredHolder<Block, SeatBackBlock>        SEAT_BACK         = coloredWoodenBlock("seat_back",         SeatBackBlock::new);
    RegistryObject<FancyLampBlock>         CLEAR_FANCY_GOLD_LAMP = BCRegistries.BLOCKS.register("fancy_gold_lamp", () -> new FancyLampBlock(AbstractBlock.Properties.copy(Blocks.GOLD_BLOCK).sound(SoundType.LANTERN).lightLevel(state -> state.getValue(AbstractFancyLightBlock.LIT) ? 15 : 0).noOcclusion()));
    ColoredDeferredHolder<Block, FancyLampBlock> FANCY_GOLD_LAMP = coloredBlock(                "fancy_gold_lamp", () -> new FancyLampBlock(AbstractBlock.Properties.copy(Blocks.GOLD_BLOCK).sound(SoundType.LANTERN).lightLevel(state -> state.getValue(AbstractFancyLightBlock.LIT) ? 15 : 0).noOcclusion()));
    RegistryObject<FancyLampBlock>         CLEAR_FANCY_IRON_LAMP = BCRegistries.BLOCKS.register("fancy_iron_lamp", () -> new FancyLampBlock(AbstractBlock.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.LANTERN).lightLevel(state -> state.getValue(AbstractFancyLightBlock.LIT) ? 15 : 0).noOcclusion()));
    ColoredDeferredHolder<Block, FancyLampBlock> FANCY_IRON_LAMP = coloredBlock(                "fancy_iron_lamp", () -> new FancyLampBlock(AbstractBlock.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.LANTERN).lightLevel(state -> state.getValue(AbstractFancyLightBlock.LIT) ? 15 : 0).noOcclusion()));
    RegistryObject<FancyLanternBlock>      CLEAR_FANCY_GOLD_LANTERN = BCRegistries.BLOCKS.register("fancy_gold_lantern",        () -> new FancyLanternBlock(AbstractBlock.Properties.copy(Blocks.GOLD_BLOCK).sound(SoundType.LANTERN).lightLevel(state -> state.getValue(AbstractFancyLightBlock.LIT) ? 15 : 0).noOcclusion()));
    ColoredDeferredHolder<Block, FancyLanternBlock> FANCY_GOLD_LANTERN = coloredBlock(                "fancy_gold_lantern",        () -> new FancyLanternBlock(AbstractBlock.Properties.copy(Blocks.GOLD_BLOCK).sound(SoundType.LANTERN).lightLevel(state -> state.getValue(AbstractFancyLightBlock.LIT) ? 15 : 0).noOcclusion()));
    RegistryObject<FancyLanternBlock>      SOUL_FANCY_GOLD_LANTERN = BCRegistries.BLOCKS.register("soul_fancy_gold_lantern",   () -> new FancyLanternBlock(AbstractBlock.Properties.copy(Blocks.GOLD_BLOCK).sound(SoundType.LANTERN).lightLevel(state -> state.getValue(AbstractFancyLightBlock.LIT) ? 10 : 0).noOcclusion(), BCUtil.modLoc("buzzier_bees", "small_soul_fire_flame")));
    RegistryObject<FancyLanternBlock>      CLEAR_FANCY_IRON_LANTERN = BCRegistries.BLOCKS.register("fancy_iron_lantern",        () -> new FancyLanternBlock(AbstractBlock.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.LANTERN).lightLevel(state -> state.getValue(AbstractFancyLightBlock.LIT) ? 15 : 0).noOcclusion()));
    ColoredDeferredHolder<Block, FancyLanternBlock> FANCY_IRON_LANTERN = coloredBlock(                "fancy_iron_lantern",        () -> new FancyLanternBlock(AbstractBlock.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.LANTERN).lightLevel(state -> state.getValue(AbstractFancyLightBlock.LIT) ? 15 : 0).noOcclusion()));
    RegistryObject<FancyLanternBlock>      SOUL_FANCY_IRON_LANTERN = BCRegistries.BLOCKS.register("soul_fancy_iron_lantern",   () -> new FancyLanternBlock(AbstractBlock.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.LANTERN).lightLevel(state -> state.getValue(AbstractFancyLightBlock.LIT) ? 10 : 0).noOcclusion(), BCUtil.modLoc("buzzier_bees", "small_soul_fire_flame")));
    RegistryObject<TypewriterBlock>        CLEAR_TYPEWRITER = BCRegistries.BLOCKS.register("typewriter", () -> new TypewriterBlock(AbstractBlock.Properties.copy(Blocks.TERRACOTTA).noOcclusion()));
    ColoredDeferredHolder<Block, TypewriterBlock> TYPEWRITER = coloredBlock(                "typewriter", () -> new TypewriterBlock(AbstractBlock.Properties.copy(Blocks.TERRACOTTA).noOcclusion()));
    RegistryObject<CookieJarBlock>     COOKIE_JAR             = BCRegistries.BLOCKS.register("cookie_jar",             () -> new CookieJarBlock      (AbstractBlock.Properties.copy(Blocks.GLASS)));
    RegistryObject<ClipboardBlock>      CLIPBOARD              = BCRegistries.BLOCKS.register("clipboard",              () -> new ClipboardBlock      (AbstractBlock.Properties.of(net.minecraft.block.material.Material.WOOD).instabreak().sound(SoundType.WOOD)));
    RegistryObject<DeskBellBlock>      DESK_BELL              = BCRegistries.BLOCKS.register("desk_bell",              () -> new DeskBellBlock       (AbstractBlock.Properties.copy(Blocks.IRON_BLOCK).noOcclusion()));
    RegistryObject<DinnerPlateBlock>    DINNER_PLATE           = BCRegistries.BLOCKS.register("dinner_plate",           () -> new DinnerPlateBlock    (AbstractBlock.Properties.copy(Blocks.SMOOTH_QUARTZ).noOcclusion()));
    RegistryObject<DiscRackBlock>       DISC_RACK              = BCRegistries.BLOCKS.register("disc_rack",              () -> new DiscRackBlock       (AbstractBlock.Properties.copy(Blocks.JUKEBOX).noOcclusion()));
    RegistryObject<WallDiscRackBlock>   WALL_DISC_RACK         = BCRegistries.BLOCKS.register("wall_disc_rack",         () -> new WallDiscRackBlock   (AbstractBlock.Properties.copy(Blocks.JUKEBOX).noOcclusion()));
    RegistryObject<FancyArmorStandBlock> IRON_FANCY_ARMOR_STAND = BCRegistries.BLOCKS.register("iron_fancy_armor_stand", () -> new FancyArmorStandBlock(AbstractBlock.Properties.copy(Blocks.IRON_BLOCK).noOcclusion()));
    RegistryObject<ChainBlock>          GOLD_CHAIN             = BCRegistries.BLOCKS.register("gold_chain",             () -> new ChainBlock          (AbstractBlock.Properties.copy(Blocks.CHAIN)));
    RegistryObject<LanternBlock>        GOLD_LANTERN           = BCRegistries.BLOCKS.register("gold_lantern",           () -> new LanternBlock        (AbstractBlock.Properties.copy(Blocks.LANTERN)));
    RegistryObject<LanternBlock>        GOLD_SOUL_LANTERN      = BCRegistries.BLOCKS.register("gold_soul_lantern",      () -> new LanternBlock        (AbstractBlock.Properties.copy(Blocks.SOUL_LANTERN)));
    RegistryObject<PrintingTableBlock>  PRINTING_TABLE         = BCRegistries.BLOCKS.register("printing_table",         () -> new PrintingTableBlock  (AbstractBlock.Properties.copy(Blocks.OAK_PLANKS).noOcclusion()));
    RegistryObject<PrintingTableBlock>  IRON_PRINTING_TABLE    = BCRegistries.BLOCKS.register("iron_printing_table",    () -> new PrintingTableBlock  (AbstractBlock.Properties.copy(Blocks.IRON_BLOCK).noOcclusion()));
    RegistryObject<SwordPedestalBlock>  SWORD_PEDESTAL         = BCRegistries.BLOCKS.register("sword_pedestal",         () -> new SwordPedestalBlock  (AbstractBlock.Properties.copy(Blocks.SMOOTH_STONE).noOcclusion()));
    //TODO Map Frame
    //TODO Painting Frame
    //TODO Painting Press
    //TODO Writing Desk
    // @formatter:on

    /**
     * Registration helper method for {@link WoodTypeDeferredHolder}s.
     *
     * @param suffix  The suffix for the {@link WoodTypeDeferredHolder}.
     * @param creator An adapted creator function for the {@link WoodTypeDeferredHolder}. Passes in a copy of the wood type's associated plank block properties.
     * @param <T>     The type of the block registered.
     * @return A {@code WoodTypeDeferredHolder<Block, T>}.
     */
    static <T extends Block> WoodTypeDeferredHolder<Block, T> woodenBlock(String suffix, Function<AbstractBlock.Properties, T> creator) {
        return new WoodTypeDeferredHolder<>(BCRegistries.BLOCKS, suffix, wood -> creator.apply(wood.properties().get().noOcclusion()));
    }

    /**
     * Registration helper method for {@link ColoredDeferredHolder}s.
     *
     * @param suffix   The suffix for the {@link ColoredDeferredHolder}.
     * @param supplier A supplier for the {@link ColoredDeferredHolder}.
     * @param <T>      The type of the block registered.
     * @return A {@code ColoredDeferredHolder<Block, T>}.
     */
    static <T extends Block> ColoredDeferredHolder<Block, T> coloredBlock(String suffix, Supplier<T> supplier) {
        return new ColoredDeferredHolder<>(BCRegistries.BLOCKS, suffix, color -> supplier.get());
    }

    /**
     * Registration helper method for {@link ColoredWoodTypeDeferredHolder}s.
     *
     * @param suffix  The suffix for the {@link ColoredWoodTypeDeferredHolder}.
     * @param creator An adapted creator function for the {@link ColoredWoodTypeDeferredHolder}. Passes in a copy of the wood type's associated plank block properties.
     * @param <T>     The type of the block registered.
     * @return A {@code WoodTypeDeferredHolder<Block, T>}.
     */
    static <T extends Block> ColoredWoodTypeDeferredHolder<Block, T> coloredWoodenBlock(String suffix, Function<AbstractBlock.Properties, T> creator) {
        return new ColoredWoodTypeDeferredHolder<>(BCRegistries.BLOCKS, suffix, (wood, color) -> creator.apply(wood.properties().get().noOcclusion()));
    }

    /**
     * Empty method, called by {@link BCRegistries#init(net.minecraftforge.eventbus.api.IEventBus)} to classload this class.
     */
    static void init() {
    }
}
