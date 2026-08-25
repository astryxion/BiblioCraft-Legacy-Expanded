package com.github.minecraftschurlimods.bibliocraft.api.lockandkey;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.LockCode;
import net.minecraft.tileentity.TileEntity;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Holds all information Bibliocraft needs to make the Lock and Key item work on a block entity.
 *
 * @param <T> The type of the {@link TileEntity}.
 */
public interface LockAndKeyBehavior<T extends TileEntity> {

    /**
     * Returns the {@link TileEntity}'s {@link LockCode}.
     *
     * @param blockEntity the {@link TileEntity}
     * @return the {@link TileEntity}'s {@link LockCode}.
     */
    LockCode getLockKey(T blockEntity);

    /**
     * Sets the {@link TileEntity}'s {@link LockCode}.
     *
     * @param blockEntity the {@link TileEntity}
     * @param lock        the {@link LockCode} to set
     */
    void setLockKey(T blockEntity, LockCode lock);

    /**
     * Returns the {@link TileEntity}'s display name.
     *
     * @param blockEntity the {@link TileEntity}
     * @return the {@link TileEntity}'s display name.
     */
    ITextComponent getDisplayName(T blockEntity);

    static final class Simple<T extends TileEntity> implements LockAndKeyBehavior<T> {
    private final Function<T, LockCode> lockGetter;
    private final BiConsumer<T, LockCode> lockSetter;
    private final Function<T, ITextComponent> nameGetter;

    public Simple(Function<T, LockCode> lockGetter, BiConsumer<T, LockCode> lockSetter, Function<T, ITextComponent> nameGetter) {
        this.lockGetter = lockGetter;
        this.lockSetter = lockSetter;
        this.nameGetter = nameGetter;
    }

    public Function<T, LockCode> lockGetter() { return this.lockGetter; }
    public BiConsumer<T, LockCode> lockSetter() { return this.lockSetter; }
    public Function<T, ITextComponent> nameGetter() { return this.nameGetter; }

        @Override
        public LockCode getLockKey(T blockEntity) {
            return lockGetter.apply(blockEntity);
        }

        @Override
        public void setLockKey(T blockEntity, LockCode lock) {
            lockSetter.accept(blockEntity, lock);
        }

        @Override
        public ITextComponent getDisplayName(T blockEntity) {
            return nameGetter.apply(blockEntity);
        }
    

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Simple other = (Simple) o;
        return java.util.Objects.equals(this.lockGetter, other.lockGetter) && java.util.Objects.equals(this.lockSetter, other.lockSetter) && java.util.Objects.equals(this.nameGetter, other.nameGetter);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.lockGetter, this.lockSetter, this.nameGetter);
    }

    @Override
    public String toString() {
        return "Simple[" + "lockGetter=" + this.lockGetter + ", " + "lockSetter=" + this.lockSetter + ", " + "nameGetter=" + this.nameGetter + "]";
    }

}
}
