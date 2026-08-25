package com.github.minecraftschurlimods.bibliocraft.content.toolrack;

import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.init.BCTags;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCMenuBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.block.BlockState;

public class ToolRackBlockEntity extends BCMenuBlockEntity {
    private static final ITag.INamedTag<Item> PICKAXES = ItemTags.createOptional(new ResourceLocation("minecraft", "pickaxes"));
    private static final ITag.INamedTag<Item> AXES = ItemTags.createOptional(new ResourceLocation("minecraft", "axes"));
    private static final ITag.INamedTag<Item> SHOVELS = ItemTags.createOptional(new ResourceLocation("minecraft", "shovels"));
    private static final ITag.INamedTag<Item> HOES = ItemTags.createOptional(new ResourceLocation("minecraft", "hoes"));
    private static final ITag.INamedTag<Item> SWORDS = ItemTags.createOptional(new ResourceLocation("minecraft", "swords"));

    public ToolRackBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntities.TOOL_RACK.get(), 4, defaultName("tool_rack"), pos, state);
    }

    @Override
    public Container createMenu(int id, PlayerInventory inventory) {
        return new ToolRackMenu(id, inventory, this);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (BCTags.Items.contains(BCTags.Items.TOOL_RACK_TOOLS, stack.getItem())) return true;
        Item item = stack.getItem();
        return item instanceof PickaxeItem || item instanceof AxeItem || item instanceof ShovelItem || item instanceof HoeItem || item instanceof SwordItem
                || containsIfBound(PICKAXES, item) || containsIfBound(AXES, item) || containsIfBound(SHOVELS, item) || containsIfBound(HOES, item) || containsIfBound(SWORDS, item);
    }

    /**
     * 1.20.1 TagKey.contains is false when the tag is missing. 1.16.5 optional tags throw if they were never bound.
     */
    private static boolean containsIfBound(ITag.INamedTag<Item> tag, Item item) {
        ITag<Item> resolved = ItemTags.getAllTags().getTag(tag.getName());
        return resolved != null && resolved.contains(item);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
