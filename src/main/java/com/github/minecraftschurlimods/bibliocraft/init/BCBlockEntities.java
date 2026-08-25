package com.github.minecraftschurlimods.bibliocraft.init;

import com.github.minecraftschurlimods.bibliocraft.content.bookcase.BookcaseBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.content.clipboard.ClipboardBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.content.clock.ClockBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.content.cookiejar.CookieJarBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.content.dinnerplate.DinnerPlateBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.content.discrack.DiscRackBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.content.displaycase.DisplayCaseBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.content.fancyarmorstand.FancyArmorStandBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.content.fancycrafter.FancyCrafterBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.content.fancysign.FancySignBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.content.label.LabelBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.content.potionshelf.PotionShelfBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.content.shelf.ShelfBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.content.swordpedestal.SwordPedestalBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.content.table.TableBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.content.toolrack.ToolRackBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.content.typewriter.TypewriterBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public interface BCBlockEntities {
    // @formatter:off
    Supplier<TileEntityType<BookcaseBlockEntity>>        BOOKCASE          = register("bookcase",          BookcaseBlockEntity::new,        BCBlocks.BOOKCASE.holders());
    Supplier<TileEntityType<ClipboardBlockEntity>>       CLIPBOARD         = register("clipboard",         ClipboardBlockEntity::new,       BCBlocks.CLIPBOARD);
    Supplier<TileEntityType<ClockBlockEntity>>           CLOCK             = register("clock",             ClockBlockEntity::new,           BCUtil.mergeRaw(BCBlocks.FANCY_CLOCK.holders(), BCBlocks.WALL_FANCY_CLOCK.holders(), BCBlocks.GRANDFATHER_CLOCK.holders()));
    Supplier<TileEntityType<CookieJarBlockEntity>>       COOKIE_JAR        = register("cookie_jar",        CookieJarBlockEntity::new,       BCBlocks.COOKIE_JAR);
    Supplier<TileEntityType<DinnerPlateBlockEntity>>     DINNER_PLATE      = register("dinner_plate",      DinnerPlateBlockEntity::new,     BCBlocks.DINNER_PLATE);
    Supplier<TileEntityType<DiscRackBlockEntity>>        DISC_RACK         = register("disc_rack",         DiscRackBlockEntity::new,        BCBlocks.DISC_RACK, BCBlocks.WALL_DISC_RACK);
    Supplier<TileEntityType<DisplayCaseBlockEntity>>     DISPLAY_CASE      = register("display_case",      DisplayCaseBlockEntity::new,     BCUtil.mergeRaw(BCBlocks.DISPLAY_CASE.holders(), BCBlocks.WALL_DISPLAY_CASE.holders()));
    Supplier<TileEntityType<FancyArmorStandBlockEntity>> FANCY_ARMOR_STAND = register("fancy_armor_stand", FancyArmorStandBlockEntity::new, BCUtil.merge(BCBlocks.FANCY_ARMOR_STAND.holders(), BCBlocks.IRON_FANCY_ARMOR_STAND));
    Supplier<TileEntityType<FancyCrafterBlockEntity>>    FANCY_CRAFTER     = register("fancy_crafter",     FancyCrafterBlockEntity::new,    BCBlocks.FANCY_CRAFTER.holders());
    Supplier<TileEntityType<FancySignBlockEntity>>       FANCY_SIGN        = register("fancy_sign",        FancySignBlockEntity::new,       BCUtil.mergeRaw(BCBlocks.FANCY_SIGN.holders(), BCBlocks.WALL_FANCY_SIGN.holders()));
    Supplier<TileEntityType<LabelBlockEntity>>           LABEL             = register("label",             LabelBlockEntity::new,           BCBlocks.LABEL.holders());
    Supplier<TileEntityType<PotionShelfBlockEntity>>     POTION_SHELF      = register("potion_shelf",      PotionShelfBlockEntity::new,     BCBlocks.POTION_SHELF.holders());
    Supplier<TileEntityType<PrintingTableBlockEntity>>   PRINTING_TABLE    = register("printing_table",    PrintingTableBlockEntity::new,   BCBlocks.PRINTING_TABLE, BCBlocks.IRON_PRINTING_TABLE);
    Supplier<TileEntityType<ShelfBlockEntity>>           SHELF             = register("shelf",             ShelfBlockEntity::new,           BCBlocks.SHELF.holders());
    Supplier<TileEntityType<SwordPedestalBlockEntity>>   SWORD_PEDESTAL    = register("sword_pedestal",    SwordPedestalBlockEntity::new,   BCBlocks.SWORD_PEDESTAL);
    Supplier<TileEntityType<TableBlockEntity>>           TABLE             = register("table",             TableBlockEntity::new,           BCBlocks.TABLE.holders());
    Supplier<TileEntityType<ToolRackBlockEntity>>        TOOL_RACK         = register("tool_rack",         ToolRackBlockEntity::new,        BCBlocks.TOOL_RACK.holders());
    Supplier<TileEntityType<TypewriterBlockEntity>>      TYPEWRITER        = register("typewriter",        TypewriterBlockEntity::new,      BCUtil.merge(BCBlocks.TYPEWRITER.holders(), BCBlocks.CLEAR_TYPEWRITER));
    // @formatter:on

    /**
     * Registration helper method that takes a supplier list instead of a vararg parameter.
     *
     * @param name     The registry name to use.
     * @param supplier The block entity supplier to use.
     * @param blocks   A list of block suppliers that are associated with the block entity.
     * @param <T>      The exact type of the block entity.
     * @return A block entity type supplier.
     */
    @SuppressWarnings("DataFlowIssue")
    static <T extends TileEntity> Supplier<TileEntityType<T>> register(String name, java.util.function.BiFunction<net.minecraft.util.math.BlockPos, net.minecraft.block.BlockState, T> supplier, Collection<? extends Supplier<? extends Block>> blocks) {
        return BCRegistries.BLOCK_ENTITIES.register(name, () -> TileEntityType.Builder.of(() -> supplier.apply(net.minecraft.util.math.BlockPos.ZERO, null), blocks.stream().map(Supplier::get).collect(java.util.stream.Collectors.toList()).toArray(new Block[0])).build(null));
    }

    /**
     * Registration helper method that takes a supplier vararg parameter instead of a regular vararg parameter.
     *
     * @param name     The registry name to use.
     * @param supplier The block entity supplier to use.
     * @param blocks   A vararg of block suppliers that are associated with the block entity.
     * @param <T>      The exact type of the block entity.
     * @return A block entity type supplier.
     */
    @SafeVarargs
    static <T extends TileEntity> Supplier<TileEntityType<T>> register(String name, java.util.function.BiFunction<net.minecraft.util.math.BlockPos, net.minecraft.block.BlockState, T> supplier, Supplier<? extends Block>... blocks) {
        return register(name, supplier, java.util.Arrays.asList(blocks));
    }

    /**
     * Empty method, called by {@link BCRegistries#init(net.neoforged.bus.api.IEventBus)} to classload this class.
     */
    static void init() {
    }
}
