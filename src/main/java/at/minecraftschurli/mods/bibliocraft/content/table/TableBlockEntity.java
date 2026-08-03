package at.minecraftschurli.mods.bibliocraft.content.table;

import at.minecraftschurli.mods.bibliocraft.init.BCBlockEntities;
import at.minecraftschurli.mods.bibliocraft.util.block.BCBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import org.jspecify.annotations.Nullable;

public class TableBlockEntity extends BCBlockEntity {
    public TableBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntities.TABLE.get(), 2, pos, state);
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

    @Override
    public boolean isValid(int slot, ItemVariant stack) {
        if (slot == 1) {
            return isEmpty(1) && !stack.isBlank() && stack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof WoolCarpetBlock;
        }
        return super.isValid(slot, stack);
    }

    @Nullable
    public DyeColor getClothColor() {
        if (isEmpty(1) || !(getItemHandler().getResource(1).getItem() instanceof BlockItem bi) || !(bi.getBlock() instanceof WoolCarpetBlock carpet)) return null;
        return carpet.getColor();
    }
}
