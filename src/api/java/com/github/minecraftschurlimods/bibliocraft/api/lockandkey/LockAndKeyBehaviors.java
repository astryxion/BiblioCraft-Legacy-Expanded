package com.github.minecraftschurlimods.bibliocraft.api.lockandkey;

import com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi;
import net.minecraft.tileentity.TileEntity;
import javax.annotation.Nullable;

/**
 * Holds the behaviors of the Lock and Key item for different block entities. Get via {@link BibliocraftApi#getLockAndKeyBehaviors()}.
 * Register a new behavior by subscribing to {@link RegisterLockAndKeyBehaviorEvent} and calling {@code register(...)} on it.
 * Make sure to put this call behind a {@code ModList.isLoaded("bibliocraft")} check, and in a separate class, to prevent accidental classloading (like you would with client classes).
 */
public interface LockAndKeyBehaviors {
    /**
     * @param blockEntity The {@link TileEntity} to get the behavior for.
     * @param <T>         The type of the {@link TileEntity}.
     * @return The {@link LockAndKeyBehavior} associated with the given {@link TileEntity}, or null if no such {@link LockAndKeyBehavior} has been registered.
     */
    @Nullable
    <T extends TileEntity> LockAndKeyBehavior<T> get(T blockEntity);
}
