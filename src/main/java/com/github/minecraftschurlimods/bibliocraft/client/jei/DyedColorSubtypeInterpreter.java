package com.github.minecraftschurlimods.bibliocraft.client.jei;

import com.github.minecraftschurlimods.bibliocraft.content.swordpedestal.SwordPedestalBlock;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import net.minecraft.item.ItemStack;

/**
 * Provides subtype info for sword pedestal item (dyed color) for JEI.
 */
public final class DyedColorSubtypeInterpreter implements ISubtypeInterpreter {
    public static final DyedColorSubtypeInterpreter INSTANCE = new DyedColorSubtypeInterpreter();

    private DyedColorSubtypeInterpreter() {
    }

    /**
     * Returns a string that uniquely identifies the subtype for the given stack, or empty if default.
     */
    public String getSubtypeInfo(ItemStack ingredient) {
        return apply(ingredient);
    }

    @Override
    public String apply(ItemStack ingredient) {
        int rgb = SwordPedestalBlock.getColorFromStack(ingredient);
        return rgb != SwordPedestalBlock.DEFAULT_COLOR.rgb() ? String.valueOf(rgb) : NONE;
    }
}
