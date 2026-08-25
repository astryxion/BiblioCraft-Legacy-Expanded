package com.github.minecraftschurlimods.bibliocraft.content.printingtable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketBuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public final class PrintingTableInputPacket  {
    private final BlockPos pos;
    private final Optional<PrintingTableMode> mode;
    private final Optional<Integer> experience;

    public PrintingTableInputPacket(BlockPos pos, Optional<PrintingTableMode> mode, Optional<Integer> experience) {
        this.pos = pos;
        this.mode = mode;
        this.experience = experience;
    }

    public BlockPos pos() { return this.pos; }
    public Optional<PrintingTableMode> mode() { return this.mode; }
    public Optional<Integer> experience() { return this.experience; }

    public PrintingTableInputPacket(BlockPos pos, PrintingTableMode mode) {
        this(pos, Optional.of(mode), Optional.empty());
    }

    public PrintingTableInputPacket(BlockPos pos, int experience) {
        this(pos, Optional.empty(), Optional.of(experience));
    }

    public void encode(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeBoolean(mode.isPresent());
        if (mode.isPresent()) {
            PrintingTableMode.write(buf, mode.get());
        }
        buf.writeBoolean(experience.isPresent());
        if (experience.isPresent()) {
            buf.writeInt(experience.get());
        }
    }

    public static PrintingTableInputPacket decode(PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        Optional<PrintingTableMode> mode = buf.readBoolean() ? Optional.of(PrintingTableMode.read(buf)) : Optional.empty();
        Optional<Integer> experience = buf.readBoolean() ? Optional.of(buf.readInt()) : Optional.empty();
        return new PrintingTableInputPacket(pos, mode, experience);
    }

    public static void handle(PrintingTableInputPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() == null) return;
            PlayerEntity player = ctx.get().getSender();
            TileEntity be = player.level.getBlockEntity(msg.pos());
            if (!(be instanceof PrintingTableBlockEntity)) return;
            PrintingTableBlockEntity blockEntity = (PrintingTableBlockEntity) be;
            msg.mode().ifPresent(blockEntity::setMode);
            if (msg.experience().isPresent() && (player.isCreative() || player.totalExperience >= msg.experience().get())) {
                blockEntity.addExperience(msg.experience().get());
                player.giveExperiencePoints(-msg.experience().get());
            }
        });
        ctx.get().setPacketHandled(true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrintingTableInputPacket other = (PrintingTableInputPacket) o;
        return java.util.Objects.equals(this.pos, other.pos) && java.util.Objects.equals(this.mode, other.mode) && java.util.Objects.equals(this.experience, other.experience);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.pos, this.mode, this.experience);
    }

    @Override
    public String toString() {
        return "PrintingTableInputPacket[" + "pos=" + this.pos + ", " + "mode=" + this.mode + ", " + "experience=" + this.experience + "]";
    }

}
