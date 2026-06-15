package com.github.minecraftschurlimods.bibliocraft.content.fancycrafter;

import com.github.minecraftschurlimods.bibliocraft.init.BCMenus;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCMenu;
import com.github.minecraftschurlimods.bibliocraft.util.slot.HasToggleableSlots;
import com.github.minecraftschurlimods.bibliocraft.util.slot.ToggleableSlot;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

public class FancyCrafterMenu extends BCMenu<FancyCrafterBlockEntity> implements HasToggleableSlots {
    private static final int RESULT_MENU_SLOT = 9;

    private final Player player;
    private FancyCrafterCraftingContainer craftSlots;
    private ResultContainer resultSlots;

    public FancyCrafterMenu(int id, Inventory inventory, FancyCrafterBlockEntity blockEntity) {
        super(BCMenus.FANCY_CRAFTER.get(), id, inventory, blockEntity);
        this.player = inventory.player;
        blockEntity.startOpen(inventory.player);
        updateCraftingResult();
    }

    public FancyCrafterMenu(int id, Inventory inventory, FriendlyByteBuf data) {
        super(BCMenus.FANCY_CRAFTER.get(), id, inventory, data);
        this.player = inventory.player;
        blockEntity.startOpen(inventory.player);
    }

    public FancyCrafterMenu(int id, Inventory inventory, BlockPos pos) {
        super(BCMenus.FANCY_CRAFTER.get(), id, inventory, pos);
        this.player = inventory.player;
        blockEntity.startOpen(inventory.player);
        updateCraftingResult();
    }

    public void updateCraftingResult() {
        if (craftSlots == null || resultSlots == null || !(player.level() instanceof ServerLevel)) return;
        blockEntity.calculateRecipe();
        ItemStack result = blockEntity.getItem(RESULT_MENU_SLOT);
        resultSlots.setItem(0, result);
        RecipeHolder<CraftingRecipe> recipe = blockEntity.getRecipe();
        if (recipe != null) {
            resultSlots.setRecipeUsed(recipe);
        }
        setRemoteSlot(RESULT_MENU_SLOT, result);
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(containerId, incrementStateId(), RESULT_MENU_SLOT, result));
        }
    }

    @Override
    protected void addSlots(Inventory inventory) {
        this.resultSlots = new ResultContainer();
        this.craftSlots = new FancyCrafterCraftingContainer(blockEntity, this);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                addSlot(new ToggleableSlot<>(craftSlots, x + y * 3, 30 + x * 18, 17 + y * 18));
            }
        }
        addSlot(new FancyCrafterResultSlot(inventory.player));
        for (int i = 0; i < 8; i++) {
            addSlot(new Slot(blockEntity, i + 10, 17 + i * 18, 78));
        }
        addInventorySlots(inventory, 8, 110);
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        if (container == craftSlots) {
            updateCraftingResult();
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        blockEntity.stopOpen(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        int slotCount = blockEntity.getContainerSize();
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack originalStack = stack.copy();
        if (index == RESULT_MENU_SLOT) {
            if (!moveItemStackTo(stack, slotCount, slotCount + 36, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(stack, originalStack);
        } else if (index < slotCount) {
            if (!moveItemStackTo(stack, slotCount, slotCount + 36, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < slotCount + 9) {
            if (!moveItemStackToEnabled(stack, 0, RESULT_MENU_SLOT)) {
                return ItemStack.EMPTY;
            }
            if (!moveItemStackTo(stack, RESULT_MENU_SLOT + 1, slotCount, false)) {
                return ItemStack.EMPTY;
            }
            if (!moveItemStackTo(stack, slotCount + 9, slotCount + 36, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < slotCount + 36) {
            if (!moveItemStackToEnabled(stack, 0, RESULT_MENU_SLOT)) {
                return ItemStack.EMPTY;
            }
            if (!moveItemStackTo(stack, RESULT_MENU_SLOT + 1, slotCount, false)) {
                return ItemStack.EMPTY;
            }
            if (!moveItemStackTo(stack, slotCount, slotCount + 9, false)) {
                return ItemStack.EMPTY;
            }
        }
        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() != originalStack.getCount()) {
            slot.onTake(player, stack);
            if (index == RESULT_MENU_SLOT && !stack.isEmpty()) {
                player.drop(stack, false);
            }
        }
        return originalStack;
    }

    @Override
    public void setSlotDisabled(int slot, boolean disabled) {
        if (slot > 8) return;
        blockEntity.setSlotDisabled(slot, disabled);
        updateCraftingResult();
        broadcastChanges();
    }

    @Override
    public boolean isSlotDisabled(int slot) {
        return blockEntity.isSlotDisabled(slot);
    }

    @Override
    public boolean canDisableSlot(int slot) {
        return blockEntity.canDisableSlot(slot);
    }

    @SuppressWarnings({"BooleanMethodIsAlwaysInverted", "SameParameterValue"})
    private boolean moveItemStackToEnabled(ItemStack stack, int start, int end) {
        for (int i = start; i < end; i++) {
            if (!blockEntity.isSlotDisabled(i) && !moveItemStackTo(stack, i, i + 1, false)) {
                return false;
            }
        }
        return true;
    }

    private class FancyCrafterResultSlot extends net.minecraft.world.inventory.ResultSlot {
        FancyCrafterResultSlot(Player owner) {
            super(owner, craftSlots, resultSlots, 0, 124, 35);
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            List<IngredientSnapshot> ingredientsBefore = snapshotRecipeIngredients();
            super.onTake(player, stack);
            if (player.level().isClientSide()) return;
            if (needsIngredientFallback(ingredientsBefore)) {
                blockEntity.consumeCraftingIngredients();
            }
            blockEntity.setItem(RESULT_MENU_SLOT, ItemStack.EMPTY);
            blockEntity.calculateRecipe();
            blockEntity.setChanged();
            updateCraftingResult();
            broadcastChanges();
        }

        private List<IngredientSnapshot> snapshotRecipeIngredients() {
            List<IngredientSnapshot> snapshots = new ArrayList<>();
            CraftingInput.Positioned positioned = craftSlots.asPositionedCraftInput();
            CraftingInput input = positioned.input();
            int left = positioned.left();
            int top = positioned.top();
            for (int row = 0; row < input.height(); row++) {
                for (int col = 0; col < input.width(); col++) {
                    if (input.getItem(col, row).isEmpty()) continue;
                    int slot = col + left + (row + top) * 3;
                    snapshots.add(new IngredientSnapshot(slot, blockEntity.getItem(slot).getCount()));
                }
            }
            return snapshots;
        }

        private boolean needsIngredientFallback(List<IngredientSnapshot> before) {
            for (IngredientSnapshot snapshot : before) {
                if (blockEntity.getItem(snapshot.slot).getCount() >= snapshot.count) {
                    return true;
                }
            }
            return false;
        }

        private record IngredientSnapshot(int slot, int count) {}
    }
}
