package com.github.minecraftschurlimods.bibliocraft.util.block;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Schedules container slot + BE sync at end of server tick. Syncs each scheduled BE at end of
 * current tick and again at end of next tick so our state wins over any deferred vanilla
 * packet (e.g. from setBlock when opening display case) that would overwrite with empty.
 */
public final class ContainerSyncScheduler {
    private static final List<BCBlockEntity> PENDING = new ArrayList<>();
    /** BEs to sync again next tick (so correct state overwrites any stale packet). */
    private static final Set<BCBlockEntity> SYNC_NEXT_TICK = new HashSet<>();

    public static void schedule(BCBlockEntity blockEntity) {
        if (blockEntity == null || blockEntity.level() == null || blockEntity.level().isClientSide) return;
        synchronized (PENDING) {
            if (!PENDING.contains(blockEntity)) PENDING.add(blockEntity);
        }
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            List<BCBlockEntity> toSync;
            Set<BCBlockEntity> toSyncAgain;
            synchronized (PENDING) {
                toSyncAgain = new HashSet<>(SYNC_NEXT_TICK);
                SYNC_NEXT_TICK.clear();
                if (!PENDING.isEmpty()) {
                    toSync = new ArrayList<>(PENDING);
                    PENDING.clear();
                    for (BCBlockEntity be : toSync) SYNC_NEXT_TICK.add(be);
                } else toSync = List.of();
            }
            for (BCBlockEntity be : toSync) {
                if (be.level() != null && !be.level().isClientSide) be.syncSlotsToViewers();
            }
            for (BCBlockEntity be : toSyncAgain) {
                if (be.level() != null && !be.level().isClientSide) be.syncSlotsToViewers();
            }
        });
    }
}
