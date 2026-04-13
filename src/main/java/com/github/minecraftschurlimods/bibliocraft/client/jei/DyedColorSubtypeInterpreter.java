package com.github.minecraftschurlimods.bibliocraft.client.jei;

import com.github.minecraftschurlimods.bibliocraft.content.swordpedestal.SwordPedestalBlock;
import net.minecraft.world.item.ItemStack;

/**
 * Provides subtype info for sword pedestal item (dyed color) for JEI.
 * Used via reflection when JEI's ISubtypeInterpreter is available (package can differ by JEI version).
 */
public final class DyedColorSubtypeInterpreter {
    public static final DyedColorSubtypeInterpreter INSTANCE = new DyedColorSubtypeInterpreter();

    private DyedColorSubtypeInterpreter() {
    }

    /**
     * Returns a string that uniquely identifies the subtype for the given stack, or empty if default.
     */
    public String getSubtypeInfo(ItemStack ingredient) {
        int rgb = SwordPedestalBlock.getColorFromStack(ingredient);
        return rgb != SwordPedestalBlock.DEFAULT_COLOR.rgb() ? String.valueOf(rgb) : "";
    }
}
