package com.github.minecraftschurlimods.bibliocraft.client;

import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.Entity;

public class EmptyEntityRenderer extends EntityRenderer<Entity> {
    private static final ResourceLocation TEXTURE = BCUtil.mcLoc("missingno");

    public EmptyEntityRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    public ResourceLocation getTextureLocation(Entity pEntity) {
        return TEXTURE;
    }
}
