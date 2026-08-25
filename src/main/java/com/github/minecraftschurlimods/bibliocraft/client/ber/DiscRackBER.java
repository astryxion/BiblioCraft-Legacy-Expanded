package com.github.minecraftschurlimods.bibliocraft.client.ber;

import com.github.minecraftschurlimods.bibliocraft.content.discrack.DiscRackBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.content.discrack.WallDiscRackBlock;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.state.properties.BlockStateProperties;

public class DiscRackBER extends TileEntityRenderer<DiscRackBlockEntity> {
    public DiscRackBER(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }


    @Override
    public void render(DiscRackBlockEntity blockEntity, float partialTick, MatrixStack stack, IRenderTypeBuffer buffer, int light, int overlay) {
        for (int i = 0; i < blockEntity.getContainerSize(); i++) {
            stack.pushPose();
            if (blockEntity.getBlockState().getBlock() instanceof WallDiscRackBlock) {
                stack.translate(0.5, 0.5, 0.5);
                Direction facing = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
                stack.mulPose(Vector3f.YP.rotationDegrees(facing.getAxis() == Direction.Axis.X ? 90 : 0));
                if (facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE) {
                    stack.mulPose(Vector3f.YP.rotationDegrees(180));
                }
                stack.mulPose(Vector3f.XP.rotationDegrees(90));
            } else {
                ClientUtil.setupCenteredBER(stack, blockEntity);
            }
            stack.translate(0, -0.25f, (i - 4) / 16f);
            stack.scale(0.5f, 0.5f, 0.5f);
            ClientUtil.renderFixedItem(blockEntity.getItem(i), stack, buffer, light, overlay);
            stack.popPose();
        }
    }
}
