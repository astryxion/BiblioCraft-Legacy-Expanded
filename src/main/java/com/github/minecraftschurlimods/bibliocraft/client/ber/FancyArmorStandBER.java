package com.github.minecraftschurlimods.bibliocraft.client.ber;

import com.github.minecraftschurlimods.bibliocraft.content.fancyarmorstand.FancyArmorStandBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.content.fancyarmorstand.FancyArmorStandEntity;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.AxisAlignedBB;

public class FancyArmorStandBER extends TileEntityRenderer<FancyArmorStandBlockEntity> {
    public FancyArmorStandBER(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }


    @Override
    public void render(FancyArmorStandBlockEntity blockEntity, float partialTick, MatrixStack stack, IRenderTypeBuffer buffer, int light, int overlay) {
        FancyArmorStandEntity entity = blockEntity.getDisplayEntity();
        if (entity != null) {
            stack.pushPose();
            stack.translate(0.5, 0.0625, 0.5);
            stack.mulPose(Vector3f.YP.rotationDegrees(entity.getYHeadRot()));
            ClientUtil.getMc().getEntityRenderDispatcher().render(entity, 0, 0, 0, 0, partialTick, stack, buffer, light);
            stack.popPose();
        }
    }

    public AxisAlignedBB getRenderBoundingBox(FancyArmorStandBlockEntity blockEntity) {
        return new AxisAlignedBB(blockEntity.getBlockPos()).expandTowards(0, 1, 0);
    }
}
