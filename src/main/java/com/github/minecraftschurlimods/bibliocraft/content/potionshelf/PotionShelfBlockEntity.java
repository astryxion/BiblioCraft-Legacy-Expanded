package com.github.minecraftschurlimods.bibliocraft.content.potionshelf;

import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.init.BCTags;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCMenuBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

public class PotionShelfBlockEntity extends BCMenuBlockEntity {
    public PotionShelfBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntities.POTION_SHELF.get(), 12, defaultName("potion_shelf"), pos, state);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new PotionShelfMenu(id, inventory, this);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (stack.is(BCTags.Items.POTION_SHELF_POTIONS)) return true;
        // 1.20.1: fallback for vanilla potion items so GUI placement works even if tag fails to load
        return stack.is(Items.POTION) || stack.is(Items.SPLASH_POTION) || stack.is(Items.LINGERING_POTION)
                || stack.is(Items.GLASS_BOTTLE) || stack.is(Items.EXPERIENCE_BOTTLE) || stack.is(Items.HONEY_BOTTLE)
                || stack.is(Items.DRAGON_BREATH);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
