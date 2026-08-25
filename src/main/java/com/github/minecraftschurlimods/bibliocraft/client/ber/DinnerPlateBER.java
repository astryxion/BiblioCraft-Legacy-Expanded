package com.github.minecraftschurlimods.bibliocraft.client.ber;

import com.github.minecraftschurlimods.bibliocraft.content.dinnerplate.DinnerPlateBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class DinnerPlateBER extends TileEntityRenderer<DinnerPlateBlockEntity> {
    public DinnerPlateBER(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }


    @Override
    public void render(DinnerPlateBlockEntity blockEntity, float partialTick, MatrixStack stack, IRenderTypeBuffer buffer, int light, int overlay) {
        stack.pushPose();
        stack.translate(0.5, 0.0625, 0.5);
        stack.mulPose(Vector3f.XP.rotationDegrees(90));
        stack.scale(0.4375f, 0.4375f, 0.4375f);
        ClientUtil.renderFixedItem(blockEntity.getItem(0), stack, buffer, light, overlay);
        stack.popPose();
    }
}
