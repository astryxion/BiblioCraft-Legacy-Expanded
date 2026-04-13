package com.github.minecraftschurlimods.bibliocraft.content.bookcase;

import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.init.BCTags;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCMenuBlockEntity;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import com.github.minecraftschurlimods.bibliocraft.client.model.BlockModelData;
import com.github.minecraftschurlimods.bibliocraft.client.model.BlockModelData.BlockModelProperty;

import java.util.ArrayList;
import java.util.List;

public class BookcaseBlockEntity extends BCMenuBlockEntity {
    public static final List<BlockModelProperty<Boolean>> MODEL_PROPERTIES = Util.make(new ArrayList<>(), list -> {
        for (int i = 0; i < 16; i++) {
            list.add(new BlockModelProperty<>());
        }
    });

    public BookcaseBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntities.BOOKCASE.get(), 16, defaultName("bookcase"), pos, state);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new BookcaseMenu(id, inventory, this);
    }

    public BlockModelData getBlockModelData() {
        BlockModelData.Builder builder = BlockModelData.builder();
        for (int i = 0; i < MODEL_PROPERTIES.size(); i++) {
            builder.with(MODEL_PROPERTIES.get(i), !items.getStackInSlot(i).isEmpty());
        }
        return builder.build();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return stack.is(BCTags.Items.BOOKCASE_BOOKS);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        super.setItem(slot, stack);
        if (level() != null) {
            level().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
