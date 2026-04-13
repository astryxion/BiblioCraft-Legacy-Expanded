package com.github.minecraftschurlimods.bibliocraft.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * After the client applies a block entity data packet, force a section rebuild so
 * BlockEntityRenderers re-render with the new data. Fixes cookie jar, bookshelf, display case,
 * and fast-equip not updating until the GUI is opened. Running on the main thread and calling
 * setSectionDirty forces the chunk section to recompile so BERs see the updated BE.
 */
@Mixin(ClientPacketListener.class)
public class ClientBlockEntityDataMixin {

    @Inject(method = "handleBlockEntityData", at = @At("RETURN"))
    private void bibliocraft$afterBlockEntityData(ClientboundBlockEntityDataPacket packet, CallbackInfo ci) {
        BlockPos pos = packet.getPos();
        Minecraft mc = Minecraft.getInstance();
        Runnable invalidate = () -> {
            Level level = mc.level;
            if (level == null) return;
            BlockState state = level.getBlockState(pos);
            level.sendBlockUpdated(pos, state, state, Block.UPDATE_ALL);
            LevelRenderer lr = mc.levelRenderer;
            if (lr != null) {
                int sx = pos.getX() >> 4, sy = pos.getY() >> 4, sz = pos.getZ() >> 4;
                lr.setSectionDirty(sx, sy, sz);
                lr.setSectionDirtyWithNeighbors(sx, sy, sz);
            }
        };
        mc.execute(invalidate);
        mc.execute(() -> mc.execute(invalidate));
    }
}
