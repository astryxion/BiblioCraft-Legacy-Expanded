package com.github.minecraftschurlimods.bibliocraft.util;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Packet send helpers. Implementations are set by the Fabric networking layer.
 * Call sendToServer from client only; sendToPlayer/sendToTracking from server only.
 */
public final class BCPackets {
    public static Consumer<CustomPacketPayload> sendToServerImpl;
    public static BiConsumer<ServerPlayer, CustomPacketPayload> sendToPlayerImpl;
    public static TriConsumer<ServerLevel, ChunkPos, CustomPacketPayload> sendToTrackingImpl;

    public static void sendToServer(CustomPacketPayload payload) {
        if (sendToServerImpl != null) sendToServerImpl.accept(payload);
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        if (sendToPlayerImpl != null) sendToPlayerImpl.accept(player, payload);
    }

    public static void sendToTracking(ServerLevel level, ChunkPos chunkPos, CustomPacketPayload payload) {
        if (sendToTrackingImpl != null) sendToTrackingImpl.accept(level, chunkPos, payload);
    }

    @FunctionalInterface
    public interface TriConsumer<A, B, C> {
        void accept(A a, B b, C c);
    }
}
