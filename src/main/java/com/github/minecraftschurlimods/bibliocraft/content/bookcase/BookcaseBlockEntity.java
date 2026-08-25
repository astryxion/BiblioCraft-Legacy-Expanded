package com.github.minecraftschurlimods.bibliocraft.content.bookcase;

import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.init.BCTags;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCMenuBlockEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;

import java.util.ArrayList;
import java.util.List;

public class BookcaseBlockEntity extends BCMenuBlockEntity {
    public static final List<ModelProperty<Boolean>> MODEL_PROPERTIES = Util.make(new ArrayList<>(), list -> {
        for (int i = 0; i < 16; i++) {
            list.add(new ModelProperty<>());
        }
    });

    public BookcaseBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntities.BOOKCASE.get(), 16, defaultName("bookcase"), pos, state);
    }

    @Override
    public Container createMenu(int id, PlayerInventory inventory) {
        return new BookcaseMenu(id, inventory, this);
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.handleUpdateTag(state, tag);
        requestModelDataUpdate();
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        level().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        requestModelDataUpdate();
    }

    @Override
    public IModelData getModelData() {
        ModelDataMap.Builder builder = new ModelDataMap.Builder();
        for (int i = 0; i < MODEL_PROPERTIES.size(); i++) {
            builder.withInitial(MODEL_PROPERTIES.get(i), !items.getStackInSlot(i).isEmpty());
        }
        return builder.build();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (BCTags.Items.contains(BCTags.Items.BOOKCASE_BOOKS, stack.getItem())) return true;
        return stack.getItem() == Items.BOOK || stack.getItem() == Items.ENCHANTED_BOOK;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        super.setItem(slot, stack);
        requestModelDataUpdate();
        World level = getLevel();
        if (level != null && !level.isClientSide()) {
            level.updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
        }
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
