package at.minecraftschurli.mods.bibliocraft.content.typewriter;

import at.minecraftschurli.mods.bibliocraft.init.BCBlockEntities;
import at.minecraftschurli.mods.bibliocraft.init.BCDataComponents;
import at.minecraftschurli.mods.bibliocraft.init.BCItems;
import at.minecraftschurli.mods.bibliocraft.init.BCTags;
import at.minecraftschurli.mods.bibliocraft.util.block.BCBlockEntity;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import at.minecraftschurli.mods.bibliocraft.util.block.LimitedAccessItemHandler;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

public class TypewriterBlockEntity extends BCBlockEntity {
    public static final int INPUT = 0;
    public static final int OUTPUT = 1;
    private static final IntList INPUTS = IntList.of(INPUT);
    private static final IntList OUTPUTS = IntList.of(OUTPUT);
    private static final String PAGE_KEY = "page";
    private final LimitedAccessItemHandler inputItemHandler;
    private final LimitedAccessItemHandler outputItemHandler;
    private TypewriterPage page = TypewriterPage.DEFAULT;

    public TypewriterBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntities.TYPEWRITER.get(), 2, pos, state);
        this.inputItemHandler = getItemHandler().forInput(INPUTS);
        this.outputItemHandler = getItemHandler().forOutput(OUTPUTS);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.read(PAGE_KEY, TypewriterPage.CODEC).ifPresent(page -> this.page = page);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store(PAGE_KEY, TypewriterPage.CODEC, page);
    }

    @Override
    public boolean isValid(int slot, ItemVariant resource) {
        return slot == INPUT && resource.toStack(1).is(BCTags.Items.TYPEWRITER_PAPER);
    }

    public boolean insertPaper(ItemStack stack) {
        ItemStack input = getItem(INPUT);
        if (!input.isEmpty() && !ItemStack.isSameItemSameComponents(input, stack)) return false;
        if (input.isEmpty()) {
            getItemHandler().set(INPUT, ItemVariant.of(stack), 1);
        } else if (input.getCount() < input.getMaxStackSize()) {
            input.grow(1);
        } else return false;
        stack.shrink(1);
        setChanged();
        return true;
    }

    public ItemStack takeOutput() {
        ItemStack output = getItem(OUTPUT);
        getItemHandler().set(OUTPUT, ItemVariant.blank(), 0);
        level().setBlockAndUpdate(getBlockPos(), getBlockState().setValue(TypewriterBlock.PAPER, 0));
        setChanged();
        return output;
    }

    public TypewriterPage getPage() {
        if (getItem(OUTPUT).isEmpty()) {
            setPage(TypewriterPage.DEFAULT);
            setChanged();
        }
        return page;
    }

    public void setPage(TypewriterPage page) {
        this.page = page;
        if (page.line() == TypewriterPage.MAX_LINES) {
            ItemStack output = new ItemStack(BCItems.TYPEWRITER_PAGE.get());
            output.set(BCDataComponents.TYPEWRITER_PAGE.get(), page);
            getItemHandler().set(OUTPUT, ItemVariant.of(output), 1);
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
