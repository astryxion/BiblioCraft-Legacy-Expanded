package com.github.minecraftschurlimods.bibliocraft.content.printingtable;

import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.init.BCBlocks;
import com.github.minecraftschurlimods.bibliocraft.init.BCRecipes;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.CodecUtil;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCMenuBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.IntStream;

public class PrintingTableBlockEntity extends BCMenuBlockEntity {
    private static final String MODE_KEY = "mode";
    private static final String DURATION_KEY = "duration";
    private static final String PLAYER_NAME_KEY = "player_name";
    private final PrintingTableTank tank;
    private final Direction[] directions;
    private PrintingTableRecipe recipe;
    private PrintingTableRecipeInput recipeInput;
    private PrintingTableMode mode = PrintingTableMode.BIND;
    private int levelCost = 0;
    private int duration = 0;
    private int maxDuration = 0;
    private Component playerName = null;

    public PrintingTableBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntities.PRINTING_TABLE.get(), 11, defaultName("printing_table"), pos, state);
        tank = new PrintingTableTank(this, state.is(BCBlocks.IRON_PRINTING_TABLE.get()));
        Direction facing = state.getValue(PrintingTableBlock.FACING);
        directions = new Direction[]{Direction.UP, facing, facing.getClockWise(), facing.getOpposite(), facing.getCounterClockWise(), Direction.DOWN};
    }

    @SuppressWarnings("unused")
    public static void tick(Level level, BlockPos pos, BlockState state, PrintingTableBlockEntity blockEntity) {
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
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new PrintingTableMenu(id, inventory, this);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        setMode(CodecUtil.decodeNbt(PrintingTableMode.CODEC, tag.get(MODE_KEY)));
        duration = tag.getInt(DURATION_KEY);
        if (tag.contains(PLAYER_NAME_KEY, CompoundTag.TAG_STRING)) {
            playerName = Component.Serializer.fromJson(tag.getString(PLAYER_NAME_KEY));
        }
        tank.loadAdditional(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(MODE_KEY, CodecUtil.encodeNbt(PrintingTableMode.CODEC, getMode()));
        tag.putInt(DURATION_KEY, duration);
        if (playerName != null) {
            tag.putString(PLAYER_NAME_KEY, Component.Serializer.toJson(playerName));
        }
        tank.saveAdditional(tag);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        ItemStack old = getItem(slot).copy();
        super.setItem(slot, stack);
        recipeInput = null;
        if (!ItemStack.isSameItemSameTags(old, stack) || recipe == null || !recipe.matches(getRecipeInput(), BCUtil.nonNull(getLevel()))) {
            calculateRecipe(false);
            setChanged();
        }
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot < 10 && !stack.hasCraftingRemainingItem() && super.canPlaceItem(slot, stack);
    }

    @Override
    public void onLoad() {
        if (!level().isClientSide()) {
            calculateRecipe(true);
        } else {
            com.github.minecraftschurlimods.bibliocraft.BCEventHandler.getChannel().sendToServer(new PrintingTableSetRecipePacket(getBlockPos(), 0, 0, 0));
        }
    }

    public static IFluidHandler getFluidCapability(Level level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, @Nullable Direction context) {
        return blockEntity instanceof PrintingTableBlockEntity printingTable ? printingTable.getFluidCapability() : null;
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

    public Component getPlayerName() {
        return playerName;
    }

    public void setPlayerName(Component playerName) {
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
        ItemStack result = recipe.postProcess(recipe.assemble(getRecipeInput(), level().registryAccess()), this);
        if (!stack.isEmpty() && !ItemStack.isSameItemSameTags(stack, result)) return;
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

    private static final TagKey<net.minecraft.world.level.material.Fluid> EXPERIENCE_FLUID_TAG = TagKey.create(Registries.FLUID, new ResourceLocation("forge", "experience"));

    private void pullExperience() {
        List<Fluid> fluids = level()
                .registryAccess()
                .registryOrThrow(Registries.FLUID)
                .getTag(EXPERIENCE_FLUID_TAG)
                .map(HolderSet.Named::stream)
                .orElseGet(java.util.stream.Stream::empty)
                .map(Holder::value)
                .toList();
        for (Direction direction : directions) {
            BlockEntity adjacent = level().getBlockEntity(getBlockPos().offset(direction.getNormal()));
            IFluidHandler capability = adjacent != null ? adjacent.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite()).orElse(null) : null;
            if (capability == null) continue;
            for (Fluid fluid : fluids) {
                tank.fillFromCapability(capability, fluid);
                if (isExperienceFull()) return;
            }
        }
    }

    private void calculateRecipe(boolean onLoad) {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        recipe = serverLevel
                .getRecipeManager()
                .getRecipeFor(BCRecipes.PRINTING_TABLE.get(), getRecipeInput(), serverLevel)
                .filter(e -> e.getMode() == mode)
                .orElse(null);
        if (recipe != null) {
            ItemStack output = getItem(10);
            if (!output.isEmpty() && (output.getCount() >= output.getMaxStackSize() || !ItemStack.isSameItemSameTags(recipe.assemble(getRecipeInput(), level().registryAccess()), output))) {
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
            recipeInput = new PrintingTableRecipeInput(IntStream.range(0, 9).mapToObj(this::getItem).toList(), getItem(9));
        }
        return recipeInput;
    }

    private boolean isCraftingSlot(int slot) {
        return slot >= 0 && slot < 9;
    }
}
