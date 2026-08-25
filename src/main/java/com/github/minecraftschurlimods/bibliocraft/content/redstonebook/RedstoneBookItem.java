package com.github.minecraftschurlimods.bibliocraft.content.redstonebook;

import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RedstoneBookItem extends Item {
    public RedstoneBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        if (level.isClientSide()) {
            ClientUtil.openRedstoneBookScreen();
        }
        return super.use(level, player, hand);
    }
}
