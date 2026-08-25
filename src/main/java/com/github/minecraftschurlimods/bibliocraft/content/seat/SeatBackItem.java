package com.github.minecraftschurlimods.bibliocraft.content.seat;

import com.github.minecraftschurlimods.bibliocraft.api.woodtype.BibliocraftWoodType;
import com.github.minecraftschurlimods.bibliocraft.init.BCBlocks;
import com.github.minecraftschurlimods.bibliocraft.init.BCItems;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.block.ColoredWoodTypeBlockItem;
import com.github.minecraftschurlimods.bibliocraft.util.holder.ColoredWoodTypeDeferredHolder;
import net.minecraft.util.Util;
import net.minecraft.item.DyeColor;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import javax.annotation.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SeatBackItem extends ColoredWoodTypeBlockItem {
    @SuppressWarnings("CodeBlock2Expr")
    public static final Map<SeatBackBlock, Map<SeatBackType, Supplier<SeatBackItem>>> BLOCK_MAP = Collections.unmodifiableMap(Util.make(new HashMap<>(), map -> {
        BCBlocks.SEAT_BACK.map().forEach((wood, coloredHolder) -> coloredHolder.map().forEach((color, holder) -> {
            map.put(holder.get(), (new java.util.function.Supplier<java.util.Map>() { public java.util.Map get() { java.util.Map m = new java.util.LinkedHashMap(); m.put(SeatBackType.SMALL, BCItems.SMALL_SEAT_BACK.holder(wood, color)); m.put(SeatBackType.RAISED, BCItems.RAISED_SEAT_BACK.holder(wood, color)); m.put(SeatBackType.FLAT, BCItems.FLAT_SEAT_BACK.holder(wood, color)); m.put(SeatBackType.TALL, BCItems.TALL_SEAT_BACK.holder(wood, color)); m.put(SeatBackType.FANCY, BCItems.FANCY_SEAT_BACK.holder(wood, color)); return java.util.Collections.unmodifiableMap(m); } }).get());
        }));
    }));
    public final SeatBackType type;

    public SeatBackItem(ColoredWoodTypeDeferredHolder<Block, ? extends Block> holder, BibliocraftWoodType woodType, DyeColor color, SeatBackType type) {
        super(holder, woodType, color);
        this.type = type;
    }

    @Override
    @Nullable
    protected BlockState getPlacementState(BlockItemUseContext context) {
        BlockState state = BCUtil.nonNull(getBlock().getStateForPlacement(context));
        return canPlace(context, state) ? state.setValue(SeatBackBlock.TYPE, type) : null;
    }

    @Override
    public String getDescriptionId() {
        return super.getOrCreateDescriptionId();
    }
}

