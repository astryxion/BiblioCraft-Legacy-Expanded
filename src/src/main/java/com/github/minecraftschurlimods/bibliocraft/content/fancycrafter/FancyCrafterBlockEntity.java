package com.github.minecraftschurlimods.bibliocraft.content.fancycrafter;

import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCMenuBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class FancyCrafterBlockEntity extends BCMenuBlockEntity {
    private static final String CRAFTING_TICKS_REMAINING_KEY = "crafting_ticks_remaining";
    private static final int MAX_CRAFTING_TICKS = 6;
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
    private CraftingRecipe recipe;

    public FancyCrafterBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntities.FANCY_CRAFTER.get(), 18, defaultName("fancy_crafter"), pos, state);
    }

    public int getRedstoneSignal() {
        return (int) IntStream.range(0, 9).filter(i -> !getItem(i).isEmpty()).count();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FancyCrafterBlockEntity blockEntity) {
        if (blockEntity.recipe == null) return;
        CraftingRecipe recipe = blockEntity.recipe;
        ItemStack result = recipe.getResultItem(level.registryAccess());
        ItemStack resultStack = blockEntity.getItem(9);
        if (!resultStack.isEmpty() && (!ItemStack.isSameItemSameTags(result, resultStack) || result.getCount() + resultStack.getCount() > result.getMaxStackSize()))
            return;
        blockEntity.craftingTicksRemaining--;
        if (blockEntity.craftingTicksRemaining > 0) return;
        CraftingContainer input = blockEntity.createCraftingInput(blockEntity.getInputs());
        ItemStack assembled = recipe.assemble(input, level.registryAccess());
        blockEntity.setItem(9, blockEntity.tryDispense(level, pos, assembled, state));
        blockEntity.craftingTicksRemaining = MAX_CRAFTING_TICKS;
        recipe.getRemainingItems(input).stream()
                .filter(e -> !e.isEmpty())
                .forEach(e -> blockEntity.tryDispense(level, pos, e, state));
        List<ItemStack> inputs = new ArrayList<>(blockEntity.getInputs()
                .stream()
                .filter(e -> !e.isEmpty())
                .toList());
        // for loop instead of stream chain to prevent CME
        for (int i = 10; i < 18; i++) {
            ItemStack stack = blockEntity.getItem(i);
            if (stack.isEmpty()) continue;
            List<ItemStack> toRemove = new ArrayList<>();
            for (ItemStack e : inputs) {
                if (!ItemStack.isSameItemSameTags(e, stack)) continue;
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
        super.setItem(slot, stack);
        calculateRecipe();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (stack.hasCraftingRemainingItem()) return false;
        ItemStack slotStack = getItem(slot);
        return slotStack.isEmpty() || slotStack.getCount() < slotStack.getMaxStackSize() && !smallerStackExists(slotStack.getCount(), stack, slot);
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new FancyCrafterMenu(id, inventory, this);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        craftingTicksRemaining = tag.getInt(CRAFTING_TICKS_REMAINING_KEY);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt(CRAFTING_TICKS_REMAINING_KEY, craftingTicksRemaining);
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
            ItemStack slotStack = getItem(i);
            if (slotStack.isEmpty() || slotStack.getCount() < currentSize && ItemStack.isSameItemSameTags(slotStack, stack))
                return true;
        }
        return false;
    }

    private CraftingContainer createCraftingInput(List<ItemStack> items) {
        TransientCraftingContainer inv = new TransientCraftingContainer();
        for (int i = 0; i < 9 && i < items.size(); i++) {
            inv.setItem(i, items.get(i));
        }
        return inv;
    }

    /** Minimal CraftingContainer implementation for recipe matching (1.20.1: interface has no concrete type). */
    private static final class TransientCraftingContainer implements CraftingContainer {
        private final NonNullList<ItemStack> craftItems = NonNullList.withSize(9, ItemStack.EMPTY);

        @Override
        public int getWidth() { return 3; }
        @Override
        public int getHeight() { return 3; }
        @Override
        public NonNullList<ItemStack> getItems() { return craftItems; }
        @Override
        public int getContainerSize() { return 9; }
        @Override
        public boolean isEmpty() { return craftItems.stream().allMatch(ItemStack::isEmpty); }
        @Override
        public ItemStack getItem(int slot) { return slot >= 0 && slot < 9 ? craftItems.get(slot) : ItemStack.EMPTY; }
        @Override
        public ItemStack removeItem(int slot, int count) {
            if (slot < 0 || slot >= 9) return ItemStack.EMPTY;
            return craftItems.get(slot).split(count);
        }
        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            if (slot < 0 || slot >= 9) return ItemStack.EMPTY;
            ItemStack stack = craftItems.get(slot);
            craftItems.set(slot, ItemStack.EMPTY);
            return stack;
        }
        @Override
        public void setItem(int slot, ItemStack stack) {
            if (slot >= 0 && slot < 9) craftItems.set(slot, stack == null ? ItemStack.EMPTY : stack);
        }
        @Override
        public void clearContent() { craftItems.clear(); }
        @Override
        public boolean stillValid(Player player) { return false; }
        @Override
        public void setChanged() {}
        @Override
        public void fillStackedContents(net.minecraft.world.entity.player.StackedContents contents) {
            for (ItemStack stack : craftItems) contents.accountStack(stack);
        }
    }

    private void calculateRecipe() {
        if (level() == null) return;
        RecipeManager recipes = level().getRecipeManager();
        CraftingContainer input = createCraftingInput(getInputs());
        recipe = recipes.getRecipeFor(RecipeType.CRAFTING, input, level()).orElse(null);
        items.setStackInSlot(9, recipe == null ? ItemStack.EMPTY : recipe.getResultItem(level().registryAccess()).copy());
    }

    private ItemStack tryDispense(Level level, BlockPos pos, ItemStack stack, BlockState state) {
        Direction direction = state.getValue(FancyCrafterBlock.FACING);
        stack = BCUtil.tryInsert(level, pos, direction, stack, this);
        if (!stack.isEmpty() && !level.isClientSide() && level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty()) {
            Vec3 vec3 = Vec3.atCenterOf(pos.above());
            ItemEntity entity = new ItemEntity(level, vec3.x(), vec3.y(), vec3.z(), stack);
            level.addFreshEntity(entity);
            level.playSound(null, pos, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 1, 1);
            return ItemStack.EMPTY;
        }
        return stack;
    }

    private List<ItemStack> getInputs() {
        return IntStream.range(0, 9).mapToObj(this::getItem).toList();
    }

    /**
     * Consumes one set of recipe ingredients from the crafting grid (slots 0–8)
     * and applies remaining items (e.g. empty bucket). Call when the player takes the result.
     */
    public void consumeIngredientsForResult() {
        if (recipe == null || level() == null) return;
        CraftingContainer input = createCraftingInput(getInputs());
        List<ItemStack> remaining = recipe.getRemainingItems(input);
        for (int i = 0; i < 9 && i < remaining.size(); i++) {
            setItem(i, remaining.get(i));
        }
        setChanged();
        calculateRecipe();
    }
}
