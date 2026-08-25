package com.github.minecraftschurlimods.bibliocraft.content.fancyarmorstand;

import com.github.minecraftschurlimods.bibliocraft.init.BCEntities;
import net.minecraft.util.Direction;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Helper entity for rendering the contents of a {@link FancyArmorStandBlockEntity}. Defers item querying and rotations to the block entity.
 */
public class FancyArmorStandEntity extends ArmorStandEntity {
    private FancyArmorStandBlockEntity blockEntity;

    public FancyArmorStandEntity(EntityType<? extends ArmorStandEntity> entityType, World level) {
        super(entityType, level);
    }

    public FancyArmorStandEntity(World level, FancyArmorStandBlockEntity blockEntity) {
        this(BCEntities.FANCY_ARMOR_STAND.get(), level);
        this.blockEntity = blockEntity;
    }

    @Override
    public boolean isInvisible() {
        return true;
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    @Override
    public float getYHeadRot() {
        if (blockEntity == null) return super.getYHeadRot();
        Direction facing = blockEntity.getBlockState().getValue(FancyArmorStandBlock.FACING);
        return facing.toYRot();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlotType slot) {
        return slot.getType() == EquipmentSlotType.Group.ARMOR && blockEntity != null ? blockEntity.getItem(3 - slot.getIndex()) : super.getItemBySlot(slot);
    }
}
