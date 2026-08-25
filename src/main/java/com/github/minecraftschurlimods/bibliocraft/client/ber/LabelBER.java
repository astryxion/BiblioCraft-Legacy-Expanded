package com.github.minecraftschurlimods.bibliocraft.client.ber;

import com.github.minecraftschurlimods.bibliocraft.content.label.LabelBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class LabelBER extends TileEntityRenderer<LabelBlockEntity> {
    public LabelBER(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }


    @Override
    public void render(LabelBlockEntity blockEntity, float partialTick, MatrixStack stack, IRenderTypeBuffer buffer, int light, int overlay) {
        stack.pushPose();
        ClientUtil.setupCenteredBER(stack, blockEntity);
        stack.mulPose(Vector3f.YP.rotationDegrees(180));
        stack.translate(0, 0, 0.4375);
        stack.pushPose();
        stack.translate(0, -0.3125, 0);
        stack.scale(0.2f, 0.2f, 0.2f);
        ClientUtil.renderFixedItem(blockEntity.getItem(0), stack, buffer, light, overlay);
        stack.popPose();
        stack.pushPose();
        stack.translate(0.1875, -0.1875, 0);
        stack.scale(0.2f, 0.2f, 0.2f);
        ClientUtil.renderFixedItem(blockEntity.getItem(1), stack, buffer, light, overlay);
        stack.popPose();
        stack.pushPose();
        stack.translate(-0.1875, -0.1875, 0);
        stack.scale(0.2f, 0.2f, 0.2f);
        ClientUtil.renderFixedItem(blockEntity.getItem(2), stack, buffer, light, overlay);
        stack.popPose();
        stack.popPose();
    }
}
