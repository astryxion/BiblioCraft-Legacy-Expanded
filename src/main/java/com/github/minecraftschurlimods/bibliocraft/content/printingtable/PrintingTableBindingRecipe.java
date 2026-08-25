package com.github.minecraftschurlimods.bibliocraft.content.printingtable;

import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;

public abstract class PrintingTableBindingRecipe extends PrintingTableRecipe {
    public PrintingTableBindingRecipe(ResourceLocation id, ItemStack result, int duration) {
        super(id, result, duration);
    }

    @Override
    public PrintingTableMode getMode() {
        return PrintingTableMode.BIND;
    }
}
