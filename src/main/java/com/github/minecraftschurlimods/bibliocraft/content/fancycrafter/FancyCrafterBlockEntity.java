package com.github.minecraftschurlimods.bibliocraft.content.fancycrafter;

import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCMenuBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.util.slot.HasToggleableSlots;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.world.World;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class FancyCrafterBlockEntity extends BCMenuBlockEntity implements net.minecraft.tileentity.ITickableTileEntity, HasToggleableSlots {
    private static final String CRAFTING_TICKS_REMAINING_KEY = "crafting_ticks_remaining";
    private static final String DISABLED_SLOTS_KEY = "disabled_slots";
    static final int SLOT_DISABLED = 1;
    static final int SLOT_ENABLED = 0;
    static final int CRAFTING_SLOTS = 9;
    private static final int MAX_CRAFTING_TICKS = 6;
    private final boolean[] disabledSlots = new boolean[CRAFTING_SLOTS];
    private final InvWrapper wrapper = new InvWrapper(this) {
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return slot == 9 ? stack : super.insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return slot == 9 ? ItemStack.EMPTY : super.extractItem(slot, amount, simulate);
        }
    };
    private int craftingTicksRemaining = MAX_CRAFTING_TICKS;
    private ICraftingRecipe recipe;

    public FancyCrafterBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntities.FANCY_CRAFTER.get(), 18, defaultName("fancy_crafter"), pos, state);
    }

    @Override
    public void tick() {
        // 1.20.1 getTicker is server-only; ITickableTileEntity runs on both sides in 1.16.5.
        if (level != null && !level.isClientSide()) {
            tick(level, getBlockPos(), getBlockState(), this);
        }
    }

    public int getRedstoneSignal() {
        int count = 0;
        for (int i = 0; i < CRAFTING_SLOTS; i++) {
            if (!getItem(i).isEmpty() || isSlotDisabled(i)) {
                count++;
            }
        }
        return count;
    }

    public static void tick(World level, BlockPos pos, BlockState state, FancyCrafterBlockEntity blockEntity) {
        if (!state.getValue(FancyCrafterBlock.POWERED)) return;
        if (blockEntity.recipe == null) return;
        ICraftingRecipe recipe = blockEntity.recipe;
        ItemStack result = recipe.getResultItem();
        ItemStack resultStack = blockEntity.getItem(9);
        if (!resultStack.isEmpty() && (!(ItemStack.isSame(result, resultStack) && ItemStack.tagMatches(result, resultStack)) || result.getCount() + resultStack.getCount() > result.getMaxStackSize()))
            return;
        blockEntity.craftingTicksRemaining--;
        if (blockEntity.craftingTicksRemaining > 0) return;
        CraftingInventory input = blockEntity.createCraftingInput(blockEntity.getInputs());
        ItemStack assembled = recipe.assemble(input);
        blockEntity.setItem(9, blockEntity.tryDispense(level, pos, assembled, state));
        blockEntity.craftingTicksRemaining = MAX_CRAFTING_TICKS;
        recipe.getRemainingItems(input).stream()
                .filter(e -> !e.isEmpty())
                .forEach(e -> blockEntity.tryDispense(level, pos, e, state));
        List<ItemStack> inputs = new ArrayList<>(blockEntity.getInputs()
                .stream()
                .filter(e -> !e.isEmpty())
                .collect(java.util.stream.Collectors.toList()));
        // for loop instead of stream chain to prevent CME
        for (int i = 10; i < 18; i++) {
            ItemStack stack = blockEntity.getItem(i);
            if (stack.isEmpty()) continue;
            List<ItemStack> toRemove = new ArrayList<>();
            for (ItemStack e : inputs) {
                if (!(ItemStack.isSame(e, stack) && ItemStack.tagMatches(e, stack))) continue;
                if (e.getCount() >= e.getMaxStackSize()) continue;
                if (stack.isEmpty()) continue;
                e.grow(1);
                toRemove.add(e);
                stack.shrink(1);
            }
            toRemove.forEach(inputs::remove);
        }
        blockEntity.getInputs()
                .stream()
                .filter(e -> !e.isEmpty())
                .forEach(e -> e.shrink(1));
        blockEntity.setChanged();
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (isSlotDisabled(slot) && !stack.isEmpty()) {
            setSlotDisabled(slot, false);
        }
        super.setItem(slot, stack);
        calculateRecipe();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (isSlotDisabled(slot)) return false;
        if (stack.hasContainerItem()) return false;
        ItemStack slotStack = getItem(slot);
        return slotStack.isEmpty() || slotStack.getCount() < slotStack.getMaxStackSize() && !smallerStackExists(slotStack.getCount(), stack, slot);
    }

    @Override
    public boolean canDisableSlot(int slot) {
        return isCraftingSlot(slot) && getItem(slot).isEmpty();
    }

    @Override
    public boolean isSlotDisabled(int slot) {
        return isCraftingSlot(slot) && disabledSlots[slot];
    }

    @Override
    public void setSlotDisabled(int slot, boolean disabled) {
        if (!canDisableSlot(slot)) return;
        disabledSlots[slot] = disabled;
        setChanged();
    }

    @Override
    protected Container createMenu(int id, PlayerInventory inventory) {
        return new FancyCrafterMenu(id, inventory, this);
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        craftingTicksRemaining = tag.getInt(CRAFTING_TICKS_REMAINING_KEY);
        for (int i = 0; i < CRAFTING_SLOTS; i++) {
            disabledSlots[i] = false;
        }
        if (tag.contains(DISABLED_SLOTS_KEY)) {
            for (int slot : tag.getIntArray(DISABLED_SLOTS_KEY)) {
                if (canDisableSlot(slot)) {
                    disabledSlots[slot] = true;
                }
            }
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        super.save(tag);
        tag.putInt(CRAFTING_TICKS_REMAINING_KEY, craftingTicksRemaining);
        java.util.List<Integer> disabled = new java.util.ArrayList<>();
        for (int i = 0; i < CRAFTING_SLOTS; i++) {
            if (isSlotDisabled(i)) {
                disabled.add(i);
            }
        }
        int[] stored = new int[disabled.size()];
        for (int i = 0; i < disabled.size(); i++) {
            stored[i] = disabled.get(i);
        }
        tag.putIntArray(DISABLED_SLOTS_KEY, stored);
        return tag;
    }

    @Override
    public IItemHandler getItemCapability(@Nullable Direction side) {
        return wrapper;
    }

    private boolean isCraftingSlot(int slot) {
        return slot >= 0 && slot < 9;
    }

    private boolean smallerStackExists(int currentSize, ItemStack stack, int slot) {
        for (int i = slot + 1; i < 9; i++) {
            if (isSlotDisabled(i)) continue;
            ItemStack slotStack = getItem(i);
            if (slotStack.isEmpty() || slotStack.getCount() < currentSize && (ItemStack.isSame(slotStack, stack) && ItemStack.tagMatches(slotStack, stack)))
                return true;
        }
        return false;
    }

    private CraftingInventory createCraftingInput(List<ItemStack> items) {
        CraftingInventory inv = new CraftingInventory(new Container((ContainerType<?>) null, -1) {
            @Override
            public boolean stillValid(PlayerEntity player) {
                return false;
            }
        }, 3, 3);
        for (int i = 0; i < 9 && i < items.size(); i++) {
            inv.setItem(i, items.get(i));
        }
        return inv;
    }

    private void calculateRecipe() {
        if (level() == null) return;
        RecipeManager recipes = level().getRecipeManager();
        CraftingInventory input = createCraftingInput(getInputs());
        recipe = recipes.getRecipeFor(IRecipeType.CRAFTING, input, level()).orElse(null);
        items.setStackInSlot(9, recipe == null ? ItemStack.EMPTY : recipe.getResultItem().copy());
    }

    private ItemStack tryDispense(World level, BlockPos pos, ItemStack stack, BlockState state) {
        Direction direction = state.getValue(FancyCrafterBlock.FACING);
        stack = BCUtil.tryInsert(level, pos, direction, stack, this);
        if (!stack.isEmpty() && !level.isClientSide() && level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty()) {
            Vector3d vec3 = Vector3d.atCenterOf(pos.above());
            ItemEntity entity = new ItemEntity(level, vec3.x(), vec3.y(), vec3.z(), stack);
            level.addFreshEntity(entity);
            level.playSound(null, pos, SoundEvents.DISPENSER_DISPENSE, SoundCategory.BLOCKS, 1, 1);
            return ItemStack.EMPTY;
        }
        return stack;
    }

    private List<ItemStack> getInputs() {
        return IntStream.range(0, 9).mapToObj(this::getItem).collect(java.util.stream.Collectors.toList());
    }

    /**
     * Consumes one set of recipe ingredients from the crafting grid (slots 0–8)
     * and applies remaining items (e.g. empty bucket). Call when the player takes the result.
     */
    public void consumeIngredientsForResult() {
        if (recipe == null || level() == null) return;
        CraftingInventory input = createCraftingInput(getInputs());
        List<ItemStack> remaining = recipe.getRemainingItems(input);
        for (int i = 0; i < 9 && i < remaining.size(); i++) {
            setItem(i, remaining.get(i));
        }
        setChanged();
        calculateRecipe();
    }
}
