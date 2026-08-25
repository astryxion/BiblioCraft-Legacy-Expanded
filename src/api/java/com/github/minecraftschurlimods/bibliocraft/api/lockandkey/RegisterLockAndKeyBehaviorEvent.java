package com.github.minecraftschurlimods.bibliocraft.api.lockandkey;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.LockCode;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Register your own {@link LockAndKeyBehavior}s here.
 * <p>
 * This event is not cancelable. This event is fired on the mod event bus.
 */
public class RegisterLockAndKeyBehaviorEvent extends Event implements IModBusEvent {
    private final Map<Class<? extends TileEntity>, LockAndKeyBehavior<? extends TileEntity>> values;

    
    public RegisterLockAndKeyBehaviorEvent(Map<Class<? extends TileEntity>, LockAndKeyBehavior<? extends TileEntity>> values) {
        this.values = values;
    }

    /**
     * Registers a new {@link LockAndKeyBehavior}.
     *
     * @param clazz      The class of the {@link TileEntity} to register the behavior for.
     * @param lockGetter The getter for the {@link TileEntity}'s {@link LockCode}.
     * @param lockSetter The setter for the {@link TileEntity}'s {@link LockCode}.
     * @param nameGetter The getter for the {@link TileEntity}'s display name.
     * @param <T>        The type of the {@link TileEntity}.
     */
    public <T extends TileEntity> void register(Class<T> clazz, Function<T, LockCode> lockGetter, BiConsumer<T, LockCode> lockSetter, Function<T, ITextComponent> nameGetter) {
        values.put(clazz, new LockAndKeyBehavior.Simple<>(lockGetter, lockSetter, nameGetter));
    }
}
