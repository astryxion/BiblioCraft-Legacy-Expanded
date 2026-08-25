package com.github.minecraftschurlimods.bibliocraft.client.ber;

import com.github.minecraftschurlimods.bibliocraft.content.clock.ClockBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.content.clock.FancyClockBlock;
import com.github.minecraftschurlimods.bibliocraft.content.clock.GrandfatherClockBlock;
import com.github.minecraftschurlimods.bibliocraft.content.clock.WallFancyClockBlock;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

/**
 * Conceptual credit for the clock hands:
 * <a href="https://github.com/MehVahdJukaar/Supplementaries/blob/master/common/src/main/java/net/mehvahdjukaar/supplementaries/client/renderers/tiles/ClockBlockTileRenderer.java">ClockBlockTileRenderer from the Supplementaries mod</a>
 */
public class ClockBER extends TileEntityRenderer<ClockBlockEntity> {
    public static final RenderMaterial HAND_MATERIAL = new RenderMaterial(PlayerContainer.BLOCK_ATLAS, BCUtil.bcLoc("block/clock_hand"));
    public static final RenderMaterial PENDULUM_MATERIAL = new RenderMaterial(PlayerContainer.BLOCK_ATLAS, BCUtil.mcLoc("block/gold_block"));
    private final ModelRenderer hourHand;
    private final ModelRenderer minuteHand;
    private final ModelRenderer pendulum;
    private final ModelRenderer grandfatherHourHand;
    private final ModelRenderer grandfatherMinuteHand;
    private final ModelRenderer grandfatherPendulum;
    private float rotation;
    private float rotationOld;
    private long lastUpdateTick;

    public ClockBER(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
        hourHand = new ModelRenderer(16, 16, 0, 0);
        hourHand.addBox(-0.25f, -0.25f, -0.25f, 0.5f, 1.5f, 0.5f);
        minuteHand = new ModelRenderer(16, 16, 0, 0);
        minuteHand.addBox(-0.25f, -0.25f, -0.25f, 0.5f, 2, 0.5f);
        pendulum = new ModelRenderer(16, 16, 0, 0);
        pendulum.addBox(-0.25f, 0, -0.25f, 0.5f, 2, 0.5f);
        ModelRenderer pendulumHead = new ModelRenderer(16, 16, 7, 7);
        pendulumHead.addBox(-1, 2, -0.5f, 2, 2, 1);
        pendulum.addChild(pendulumHead);
        grandfatherHourHand = new ModelRenderer(16, 16, 0, 0);
        grandfatherHourHand.addBox(-0.25f, -0.25f, -0.25f, 0.5f, 2, 0.5f);
        grandfatherMinuteHand = new ModelRenderer(16, 16, 0, 0);
        grandfatherMinuteHand.addBox(-0.25f, -0.25f, -0.25f, 0.5f, 2.5f, 0.5f);
        grandfatherPendulum = new ModelRenderer(16, 16, 0, 0);
        grandfatherPendulum.addBox(-0.25f, 0, -0.25f, 0.5f, 15, 0.5f);
        ModelRenderer grandfatherHead = new ModelRenderer(16, 16, 7, 7);
        grandfatherHead.addBox(-1, 15, -0.5f, 2, 2, 1);
        grandfatherPendulum.addChild(grandfatherHead);
    }

    @Override
    public void render(ClockBlockEntity blockEntity, float partialTick, MatrixStack stack, IRenderTypeBuffer buffer, int light, int overlay) {
        IVertexBuilder handMaterial = HAND_MATERIAL.buffer(buffer, RenderType::entityCutout);
        IVertexBuilder pendulumMaterial = PENDULUM_MATERIAL.buffer(buffer, RenderType::entityCutout);
        stack.pushPose();
        ClientUtil.setupCenteredBER(stack, blockEntity);
        if (blockEntity.getBlockState().getBlock() instanceof FancyClockBlock) {
            renderHands(blockEntity, hourHand, minuteHand, 0.15625, 0.15625, stack, handMaterial, light, overlay);
            renderPendulum(blockEntity, pendulum, 4, -0.0625, 0.09375, stack, pendulumMaterial, light, overlay);
        } else if (blockEntity.getBlockState().getBlock() instanceof WallFancyClockBlock) {
            renderHands(blockEntity, hourHand, minuteHand, 0.15625, -0.15625, stack, handMaterial, light, overlay);
            renderPendulum(blockEntity, pendulum, 4, -0.0625, -0.21875, stack, pendulumMaterial, light, overlay);
        } else if (blockEntity.getBlockState().getBlock() instanceof GrandfatherClockBlock) {
            renderHands(blockEntity, grandfatherHourHand, grandfatherMinuteHand, 0.125, 0.15625, stack, handMaterial, light, overlay);
            renderPendulum(blockEntity, grandfatherPendulum, 17, -0.125, 0.09375, stack, pendulumMaterial, light, overlay);
        }
        stack.popPose();
    }

    private void renderHands(ClockBlockEntity blockEntity, ModelRenderer hourHand, ModelRenderer minuteHand, double y, double z, MatrixStack stack, IVertexBuilder vc, int light, int overlay) {
        World level = BCUtil.nonNull(blockEntity.getLevel());
        float rotation = level.dimensionType().natural() ? -((level.getDayTime() + 6000) % 12000) * 0.03f : getRotation(level);
        stack.pushPose();
        stack.translate(0, y, z);
        stack.pushPose();
        stack.mulPose(Vector3f.ZP.rotationDegrees(rotation));
        hourHand.render(stack, vc, light, overlay);
        stack.popPose();
        stack.pushPose();
        stack.mulPose(Vector3f.ZP.rotationDegrees((rotation * 12) % 360));
        minuteHand.render(stack, vc, light, overlay);
        stack.popPose();
        stack.popPose();
    }

    private void renderPendulum(ClockBlockEntity blockEntity, ModelRenderer pendulum, float pendulumSize, double y, double z, MatrixStack stack, IVertexBuilder vc, int light, int overlay) {
        float rotation = (float) Math.sin((BCUtil.nonNull(blockEntity.getLevel()).getDayTime() % 40 - 20) * Math.PI / 20);
        stack.pushPose();
        stack.translate(0, y, z);
        stack.mulPose(Vector3f.ZP.rotationDegrees(180));
        stack.mulPose(Vector3f.ZP.rotation(rotation / pendulumSize));
        pendulum.render(stack, vc, light, overlay);
        stack.popPose();
    }

    /**
     * Get the rotation values for "unnatural" dimensions, e.g. the nether or end.
     * Adapted from the {@link net.minecraft.item.ItemModelsProperties} for the clock.
     */
    private float getRotation(World level) {
        float rotationNew = (float) Math.random();
        if (level.getGameTime() != lastUpdateTick) {
            lastUpdateTick = level.getGameTime();
            float f = rotationNew - rotation;
            f = MathHelper.positiveModulo(f + 0.5f, 1) - 0.5f;
            rotationOld += f * 0.1f;
            rotationOld *= 0.9f;
            rotation = MathHelper.positiveModulo(rotation + rotationOld, 1);
        }
        return rotation * (float) Math.PI * 4;
    }
}
