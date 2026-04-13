package com.github.minecraftschurlimods.bibliocraft.content.discrack;

import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.init.BCTags;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCMenuBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class DiscRackBlockEntity extends BCMenuBlockEntity {
    public DiscRackBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntities.DISC_RACK.get(), 9, defaultName("disc_rack"), pos, state);
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new DiscRackMenu(id, inventory, this);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (stack.is(BCTags.Items.DISC_RACK_DISCS)) return true;
        // 1.20.1: fallback so music discs work even if custom tag fails to load (1.21.1 parity)
        return stack.is(ItemTags.MUSIC_DISCS);
    }
}
