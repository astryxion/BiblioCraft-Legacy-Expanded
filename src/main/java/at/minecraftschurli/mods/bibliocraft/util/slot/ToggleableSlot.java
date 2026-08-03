package at.minecraftschurli.mods.bibliocraft.util.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.IntPredicate;

/// Represents a slot that can be disabled or enabled. If disabled, items will not be placed inside the slot.
public class ToggleableSlot extends Slot {
    private final IntPredicate slotDisabledPredicate;

    public ToggleableSlot(Container container, IntPredicate slotDisabledPredicate, int slot, int x, int y) {
        super(container, slot, x, y);
        this.slotDisabledPredicate = slotDisabledPredicate;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return super.mayPlace(stack) && !slotDisabledPredicate.test(getContainerSlot());
    }
}
