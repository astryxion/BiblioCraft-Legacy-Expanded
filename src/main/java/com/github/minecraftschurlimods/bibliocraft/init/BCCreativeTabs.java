package com.github.minecraftschurlimods.bibliocraft.init;

import net.minecraft.util.text.TranslationTextComponent;
import com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi;
import com.github.minecraftschurlimods.bibliocraft.content.swordpedestal.SwordPedestalBlock;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;

import java.util.Collection;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public interface BCCreativeTabs {
    /**
     * 1.16.5 equivalent of CreativeModeTab.builder(): static ItemGroup with the same icon, title, and item list.
     * Search bar is not a 1.16.5 ItemGroup feature; items and order are unchanged.
     */
    ItemGroup BIBLIOCRAFT_TAB = new ItemGroup(BibliocraftApi.MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(BCItems.BOOKCASE.get(BibliocraftApi.getWoodTypeRegistry().get(BCUtil.mcLoc("oak"))));
        }

        @Override
        public net.minecraft.util.text.ITextComponent getDisplayName() {
            return new TranslationTextComponent("itemGroup." + BibliocraftApi.MOD_ID);
        }

        @Override
        public void fillItemList(NonNullList<ItemStack> output) {
            addToTab(output, BCItems.BOOKCASE.values());
            addToTab(output, BCItems.FANCY_ARMOR_STAND.values());
            addToTab(output, BCItems.FANCY_CLOCK.values());
            addToTab(output, BCItems.FANCY_CRAFTER.values());
            addToTab(output, BCItems.FANCY_SIGN.values());
            addToTab(output, BCItems.GRANDFATHER_CLOCK.values());
            addToTab(output, BCItems.LABEL.values());
            addToTab(output, BCItems.POTION_SHELF.values());
            addToTab(output, BCItems.SHELF.values());
            addToTab(output, BCItems.TABLE.values());
            addToTab(output, BCItems.TOOL_RACK.values());
            addToTab(output, BCItems.DISPLAY_CASE.values());
            addToTab(output, BCItems.SEAT.values());
            addToTab(output, BCItems.SMALL_SEAT_BACK.values());
            addToTab(output, BCItems.RAISED_SEAT_BACK.values());
            addToTab(output, BCItems.FLAT_SEAT_BACK.values());
            addToTab(output, BCItems.TALL_SEAT_BACK.values());
            addToTab(output, BCItems.FANCY_SEAT_BACK.values());
            output.add(new ItemStack(BCItems.CLEAR_FANCY_GOLD_LAMP.get()));
            addToTab(output, BCItems.FANCY_GOLD_LAMP.values());
            output.add(new ItemStack(BCItems.CLEAR_FANCY_IRON_LAMP.get()));
            addToTab(output, BCItems.FANCY_IRON_LAMP.values());
            output.add(new ItemStack(BCItems.CLEAR_FANCY_GOLD_LANTERN.get()));
            addToTab(output, BCItems.FANCY_GOLD_LANTERN.values());
            output.add(new ItemStack(BCItems.SOUL_FANCY_GOLD_LANTERN.get()));
            output.add(new ItemStack(BCItems.CLEAR_FANCY_IRON_LANTERN.get()));
            addToTab(output, BCItems.FANCY_IRON_LANTERN.values());
            output.add(new ItemStack(BCItems.SOUL_FANCY_IRON_LANTERN.get()));
            output.add(new ItemStack(BCItems.CLEAR_TYPEWRITER.get()));
            addToTab(output, BCItems.TYPEWRITER.values());
            output.add(new ItemStack(BCItems.CLIPBOARD.get()));
            output.add(new ItemStack(BCItems.COOKIE_JAR.get()));
            output.add(new ItemStack(BCItems.DESK_BELL.get()));
            output.add(new ItemStack(BCItems.DINNER_PLATE.get()));
            output.add(new ItemStack(BCItems.DISC_RACK.get()));
            output.add(new ItemStack(BCItems.IRON_FANCY_ARMOR_STAND.get()));
            output.add(new ItemStack(BCItems.GOLD_CHAIN.get()));
            output.add(new ItemStack(BCItems.GOLD_LANTERN.get()));
            output.add(new ItemStack(BCItems.GOLD_SOUL_LANTERN.get()));
            output.add(new ItemStack(BCItems.PRINTING_TABLE.get()));
            output.add(new ItemStack(BCItems.IRON_PRINTING_TABLE.get()));
            for (DyeColor color : DyeColor.values()) {
                ItemStack stack = new ItemStack(BCItems.SWORD_PEDESTAL.get());
                SwordPedestalBlock.DyedColor.putOnStack(stack, new SwordPedestalBlock.DyedColor(color.getTextColor(), true));
                output.add(stack);
            }
            output.add(new ItemStack(BCItems.BIG_BOOK.get()));
            output.add(new ItemStack(BCItems.LOCK_AND_KEY.get()));
            output.add(new ItemStack(BCItems.PLUMB_LINE.get()));
            output.add(new ItemStack(BCItems.REDSTONE_BOOK.get()));
            output.add(new ItemStack(BCItems.SLOTTED_BOOK.get()));
            output.add(new ItemStack(BCItems.STOCKROOM_CATALOG.get()));
            output.add(new ItemStack(BCItems.TAPE_MEASURE.get()));
            output.add(new ItemStack(BCItems.TAPE_REEL.get()));
        }
    };

    Supplier<ItemGroup> BIBLIOCRAFT = () -> BIBLIOCRAFT_TAB;

    /**
     * Helper method to add all {@link IItemProvider}s in a list to a creative tab.
     *
     * @param output The list to add the elements to.
     * @param list   A list of {@link IItemProvider}s to add.
     */
    static void addToTab(NonNullList<ItemStack> output, Collection<? extends IItemProvider> list) {
        for (IItemProvider item : list) {
            output.add(new ItemStack(item));
        }
    }

    /**
     * Empty method, called by {@link BCRegistries#init(net.minecraftforge.eventbus.api.IEventBus)} to classload this class.
     */
    static void init() {
    }
}
