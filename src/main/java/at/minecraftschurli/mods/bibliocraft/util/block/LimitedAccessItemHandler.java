package at.minecraftschurli.mods.bibliocraft.util.block;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

// TODO PR this to neo?
public class LimitedAccessItemHandler {
    private final BCItemHandler delegate;
    private final AccessPredicate accessPredicate;

    public LimitedAccessItemHandler(BCItemHandler delegate, AccessPredicate accessPredicate) {
        this.delegate = delegate;
        this.accessPredicate = accessPredicate;
    }

    public int extract(int index, ItemVariant resource, int amount, TransactionContext transaction) {
        if (accessPredicate.canAccess(index, AccessPredicate.Mode.EXTRACT)) {
            return delegate.extract(index, resource, amount, transaction);
        }
        return 0;
    }

    public int insert(int index, ItemVariant resource, int amount, TransactionContext transaction) {
        if (accessPredicate.canAccess(index, AccessPredicate.Mode.INSERT)) {
            return delegate.insert(index, resource, amount, transaction);
        }
        return 0;
    }

    @FunctionalInterface
    public interface AccessPredicate {
        boolean canAccess(int index, AccessPredicate.Mode mode);

        enum Mode {
            INSERT, EXTRACT
        }
    }
}
