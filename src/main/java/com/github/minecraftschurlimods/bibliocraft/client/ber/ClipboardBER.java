package com.github.minecraftschurlimods.bibliocraft.client.ber;

import com.github.minecraftschurlimods.bibliocraft.client.ClipboardReadOnlyRenderer;
import com.github.minecraftschurlimods.bibliocraft.content.clipboard.ClipboardBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class ClipboardBER extends TileEntityRenderer<ClipboardBlockEntity> {
    public ClipboardBER(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }


    @Override
    public void render(ClipboardBlockEntity blockEntity, float partialTick, MatrixStack stack, IRenderTypeBuffer buffer, int light, int overlay) {
        stack.pushPose();
        ClientUtil.setupCenteredBER(stack, blockEntity);
        stack.mulPose(Vector3f.XP.rotationDegrees(180));
        stack.translate(-0.25, -0.25, 0.4375);
        stack.translate(0, 0, -1 / 1024d);
        float scale = 1 / 256f;
        stack.scale(scale, scale, 0);
        ClipboardReadOnlyRenderer.render(stack, buffer, blockEntity.getContent(), 128, 148);
        stack.popPose();
    }
}
