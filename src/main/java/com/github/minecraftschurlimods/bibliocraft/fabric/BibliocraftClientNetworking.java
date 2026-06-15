package com.github.minecraftschurlimods.bibliocraft.fabric;

import com.github.minecraftschurlimods.bibliocraft.content.clock.ClockSyncPacket;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableSetRecipePacket;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableTankSyncPacket;
import com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog.StockroomCatalogListPacket;
import com.github.minecraftschurlimods.bibliocraft.util.BCPackets;
import com.github.minecraftschurlimods.bibliocraft.util.lectern.OpenBookInLecternPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/**
 * Client-side Fabric networking: S2C receivers and sendToServer impl.
 */
public final class BibliocraftClientNetworking {

    public static void register() {
        BCPackets.sendToServerImpl = ClientPlayNetworking::send;

        ClientPlayNetworking.registerGlobalReceiver(OpenBookInLecternPacket.TYPE, (payload, context) -> payload.handleClient(context.player()));
        ClientPlayNetworking.registerGlobalReceiver(StockroomCatalogListPacket.TYPE, (payload, context) -> payload.handleClient(context.player()));
        ClientPlayNetworking.registerGlobalReceiver(ClockSyncPacket.TYPE, (payload, context) -> payload.handle(context.player()));
        ClientPlayNetworking.registerGlobalReceiver(PrintingTableTankSyncPacket.TYPE, (payload, context) -> payload.handleClient(context.player()));
        ClientPlayNetworking.registerGlobalReceiver(PrintingTableSetRecipePacket.TYPE, (payload, context) -> payload.handleClient(context.player()));
    }
}
