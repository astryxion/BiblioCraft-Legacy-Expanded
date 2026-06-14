package at.minecraftschurli.mods.bibliocraft.util.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BCItemHandler {
    public static final String VALUE_IO_KEY = "stacks";
    static final String ITEMS_TAG = "items";
    private record SlotEntry(int slot, ItemStack item) {
        private static final Codec<SlotEntry> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Codec.INT.fieldOf("slot").forGetter(SlotEntry::slot),
                ItemStack.CODEC.fieldOf("item").forGetter(SlotEntry::item)
        ).apply(inst, SlotEntry::new));
    }

    private static final Codec<List<SlotEntry>> STACKS_CODEC = SlotEntry.CODEC.listOf();
    private final NonNullList<ItemStack> stacks;
    private final List<StackJournal> snapshotJournals;
    private final ValidityPredicate validityPredicate;
    private final CapacityProvider capacityProvider;
    private final ChangeListener changeListener;

    public BCItemHandler(int size, ValidityPredicate validityPredicate, CapacityProvider capacityProvider, ChangeListener changeListener) {
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        this.snapshotJournals = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.snapshotJournals.add(new StackJournal());
        }
        this.validityPredicate = validityPredicate;
        this.capacityProvider = capacityProvider;
        this.changeListener = changeListener;
    }

    /// Overridden for legacy compat
    public void deserialize(ValueInput input) {
        if (input.keySet().contains(VALUE_IO_KEY)) {
            input.read(VALUE_IO_KEY, STACKS_CODEC).ifPresent(entries -> {
                for (int i = 0; i < stacks.size(); i++) {
                    stacks.set(i, ItemStack.EMPTY);
                }
                for (SlotEntry entry : entries) {
                    if (entry.slot >= 0 && entry.slot < stacks.size()) {
                        stacks.set(entry.slot, entry.item.copy());
                    }
                }
            });
        } else {
            loadLegacyInventory(input);
        }
    }

    public void serialize(ValueOutput output) {
        List<SlotEntry> entries = new ArrayList<>();
        for (int i = 0; i < stacks.size(); i++) {
            if (!stacks.get(i).isEmpty()) {
                entries.add(new SlotEntry(i, stacks.get(i).copy()));
            }
        }
        output.store(VALUE_IO_KEY, STACKS_CODEC, entries);
    }

    public void set(int index, ItemVariant resource, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be non-negative");
        }
        if (resource.isBlank() && amount > 0) {
            throw new IllegalArgumentException("Cannot set a non-empty amount for an empty resource: " + amount);
        }
        ItemStack previous = stacks.set(index, getStackFrom(resource, amount));
        onContentsChanged(index, previous);
    }

    public void setStackWithoutNotify(int index, ItemStack stack) {
        stacks.set(index, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
    }

    public boolean isValid(int index, ItemVariant resource) {
        return this.validityPredicate.isValid(index, resource);
    }

    protected void onContentsChanged(int index, ItemStack previousContents) {
        this.changeListener.onChanged();
    }

    public boolean isEmpty(int index) {
        return this.stacks.get(index).isEmpty();
    }

    public ItemStack getStack(int index) {
        return stacks.get(index);
    }

    public ItemStack getStackCopy(int index) {
        ItemStack stack = stacks.get(index);
        return stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
    }

    protected void fillFromComponent(ItemContainerContents containerContents) {
        modifyContents(containerContents::copyInto);
    }

    private void loadLegacyInventory(ValueInput input) {
        input.child(ITEMS_TAG).ifPresent(i -> modifyContents(stacks -> ContainerHelper.loadAllItems(i, stacks)));
    }

    protected void modifyContents(Consumer<NonNullList<ItemStack>> modifier) {
        NonNullList<ItemStack> stacks = copyToList();
        modifier.accept(stacks);
        setStacks(stacks);
    }

    protected void setStacks(NonNullList<ItemStack> newStacks) {
        if (newStacks.size() != this.stacks.size()) {
            throw new IllegalStateException("Deserialized inventory size does not match expected size: expected " + this.stacks.size() + ", got " + newStacks.size());
        }
        for (int i = 0; i < this.stacks.size(); i++) {
            this.stacks.set(i, newStacks.get(i).copy());
        }
    }

    public LimitedAccessItemHandler forOutput(IntList outputs) {
        return new LimitedAccessItemHandler(this, (index, mode) -> mode == LimitedAccessItemHandler.AccessPredicate.Mode.EXTRACT && outputs.contains(index));
    }

    public LimitedAccessItemHandler forInput(IntList inputs) {
        return new LimitedAccessItemHandler(this, (index, mode) -> mode == LimitedAccessItemHandler.AccessPredicate.Mode.INSERT && inputs.contains(index));
    }

    public NonNullList<ItemStack> copyToList() {
        NonNullList<ItemStack> copy = NonNullList.withSize(stacks.size(), ItemStack.EMPTY);
        for (int i = 0; i < stacks.size(); i++) {
            copy.set(i, stacks.get(i).copy());
        }
        return copy;
    }

    public int size() {
        return stacks.size();
    }

    public ItemVariant getResource(int index) {
        return getResourceFrom(stacks.get(index));
    }

    public int getAmountAsInt(int index) {
        return stacks.get(index).getCount();
    }

    public int getCapacityAsInt(int index, ItemVariant resource) {
        return getCapacity(index, resource);
    }

    protected int getCapacity(int index, ItemVariant resource) {
        return this.capacityProvider.getCapacity(resource);
    }

    public int insert(int index, ItemVariant resource, int amount, TransactionContext transaction) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException(index);
        }
        if (resource.isBlank() || amount <= 0) {
            return 0;
        }
        ItemStack existing = stacks.get(index);
        int existingAmount = existing.getCount();
        if (existingAmount != 0 && !matches(existing, resource)) {
            return 0;
        }
        if (!isValid(index, resource)) {
            return 0;
        }
        int inserted = Math.min(amount, getCapacity(index, resource) - existingAmount);
        if (inserted <= 0) {
            return 0;
        }
        snapshotJournals.get(index).updateSnapshot(index, transaction);
        if (existingAmount == 0) {
            stacks.set(index, resource.toStack(inserted));
        } else {
            existing.grow(inserted);
        }
        onContentsChanged(index, existing);
        return inserted;
    }

    public int extract(int index, ItemVariant resource, int amount, TransactionContext transaction) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException(index);
        }
        if (resource.isBlank() || amount <= 0) {
            return 0;
        }
        ItemStack existing = stacks.get(index);
        if (!matches(existing, resource)) {
            return 0;
        }
        int extracted = Math.min(amount, existing.getCount());
        if (extracted <= 0) {
            return 0;
        }
        snapshotJournals.get(index).updateSnapshot(index, transaction);
        ItemStack previous = existing.copy();
        if (extracted == existing.getCount()) {
            stacks.set(index, ItemStack.EMPTY);
        } else {
            existing.shrink(extracted);
        }
        onContentsChanged(index, previous);
        return extracted;
    }

    private static ItemVariant getResourceFrom(ItemStack stack) {
        return stack.isEmpty() ? ItemVariant.blank() : ItemVariant.of(stack);
    }

    private static ItemStack getStackFrom(ItemVariant resource, int amount) {
        return resource.isBlank() ? ItemStack.EMPTY : resource.toStack(amount);
    }

    private static boolean matches(ItemStack stack, ItemVariant resource) {
        return resource.isBlank() ? stack.isEmpty() : resource.matches(stack);
    }

    private final class StackJournal {
        private ItemStack snapshot;

        private void updateSnapshot(int index, TransactionContext transaction) {
            if (snapshot == null) {
                snapshot = stacks.get(index).copy();
                transaction.addCloseCallback((context, result) -> {
                    if (result.wasAborted()) {
                        stacks.set(index, snapshot);
                    }
                    snapshot = null;
                });
            }
        }
    }

    @FunctionalInterface
    public interface ValidityPredicate {
        boolean isValid(int slot, ItemVariant resource);
    }

    @FunctionalInterface
    public interface CapacityProvider {
        int getCapacity(ItemVariant resource);
    }

    @FunctionalInterface
    public interface ChangeListener {
        void onChanged();
    }
}
