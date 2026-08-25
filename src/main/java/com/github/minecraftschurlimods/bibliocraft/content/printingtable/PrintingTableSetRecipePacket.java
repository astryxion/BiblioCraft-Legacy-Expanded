package com.github.minecraftschurlimods.bibliocraft.content.printingtable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public final class PrintingTableSetRecipePacket  {
    private final BlockPos pos;
    private final int duration;
    private final int maxDuration;
    private final int levelCost;

    public PrintingTableSetRecipePacket(BlockPos pos, int duration, int maxDuration, int levelCost) {
        this.pos = pos;
        this.duration = duration;
        this.maxDuration = maxDuration;
        this.levelCost = levelCost;
    }

    public BlockPos pos() { return this.pos; }
    public int duration() { return this.duration; }
    public int maxDuration() { return this.maxDuration; }
    public int levelCost() { return this.levelCost; }

    public void encode(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(duration);
        buf.writeInt(maxDuration);
        buf.writeInt(levelCost);
    }

    public static PrintingTableSetRecipePacket decode(PacketBuffer buf) {
        return new PrintingTableSetRecipePacket(buf.readBlockPos(), buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static void handle(PrintingTableSetRecipePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() == null) return;
            PlayerEntity player = ctx.get().getSender();
            World level = player.level;
            TileEntity be = level.getBlockEntity(msg.pos());
            if (!(be instanceof PrintingTableBlockEntity)) return;
            PrintingTableBlockEntity blockEntity = (PrintingTableBlockEntity) be;
            if (level.isClientSide()) {
                blockEntity.setFromPacket(msg);
            } else if (player instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                com.github.minecraftschurlimods.bibliocraft.BCEventHandler.getChannel().send(PacketDistributor.PLAYER.with(() -> serverPlayer), new PrintingTableSetRecipePacket(msg.pos(), blockEntity.getDuration(), blockEntity.getMaxDuration(), blockEntity.getLevelCost()));
            }
        });
        ctx.get().setPacketHandled(true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrintingTableSetRecipePacket other = (PrintingTableSetRecipePacket) o;
        return java.util.Objects.equals(this.pos, other.pos) && this.duration == other.duration && this.maxDuration == other.maxDuration && this.levelCost == other.levelCost;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.pos, this.duration, this.maxDuration, this.levelCost);
    }

    @Override
    public String toString() {
        return "PrintingTableSetRecipePacket[" + "pos=" + this.pos + ", " + "duration=" + this.duration + ", " + "maxDuration=" + this.maxDuration + ", " + "levelCost=" + this.levelCost + "]";
    }

}
