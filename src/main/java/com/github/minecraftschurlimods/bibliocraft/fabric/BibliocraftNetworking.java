package com.github.minecraftschurlimods.bibliocraft.fabric;

import com.github.minecraftschurlimods.bibliocraft.content.bigbook.BigBookSignPacket;
import com.github.minecraftschurlimods.bibliocraft.content.bigbook.BigBookSyncPacket;
import com.github.minecraftschurlimods.bibliocraft.content.bigbook.SetBigBookPageInLecternPacket;
import com.github.minecraftschurlimods.bibliocraft.content.clipboard.ClipboardSyncPacket;
import com.github.minecraftschurlimods.bibliocraft.content.clock.ClockSyncPacket;
import com.github.minecraftschurlimods.bibliocraft.content.fancysign.FancySignSyncPacket;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableInputPacket;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableSetRecipePacket;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableTankSyncPacket;
import com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog.StockroomCatalogListPacket;
import com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog.StockroomCatalogRequestListPacket;
import com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog.StockroomCatalogSyncPacket;
import com.github.minecraftschurlimods.bibliocraft.content.typewriter.TypewriterSyncPacket;
import com.github.minecraftschurlimods.bibliocraft.util.BCPackets;
import com.github.minecraftschurlimods.bibliocraft.util.lectern.OpenBookInLecternPacket;
import com.github.minecraftschurlimods.bibliocraft.util.lectern.TakeLecternBookPacket;
import com.github.minecraftschurlimods.bibliocraft.util.slot.ToggleableSlotSyncPacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

/**
 * Fabric packet registration. Registers all mod packets with PayloadTypeRegistry,
 * ServerPlayNetworking (C2S), and sets BCPackets server-side send impls.
 * Client-side S2C receivers and sendToServerImpl are set in client init.
 */
public final class BibliocraftNetworking {

    public static void register() {
        registerC2S();
        registerS2C();
        registerC2SReceivers();
        setServerSendImpls();
    }

    private static void registerC2S() {
        PayloadTypeRegistry.playC2S().register(ClockSyncPacket.TYPE, ClockSyncPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ClipboardSyncPacket.TYPE, ClipboardSyncPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(TypewriterSyncPacket.TYPE, TypewriterSyncPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(ToggleableSlotSyncPacket.TYPE, ToggleableSlotSyncPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(TakeLecternBookPacket.TYPE, TakeLecternBookPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(BigBookSignPacket.TYPE, BigBookSignPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(BigBookSyncPacket.TYPE, BigBookSyncPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(SetBigBookPageInLecternPacket.TYPE, SetBigBookPageInLecternPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(FancySignSyncPacket.TYPE, FancySignSyncPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(StockroomCatalogRequestListPacket.TYPE, StockroomCatalogRequestListPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(StockroomCatalogSyncPacket.TYPE, StockroomCatalogSyncPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(PrintingTableInputPacket.TYPE, PrintingTableInputPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(PrintingTableSetRecipePacket.TYPE, PrintingTableSetRecipePacket.STREAM_CODEC);
    }

    private static void registerS2C() {
        PayloadTypeRegistry.playS2C().register(OpenBookInLecternPacket.TYPE, OpenBookInLecternPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(StockroomCatalogListPacket.TYPE, StockroomCatalogListPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ClockSyncPacket.TYPE, ClockSyncPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(PrintingTableTankSyncPacket.TYPE, PrintingTableTankSyncPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(PrintingTableSetRecipePacket.TYPE, PrintingTableSetRecipePacket.STREAM_CODEC);
    }

    private static void registerC2SReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(ClockSyncPacket.TYPE, (payload, context) -> payload.handle(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(ClipboardSyncPacket.TYPE, (payload, context) -> payload.handleServer(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(TypewriterSyncPacket.TYPE, (payload, context) -> payload.handleServer(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(ToggleableSlotSyncPacket.TYPE, (payload, context) -> payload.handleServer(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(TakeLecternBookPacket.TYPE, (payload, context) -> payload.handleServer(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(BigBookSignPacket.TYPE, (payload, context) -> payload.handleServer(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(BigBookSyncPacket.TYPE, (payload, context) -> payload.handleServer(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(SetBigBookPageInLecternPacket.TYPE, (payload, context) -> payload.handleServer(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(FancySignSyncPacket.TYPE, (payload, context) -> payload.handleServer(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(StockroomCatalogRequestListPacket.TYPE, (payload, context) -> payload.handleServer(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(StockroomCatalogSyncPacket.TYPE, (payload, context) -> payload.handleServer(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(PrintingTableInputPacket.TYPE, (payload, context) -> payload.handleServer(context.player()));
        ServerPlayNetworking.registerGlobalReceiver(PrintingTableSetRecipePacket.TYPE, (payload, context) -> payload.handleServer(context.player()));
    }

    private static void setServerSendImpls() {
        BCPackets.sendToPlayerImpl = ServerPlayNetworking::send;
        BCPackets.sendToTrackingImpl = (level, chunkPos, payload) -> {
            for (ServerPlayer player : PlayerLookup.tracking(level, chunkPos)) {
                ServerPlayNetworking.send(player, payload);
            }
        };
    }
}
