package at.minecraftschurli.mods.bibliocraft.content.fancycrafter;

import at.minecraftschurli.mods.bibliocraft.init.BCBlockEntities;
import at.minecraftschurli.mods.bibliocraft.util.BCUtil;
import at.minecraftschurli.mods.bibliocraft.util.block.BCMenuBlockEntity;
import at.minecraftschurli.mods.bibliocraft.util.block.LimitedAccessItemHandler;
import at.minecraftschurli.mods.bibliocraft.util.slot.HasToggleableSlots;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class FancyCrafterBlockEntity extends BCMenuBlockEntity implements HasToggleableSlots {
    private static final String CRAFTING_TICKS_REMAINING_KEY = "crafting_ticks_remaining";
    private static final String DISABLED_SLOTS_KEY = "disabled_slots";
    private static final IntList INPUTS = IntList.of(IntStream.range(0, 18).filter(i -> i != 9).toArray());
    private static final IntList OUTPUTS = IntList.of();
    static final int WIDTH = 3;
    static final int HEIGHT = 3;
    static final int SLOT_DISABLED = 1;
    static final int SLOT_ENABLED = 0;
    static final int CRAFTING_SLOTS = WIDTH * HEIGHT;
    static final int CRAFTING_RESULT_SLOTS = 1;
    static final int STORAGE_SLOTS = 8;
    static final int CRAFTING_RESULT_SLOT_INDEX = CRAFTING_SLOTS;
    private static final int MAX_CRAFTING_TICKS = 6;
    private final LimitedAccessItemHandler inputItemHandler;
    private final LimitedAccessItemHandler outputItemHandler;
    private final ContainerData containerData = new ContainerData() {
        private final int[] slotStates = new int[CRAFTING_SLOTS];

        @Override
        public int get(int index) {
            return slotStates[index];
        }

        @Override
        public void set(int index, int value) {
            slotStates[index] = value;
        }

        @Override
        public int getCount() {
            return CRAFTING_SLOTS;
        }
    };
    private int craftingTicksRemaining = MAX_CRAFTING_TICKS;
    private int openCount;
    private boolean recipeNeedsRecalculation;
    @Nullable
    private RecipeHolder<CraftingRecipe> recipe;

    public FancyCrafterBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntities.FANCY_CRAFTER.get(), CRAFTING_SLOTS + CRAFTING_RESULT_SLOTS + STORAGE_SLOTS, defaultName("fancy_crafter"), pos, state);
        this.inputItemHandler = getItemHandler().forInput(INPUTS);
        this.outputItemHandler = getItemHandler().forOutput(OUTPUTS);
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new FancyCrafterMenu(id, inventory, this, containerData);
    }

    public void startOpen(Player player) {
        openCount++;
    }

    public void stopOpen(Player player) {
        if (openCount > 0) {
            openCount--;
        }
        if (openCount == 0) {
            calculateRecipe();
            setChanged();
        }
    }

    public boolean isMenuOpen() {
        return openCount > 0;
    }

    @Override
    public boolean canDisableSlot(int slot) {
        return isCraftingSlot(slot) && getItem(slot).isEmpty();
    }

    @Override
    public boolean isSlotDisabled(int slot) {
        return isCraftingSlot(slot) && containerData.get(slot) == SLOT_DISABLED;
    }

    @Override
    public void setSlotDisabled(int slot, boolean disabled) {
        if (disabled && !canDisableSlot(slot)) return;
        containerData.set(slot, disabled ? SLOT_DISABLED : SLOT_ENABLED);
        calculateRecipe();
        setChanged();
    }

    public List<ItemStack> getCraftingGridItems() {
        return IntStream.range(0, CRAFTING_SLOTS)
                .mapToObj(slot -> isSlotDisabled(slot) ? ItemStack.EMPTY : getItem(slot))
                .toList();
    }

    public CraftingInput getCraftingInput() {
        return CraftingInput.of(WIDTH, HEIGHT, getCraftingGridItems());
    }

    public void calculateRecipe() {
        if (!(this.level instanceof ServerLevel serverLevel)) return;
        RecipeManager recipes = serverLevel.recipeAccess();
        CraftingInput input = getCraftingInput();
        recipe = recipes.getRecipeFor(RecipeType.CRAFTING, input, serverLevel).orElse(null);
        ItemStack assembled = recipe == null ? ItemStack.EMPTY : recipe.value().assemble(input);
        getItemHandler().setStackWithoutNotify(CRAFTING_RESULT_SLOT_INDEX, assembled);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (isSlotDisabled(slot) && !stack.isEmpty() && isCraftingSlot(slot)) {
            setSlotDisabled(slot, false);
        }
        super.setItem(slot, stack);
        if (isCraftingSlot(slot)) {
            calculateRecipe();
        }
    }

    @Override
    public boolean isValid(int slot, ItemVariant resource) {
        if (resource.isBlank()) return false;
        if (resource.getItem().getCraftingRemainder(resource.toStack(1)) != null || isSlotDisabled(slot) || !isCraftingSlot(slot)) return false;
        ItemStack slotStack = getItem(slot);
        return slotStack.isEmpty() || slotStack.getCount() < slotStack.getMaxStackSize() && !smallerStackExists(slotStack.getCount(), resource.toStack(1), slot);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        craftingTicksRemaining = input.getIntOr(CRAFTING_TICKS_REMAINING_KEY, 0);
        for (int i = 0; i < CRAFTING_SLOTS; i++) {
            containerData.set(i, SLOT_ENABLED);
        }
        input.getIntArray(DISABLED_SLOTS_KEY).ifPresent(i -> {
            for (int j : i) {
                if (j >= 0 && j < CRAFTING_SLOTS) {
                    containerData.set(j, SLOT_DISABLED);
                }
            }
        });
        recipeNeedsRecalculation = true;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt(CRAFTING_TICKS_REMAINING_KEY, this.craftingTicksRemaining);
        IntList intlist = new IntArrayList();
        for (int i = 0; i < CRAFTING_SLOTS; i++) {
            if (this.isSlotDisabled(i)) {
                intlist.add(i);
            }
        }
        output.putIntArray(DISABLED_SLOTS_KEY, intlist.toIntArray());
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FancyCrafterBlockEntity blockEntity) {
        if (!level.isClientSide() && blockEntity.recipeNeedsRecalculation) {
            blockEntity.recipeNeedsRecalculation = false;
            blockEntity.calculateRecipe();
            blockEntity.setChanged();
        }
        if (blockEntity.isMenuOpen() || blockEntity.recipe == null) return;
        CraftingRecipe recipe = blockEntity.recipe.value();
        CraftingInput input = blockEntity.getCraftingInput();
        ItemStack result = recipe.assemble(input);
        ItemStack resultStack = blockEntity.getItem(CRAFTING_RESULT_SLOT_INDEX);
        if (!resultStack.isEmpty() && !ItemStack.isSameItemSameComponents(result, resultStack)) return;
        blockEntity.craftingTicksRemaining--;
        if (blockEntity.craftingTicksRemaining > 0) return;
        result.onCraftedBySystem(level);
        try (Transaction transaction = Transaction.openOuter()) {
            ItemStack dispensed = blockEntity.tryDispense(level, pos, result, state, transaction);
            if (dispensed.isEmpty()) {
                for (ItemStack remainder : recipe.getRemainingItems(input)) {
                    if (!remainder.isEmpty()) {
                        blockEntity.tryDispense(level, pos, remainder, state, transaction);
                    }
                }
                List<ItemStack> consumed = new ArrayList<>(input.items().stream().filter(stack -> !stack.isEmpty()).toList());
                for (int i = CRAFTING_SLOTS + CRAFTING_RESULT_SLOTS; i < blockEntity.getContainerSize(); i++) {
                    ItemStack stack = blockEntity.getItem(i);
                    if (stack.isEmpty()) continue;
                    List<ItemStack> toRemove = new ArrayList<>();
                    for (ItemStack ingredient : consumed) {
                        if (!ItemStack.isSameItemSameComponents(ingredient, stack) || ingredient.getCount() >= ingredient.getMaxStackSize()) continue;
                        ingredient.grow(1);
                        toRemove.add(ingredient);
                        stack.shrink(1);
                    }
                    toRemove.forEach(consumed::remove);
                }
                blockEntity.consumeCraftingIngredients();
                transaction.commit();
            }
            blockEntity.craftingTicksRemaining = MAX_CRAFTING_TICKS;
        }
        blockEntity.calculateRecipe();
        blockEntity.setChanged();
    }

    public void consumeCraftingIngredients() {
        CraftingInput.Positioned positioned = CraftingInput.ofPositioned(WIDTH, HEIGHT, getCraftingGridItems());
        CraftingInput positionedInput = positioned.input();
        int left = positioned.left();
        int top = positioned.top();
        for (int row = 0; row < positionedInput.height(); row++) {
            for (int col = 0; col < positionedInput.width(); col++) {
                if (positionedInput.getItem(col, row).isEmpty()) continue;
                int slot = col + left + (row + top) * WIDTH;
                if (isSlotDisabled(slot)) continue;
                ItemStack stack = getItem(slot);
                if (stack.isEmpty()) continue;
                if (stack.getCount() <= 1) {
                    super.setItem(slot, ItemStack.EMPTY);
                } else {
                    super.setItem(slot, stack.copyWithCount(stack.getCount() - 1));
                }
            }
        }
        calculateRecipe();
        setChanged();
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level != null) {
            NonNullList<ItemStack> drops = getContents();
            drops.set(CRAFTING_RESULT_SLOT_INDEX, ItemStack.EMPTY);
            Containers.dropContents(level, pos, drops);
        }
    }

    @Nullable
    public RecipeHolder<CraftingRecipe> getRecipe() {
        return recipe;
    }

    private boolean smallerStackExists(int currentSize, ItemStack stack, int slot) {
        for (int i = slot + 1; i < CRAFTING_SLOTS; i++) {
            if (isSlotDisabled(i)) continue;
            ItemStack slotStack = getItem(i);
            if (slotStack.isEmpty() || slotStack.getCount() < currentSize && ItemStack.isSameItemSameComponents(slotStack, stack)) return true;
        }
        return false;
    }

    private ItemStack tryDispense(Level level, BlockPos pos, ItemStack stack, BlockState state, Transaction parent) {
        if (level.isClientSide()) return stack;
        Direction direction = state.getValue(FancyCrafterBlock.FACING);
        Storage<ItemVariant> handler = BCUtil.getItemHandler(level, pos.relative(direction), direction.getOpposite());
        if (handler != null && StorageUtil.simulateInsert(handler, ItemVariant.of(stack), 1, null) > 0) {
            try (Transaction transaction = Transaction.openOuter()) {
                if (handler.insert(ItemVariant.of(stack), 1, transaction) == 1) {
                    transaction.commit();
                    stack.shrink(1);
                }
            }
            return stack;
        }
        if (!stack.isEmpty() && level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty()) {
            Vec3 vec3 = Vec3.atCenterOf(pos.above());
            ItemEntity entity = new ItemEntity(level, vec3.x(), vec3.y(), vec3.z(), stack);
            level.addFreshEntity(entity);
            level.playSound(null, pos, SoundEvents.CRAFTER_CRAFT, SoundSource.BLOCKS, 1, 1);
            return ItemStack.EMPTY;
        }
        return stack;
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

    private boolean isCraftingSlot(int slot) {
        return slot >= 0 && slot < CRAFTING_SLOTS;
    }
}
