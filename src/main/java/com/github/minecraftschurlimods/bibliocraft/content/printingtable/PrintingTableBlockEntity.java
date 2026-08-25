package com.github.minecraftschurlimods.bibliocraft.content.printingtable;

import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.init.BCBlocks;
import com.github.minecraftschurlimods.bibliocraft.init.BCRecipes;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.CodecUtil;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCMenuBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.util.slot.HasToggleableSlots;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraft.tags.ITag;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.tags.ITag;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.network.PacketDistributor;
import javax.annotation.Nullable;

import java.util.List;
import java.util.stream.IntStream;

public class PrintingTableBlockEntity extends BCMenuBlockEntity implements net.minecraft.tileentity.ITickableTileEntity, HasToggleableSlots {
    private static final String MODE_KEY = "mode";
    private static final String DURATION_KEY = "duration";
    private static final String PLAYER_NAME_KEY = "player_name";
    private static final String DISABLED_SLOTS_KEY = "disabled_slots";
    private static final int SLOT_DISABLED = 1;
    private static final int SLOT_ENABLED = 0;
    private final PrintingTableTank tank;
    private final Direction[] directions;
    private final boolean[] disabledSlots = new boolean[9];
    private PrintingTableRecipe recipe;
    private PrintingTableRecipeInput recipeInput;
    private PrintingTableMode mode = PrintingTableMode.BIND;
    private int levelCost = 0;
    private int duration = 0;
    private int maxDuration = 0;
    private ITextComponent playerName = null;

    public PrintingTableBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntities.PRINTING_TABLE.get(), 11, defaultName("printing_table"), pos, state);
        tank = new PrintingTableTank(this, state.is(BCBlocks.IRON_PRINTING_TABLE.get()));
        Direction facing = state.getValue(PrintingTableBlock.FACING);
        directions = new Direction[]{Direction.UP, facing, facing.getClockWise(), facing.getOpposite(), facing.getCounterClockWise(), Direction.DOWN};
    }

    @Override
    public void tick() {
        if (level != null) {
            tick(level, getBlockPos(), getBlockState(), this);
        }
    }

    @SuppressWarnings("unused")
    public static void tick(World level, BlockPos pos, BlockState state, PrintingTableBlockEntity blockEntity) {
        if (blockEntity.duration < blockEntity.maxDuration && blockEntity.isExperienceFull()) {
            blockEntity.duration++;
        }
        if (blockEntity.duration >= blockEntity.maxDuration) {
            blockEntity.duration = 0;
            if (!level.isClientSide()) {
                blockEntity.finishRecipe();
            }
        }
        if (!blockEntity.isExperienceFull()) {
            blockEntity.pullExperience();
        }
        blockEntity.setChanged();
    }

    @Override
    protected Container createMenu(int id, PlayerInventory inventory) {
        return new PrintingTableMenu(id, inventory, this);
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        setMode(CodecUtil.decodeNbt(PrintingTableMode.CODEC, tag.get(MODE_KEY)));
        duration = tag.getInt(DURATION_KEY);
        if (tag.contains(PLAYER_NAME_KEY, net.minecraftforge.common.util.Constants.NBT.TAG_STRING)) {
            playerName = ITextComponent.Serializer.fromJson(tag.getString(PLAYER_NAME_KEY));
        }
        tank.loadAdditional(tag);
        int[] tagSlots = tag.contains(DISABLED_SLOTS_KEY) ? tag.getIntArray(DISABLED_SLOTS_KEY) : new int[9];
        for (int i = 0; i < 9; i++) {
            disabledSlots[i] = canDisableSlot(i) && i < tagSlots.length && tagSlots[i] == SLOT_DISABLED;
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        super.save(tag);
        tag.put(MODE_KEY, CodecUtil.encodeNbt(PrintingTableMode.CODEC, getMode()));
        tag.putInt(DURATION_KEY, duration);
        if (playerName != null) {
            tag.putString(PLAYER_NAME_KEY, ITextComponent.Serializer.toJson(playerName));
        }
        tank.saveAdditional(tag);
        int[] tagSlots = new int[9];
        for (int i = 0; i < 9; i++) {
            tagSlots[i] = disabledSlots[i] ? SLOT_DISABLED : SLOT_ENABLED;
        }
        tag.putIntArray(DISABLED_SLOTS_KEY, tagSlots);
        return tag;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (isSlotDisabled(slot) && !stack.isEmpty()) {
            setSlotDisabled(slot, false);
        }
        ItemStack old = getItem(slot).copy();
        super.setItem(slot, stack);
        recipeInput = null;
        if (!(ItemStack.isSame(old, stack) && ItemStack.tagMatches(old, stack)) || recipe == null || !recipe.matches(getRecipeInput(), BCUtil.nonNull(getLevel()))) {
            calculateRecipe(false);
            setChanged();
        }
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot < 10 && !stack.hasContainerItem() && !isSlotDisabled(slot) && super.canPlaceItem(slot, stack);
    }

    @Override
    public void setSlotDisabled(int slot, boolean disabled) {
        if (!canDisableSlot(slot)) return;
        disabledSlots[slot] = disabled;
        setChanged();
    }

    @Override
    public boolean isSlotDisabled(int slot) {
        return isCraftingSlot(slot) && disabledSlots[slot];
    }

    @Override
    public boolean canDisableSlot(int slot) {
        return isCraftingSlot(slot) && getItem(slot).isEmpty();
    }

    @Override
    public void onLoad() {
        if (!level().isClientSide()) {
            calculateRecipe(true);
        } else {
            com.github.minecraftschurlimods.bibliocraft.BCEventHandler.getChannel().sendToServer(new PrintingTableSetRecipePacket(getBlockPos(), 0, 0, 0));
        }
    }

    public static IFluidHandler getFluidCapability(World level, BlockPos pos, BlockState state, @Nullable TileEntity blockEntity, @Nullable Direction context) {
        return blockEntity instanceof PrintingTableBlockEntity ? ((PrintingTableBlockEntity) blockEntity).getFluidCapability() : null;
    }

    public PrintingTableTank getFluidCapability() {
        return tank;
    }

    public PrintingTableMode getMode() {
        return mode;
    }

    public void setMode(PrintingTableMode mode) {
        this.mode = mode;
        calculateRecipe(false);
        setChanged();
    }

    public ITextComponent getPlayerName() {
        return playerName;
    }

    public void setPlayerName(ITextComponent playerName) {
        this.playerName = playerName;
    }

    public int getExperience() {
        return tank.getExperience();
    }

    public void addExperience(int experience) {
        tank.addExperience(experience);
        setChanged();
    }

    public float getProgress() {
        return !isExperienceFull() || maxDuration == 0 ? 0 : duration / (float) maxDuration;
    }

    public int getDuration() {
        return duration;
    }

    public int getMaxDuration() {
        return maxDuration;
    }

    public int getLevelCost() {
        return levelCost;
    }

    public int getExperienceCost() {
        return BCUtil.getExperienceForLevel(levelCost);
    }

    public boolean isExperienceFull() {
        int experienceCost = getExperienceCost();
        return experienceCost <= 0 || getExperience() >= experienceCost;
    }

    public void setFromPacket(PrintingTableSetRecipePacket packet) {
        duration = packet.duration();
        maxDuration = packet.maxDuration();
        levelCost = packet.levelCost();
        tank.clear();
        setChanged();
    }

    private void finishRecipe() {
        if (recipe == null) return;
        List<ItemStack> remainingItems = recipe.getRemainingItems(getRecipeInput());
        ItemStack stack = getItem(10);
        ItemStack result = recipe.postProcess(recipe.assemble(getRecipeInput()), this);
        if (!stack.isEmpty() && !(ItemStack.isSame(stack, result) && ItemStack.tagMatches(stack, result))) return;
        result.setCount(stack.getCount() + 1);
        setItem(10, result);
        IntStream.range(0, 10)
                .mapToObj(this::getItem)
                .forEach(e -> e.shrink(1));
        for (int i = 0; i < remainingItems.size(); i++) {
            ItemStack remaining = remainingItems.get(i);
            if (remaining.isEmpty()) continue;
            setItem(i, remaining.copy());
        }
        calculateRecipe(false);
    }

    private void pullExperience() {
        for (Direction direction : directions) {
            TileEntity adjacent = level().getBlockEntity(getBlockPos().offset(direction.getNormal()));
            IFluidHandler capability = adjacent != null ? adjacent.getCapability(net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite()).orElse(null) : null;
            if (capability == null) continue;
            for (Fluid fluid : PrintingTableTank.experienceFluids()) {
                tank.fillFromCapability(capability, fluid);
                if (isExperienceFull()) return;
            }
        }
    }

    private void calculateRecipe(boolean onLoad) {
        if (!(level() instanceof ServerWorld)) return;
        ServerWorld serverLevel = (ServerWorld) level();
        recipe = serverLevel
                .getRecipeManager()
                .getRecipesFor(BCRecipes.PRINTING_TABLE, getRecipeInput(), serverLevel)
                .stream()
                .filter(e -> e.getMode() == mode)
                .findFirst()
                .orElse(null);
        if (recipe != null) {
            ItemStack output = getItem(10);
            ItemStack assembled = recipe.assemble(getRecipeInput());
            if (!output.isEmpty() && (output.getCount() >= output.getMaxStackSize() || !(ItemStack.isSame(assembled, output) && ItemStack.tagMatches(assembled, output)))) {
                recipe = null;
            }
        }
        if (!onLoad) {
            duration = 0;
        }
        maxDuration = recipe == null ? 0 : recipe.getDuration();
        levelCost = recipe == null ? 0 : recipe.getExperienceLevelCost(recipeInput.right().copy(), serverLevel);
        tank.clear();
        com.github.minecraftschurlimods.bibliocraft.BCEventHandler.getChannel().send(PacketDistributor.TRACKING_CHUNK.with(() -> serverLevel.getChunkAt(getBlockPos())), new PrintingTableSetRecipePacket(getBlockPos(), duration, maxDuration, levelCost));
    }

    private PrintingTableRecipeInput getRecipeInput() {
        if (recipeInput == null) {
            recipeInput = new PrintingTableRecipeInput(IntStream.range(0, 9).mapToObj(this::getItem).collect(java.util.stream.Collectors.toList()), getItem(9));
        }
        return recipeInput;
    }

    private boolean isCraftingSlot(int slot) {
        return slot >= 0 && slot < 9;
    }
}
