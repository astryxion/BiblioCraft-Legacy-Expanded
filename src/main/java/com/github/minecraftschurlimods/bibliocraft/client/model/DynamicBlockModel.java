package com.github.minecraftschurlimods.bibliocraft.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.RenderType;

import java.util.List;

public abstract class DynamicBlockModel implements BakedModel {
    private final boolean useAmbientOcclusion;
    private final boolean isGui3d;
    private final boolean usesBlockLight;
    private final TextureAtlasSprite particle;

    public DynamicBlockModel(boolean useAmbientOcclusion, boolean isGui3d, boolean usesBlockLight, TextureAtlasSprite particle) {
        this.useAmbientOcclusion = useAmbientOcclusion;
        this.isGui3d = isGui3d;
        this.usesBlockLight = usesBlockLight;
        this.particle = particle;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return useAmbientOcclusion;
    }

    @Override
    public boolean isGui3d() {
        return isGui3d;
    }

    @Override
    public boolean usesBlockLight() {
        return usesBlockLight;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return particle;
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @Override
    public ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand) {
        return getQuads(state, side, rand, BlockModelData.EMPTY, null);
    }

    /**
     * Extended quads with optional model data and render type (for BER/custom render paths).
     */
    public abstract List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, BlockModelData extraData, RenderType renderType);
}
