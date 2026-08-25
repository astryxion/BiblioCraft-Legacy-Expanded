package com.github.minecraftschurlimods.bibliocraft.content.slottedbook;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SlottedBookItem extends Item {
    public SlottedBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand usedHand) {
        if (level.isClientSide()) return ActionResult.success(player.getItemInHand(usedHand));
        if (player instanceof ServerPlayerEntity) {
            ServerPlayerEntity sp = (ServerPlayerEntity) player;
            net.minecraftforge.fml.network.NetworkHooks.openGui(sp, new SimpleNamedContainerProvider((id, inv, p) -> new SlottedBookMenu(id, inv, usedHand), getDescription()), buf -> buf.writeEnum(usedHand));
        }
        return ActionResult.consume(player.getItemInHand(usedHand));
    }
}
