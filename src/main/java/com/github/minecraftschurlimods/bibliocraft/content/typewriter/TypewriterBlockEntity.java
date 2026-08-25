package com.github.minecraftschurlimods.bibliocraft.content.typewriter;

import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.init.BCItems;
import com.github.minecraftschurlimods.bibliocraft.init.BCTags;
import com.github.minecraftschurlimods.bibliocraft.util.CodecUtil;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCBlockEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.block.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import javax.annotation.Nullable;

import java.util.EnumMap;

public class TypewriterBlockEntity extends BCBlockEntity implements ISidedInventory {
    public static final int INPUT = 0;
    public static final int OUTPUT = 1;
    private static final int[] INPUTS = new int[]{INPUT};
    private static final int[] OUTPUTS = new int[]{OUTPUT};
    private static final String PAGE_KEY = "page";
    private final EnumMap<Direction, SidedInvWrapper> wrappers = Util.make(new EnumMap<>(Direction.class), map -> {
        for (Direction direction : Direction.values()) {
            map.put(direction, new SidedInvWrapper(this, direction));
        }
    });
    private TypewriterPage page = new TypewriterPage();

    public TypewriterBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntities.TYPEWRITER.get(), 2, pos, state);
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        page = CodecUtil.decodeNbt(TypewriterPage.CODEC, tag.getCompound(PAGE_KEY));
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        super.save(tag);
        tag.put(PAGE_KEY, CodecUtil.encodeNbt(TypewriterPage.CODEC, page));
            return tag;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot != INPUT) return false;
        if (BCTags.Items.contains(BCTags.Items.TYPEWRITER_PAPER, stack.getItem())) return true;
        return stack.getItem() == Items.PAPER;
    }

    @Override
    @Nullable
    public IItemHandler getItemCapability(@Nullable Direction side) {
        return side == null ? null : wrappers.get(side);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return side == Direction.DOWN ? OUTPUTS : INPUTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
        return direction != Direction.DOWN && canPlaceItem(index, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return direction == Direction.DOWN && index == OUTPUT;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        super.setItem(slot, stack);
        if (slot == OUTPUT && getItem(OUTPUT).isEmpty()) {
            setPage(TypewriterPage.DEFAULT);
            setChanged();
        }
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack stack = super.removeItem(slot, count);
        if (slot == OUTPUT && getItem(OUTPUT).isEmpty()) {
            setPage(TypewriterPage.DEFAULT);
            setChanged();
        }
        return stack;
    }

    public boolean insertPaper(ItemStack stack) {
        ItemStack input = getItem(INPUT);
        if (!input.isEmpty() && !(ItemStack.isSame(input, stack) && ItemStack.tagMatches(input, stack))) return false;
        if (input.isEmpty()) {
            ItemStack one = stack.copy();
            one.setCount(1);
            setItem(INPUT, one);
        } else if (input.getCount() < input.getMaxStackSize()) {
            input.grow(1);
        } else return false;
        stack.shrink(1);
        setChanged();
        return true;
    }

    public ItemStack takeOutput() {
        ItemStack output = getItem(OUTPUT);
        setItem(OUTPUT, ItemStack.EMPTY);
        level().setBlockAndUpdate(getBlockPos(), getBlockState().setValue(TypewriterBlock.PAPER, 0));
        setChanged();
        return output;
    }

    public TypewriterPage getPage() {
        return page;
    }

    public void setPage(TypewriterPage page) {
        this.page = page;
        if (page.line() == TypewriterPage.MAX_LINES) {
            ItemStack output = new ItemStack(BCItems.TYPEWRITER_PAGE.get());
            TypewriterPage.setOnStack(output, page);
            setItem(OUTPUT, output);
            getItem(INPUT).shrink(1);
            this.page = TypewriterPage.DEFAULT;
        }
        BlockState state = getBlockState().setValue(TypewriterBlock.PAPER, page.line() / 2);
        if (getBlockState() != state) {
            level().setBlockAndUpdate(getBlockPos(), state);
        }
        setChanged();
    }
}
