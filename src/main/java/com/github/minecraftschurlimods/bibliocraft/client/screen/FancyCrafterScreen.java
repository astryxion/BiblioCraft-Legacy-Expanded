package com.github.minecraftschurlimods.bibliocraft.client.screen;

import com.github.minecraftschurlimods.bibliocraft.content.fancycrafter.FancyCrafterMenu;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.player.PlayerInventory;

public class FancyCrafterScreen extends BCScreenWithToggleableSlots<FancyCrafterMenu> {
    private static final ResourceLocation BACKGROUND = BCUtil.bcLoc("textures/gui/fancy_crafter.png");

    public FancyCrafterScreen(FancyCrafterMenu menu, PlayerInventory inventory, ITextComponent title) {
        super(menu, inventory, title, BACKGROUND);
        imageHeight = 192;
        inventoryLabelY = 99;
    }
}
