package at.minecraftschurli.mods.bibliocraft.content.printingtable;

import at.minecraftschurli.mods.bibliocraft.util.BCUtil;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public record PrintingTableTankSyncPacket(BlockPos pos, FluidVariant resource, int amount) implements CustomPacketPayload {
    public static final Type<PrintingTableTankSyncPacket> TYPE = new Type<>(BCUtil.bcLoc("printing_table_tank_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PrintingTableTankSyncPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PrintingTableTankSyncPacket::pos,
            FluidVariant.PACKET_CODEC, PrintingTableTankSyncPacket::resource,
            ByteBufCodecs.INT, PrintingTableTankSyncPacket::amount,
            PrintingTableTankSyncPacket::new);

    public void handle(Player player) {
        if (player.level().getBlockEntity(pos) instanceof PrintingTableBlockEntity printingTable) {
            printingTable.syncTank(this);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
