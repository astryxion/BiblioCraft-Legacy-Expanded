package com.github.minecraftschurlimods.bibliocraft.client.model;

import net.minecraft.client.renderer.model.ItemCameraTransforms;
import com.github.minecraftschurlimods.bibliocraft.content.table.TableBlock;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.JSONUtils;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.common.data.ExistingFileHelper;
import javax.annotation.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class TableModel extends DynamicBlockModel {
    public static final IModelLoader<Geometry> LOADER = new IModelLoader<Geometry>() {
        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {
        }

        @Override
        public Geometry read(JsonDeserializationContext context, JsonObject jsonObject) {
            Map<TableBlock.Type, BlockModel> map = new HashMap<>();
            for (TableBlock.Type type : TableBlock.Type.values()) {
                map.put(type, context.deserialize(JSONUtils.getAsJsonObject(jsonObject, type.getSerializedName()), BlockModel.class));
            }
            return new Geometry(map);
        }
    };
    private final Map<TableBlock.Type, IBakedModel> baseMap;

    public TableModel(boolean useAmbientOcclusion, boolean isGui3d, boolean usesBlockLight, TextureAtlasSprite particle, Map<TableBlock.Type, IBakedModel> baseMap) {
        super(useAmbientOcclusion, isGui3d, usesBlockLight, particle);
        this.baseMap = baseMap;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand, IModelData extraData) {
        TableBlock.Type type = TableBlock.Type.NONE;
        if (state != null && state.hasProperty(TableBlock.TYPE)) {
            type = state.getValue(TableBlock.TYPE);
        }
        return baseMap.get(type).getQuads(state, side, rand, extraData);
    }

    @Override
    public IBakedModel handlePerspective(ItemCameraTransforms.TransformType transformType, MatrixStack poseStack) {
        return baseMap.get(TableBlock.Type.NONE).handlePerspective(transformType, poseStack);
    }

    public static class Geometry implements IModelGeometry<Geometry> {
        private final Map<TableBlock.Type, BlockModel> baseMap;

        public Geometry(Map<TableBlock.Type, BlockModel> baseMap) {
            this.baseMap = baseMap;
        }

        @Override
        public IBakedModel bake(IModelConfiguration context, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelState, ItemOverrideList overrides, ResourceLocation modelId) {
            boolean useBlockLight = context.isSideLit();
            Map<TableBlock.Type, IBakedModel> newBaseMap = new HashMap<>();
            baseMap.forEach((k, v) -> newBaseMap.put(k, v.bake(bakery, spriteGetter, modelState, new ResourceLocation(modelId.getNamespace(), modelId.getPath() + "_" + k.getSerializedName()))));
            return new TableModel(context.useSmoothLighting(), context.isShadedInGui(), useBlockLight, spriteGetter.apply(context.resolveTexture("particle")), newBaseMap);
        }

        @Override
        public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
            Set<RenderMaterial> textures = new HashSet<RenderMaterial>();
            baseMap.forEach((k, v) -> textures.addAll(v.getMaterials(modelGetter, missingTextureErrors)));
            return textures;
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public static class Builder extends CustomLoaderBuilder<BlockModelBuilder> {
        private final Map<TableBlock.Type, JsonObject> modelMap = new HashMap<>();
        private ResourceLocation particle;

        public Builder(BlockModelBuilder parent, ExistingFileHelper existingFileHelper) {
            super(BCUtil.bcLoc("table"), parent, existingFileHelper);
        }

        public Builder withModelForType(TableBlock.Type type, JsonObject model) {
            modelMap.put(type, model);
            return this;
        }

        public Builder withParticle(ResourceLocation particle) {
            this.particle = particle;
            return this;
        }

        @Override
        public JsonObject toJson(JsonObject json) {
            if (particle == null) throw new IllegalStateException("Block particle was not specified!");
            JsonObject textures = new JsonObject();
            textures.addProperty("particle", particle.toString());
            json.add("textures", textures);
            modelMap.forEach((k, v) -> json.add(k.getSerializedName(), v));
            return super.toJson(json);
        }
    }
}
