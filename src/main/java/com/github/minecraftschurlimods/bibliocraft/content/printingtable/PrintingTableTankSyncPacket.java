package com.github.minecraftschurlimods.bibliocraft.content.printingtable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public final class PrintingTableTankSyncPacket  {
    private final BlockPos pos;
    private final Fluid fluid;
    private final int amount;

    public PrintingTableTankSyncPacket(BlockPos pos, Fluid fluid, int amount) {
        this.pos = pos;
        this.fluid = fluid;
        this.amount = amount;
    }

    public BlockPos pos() { return this.pos; }
    public Fluid fluid() { return this.fluid; }
    public int amount() { return this.amount; }

    public void encode(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(Registry.FLUID.getKey(fluid).toString());
        buf.writeInt(amount);
    }

    public static PrintingTableTankSyncPacket decode(PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        Fluid fluid = Registry.FLUID.get(new ResourceLocation(buf.readUtf()));
        int amount = buf.readInt();
        return new PrintingTableTankSyncPacket(pos, fluid, amount);
    }

    public static void handle(PrintingTableTankSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() == null) return;
            if (ctx.get().getSender().level.getBlockEntity(msg.pos()) instanceof PrintingTableBlockEntity) {
                PrintingTableBlockEntity printingTable = (PrintingTableBlockEntity) ctx.get().getSender().level.getBlockEntity(msg.pos());
                printingTable.getFluidCapability().update(msg);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrintingTableTankSyncPacket other = (PrintingTableTankSyncPacket) o;
        return java.util.Objects.equals(this.pos, other.pos) && java.util.Objects.equals(this.fluid, other.fluid) && this.amount == other.amount;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.pos, this.fluid, this.amount);
    }

    @Override
    public String toString() {
        return "PrintingTableTankSyncPacket[" + "pos=" + this.pos + ", " + "fluid=" + this.fluid + ", " + "amount=" + this.amount + "]";
    }

}
