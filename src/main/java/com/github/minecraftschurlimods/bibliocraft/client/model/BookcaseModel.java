package com.github.minecraftschurlimods.bibliocraft.client.model;

import net.minecraft.client.renderer.model.ItemCameraTransforms;
import com.github.minecraftschurlimods.bibliocraft.content.bookcase.BookcaseBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.Util;
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
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class BookcaseModel extends DynamicBlockModel {
    private static final Random RANDOM = new Random(Util.getNanos());
    public static final IModelLoader<BookcaseGeometry> LOADER = new IModelLoader<BookcaseGeometry>() {
        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {
        }

        @Override
        public BookcaseGeometry read(JsonDeserializationContext context, JsonObject jsonObject) {
            jsonObject.remove("loader");
            BlockModel base = context.deserialize(jsonObject, BlockModel.class);
            BlockModel[] books = new BlockModel[16];
            if (ClientUtil.isPride()) {
                JsonArray prideBooks = jsonObject.getAsJsonArray("pride_books");
                if (prideBooks.size() != 0) {
                    jsonObject = prideBooks.get(RANDOM.nextInt(prideBooks.size())).getAsJsonObject();
                }
            }
            for (int i = 0; i < 16; i++) {
                books[i] = context.deserialize(JSONUtils.getAsJsonObject(jsonObject, "book_" + i), BlockModel.class);
            }
            return new BookcaseGeometry(base, books);
        }
    };
    private final IBakedModel base;
    private final IBakedModel[] books;

    public BookcaseModel(boolean useAmbientOcclusion, boolean isGui3d, boolean usesBlockLight, TextureAtlasSprite particle, IBakedModel base, IBakedModel[] books) {
        super(useAmbientOcclusion, isGui3d, usesBlockLight, particle);
        this.base = base;
        this.books = books;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand, IModelData extraData) {
        List<BakedQuad> quads = new ArrayList<>(base.getQuads(state, side, rand, extraData));
        for (int i = 0; i < books.length; i++) {
            Boolean book = extraData.getData(BookcaseBlockEntity.MODEL_PROPERTIES.get(i));
            if (book == null || !book) continue;
            quads.addAll(books[i].getQuads(state, side, rand, extraData));
        }
        return quads;
    }

    @Override
    public IBakedModel handlePerspective(ItemCameraTransforms.TransformType transformType, MatrixStack poseStack) {
        return base.handlePerspective(transformType, poseStack);
    }

    public static class BookcaseGeometry implements IModelGeometry<BookcaseGeometry> {
        private final BlockModel base;
        private final BlockModel[] books;

        public BookcaseGeometry(BlockModel base, BlockModel[] books) {
            this.base = base;
            this.books = books;
        }

        @SuppressWarnings("deprecation")
        @Override
        public IBakedModel bake(IModelConfiguration context, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelState, ItemOverrideList overrides, ResourceLocation modelId) {
            IBakedModel baseBaked = new ModelLoaderRegistry.VanillaProxy(this.base.getElements()).bake(context, bakery, spriteGetter, modelState, overrides, modelId);
            IBakedModel[] booksBaked = new IBakedModel[16];
            boolean useBlockLight = context.isSideLit();
            for (int j = 0; j < booksBaked.length; j++) {
                ResourceLocation bookId = new ResourceLocation(modelId.getNamespace(), modelId.getPath() + "_book_" + j);
                booksBaked[j] = this.books[j].bake(bakery, spriteGetter, modelState, bookId);
            }
            return new BookcaseModel(context.useSmoothLighting(), context.isShadedInGui(), useBlockLight, spriteGetter.apply(context.resolveTexture("particle")), baseBaked, booksBaked);
        }

        @Override
        public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
            Set<RenderMaterial> textures = new HashSet<RenderMaterial>();
            textures.addAll(base.getMaterials(modelGetter, missingTextureErrors));
            for (BlockModel book : books) {
                textures.addAll(book.getMaterials(modelGetter, missingTextureErrors));
            }
            return textures;
        }
    }
}
