package com.github.minecraftschurlimods.bibliocraft.client.ber;

import com.github.minecraftschurlimods.bibliocraft.client.model.BlockModelData;
import com.github.minecraftschurlimods.bibliocraft.content.bookcase.BookcaseBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.model.loading.v1.FabricBakedModelManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders the 3D book quads for the bookcase block. Loads the 16 book sub-models from the
 * template (block/template/bookcase/book_0 ... book_15) and draws them for each slot that
 * has a book. Does not depend on BookcaseModel so it works even when the block model is
 * vanilla-parsed (loadBookcaseGeometry returns null to avoid "BlockModel parent has to be
 * a block model").
 */
public class BookcaseBER implements net.minecraft.client.renderer.blockentity.BlockEntityRenderer<BookcaseBlockEntity> {

    private static final RandomSource RANDOM = RandomSource.create();
    private static final String BOOK_MODEL_PATH = "block/template/bookcase/book_";
    private static BakedModel[] BOOK_MODELS;

    private static BakedModel[] getBookModels() {
        if (BOOK_MODELS != null) return BOOK_MODELS;
        Minecraft mc = Minecraft.getInstance();
        if (mc.getModelManager() == null) return null;
        if (!(mc.getModelManager() instanceof FabricBakedModelManager fabricManager)) return null;
        BakedModel[] models = new BakedModel[16];
        for (int i = 0; i < 16; i++) {
            ResourceLocation id = BCUtil.bcLoc(BOOK_MODEL_PATH + i);
            BakedModel baked = fabricManager.getModel(id);
            if (baked == null || baked == mc.getModelManager().getMissingModel())
                baked = null;
            models[i] = baked;
        }
        BOOK_MODELS = models;
        return BOOK_MODELS;
    }

    /** Call when resource reload starts so cache is rebuilt on next use. */
    public static void clearBookModelCache() {
        BOOK_MODELS = null;
    }

    @Override
    public void render(BookcaseBlockEntity blockEntity, float partialTick, PoseStack stack, MultiBufferSource buffer, int light, int overlay) {
        BakedModel[] books = getBookModels();
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
            public TextureAtlasSprite getParticleIcon() {
                return books[0] != null ? books[0].getParticleIcon() : Minecraft.getInstance().getModelManager().getMissingModel().getParticleIcon();
            }
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
