package com.github.minecraftschurlimods.bibliocraft.client.ber;

import com.github.minecraftschurlimods.bibliocraft.content.toolrack.ToolRackBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class ToolRackBER extends TileEntityRenderer<ToolRackBlockEntity> {
    public ToolRackBER(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }


    @Override
    public void render(ToolRackBlockEntity blockEntity, float partialTick, MatrixStack stack, IRenderTypeBuffer buffer, int light, int overlay) {
        stack.pushPose();
        ClientUtil.setupCenteredBER(stack, blockEntity);
        stack.translate(-0.21875f, 0.21875f, -0.03125f); // -3.5 / 16, 3.5 / 16, -1 / 32
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                stack.pushPose();
                stack.translate(j * 0.4375f, -i * 0.4375f, 0); // j * 7 / 16, -i * 7 / 16
                stack.scale(0.4375f, 0.4375f, 0.4375f); // 7 / 16
                stack.mulPose(Vector3f.YP.rotationDegrees(180));
                ClientUtil.renderFixedItem(blockEntity.getItem(i * 2 + j), stack, buffer, light, overlay);
                stack.popPose();
            }
        }
        stack.popPose();
    }
}
