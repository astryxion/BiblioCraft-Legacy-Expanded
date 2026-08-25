package com.github.minecraftschurlimods.bibliocraft.util.block;

import com.github.minecraftschurlimods.bibliocraft.api.woodtype.BibliocraftWoodType;
import com.github.minecraftschurlimods.bibliocraft.util.holder.ColoredWoodTypeDeferredHolder;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.block.Block;

public class ColoredWoodTypeBlockItem extends BlockItem {
    protected final BibliocraftWoodType woodType;
    protected final DyeColor color;

    public ColoredWoodTypeBlockItem(ColoredWoodTypeDeferredHolder<Block, ? extends Block> holder, BibliocraftWoodType woodType, DyeColor color) {
        super(holder.get(woodType, color), new Properties());
        this.woodType = woodType;
        this.color = color;
    }

    public BibliocraftWoodType getWoodType() {
        return woodType;
    }

    public DyeColor getColor() {
        return color;
    }
}
