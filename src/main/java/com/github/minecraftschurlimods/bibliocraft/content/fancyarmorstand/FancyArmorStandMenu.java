package com.github.minecraftschurlimods.bibliocraft.content.fancyarmorstand;

import com.github.minecraftschurlimods.bibliocraft.init.BCMenus;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCMenu;
import net.minecraft.network.PacketBuffer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import javax.annotation.Nullable;

public class FancyArmorStandMenu extends BCMenu<FancyArmorStandBlockEntity> {
    public FancyArmorStandMenu(int id, PlayerInventory inventory, FancyArmorStandBlockEntity blockEntity) {
        super(BCMenus.FANCY_ARMOR_STAND.get(), id, inventory, blockEntity);
    }

    public FancyArmorStandMenu(int id, PlayerInventory inventory, PacketBuffer buf) {
        super(BCMenus.FANCY_ARMOR_STAND.get(), id, inventory, buf);
    }

    @Override
    protected void addSlots(PlayerInventory inventory) {
        addSlot(new EquipableSlot(blockEntity, 0, 80, 8, EquipmentSlotType.HEAD));
        addSlot(new EquipableSlot(blockEntity, 1, 80, 26, EquipmentSlotType.CHEST));
        addSlot(new EquipableSlot(blockEntity, 2, 80, 44, EquipmentSlotType.LEGS));
        addSlot(new EquipableSlot(blockEntity, 3, 80, 62, EquipmentSlotType.FEET));
        addInventorySlots(inventory, 8, 84);
        addSlot(new EquipableSlot(inventory, 39, 126, 8, EquipmentSlotType.HEAD));
        addSlot(new EquipableSlot(inventory, 38, 126, 26, EquipmentSlotType.CHEST));
        addSlot(new EquipableSlot(inventory, 37, 126, 44, EquipmentSlotType.LEGS));
        addSlot(new EquipableSlot(inventory, 36, 126, 62, EquipmentSlotType.FEET));
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        Slot slot = slots.get(index);
        int slotCount = blockEntity.getContainerSize();
        ItemStack stack = slot.getItem();
        ItemStack originalStack = stack.copy();
        if (index < slotCount) { // If slot is an armor stand slot
            // Try moving to the inventory armor
            ItemStack invArmorStack = tryMoveToInvArmor(index);
            if (invArmorStack != null) return invArmorStack;
            // Try moving to the hotbar or inventory
            if (!moveItemStackTo(slot.getItem(), slotCount, slotCount + 36, false)) return ItemStack.EMPTY;
        } else if (index > slotCount + 35) { // If slot is an inventory armor slot
            // Try moving to the armor stand
            ItemStack armorStandStack = tryMoveToArmorStand(index);
            if (armorStandStack != null) return armorStandStack;
            // Try moving to the hotbar or inventory
            if (!moveItemStackTo(slot.getItem(), slotCount, slotCount + 36, false)) return ItemStack.EMPTY;
        } else if (index < slotCount + 9) { // If slot is a hotbar slot
            // Try moving to the armor stand
            ItemStack tryMoveArmorStand = tryMoveToArmorStand(index);
            if (tryMoveArmorStand != null) return tryMoveArmorStand;
            // Try moving to the inventory armor
            ItemStack invArmorStack = tryMoveToInvArmor(index);
            if (invArmorStack != null) return invArmorStack;
            // Try moving to the inventory
            if (!moveItemStackTo(stack, slotCount + 9, slotCount + 36, false)) return ItemStack.EMPTY;
        } else if (index < slotCount + 36) { // If slot is an inventory slot
            // Try moving to the armor stand
            ItemStack tryMoveArmorStand = tryMoveToArmorStand(index);
            if (tryMoveArmorStand != null) return tryMoveArmorStand;
            // Try moving to the inventory armor
            ItemStack invArmorStack = tryMoveToInvArmor(index);
            if (invArmorStack != null) return invArmorStack;
            // Try moving to the hotbar
            if (!moveItemStackTo(stack, slotCount, slotCount + 9, false)) return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return originalStack;
    }

    @Nullable
    private ItemStack tryMoveToInvArmor(int index) {
        ItemStack stack = slots.get(index).getItem();
        for (int i = 40; i < 44; i++) {
            Slot beSlot = slots.get(i);
            if (!beSlot.hasItem() && beSlot.mayPlace(stack) && !moveItemStackTo(stack, i, i + 1, false))
                return ItemStack.EMPTY;
        }
        return null;
    }

    @Nullable
    private ItemStack tryMoveToArmorStand(int index) {
        ItemStack stack = slots.get(index).getItem();
        for (int i = 0; i < 4; i++) {
            Slot beSlot = slots.get(i);
            if (!beSlot.hasItem() && beSlot.mayPlace(stack) && !moveItemStackTo(stack, i, i + 1, false))
                return ItemStack.EMPTY;
        }
        return null;
    }

    /**
     * Variant of {@link Slot} that only accepts items that go in a given {@link EquipmentSlotType}.
     */
    private static class EquipableSlot extends Slot {
        private final EquipmentSlotType slotType;

        /**
         * @param container The {@link IInventory} this slot is in.
         * @param index     The slot index.
         * @param x         The x position.
         * @param y         The y position.
         * @param slotType  The {@link EquipmentSlotType} to limit slot contents with.
         */
        public EquipableSlot(IInventory container, int index, int x, int y, EquipmentSlotType slotType) {
            super(container, index, x, y);
            this.slotType = slotType;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            EquipmentSlotType slot = stack.getEquipmentSlot();
            if (slot == null && stack.getItem() instanceof ArmorItem) {
                slot = ((ArmorItem) stack.getItem()).getSlot();
            }
            return slot == slotType;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }
}
