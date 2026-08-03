package at.minecraftschurli.mods.bibliocraft.content.bookcase;

import at.minecraftschurli.mods.bibliocraft.init.BCBlockEntities;
import at.minecraftschurli.mods.bibliocraft.init.BCTags;
import at.minecraftschurli.mods.bibliocraft.util.block.BCMenuBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;

public class BookcaseBlockEntity extends BCMenuBlockEntity {
    private static final int SLOTS = 16;

    public BookcaseBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntities.BOOKCASE.get(), SLOTS, 1, defaultName("bookcase"), pos, state);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new BookcaseMenu(id, inventory, this);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        requestModelDataUpdate();
        if (level() instanceof ServerLevel serverLevel) {
            for (ServerPlayer player : serverLevel.getChunkSource().chunkMap.getPlayers(ChunkPos.containing(worldPosition), false)) {
                player.connection.send(getUpdatePacket());
            }
        }
    }

    public short getBooksMask() {
        short books = 0;
        for (int i = 0; i < SLOTS; i++) {
            books |= (short) ((isEmpty(i) ? 0 : 1) << i);
        }
        return books;
    }

    @Override
    public boolean isValid(int slot, ItemVariant stack) {
        return stack.isBlank() || stack.toStack(1).is(BCTags.Items.BOOKCASE_BOOKS);
    }
}
