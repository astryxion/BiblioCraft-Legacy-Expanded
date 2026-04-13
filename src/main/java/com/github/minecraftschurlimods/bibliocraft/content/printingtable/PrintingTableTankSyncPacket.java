package com.github.minecraftschurlimods.bibliocraft.content.printingtable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record PrintingTableTankSyncPacket(BlockPos pos, Fluid fluid, int amount) {

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(BuiltInRegistries.FLUID.getKey(fluid).toString());
        buf.writeInt(amount);
    }

    public static PrintingTableTankSyncPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        Fluid fluid = BuiltInRegistries.FLUID.get(new ResourceLocation(buf.readUtf()));
        int amount = buf.readInt();
        return new PrintingTableTankSyncPacket(pos, fluid, amount);
    }

    public static void handle(PrintingTableTankSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() == null) return;
            if (ctx.get().getSender().level().getBlockEntity(msg.pos()) instanceof PrintingTableBlockEntity printingTable) {
                printingTable.getFluidCapability().update(msg);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
