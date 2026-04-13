package com.github.minecraftschurlimods.bibliocraft.client.model;

import com.github.minecraftschurlimods.bibliocraft.content.bookcase.BookcaseBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.google.gson.JsonArray;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.Util;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class BookcaseModel extends DynamicBlockModel implements FabricBakedModel {
    private static final RandomSource RANDOM = RandomSource.create(Util.getNanos());

    private final BakedModel base;
    private final BakedModel[] books;

    public BookcaseModel(boolean useAmbientOcclusion, boolean isGui3d, boolean usesBlockLight, TextureAtlasSprite particle, BakedModel base, BakedModel[] books) {
        super(useAmbientOcclusion, isGui3d, usesBlockLight, particle);
        this.base = base;
        this.books = books;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(net.minecraft.world.level.BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        // Emit only the shelf (base). Books are rendered by BookcaseBER so they always show
        // even when block view has no block entity during chunk build.
        BakedModel shelfOnly = new BakedModel() {
            @Override
            public List<BakedQuad> getQuads(@Nullable BlockState s, @Nullable Direction side, RandomSource rand) {
                return base.getQuads(s, side, rand);
            }
            @Override
            public boolean useAmbientOcclusion() { return BookcaseModel.this.useAmbientOcclusion(); }
            @Override
            public boolean isGui3d() { return BookcaseModel.this.isGui3d(); }
            @Override
            public boolean usesBlockLight() { return BookcaseModel.this.usesBlockLight(); }
            @Override
            public boolean isCustomRenderer() { return BookcaseModel.this.isCustomRenderer(); }
            @Override
            public TextureAtlasSprite getParticleIcon() { return BookcaseModel.this.getParticleIcon(); }
            @Override
            public ItemOverrides getOverrides() { return BookcaseModel.this.getOverrides(); }
            @Override
            public ItemTransforms getTransforms() { return BookcaseModel.this.getTransforms(); }
        };
        context.fallbackConsumer().accept(shelfOnly);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, BlockModelData extraData, @Nullable RenderType renderType) {
        List<BakedQuad> quads = new ArrayList<>(base.getQuads(state, side, rand));
        quads.addAll(getBookQuads(state, side, rand, extraData));
        return quads;
    }

    /**
     * Returns only the book quads (no shelf). Used by {@link com.github.minecraftschurlimods.bibliocraft.client.ber.BookcaseBER}
     * so books render correctly even when block model data is not available during chunk build.
     */
    public List<BakedQuad> getBookQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, BlockModelData extraData) {
        List<BakedQuad> quads = new ArrayList<>();
        for (int i = 0; i < books.length; i++) {
            Boolean book = extraData.get(BookcaseBlockEntity.MODEL_PROPERTIES.get(i));
            if (book == null || !book) continue;
            quads.addAll(books[i].getQuads(state, side, rand));
        }
        return quads;
    }

    /**
     * Vanilla UnbakedModel used by Fabric model loading. Created when JSON with "loader": "bibliocraft:bookcase" is loaded.
     */
    public static class BookcaseGeometry implements UnbakedModel {
        private final BlockModel base;
        private final BlockModel[] books;

        public BookcaseGeometry(BlockModel base, BlockModel[] books) {
            this.base = base;
            this.books = books;
        }

        public static BookcaseGeometry fromJson(com.google.gson.JsonObject jsonObject, Function<com.google.gson.JsonObject, BlockModel> deserializer) {
            jsonObject.remove("loader");
            BlockModel base = deserializer.apply(jsonObject);
            BlockModel[] books = new BlockModel[16];
            com.google.gson.JsonObject bookJson = jsonObject;
            if (ClientUtil.isPride()) {
                JsonArray prideBooks = jsonObject.getAsJsonArray("pride_books");
                if (prideBooks != null && !prideBooks.isEmpty()) {
                    bookJson = prideBooks.get(RANDOM.nextInt(prideBooks.size())).getAsJsonObject();
                }
            }
            for (int i = 0; i < 16; i++) {
                books[i] = deserializer.apply(GsonHelper.getAsJsonObject(bookJson, "book_" + i));
            }
            return new BookcaseGeometry(base, books);
        }

        @Override
        public Collection<ResourceLocation> getDependencies() {
            List<ResourceLocation> deps = new ArrayList<>(base.getDependencies());
            for (BlockModel b : books) {
                deps.addAll(b.getDependencies());
            }
            return deps;
        }

        @Override
        public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter) {
            base.resolveParents(modelGetter);
            for (BlockModel book : books) {
                book.resolveParents(modelGetter);
            }
        }

        @Nullable
        @Override
        public BakedModel bake(ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState state) {
            BakedModel baseBaked = base.bake(baker, spriteGetter, state);
            if (baseBaked == null) return null;
            BakedModel[] booksBaked = new BakedModel[16];
            for (int j = 0; j < books.length; j++) {
                booksBaked[j] = books[j].bake(baker, spriteGetter, state);
                if (booksBaked[j] == null) booksBaked[j] = baseBaked;
            }
            TextureAtlasSprite particle = baseBaked.getParticleIcon();
            return new BookcaseModel(true, true, true, particle, baseBaked, booksBaked);
        }
    }
}
