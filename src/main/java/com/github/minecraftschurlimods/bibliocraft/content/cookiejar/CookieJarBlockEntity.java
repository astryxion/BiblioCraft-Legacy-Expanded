package com.github.minecraftschurlimods.bibliocraft.content.cookiejar;

import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCMenuBlockEntity;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;

public class CookieJarBlockEntity extends BCMenuBlockEntity {
    private int openCount;

    public CookieJarBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntities.COOKIE_JAR.get(), 8, defaultName("cookie_jar"), pos, state);
    }

    @Override
    protected Container createMenu(int id, PlayerInventory inventory) {
        return new CookieJarMenu(id, inventory, this);
    }

    private void updateBlockState(BlockState pState, boolean pOpen) {
        level().setBlock(getBlockPos(), pState.setValue(BarrelBlock.OPEN, pOpen), 3);
    }

    @Override
    public void startOpen(PlayerEntity pPlayer) {
        if (!this.remove && !pPlayer.isSpectator()) {
            if (this.openCount < 0) this.openCount = 0;
            this.openCount++;
            if (this.openCount == 1) {
                this.updateBlockState(this.getBlockState(), true);
            }
        }
    }

    @Override
    public void stopOpen(PlayerEntity pPlayer) {
        if (!this.remove && !pPlayer.isSpectator()) {
            this.openCount--;
            if (this.openCount <= 0) {
                this.openCount = 0;
                this.updateBlockState(this.getBlockState(), false);
            }
        }
    }
}
