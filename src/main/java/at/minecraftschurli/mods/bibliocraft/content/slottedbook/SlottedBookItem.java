package at.minecraftschurli.mods.bibliocraft.content.slottedbook;

import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
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
    public InteractionResult use(Level level, Player player, InteractionHand usedHand) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (player instanceof ServerPlayer sp) {
            sp.openMenu(new ExtendedMenuProvider<InteractionHand>() {
                @Override
                public InteractionHand getScreenOpeningData(ServerPlayer player) {
                    return usedHand;
                }

                @Override
                public Component getDisplayName() {
                    return getName(player.getItemInHand(usedHand));
                }

                @Override
                public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player player) {
                    return new SlottedBookMenu(syncId, inventory, usedHand);
                }
            });
        }
        return InteractionResult.CONSUME;
    }
}
