package com.github.minecraftschurlimods.bibliocraft.client.ber;

import com.github.minecraftschurlimods.bibliocraft.content.fancyarmorstand.FancyArmorStandBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.content.fancyarmorstand.FancyArmorStandEntity;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;

public class FancyArmorStandBER implements BlockEntityRenderer<FancyArmorStandBlockEntity> {
    @Override
    public void render(FancyArmorStandBlockEntity blockEntity, float partialTick, PoseStack stack, MultiBufferSource buffer, int light, int overlay) {
        FancyArmorStandEntity entity = blockEntity.getDisplayEntity();
        if (entity != null) {
            stack.pushPose();
            stack.translate(0.5, 0.0625, 0.5);
            stack.mulPose(Axis.YP.rotationDegrees(entity.getYHeadRot()));
            ClientUtil.getMc().getEntityRenderDispatcher().render(entity, 0, 0, 0, 0, partialTick, stack, buffer, light);
            stack.popPose();
        }
    }
}
