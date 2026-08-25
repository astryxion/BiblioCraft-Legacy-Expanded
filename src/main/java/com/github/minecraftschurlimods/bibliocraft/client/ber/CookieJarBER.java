package com.github.minecraftschurlimods.bibliocraft.client.ber;

import com.github.minecraftschurlimods.bibliocraft.content.cookiejar.CookieJarBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.init.BCTags;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.registry.Registry;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class CookieJarBER extends TileEntityRenderer<CookieJarBlockEntity> {
    public CookieJarBER(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }


    private final Random random = new Random(0);

    @Override
    public void render(CookieJarBlockEntity blockEntity, float partialTick, MatrixStack stack, IRenderTypeBuffer buffer, int light, int overlay) {
        List<ItemStack> items = IntStream.rangeClosed(0, blockEntity.getContainerSize() - 1)
                .mapToObj(blockEntity::getItem)
                .filter(e -> !e.isEmpty())
                .collect(java.util.stream.Collectors.toList());
        List<ItemStack> cookies = BCTags.Items.values(BCTags.Items.COOKIE_JAR_COOKIES).stream()
                .sorted(Comparator.comparing(a -> Registry.ITEM.getKey(a)))
                .map(ItemStack::new)
                .collect(java.util.stream.Collectors.toList());
        if (cookies.isEmpty()) {
            cookies = java.util.Collections.singletonList(new ItemStack(Items.COOKIE));
        }
        random.setSeed(blockEntity.getBlockPos().asLong());
        stack.translate(0.5, 0.5, 0.5);
        for (int i = 0; i < items.size(); i++) {
            ItemStack item = items.get(i);
            ItemStack cookie = cookies.get(random.nextInt(cookies.size()));
            stack.pushPose();
            cookiePosition(i, stack);
            ClientUtil.renderFixedItem(BCTags.Items.contains(BCTags.Items.COOKIE_JAR_COOKIES, item.getItem()) ? item : cookie, stack, buffer, light, overlay);
            stack.popPose();
        }
    }

    private void cookiePosition(int index, MatrixStack stack) {
        switch (index) {
            case 0:
                stack.translate(-0.1, -0.425, 0.1);
                break;
            case 1:
                stack.translate(-0.0875, -0.3875, -0.0875);
                stack.mulPose(Vector3f.XP.rotationDegrees(-15));
                break;
            case 2:
                stack.translate(0.1, -0.3325, 0);
                stack.mulPose(Vector3f.ZP.rotationDegrees(-20));
                break;
            case 3:
                stack.translate(0.1, -0.295, -0.125);
                stack.mulPose(Vector3f.XP.rotationDegrees(-15));
                stack.mulPose(Vector3f.ZP.rotationDegrees(-15));
                break;
            case 4:
                stack.translate(-0.1, -0.245, 0.15);
                stack.mulPose(Vector3f.XP.rotationDegrees(35));
                stack.mulPose(Vector3f.YP.rotationDegrees(180));
                break;
            case 5:
                stack.translate(-0.1, -0.1625, -0.125);
                stack.mulPose(Vector3f.XP.rotationDegrees(-30));
                stack.mulPose(Vector3f.ZP.rotationDegrees(5));
                break;
            case 6:
                stack.translate(-0.1, -0.1325, 0.1575);
                stack.mulPose(Vector3f.XP.rotationDegrees(45));
                stack.mulPose(Vector3f.ZP.rotationDegrees(10));
                stack.mulPose(Vector3f.YP.rotationDegrees(180));
                break;
            case 7:
                stack.translate(0.2, -0.125, 0.1);
                stack.mulPose(Vector3f.XP.rotationDegrees(30));
                stack.mulPose(Vector3f.ZP.rotationDegrees(-60));
                stack.mulPose(Vector3f.YP.rotationDegrees(-105));
                break;
        }
        stack.mulPose(Vector3f.XP.rotationDegrees(90));
        stack.scale(0.6f, 0.6f, 0.6f);
    }
}
