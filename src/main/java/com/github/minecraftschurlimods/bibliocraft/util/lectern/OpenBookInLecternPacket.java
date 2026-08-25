package com.github.minecraftschurlimods.bibliocraft.util.lectern;

import com.github.minecraftschurlimods.bibliocraft.util.network.PacketClientDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.tileentity.LecternTileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public final class OpenBookInLecternPacket  {
    private final BlockPos pos;
    private final ItemStack stack;

    public OpenBookInLecternPacket(BlockPos pos, ItemStack stack) {
        this.pos = pos;
        this.stack = stack;
    }

    public BlockPos pos() { return this.pos; }
    public ItemStack stack() { return this.stack; }

    public void encode(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeItem(stack);
    }

    public static OpenBookInLecternPacket decode(PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        ItemStack stack = buf.readItem();
        return new OpenBookInLecternPacket(pos, stack);
    }

    public static void handle(OpenBookInLecternPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() == null) return;
            PlayerEntity player = ctx.get().getSender();
            World level = player.level;
            if (!(level.getBlockEntity(msg.pos()) instanceof LecternTileEntity)) return;
            LecternTileEntity lectern = (LecternTileEntity) level.getBlockEntity(msg.pos());
            if (lectern.getBook().isEmpty()) {
                lectern.setBook(msg.stack());
            }
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PacketClientDispatcher.openBookInLectern(msg.stack(), player, msg.pos()));
        });
        ctx.get().setPacketHandled(true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpenBookInLecternPacket other = (OpenBookInLecternPacket) o;
        return java.util.Objects.equals(this.pos, other.pos) && java.util.Objects.equals(this.stack, other.stack);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.pos, this.stack);
    }

    @Override
    public String toString() {
        return "OpenBookInLecternPacket[" + "pos=" + this.pos + ", " + "stack=" + this.stack + "]";
    }

}
