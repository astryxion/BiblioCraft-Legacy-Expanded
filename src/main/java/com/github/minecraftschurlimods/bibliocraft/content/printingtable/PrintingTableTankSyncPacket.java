package com.github.minecraftschurlimods.bibliocraft.content.printingtable;

import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluid;

public record PrintingTableTankSyncPacket(BlockPos pos, Fluid fluid, int amount) implements CustomPacketPayload {
    public static final Type<PrintingTableTankSyncPacket> TYPE = new Type<>(BCUtil.bcLoc("printing_table_tank_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PrintingTableTankSyncPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PrintingTableTankSyncPacket::pos,
            ByteBufCodecs.registry(Registries.FLUID), PrintingTableTankSyncPacket::fluid,
            ByteBufCodecs.INT, PrintingTableTankSyncPacket::amount,
            PrintingTableTankSyncPacket::new);

    public void handleClient(Player player) {
        if (player.level().getBlockEntity(pos) instanceof PrintingTableBlockEntity printingTable) {
            printingTable.getFluidCapability().update(this);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
