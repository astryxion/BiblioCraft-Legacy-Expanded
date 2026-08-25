package com.github.minecraftschurlimods.bibliocraft.client.network;

import com.github.minecraftschurlimods.bibliocraft.content.clock.ClockBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.content.clock.ClockSyncPacket;
import com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog.StockroomCatalogListPacket;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;

/**
 * Client-only handlers for packets; loaded only when {@link com.github.minecraftschurlimods.bibliocraft.util.network.PacketClientDispatcher} runs on the client.
 */
public final class BCPacketClientHandlers {
    private BCPacketClientHandlers() {}

    public static void stockroomCatalogList(StockroomCatalogListPacket msg) {
        ClientUtil.setStockroomCatalogList(msg);
    }

    public static void clockSync(ClockSyncPacket msg) {
        World level = Minecraft.getInstance().level;
        if (level == null || !level.hasChunkAt(msg.pos())) return;
        TileEntity blockEntity = level.getBlockEntity(msg.pos());
        if (blockEntity instanceof ClockBlockEntity) {
            ClockBlockEntity clock = (ClockBlockEntity) blockEntity;
            clock.setFromPacket(msg);
        }
    }

    public static void openBookInLectern(ItemStack stack, PlayerEntity player, BlockPos pos) {
        ClientUtil.openScreenForLectern(stack, player, pos);
    }
}
