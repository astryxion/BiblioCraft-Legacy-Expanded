package com.github.minecraftschurlimods.bibliocraft.apiimpl;

import com.github.minecraftschurlimods.bibliocraft.api.lockandkey.LockAndKeyBehavior;
import com.github.minecraftschurlimods.bibliocraft.api.lockandkey.LockAndKeyBehaviors;
import com.github.minecraftschurlimods.bibliocraft.api.lockandkey.RegisterLockAndKeyBehaviorEvent;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class LockAndKeyBehaviorsImpl implements LockAndKeyBehaviors {
    private final Map<Class<? extends TileEntity>, LockAndKeyBehavior<? extends TileEntity>> values = new HashMap<>();
    private boolean loaded = false;

    
    public void register() {
        FMLJavaModLoadingContext.get().getModEventBus().post(new RegisterLockAndKeyBehaviorEvent(values));
        loaded = true;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public <T extends TileEntity> LockAndKeyBehavior<T> get(T blockEntity) {
        if (!loaded)
            throw new IllegalStateException("Tried to access LockAndKeyBehaviors#get() before registration was done!");
        return (LockAndKeyBehavior<T>) values.keySet()
                .stream()
                .filter(e -> e.isAssignableFrom(blockEntity.getClass()))
                .map(values::get)
                .findAny()
                .orElse(null);
    }
}
