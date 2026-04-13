package com.github.minecraftschurlimods.bibliocraft.init;

/**
 * Data component types are not available in Forge 1.20.1.
 * All mod item/block data is stored via NBT using getFromStack/setOnStack helpers on the content types.
 */
public interface BCDataComponents {

    /**
     * Empty method, called by {@link BCRegistries#init(net.minecraftforge.eventbus.api.IEventBus)} to classload this class.
     */
    static void init() {
    }
}
