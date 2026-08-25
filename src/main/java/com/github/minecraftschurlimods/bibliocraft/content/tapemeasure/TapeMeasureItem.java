package com.github.minecraftschurlimods.bibliocraft.content.tapemeasure;

import net.minecraft.util.text.TranslationTextComponent;
import com.github.minecraftschurlimods.bibliocraft.init.BCSoundEvents;
import com.github.minecraftschurlimods.bibliocraft.util.Translations;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.ActionResultType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;

public class TapeMeasureItem extends Item {
    public TapeMeasureItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        if (player == null) return super.useOn(context);
        ItemStack stack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        StoredPosition stored = StoredPosition.getFromStack(stack);
        if (stored.position() != BlockPos.ZERO) {
            BlockPos distance = pos.subtract(stored.position());
            player.displayClientMessage(new TranslationTextComponent(Translations.TAPE_MEASURE_DISTANCE_KEY, distance.distManhattan(BlockPos.ZERO), Math.abs(distance.getX()), Math.abs(distance.getY()), Math.abs(distance.getZ())), true);
            StoredPosition.removeFromStack(stack);
            player.playSound(BCSoundEvents.TAPE_MEASURE_CLOSE.get(), 1, 1);
        } else {
            StoredPosition.setOnStack(stack, new StoredPosition(pos));
            player.playSound(BCSoundEvents.TAPE_MEASURE_OPEN.get(), 1, 1);
        }
        return ActionResultType.SUCCESS;
    }
}
