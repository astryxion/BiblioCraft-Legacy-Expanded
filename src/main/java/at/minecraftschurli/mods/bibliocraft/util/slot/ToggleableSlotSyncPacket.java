package at.minecraftschurli.mods.bibliocraft.util.slot;

import at.minecraftschurli.mods.bibliocraft.util.BCUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import at.minecraftschurli.mods.bibliocraft.content.fancycrafter.FancyCrafterBlockEntity;
import at.minecraftschurli.mods.bibliocraft.content.fancycrafter.FancyCrafterMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public record ToggleableSlotSyncPacket(BlockPos pos, int slot, boolean disabled) implements CustomPacketPayload {
    public static final Type<ToggleableSlotSyncPacket> TYPE = new Type<>(BCUtil.bcLoc("toggleable_slot_sync"));
    public static final StreamCodec<ByteBuf, ToggleableSlotSyncPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ToggleableSlotSyncPacket::pos,
            ByteBufCodecs.INT, ToggleableSlotSyncPacket::slot,
            ByteBufCodecs.BOOL, ToggleableSlotSyncPacket::disabled,
            ToggleableSlotSyncPacket::new);

    public void handle(Player player) {
        Level level = player.level();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof HasToggleableSlots slots) {
            int craftingSlot = slot;
            if (blockEntity instanceof FancyCrafterBlockEntity && player.containerMenu instanceof FancyCrafterMenu menu
                    && menu.getBlockPos().equals(pos)) {
                craftingSlot = menu.getSlot(slot).index;
            }
            slots.setSlotDisabled(craftingSlot, disabled);
            blockEntity.setChanged();
            if (blockEntity instanceof FancyCrafterBlockEntity && player.containerMenu instanceof FancyCrafterMenu menu
                    && menu.getBlockPos().equals(pos)) {
                menu.updateCraftingResult();
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
