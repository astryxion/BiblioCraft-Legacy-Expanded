package com.github.minecraftschurlimods.bibliocraft.client.model;

import com.github.minecraftschurlimods.bibliocraft.content.table.TableBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class TableModel extends DynamicBlockModel {
    private final Map<TableBlock.Type, BakedModel> baseMap;

    public TableModel(boolean useAmbientOcclusion, boolean isGui3d, boolean usesBlockLight, TextureAtlasSprite particle, Map<TableBlock.Type, BakedModel> baseMap) {
        super(useAmbientOcclusion, isGui3d, usesBlockLight, particle);
        this.baseMap = baseMap;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, BlockModelData extraData, @Nullable RenderType renderType) {
        TableBlock.Type type = TableBlock.Type.NONE;
        if (state != null && state.hasProperty(TableBlock.TYPE)) {
            type = state.getValue(TableBlock.TYPE);
        }
        BakedModel base = baseMap.get(type);
        return base.getQuads(state, side, rand);
    }

    /**
     * Vanilla UnbakedModel used by Fabric model loading. Created when JSON with "loader": "bibliocraft:table" is loaded.
     */
    public static class Geometry implements UnbakedModel {
        private final Map<TableBlock.Type, BlockModel> baseMap;

        public Geometry(Map<TableBlock.Type, BlockModel> baseMap) {
            this.baseMap = baseMap;
        }

        @Override
        public Collection<ResourceLocation> getDependencies() {
            Set<ResourceLocation> deps = new HashSet<>();
            for (BlockModel m : baseMap.values()) {
                deps.addAll(m.getDependencies());
            }
            return deps;
        }

        @Override
        public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter) {
            for (BlockModel m : baseMap.values()) {
                m.resolveParents(modelGetter);
            }
        }

        @Nullable
        @Override
        public BakedModel bake(ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState state) {
            Map<TableBlock.Type, BakedModel> newBaseMap = new HashMap<>();
            for (Map.Entry<TableBlock.Type, BlockModel> e : baseMap.entrySet()) {
                BakedModel baked = e.getValue().bake(baker, spriteGetter, state);
                if (baked != null) {
                    newBaseMap.put(e.getKey(), baked);
                }
            }
            if (newBaseMap.isEmpty()) return null;
            TextureAtlasSprite particle = newBaseMap.containsKey(TableBlock.Type.NONE)
                ? newBaseMap.get(TableBlock.Type.NONE).getParticleIcon()
                : newBaseMap.values().iterator().next().getParticleIcon();
            return new TableModel(true, true, true, particle, newBaseMap);
        }
    }
}
