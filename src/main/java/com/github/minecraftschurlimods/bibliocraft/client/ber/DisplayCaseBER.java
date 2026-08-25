package com.github.minecraftschurlimods.bibliocraft.client.ber;

import com.github.minecraftschurlimods.bibliocraft.content.displaycase.DisplayCaseBlock;
import com.github.minecraftschurlimods.bibliocraft.content.displaycase.DisplayCaseBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.content.displaycase.WallDisplayCaseBlock;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.block.Block;

public class DisplayCaseBER extends TileEntityRenderer<DisplayCaseBlockEntity> {
    public DisplayCaseBER(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }


    @Override
    public void render(DisplayCaseBlockEntity blockEntity, float partialTick, MatrixStack stack, IRenderTypeBuffer buffer, int light, int overlay) {
        stack.pushPose();
        ClientUtil.setupCenteredBER(stack, blockEntity);
        Block block = blockEntity.getBlockState().getBlock();
        if (block instanceof WallDisplayCaseBlock) {
            stack.mulPose(Vector3f.YP.rotationDegrees(180));
        } else if (block instanceof DisplayCaseBlock) {
            stack.mulPose(Vector3f.XP.rotationDegrees(90));
            stack.mulPose(Vector3f.ZP.rotationDegrees(180));
        }
        stack.translate(0, 0, 0.25f);
        stack.scale(0.5f, 0.5f, 0.5f);
        ClientUtil.renderFixedItem(blockEntity.getItem(0), stack, buffer, light, overlay);
        stack.popPose();
    }
}
