package com.github.minecraftschurlimods.bibliocraft.content.bigbook;

import com.github.minecraftschurlimods.bibliocraft.util.lectern.LecternUtil;
import com.mojang.datafixers.util.Either;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.LecternTileEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.function.Supplier;

public final class SetBigBookPageInLecternPacket  {
    private final int page;
    private final Either<Hand, BlockPos> target;

    public SetBigBookPageInLecternPacket(int page, Either<Hand, BlockPos> target) {
        this.page = page;
        this.target = target;
    }

    public int page() { return this.page; }
    public Either<Hand, BlockPos> target() { return this.target; }

    public void encode(PacketBuffer buf) {
        buf.writeInt(page);
        buf.writeBoolean(target.left().isPresent());
        if (target.left().isPresent()) {
            buf.writeBoolean(target.left().get() == Hand.MAIN_HAND);
        } else {
            buf.writeBlockPos(target.right().get());
        }
    }

    public static SetBigBookPageInLecternPacket decode(PacketBuffer buf) {
        int page = buf.readInt();
        boolean isHand = buf.readBoolean();
        Either<Hand, BlockPos> target = isHand
                ? Either.left(buf.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND)
                : Either.right(buf.readBlockPos());
        return new SetBigBookPageInLecternPacket(page, target);
    }

    public static void handle(SetBigBookPageInLecternPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() == null) return;
            PlayerEntity player = ctx.get().getSender();
            msg.target().ifLeft(left -> updateStack(player.getItemInHand(left), msg.page()));
            msg.target().ifRight(right -> LecternUtil.tryGetLecternAndRun(player.level, right, lectern -> {
                updateStack(lectern.getBook(), msg.page());
                setLecternPage(lectern, msg.page());
            }));
        });
        ctx.get().setPacketHandled(true);
    }

    /**
     * 1.16 {@link LecternTileEntity#setPage(int)} is private. Look up the official mapped name at first use
     * so class init cannot crash the client when sending this packet. 1.20 SRG {@code m_59532_} does not exist here.
     */
    private static Method lecternSetPage;

    private static void setLecternPage(LecternTileEntity lectern, int page) {
        try {
            if (lecternSetPage == null) {
                lecternSetPage = LecternTileEntity.class.getDeclaredMethod("setPage", int.class);
                lecternSetPage.setAccessible(true);
            }
            lecternSetPage.invoke(lectern, page);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static void updateStack(ItemStack stack, int page) {
        if (stack.getOrCreateTag().contains(BigBookContent.NBT_KEY)) {
            BigBookContent bc = BigBookContent.getFromStack(stack);
            if (!bc.pages().isEmpty()) {
                int clamped = Math.min(Math.max(page, 0), bc.pages().size() - 1);
                BigBookContent.setOnStack(stack, new BigBookContent(new ArrayList<>(bc.pages()), clamped));
            }
        }
        if (stack.getOrCreateTag().contains(WrittenBigBookContent.NBT_KEY)) {
            WrittenBigBookContent wbc = WrittenBigBookContent.getFromStack(stack);
            if (!wbc.pages().isEmpty()) {
                int clamped = Math.min(Math.max(page, 0), wbc.pages().size() - 1);
                WrittenBigBookContent.setOnStack(stack, new WrittenBigBookContent(new ArrayList<>(wbc.pages()), wbc.title(), wbc.author(), wbc.generation(), clamped));
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SetBigBookPageInLecternPacket other = (SetBigBookPageInLecternPacket) o;
        return this.page == other.page && java.util.Objects.equals(this.target, other.target);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.page, this.target);
    }

    @Override
    public String toString() {
        return "SetBigBookPageInLecternPacket[" + "page=" + this.page + ", " + "target=" + this.target + "]";
    }

}
