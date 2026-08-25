package com.github.minecraftschurlimods.bibliocraft.util.network;

import com.github.minecraftschurlimods.bibliocraft.content.clock.ClockSyncPacket;
import com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog.StockroomCatalogListPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/**
 * Dispatches to client-only packet handling via reflection so common (server + client) packet classes
 * never reference {@code net.minecraft.client} types — required for dedicated server startup.
 */
public final class PacketClientDispatcher {
    private static final String HANDLERS = "com.github.minecraftschurlimods.bibliocraft.client.network.BCPacketClientHandlers";

    private PacketClientDispatcher() {}

    public static void stockroomCatalogList(StockroomCatalogListPacket msg) {
        invoke("stockroomCatalogList", StockroomCatalogListPacket.class, msg);
    }

    public static void clockSync(ClockSyncPacket msg) {
        invoke("clockSync", ClockSyncPacket.class, msg);
    }

    public static void openBookInLectern(ItemStack stack, PlayerEntity player, BlockPos pos) {
        try {
            Class<?> c = Class.forName(HANDLERS);
            c.getMethod("openBookInLectern", ItemStack.class, PlayerEntity.class, BlockPos.class).invoke(null, stack, player, pos);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static void invoke(String name, Class<?> argType, Object arg) {
        try {
            Class<?> c = Class.forName(HANDLERS);
            c.getMethod(name, argType).invoke(null, arg);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
