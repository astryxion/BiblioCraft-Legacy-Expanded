package com.github.minecraftschurlimods.bibliocraft.content.bigbook;

import com.github.minecraftschurlimods.bibliocraft.util.lectern.LecternUtil;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.network.NetworkEvent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.function.Supplier;

public record SetBigBookPageInLecternPacket(int page, Either<InteractionHand, BlockPos> target) {

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(page);
        buf.writeBoolean(target.left().isPresent());
        if (target.left().isPresent()) {
            buf.writeBoolean(target.left().get() == InteractionHand.MAIN_HAND);
        } else {
            buf.writeBlockPos(target.right().get());
        }
    }

    public static SetBigBookPageInLecternPacket decode(FriendlyByteBuf buf) {
        int page = buf.readInt();
        boolean isHand = buf.readBoolean();
        Either<InteractionHand, BlockPos> target = isHand
                ? Either.left(buf.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND)
                : Either.right(buf.readBlockPos());
        return new SetBigBookPageInLecternPacket(page, target);
    }

    public static void handle(SetBigBookPageInLecternPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() == null) return;
            Player player = ctx.get().getSender();
            msg.target().ifLeft(left -> updateStack(player.getItemInHand(left), msg.page()));
            msg.target().ifRight(right -> LecternUtil.tryGetLecternAndRun(player.level(), right, lectern -> {
                updateStack(lectern.getBook(), msg.page());
                setLecternPage(lectern, msg.page());
            }));
        });
        ctx.get().setPacketHandled(true);
    }

    /** SRG: m_59532_ — {@code setPage(int)}. Name {@code setPage} is not present at runtime under SRG. */
    private static final Method LECTERN_SET_PAGE =
            ObfuscationReflectionHelper.findMethod(LecternBlockEntity.class, "m_59532_", int.class);

    private static void setLecternPage(LecternBlockEntity lectern, int page) {
        try {
            LECTERN_SET_PAGE.invoke(lectern, page);
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
}
