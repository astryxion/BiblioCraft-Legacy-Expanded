package com.github.minecraftschurlimods.bibliocraft.client.network;

import com.github.minecraftschurlimods.bibliocraft.content.clock.ClockBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.content.clock.ClockSyncPacket;
import com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog.StockroomCatalogListPacket;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Client-only handlers for packets; loaded only when {@link com.github.minecraftschurlimods.bibliocraft.util.network.PacketClientDispatcher} runs on the client.
 */
public final class BCPacketClientHandlers {
    private BCPacketClientHandlers() {}

    public static void stockroomCatalogList(StockroomCatalogListPacket msg) {
        ClientUtil.setStockroomCatalogList(msg);
    }

    public static void clockSync(ClockSyncPacket msg) {
        Level level = Minecraft.getInstance().level;
        if (level == null || !level.hasChunkAt(msg.pos())) return;
        BlockEntity blockEntity = level.getBlockEntity(msg.pos());
        if (blockEntity instanceof ClockBlockEntity clock) {
            clock.setFromPacket(msg);
        }
    }

    public static void openBookInLectern(ItemStack stack, Player player, BlockPos pos) {
        ClientUtil.openScreenForLectern(stack, player, pos);
    }
}
