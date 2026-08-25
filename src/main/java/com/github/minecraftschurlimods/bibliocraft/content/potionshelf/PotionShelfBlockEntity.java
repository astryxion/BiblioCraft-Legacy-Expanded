package com.github.minecraftschurlimods.bibliocraft.content.potionshelf;

import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.init.BCTags;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCMenuBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.block.BlockState;

public class PotionShelfBlockEntity extends BCMenuBlockEntity {
    public PotionShelfBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntities.POTION_SHELF.get(), 12, defaultName("potion_shelf"), pos, state);
    }

    @Override
    public Container createMenu(int id, PlayerInventory inventory) {
        return new PotionShelfMenu(id, inventory, this);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (BCTags.Items.contains(BCTags.Items.POTION_SHELF_POTIONS, stack.getItem())) return true;
        // 1.20.1: fallback for vanilla potion items so GUI placement works even if tag fails to load
        return stack.getItem() == Items.POTION || stack.getItem() == Items.SPLASH_POTION || stack.getItem() == Items.LINGERING_POTION
                || stack.getItem() == Items.GLASS_BOTTLE || stack.getItem() == Items.EXPERIENCE_BOTTLE || stack.getItem() == Items.HONEY_BOTTLE
                || stack.getItem() == Items.DRAGON_BREATH;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
