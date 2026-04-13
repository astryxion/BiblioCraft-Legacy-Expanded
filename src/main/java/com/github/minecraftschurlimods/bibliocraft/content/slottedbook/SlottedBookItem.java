package com.github.minecraftschurlimods.bibliocraft.content.slottedbook;

import com.github.minecraftschurlimods.bibliocraft.init.BCItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SlottedBookItem extends Item {
    public SlottedBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (level.isClientSide()) return InteractionResultHolder.success(player.getItemInHand(usedHand));
        if (player instanceof ServerPlayer sp) {
            InteractionHand handForMenu = player.getItemInHand(InteractionHand.MAIN_HAND).is(BCItems.SLOTTED_BOOK.get()) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            sp.openMenu(new SimpleMenuProvider((id, inv, p) -> new SlottedBookMenu(id, inv, handForMenu), getDescription()));
        }
        return InteractionResultHolder.consume(player.getItemInHand(usedHand));
    }
}
