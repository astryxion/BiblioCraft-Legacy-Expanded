package com.github.minecraftschurlimods.bibliocraft.content.discrack;

import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.init.BCTags;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCMenuBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.block.BlockState;

public class DiscRackBlockEntity extends BCMenuBlockEntity {
    public DiscRackBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntities.DISC_RACK.get(), 9, defaultName("disc_rack"), pos, state);
    }

    @Override
    protected Container createMenu(int id, PlayerInventory inventory) {
        return new DiscRackMenu(id, inventory, this);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (BCTags.Items.contains(BCTags.Items.DISC_RACK_DISCS, stack.getItem())) return true;
        // 1.20.1: fallback so music discs work even if custom tag fails to load (1.21.1 parity)
        return stack.getItem().is(ItemTags.MUSIC_DISCS);
    }
}
