package com.github.minecraftschurlimods.bibliocraft.client.ber;

import com.github.minecraftschurlimods.bibliocraft.content.table.TableBlock;
import com.github.minecraftschurlimods.bibliocraft.content.table.TableBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.util.ResourceLocation;
import java.util.Random;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.FilledMapItem;
import net.minecraft.block.CarpetBlock;
import net.minecraft.block.BlockState;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraft.util.math.vector.Matrix4f;

import java.util.HashMap;
import java.util.Map;

public class TableBER extends TileEntityRenderer<TableBlockEntity> {
    public TableBER(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }


    private static final RenderType MAP_BACKGROUND = RenderType.text(BCUtil.mcLoc("textures/map/map_background.png"));
    private static final RenderType MAP_BACKGROUND_CHECKERBOARD = RenderType.text(BCUtil.mcLoc("textures/map/map_background_checkerboard.png"));
    private static final Map<TableBlock.Type, Map<DyeColor, IBakedModel>> CLOTH_MAP = new HashMap<>();
    private static final Random RANDOM = new Random();

    public static void rebuildClothModelCache() {
        CLOTH_MAP.clear();
        ModelManager models = ClientUtil.getMc().getModelManager();
        for (TableBlock.Type type : TableBlock.Type.values()) {
            Map<DyeColor, IBakedModel> map = new HashMap<>();
            for (DyeColor color : DyeColor.values()) {
                ResourceLocation loc = BCUtil.bcLoc("block/color/" + color.getSerializedName() + "/table_cloth_" + type.getSerializedName());
                map.put(color, models.getModel(loc));
            }
            CLOTH_MAP.put(type, map);
        }
    }

    @Override
    public void render(TableBlockEntity blockEntity, float partialTick, MatrixStack stack, IRenderTypeBuffer buffer, int light, int overlay) {
        ItemStack item = blockEntity.getItem(0);
        stack.pushPose();
        if (item.getItem() == Items.FILLED_MAP) {
            stack.translate(0.125f, 1.001, 0.125f);
            stack.scale(0.75f, 0.75f, 0.75f);
            stack.scale(0.0078125f, 0.0078125f, 0.0078125f);
            stack.mulPose(Vector3f.XP.rotationDegrees(90));
            Minecraft minecraft = ClientUtil.getMc();
            Integer mapId = FilledMapItem.getMapId(item);
            MapData mapData = FilledMapItem.getSavedData(item, ClientUtil.getLevel());
            IVertexBuilder vc = buffer.getBuffer(mapData == null ? MAP_BACKGROUND : MAP_BACKGROUND_CHECKERBOARD);
            Matrix4f matrix4f = stack.last().pose();
            vc.vertex(matrix4f, -7f, 135f, 0f).color(255, 255, 255, 255).uv(0f, 1f).uv2(light).endVertex();
            vc.vertex(matrix4f, 135f, 135f, 0f).color(255, 255, 255, 255).uv(1f, 1f).uv2(light).endVertex();
            vc.vertex(matrix4f, 135f, -7f, 0f).color(255, 255, 255, 255).uv(1f, 0f).uv2(light).endVertex();
            vc.vertex(matrix4f, -7f, -7f, 0f).color(255, 255, 255, 255).uv(0f, 0f).uv2(light).endVertex();
            if (mapData != null) {
                minecraft.gameRenderer.getMapRenderer().render(stack, buffer, mapData, false, light);
            }
        } else {
            stack.translate(0.5, 1.03125, 0.5);
            stack.mulPose(Vector3f.YP.rotationDegrees(180));
            stack.mulPose(Vector3f.XP.rotationDegrees(90));
            stack.scale(0.5f, 0.5f, 0.5f);
            ClientUtil.renderFixedItem(item, stack, buffer, light, overlay);
        }
        stack.popPose();
        ItemStack carpet = blockEntity.getItem(1);
        if (carpet.isEmpty() || !(carpet.getItem() instanceof BlockItem)) return;
        BlockItem bi = (BlockItem) carpet.getItem();
        if (!(bi.getBlock() instanceof CarpetBlock)) return;
        CarpetBlock carpetBlock = (CarpetBlock) bi.getBlock();
        BlockState state = blockEntity.getBlockState();
        stack.pushPose();
        stack.translate(0.5, 0, 0.5);
        float clothRot = 0;
        switch (state.getValue(TableBlock.FACING)) {
            case SOUTH:
                clothRot = 180;
                break;
            case EAST:
                clothRot = 270;
                break;
            case WEST:
                clothRot = 90;
                break;
            default:
                clothRot = 0;
                break;
        }
        stack.mulPose(Vector3f.YP.rotationDegrees(clothRot));
        stack.translate(-0.5, 0, -0.5);
        ClientUtil.renderBakedModel(CLOTH_MAP.get(state.getValue(TableBlock.TYPE)).get(carpetBlock.getColor()), stack, buffer, blockEntity.getLevel(), blockEntity.getBlockPos(), state, RANDOM, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
        stack.popPose();
    }
}
