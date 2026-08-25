package com.github.minecraftschurlimods.bibliocraft.content.lockandkey;

import net.minecraft.util.text.TranslationTextComponent;
import com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi;
import com.github.minecraftschurlimods.bibliocraft.api.lockandkey.LockAndKeyBehavior;
import com.github.minecraftschurlimods.bibliocraft.util.Translations;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ActionResultType;
import net.minecraft.world.LockCode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;

public class LockAndKeyItem extends Item {
    public LockAndKeyItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        if (!context.isSecondaryUseActive()) return super.useOn(context);
        TileEntity blockEntity = context.getLevel().getBlockEntity(context.getClickedPos());
        PlayerEntity player = context.getPlayer();
        if (blockEntity == null || player == null) return super.useOn(context);
        ItemStack stack = context.getItemInHand();
        LockAndKeyBehavior<TileEntity> behavior = BibliocraftApi.getLockAndKeyBehaviors().get(blockEntity);
        if (behavior == null) return super.useOn(context);
        ITextComponent name = behavior.getDisplayName(blockEntity);
        if (hasNoCustomName(player, stack, name)) return ActionResultType.FAIL;
        LockCode lock = behavior.getLockKey(blockEntity);
        if (lock == LockCode.NO_LOCK) {
            behavior.setLockKey(blockEntity, newLockCode(stack));
            blockEntity.setChanged();
            player.displayClientMessage(new TranslationTextComponent(Translations.LOCK_AND_KEY_LOCKED_KEY, name), true);
            return ActionResultType.SUCCESS;
        } else if (lock.unlocksWith(stack)) {
            behavior.setLockKey(blockEntity, LockCode.NO_LOCK);
            blockEntity.setChanged();
            player.displayClientMessage(new TranslationTextComponent(Translations.LOCK_AND_KEY_UNLOCKED_KEY, name), true);
            return ActionResultType.SUCCESS;
        }
        return super.useOn(context);
    }

    private LockCode newLockCode(ItemStack stack) {
        return new LockCode(stack.hasCustomHoverName() ? stack.getHoverName().getString() : "");
    }

    private boolean hasNoCustomName(PlayerEntity player, ItemStack stack, ITextComponent displayName) {
        if (stack.hasCustomHoverName()) return false;
        player.displayClientMessage(new TranslationTextComponent(Translations.LOCK_AND_KEY_NO_CUSTOM_NAME_KEY, displayName), true);
        player.playNotifySound(SoundEvents.CHEST_LOCKED, SoundCategory.BLOCKS, 1.0F, 1.0F);
        return true;
    }
}
