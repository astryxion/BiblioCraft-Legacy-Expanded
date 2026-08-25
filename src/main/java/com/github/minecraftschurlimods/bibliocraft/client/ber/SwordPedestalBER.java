package com.github.minecraftschurlimods.bibliocraft.client.ber;

import com.github.minecraftschurlimods.bibliocraft.content.swordpedestal.SwordPedestalBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class SwordPedestalBER extends TileEntityRenderer<SwordPedestalBlockEntity> {
    public SwordPedestalBER(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }


    @Override
    public void render(SwordPedestalBlockEntity blockEntity, float partialTick, MatrixStack stack, IRenderTypeBuffer buffer, int light, int overlay) {
        stack.pushPose();
        ClientUtil.setupCenteredBER(stack, blockEntity);
        stack.mulPose(Vector3f.YP.rotationDegrees(180));
        stack.translate(0, 1 / 16d, 0);
        stack.mulPose(Vector3f.ZP.rotationDegrees(135));
        stack.scale(0.6f, 0.6f, 0.6f);
        ClientUtil.renderFixedItem(blockEntity.getItem(0), stack, buffer, light, overlay);
        stack.popPose();
    }
}
