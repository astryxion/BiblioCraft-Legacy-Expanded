package com.github.minecraftschurlimods.bibliocraft.content.tapemeasure;

import com.github.minecraftschurlimods.bibliocraft.init.BCSoundEvents;
import com.github.minecraftschurlimods.bibliocraft.util.Translations;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public class TapeMeasureItem extends Item {
    public TapeMeasureItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return super.useOn(context);
        ItemStack stack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        StoredPosition stored = StoredPosition.getFromStack(stack);
        if (stored.position() != BlockPos.ZERO) {
            BlockPos distance = pos.subtract(stored.position());
            player.displayClientMessage(Component.translatable(Translations.TAPE_MEASURE_DISTANCE_KEY, distance.distManhattan(BlockPos.ZERO), Math.abs(distance.getX()), Math.abs(distance.getY()), Math.abs(distance.getZ())), true);
            StoredPosition.removeFromStack(stack);
            player.playSound(BCSoundEvents.TAPE_MEASURE_CLOSE.get(), 1, 1);
        } else {
            StoredPosition.setOnStack(stack, new StoredPosition(pos));
            player.playSound(BCSoundEvents.TAPE_MEASURE_OPEN.get(), 1, 1);
        }
        return InteractionResult.SUCCESS;
    }
}
