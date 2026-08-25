package com.github.minecraftschurlimods.bibliocraft.content.clipboard;

import com.github.minecraftschurlimods.bibliocraft.init.BCBlocks;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.world.World;

public class ClipboardItem extends BlockItem {
    public ClipboardItem() {
        super(BCBlocks.CLIPBOARD.get(), new Properties().stacksTo(1));
    }

    @Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            ClientUtil.openClipboardScreen(stack, hand);
        }
        return ActionResult.success(stack);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        return context.getPlayer() != null && context.getPlayer().isSecondaryUseActive() ? super.useOn(context) : ActionResultType.PASS;
    }
}
