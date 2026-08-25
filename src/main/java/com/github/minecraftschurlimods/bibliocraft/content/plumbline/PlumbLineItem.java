package com.github.minecraftschurlimods.bibliocraft.content.plumbline;

import net.minecraft.util.text.TranslationTextComponent;
import com.github.minecraftschurlimods.bibliocraft.util.Translations;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.ActionResultType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.block.BlockState;

public class PlumbLineItem extends Item {
    public PlumbLineItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        Direction face = context.getClickedFace();
        if (face == Direction.UP || context.getPlayer() == null) return super.useOn(context);
        BlockPos pos = context.getClickedPos().offset(face.getNormal());
        BlockState state = context.getLevel().getBlockState(pos);
        int count = 0;
        while (state.isAir() || state.getCollisionShape(context.getLevel(), pos).isEmpty()) {
            count++;
            pos = pos.below();
            state = context.getLevel().getBlockState(pos);
        }
        if (count <= 2) return super.useOn(context);
        context.getPlayer().displayClientMessage(new TranslationTextComponent(Translations.PLUMB_LINE_DISTANCE_KEY, count), true);
        return ActionResultType.SUCCESS;
    }
}
