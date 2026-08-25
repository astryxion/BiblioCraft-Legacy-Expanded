package com.github.minecraftschurlimods.bibliocraft.content.fancyarmorstand;

import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCMenuBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.block.BlockState;
import javax.annotation.Nullable;

public class FancyArmorStandBlockEntity extends BCMenuBlockEntity {
    private FancyArmorStandEntity entity;

    public FancyArmorStandBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntities.FANCY_ARMOR_STAND.get(), 4, defaultName("fancy_armor_stand"), pos, state);
    }

    @Override
    public void setLevelAndPosition(World level, BlockPos pos) {
        super.setLevelAndPosition(level, pos);
        entity = new FancyArmorStandEntity(level, this);
    }

    @Override
    public Container createMenu(int id, PlayerInventory inventory) {
        return new FancyArmorStandMenu(id, inventory, this);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        entity.remove();
        entity = null;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        if (stack.isEmpty()) return true;
        EquipmentSlotType slot = stack.getEquipmentSlot();
        if (slot == null && stack.getItem() instanceof ArmorItem) {
            slot = ((ArmorItem) stack.getItem()).getSlot();
        }
        if (slot == null) return false;
        return slot.getType() == EquipmentSlotType.Group.ARMOR && slot.getIndex() == 3 - index && super.canPlaceItem(index, stack);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    /**
     * @return The {@link FancyArmorStandEntity} used for actually displaying the armor.
     */
    @Nullable
    public FancyArmorStandEntity getDisplayEntity() {
        return entity;
    }
}
