package com.github.minecraftschurlimods.bibliocraft.util.lectern;

import com.github.minecraftschurlimods.bibliocraft.util.network.PacketClientDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record OpenBookInLecternPacket(BlockPos pos, ItemStack stack) {

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeItem(stack);
    }

    public static OpenBookInLecternPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        ItemStack stack = buf.readItem();
        return new OpenBookInLecternPacket(pos, stack);
    }

    public static void handle(OpenBookInLecternPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() == null) return;
            Player player = ctx.get().getSender();
            Level level = player.level();
            if (!(level.getBlockEntity(msg.pos()) instanceof LecternBlockEntity lectern)) return;
            if (lectern.getBook().isEmpty()) {
                lectern.setBook(msg.stack());
            }
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PacketClientDispatcher.openBookInLectern(msg.stack(), player, msg.pos()));
        });
        ctx.get().setPacketHandled(true);
    }
}
