package com.github.minecraftschurlimods.bibliocraft.client.ber;

import com.github.minecraftschurlimods.bibliocraft.content.fancycrafter.FancyCrafterBlock;
import com.github.minecraftschurlimods.bibliocraft.content.fancycrafter.FancyCrafterBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public class FancyCrafterBER extends TileEntityRenderer<FancyCrafterBlockEntity> {
    public FancyCrafterBER(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }


    @Override
    public void render(FancyCrafterBlockEntity blockEntity, float partialTick, MatrixStack stack, IRenderTypeBuffer buffer, int light, int overlay) {
        World level = BCUtil.nonNull(blockEntity.getLevel());
        BlockPos pos = blockEntity.getBlockPos();
        Direction direction = blockEntity.getBlockState().getValue(FancyCrafterBlock.FACING);
        stack.pushPose();
        ClientUtil.setupCenteredBER(stack, blockEntity);
        if (!level.getBlockState(pos.above()).isSolidRender(level, pos.above())) {
            stack.pushPose();
            stack.translate(-0.1875f, 0.5, -0.1875f);
            stack.mulPose(Vector3f.XP.rotationDegrees(90));
            stack.mulPose(Vector3f.ZP.rotationDegrees(180));
            stack.scale(0.1875f, 0.1875f, 0.1875f);
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {
                    stack.pushPose();
                    stack.translate(-x, -y, 0);
                    ClientUtil.renderFixedItem(blockEntity.getItem(y * 3 + x), stack, buffer, light, overlay);
                    stack.popPose();
                }
            }
            stack.popPose();
        }
        if (!level.getBlockState(pos.offset(direction.getNormal())).isSolidRender(level, pos.offset(direction.getNormal()))) {
            stack.pushPose();
            stack.translate(-0.28125f, 0.03125, 0.25f);
            stack.mulPose(Vector3f.YP.rotationDegrees(180));
            stack.scale(0.1875f, 0.1875f, 0.1875f);
            for (int y = 0; y < 2; y++) {
                for (int x = 0; x < 4; x++) {
                    stack.pushPose();
                    stack.translate(-x, -y, 0);
                    ClientUtil.renderFixedItem(blockEntity.getItem(y * 2 + x + 10), stack, buffer, light, overlay);
                    stack.popPose();
                }
            }
            stack.popPose();
        }
        stack.popPose();
    }
}
