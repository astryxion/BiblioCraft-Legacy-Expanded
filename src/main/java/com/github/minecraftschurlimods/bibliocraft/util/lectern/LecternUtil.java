package com.github.minecraftschurlimods.bibliocraft.util.lectern;

import com.github.minecraftschurlimods.bibliocraft.BCEventHandler;
import com.github.minecraftschurlimods.bibliocraft.content.bigbook.BigBookContent;
import com.github.minecraftschurlimods.bibliocraft.content.bigbook.WrittenBigBookContent;
import com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog.StockroomCatalogContent;
import com.github.minecraftschurlimods.bibliocraft.init.BCItems;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.block.LecternBlock;
import net.minecraft.tileentity.LecternTileEntity;
import net.minecraft.block.BlockState;
import net.minecraftforge.fml.network.PacketDistributor;
import javax.annotation.Nullable;

import java.lang.reflect.Field;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Contains helper methods related to lecterns.
 */
public final class LecternUtil {
    /**
     * 1.16 {@code pageCount} is private. Official mappings use the Mojang field name; 1.20 SRG {@code f_59529_}
     * is not present on this class.
     */
    private static final Field LECTERN_PAGE_COUNT_FIELD = findLecternPageCountField();

    private static Field findLecternPageCountField() {
        try {
            Field field = LecternTileEntity.class.getDeclaredField("pageCount");
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setLecternPageCount(LecternTileEntity lectern, int count) {
        try {
            LECTERN_PAGE_COUNT_FIELD.setInt(lectern, count);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Called to handle a lectern being right-clicked. Handles putting/taking various Bibliocraft books into/from lecterns.
     *
     * @param level   The {@link World} context.
     * @param pos     The {@link BlockPos} of the lectern.
     * @param state   The {@link BlockState} of the lectern.
     * @param lectern The {@link LecternTileEntity} of the lectern.
     * @param player  The {@link PlayerEntity} using the lectern.
     * @param hand    The {@link Hand} context.
     * @return Whether Bibliocraft has consumed the right click and it should no longer propagate, or not.
     */
    @SuppressWarnings("DataFlowIssue")
    public static boolean handleLecternUse(World level, BlockPos pos, BlockState state, LecternTileEntity lectern, PlayerEntity player, Hand hand) {
        ItemStack book = lectern.getBook();
        if (book.isEmpty()) {
            // this workaround is needed to set the page count correctly
            // if it can be added to vanilla/neo somehow, this can be scrapped
            ItemStack stack = player.getItemInHand(hand);
            if (!stack.getItem().is(ItemTags.LECTERN_BOOKS)) return false;
            if (!stack.getOrCreateTag().contains(BigBookContent.NBT_KEY) && !stack.getOrCreateTag().contains(WrittenBigBookContent.NBT_KEY) && !stack.getOrCreateTag().contains(StockroomCatalogContent.NBT_KEY))
                return false;
            ItemStack toPut = stack.copy();
            toPut.setCount(1);
            stack.shrink(1);
            lectern.setBook(toPut);
            LecternBlock.resetBookState(level, pos, state, true);
            level.playSound(null, pos, SoundEvents.BOOK_PUT, SoundCategory.BLOCKS, 1f, 1f);
            if (stack.getOrCreateTag().contains(BigBookContent.NBT_KEY)) {
                setLecternPageCount(lectern, BigBookContent.getFromStack(stack).pages().size());
            } else if (stack.getOrCreateTag().contains(WrittenBigBookContent.NBT_KEY)) {
                setLecternPageCount(lectern, WrittenBigBookContent.getFromStack(stack).pages().size());
            } else if (stack.getOrCreateTag().contains(StockroomCatalogContent.NBT_KEY)) {
                setLecternPageCount(lectern, 1);
            }
            return true;
        } else if (book.getOrCreateTag().contains(BigBookContent.NBT_KEY) || book.getOrCreateTag().contains(WrittenBigBookContent.NBT_KEY) || book.getOrCreateTag().contains(StockroomCatalogContent.NBT_KEY)) {
            if (player.isSecondaryUseActive()) {
                takeLecternBook(player, level, pos);
            } else if (!level.isClientSide() && player instanceof ServerPlayerEntity) {
                ServerPlayerEntity sp = (ServerPlayerEntity) player;
                BCEventHandler.getChannel().send(PacketDistributor.PLAYER.with(() -> sp), new OpenBookInLecternPacket(pos, book));
            }
            return true;
        }
        return false;
    }

    /**
     * Removes the book from the lectern at the given location and adds it to the {@link PlayerEntity}'s inventory.
     *
     * @param player The {@link PlayerEntity} to add the lectern's book to.
     * @param level  The {@link World} of the lectern.
     * @param pos    The {@link BlockPos} of the lectern.
     */
    public static void takeLecternBook(PlayerEntity player, World level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof LecternTileEntity)) return;
        LecternTileEntity lectern = (LecternTileEntity) level.getBlockEntity(pos);
        ItemStack stack = lectern.getBook();
        lectern.setBook(ItemStack.EMPTY);
        LecternBlock.resetBookState(level, pos, level.getBlockState(pos), false);
        if (!player.inventory.add(stack)) {
            player.drop(stack, false);
        }
    }

    /**
     * Runs the given {@link Consumer} if a {@link LecternTileEntity} is found at the given location.
     *
     * @param level    The {@link World} of the lectern.
     * @param pos      The {@link BlockPos} of the lectern.
     * @param consumer The {@link Consumer} to run.
     */
    public static void tryGetLecternAndRun(World level, BlockPos pos, Consumer<LecternTileEntity> consumer) {
        if (!level.getBlockState(pos).hasProperty(LecternBlock.HAS_BOOK) || !level.getBlockState(pos).getValue(LecternBlock.HAS_BOOK))
            return;
        if (!(level.getBlockEntity(pos) instanceof LecternTileEntity)) return;
        LecternTileEntity lectern = (LecternTileEntity) level.getBlockEntity(pos);
        consumer.accept(lectern);
    }

    /**
     * Runs the given {@link Function} and returns its result if a {@link LecternTileEntity} is found at the given location.
     *
     * @param level    The {@link World} of the lectern.
     * @param pos      The {@link BlockPos} of the lectern.
     * @param function The {@link Function} to run.
     */
    @Nullable
    public static <T> T tryGetLecternAndApply(World level, BlockPos pos, Function<LecternTileEntity, T> function) {
        if (!level.getBlockState(pos).hasProperty(LecternBlock.HAS_BOOK) || !level.getBlockState(pos).getValue(LecternBlock.HAS_BOOK))
            return null;
        if (!(level.getBlockEntity(pos) instanceof LecternTileEntity)) return null;
        LecternTileEntity lectern = (LecternTileEntity) level.getBlockEntity(pos);
        return function.apply(lectern);
    }
}
