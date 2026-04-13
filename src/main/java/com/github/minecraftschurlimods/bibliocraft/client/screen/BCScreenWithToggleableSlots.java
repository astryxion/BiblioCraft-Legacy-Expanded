package com.github.minecraftschurlimods.bibliocraft.client.screen;

import com.github.minecraftschurlimods.bibliocraft.util.block.BCMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BCScreenWithToggleableSlots<T extends BCMenu<?>> extends BCMenuScreen<T> {

    public BCScreenWithToggleableSlots(T menu, Inventory inventory, Component title, ResourceLocation background) {
        super(menu, inventory, title, background);
    }
}
