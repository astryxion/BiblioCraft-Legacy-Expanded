package com.github.minecraftschurlimods.bibliocraft.client.ber;

import com.github.minecraftschurlimods.bibliocraft.client.model.BlockModelData;
import com.github.minecraftschurlimods.bibliocraft.client.model.BookcaseBookModels;
import com.github.minecraftschurlimods.bibliocraft.content.bookcase.BookcaseBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders bookcase books using per-facing baked models from {@link BookcaseBookModels}.
 */
public class BookcaseBER implements net.minecraft.client.renderer.blockentity.BlockEntityRenderer<BookcaseBlockEntity> {

    private static final RandomSource RANDOM = RandomSource.create();

    @Override
    public void render(BookcaseBlockEntity blockEntity, float partialTick, PoseStack stack, MultiBufferSource buffer, int light, int overlay) {
        Direction facing = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        BakedModel[] books = BookcaseBookModels.getBooks(facing);
        if (books == null) return;

        BlockModelData modelData = blockEntity.getBlockModelData();
        BlockState state = blockEntity.getBlockState();
        BakedModel booksOnlyModel = new BakedModel() {
            @Override
            public List<BakedQuad> getQuads(@Nullable BlockState s, @Nullable Direction side, RandomSource rand) {
                List<BakedQuad> quads = new ArrayList<>();
                for (int i = 0; i < 16; i++) {
                    Boolean hasBook = modelData.get(BookcaseBlockEntity.MODEL_PROPERTIES.get(i));
                    if (hasBook == null || !hasBook) continue;
                    BakedModel book = books[i];
                    if (book != null) quads.addAll(book.getQuads(s, side, rand));
                }
                return quads;
            }
            @Override
            public boolean useAmbientOcclusion() { return true; }
            @Override
            public boolean isGui3d() { return true; }
            @Override
            public boolean usesBlockLight() { return true; }
            @Override
            public boolean isCustomRenderer() { return false; }
            @Override
            public TextureAtlasSprite getParticleIcon() { return books[0].getParticleIcon(); }
            @Override
            public ItemOverrides getOverrides() { return ItemOverrides.EMPTY; }
            @Override
            public ItemTransforms getTransforms() { return ItemTransforms.NO_TRANSFORMS; }
        };

        RANDOM.setSeed(blockEntity.getBlockPos().asLong());
        ClientUtil.renderBakedModel(booksOnlyModel, stack, buffer,
                blockEntity.getLevel(), blockEntity.getBlockPos(), state, RANDOM, BlockModelData.EMPTY);
    }
}
