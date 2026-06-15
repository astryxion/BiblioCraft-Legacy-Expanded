package com.github.minecraftschurlimods.bibliocraft.client.model;

import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import net.fabricmc.fabric.api.client.model.loading.v1.FabricBakedModelManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Caches north-facing book models and per-facing variants using vanilla {@link BlockModelRotation},
 * matching blockstate y rotations used by bookcase blockstates.
 */
public final class BookcaseBookModels {
    private static final String BOOK_MODEL_PATH = "block/template/bookcase/book_";
    private static BakedModel[] NORTH_BOOKS;
    private static final Map<Direction, BakedModel[]> BOOKS_BY_FACING = new EnumMap<>(Direction.class);
    private static final FaceBakery FACE_BAKERY = new FaceBakery();

    private BookcaseBookModels() {
    }

    public static void clearCache() {
        NORTH_BOOKS = null;
        BOOKS_BY_FACING.clear();
    }

    @Nullable
    public static BakedModel[] getBooks(Direction facing) {
        if (NORTH_BOOKS == null && !loadNorthBooks()) return null;
        return BOOKS_BY_FACING.computeIfAbsent(facing, BookcaseBookModels::rotateBooks);
    }

    private static boolean loadNorthBooks() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getModelManager() == null || !(mc.getModelManager() instanceof FabricBakedModelManager fabricManager)) {
            return false;
        }
        BakedModel[] models = new BakedModel[16];
        for (int i = 0; i < 16; i++) {
            ResourceLocation id = BCUtil.bcLoc(BOOK_MODEL_PATH + i);
            BakedModel baked = fabricManager.getModel(id);
            if (baked == null || baked == mc.getModelManager().getMissingModel()) {
                return false;
            }
            models[i] = baked;
        }
        NORTH_BOOKS = models;
        BOOKS_BY_FACING.put(Direction.NORTH, models);
        return true;
    }

    private static BakedModel[] rotateBooks(Direction facing) {
        if (facing == Direction.NORTH) return NORTH_BOOKS;
        BlockModelRotation rotation = switch (facing) {
            case EAST -> BlockModelRotation.X0_Y90;
            case SOUTH -> BlockModelRotation.X0_Y180;
            case WEST -> BlockModelRotation.X0_Y270;
            default -> BlockModelRotation.X0_Y0;
        };
        BakedModel[] source = NORTH_BOOKS;
        BakedModel[] rotated = new BakedModel[16];
        for (int i = 0; i < 16; i++) {
            BakedModel book = source[i];
            if (book == null) {
                rotated[i] = null;
                continue;
            }
            final BakedModel northBook = book;
            rotated[i] = new BakedModel() {
                @Override
                public List<BakedQuad> getQuads(@Nullable net.minecraft.world.level.block.state.BlockState state, @Nullable Direction side, net.minecraft.util.RandomSource rand) {
                    List<BakedQuad> quads = new ArrayList<>();
                    for (BakedQuad quad : northBook.getQuads(state, side, rand)) {
                        quads.add(rotateQuad(quad, rotation));
                    }
                    return quads;
                }
                @Override
                public boolean useAmbientOcclusion() { return northBook.useAmbientOcclusion(); }
                @Override
                public boolean isGui3d() { return northBook.isGui3d(); }
                @Override
                public boolean usesBlockLight() { return northBook.usesBlockLight(); }
                @Override
                public boolean isCustomRenderer() { return northBook.isCustomRenderer(); }
                @Override
                public net.minecraft.client.renderer.texture.TextureAtlasSprite getParticleIcon() { return northBook.getParticleIcon(); }
                @Override
                public net.minecraft.client.renderer.block.model.ItemOverrides getOverrides() { return northBook.getOverrides(); }
                @Override
                public net.minecraft.client.renderer.block.model.ItemTransforms getTransforms() { return northBook.getTransforms(); }
            };
        }
        return rotated;
    }

    private static BakedQuad rotateQuad(BakedQuad quad, BlockModelRotation rotation) {
        if (rotation == BlockModelRotation.X0_Y0) return quad;
        int[] vertices = quad.getVertices().clone();
        Vector3f pos = new Vector3f();
        for (int i = 0; i < FaceBakery.VERTEX_COUNT; i++) {
            int offset = i * FaceBakery.VERTEX_INT_SIZE;
            pos.set(
                    Float.intBitsToFloat(vertices[offset]),
                    Float.intBitsToFloat(vertices[offset + 1]),
                    Float.intBitsToFloat(vertices[offset + 2])
            );
            // applyModelRotation already pivots around block center (0.5, 0, 0.5).
            FACE_BAKERY.applyModelRotation(pos, rotation.getRotation());
            vertices[offset] = Float.floatToIntBits(pos.x());
            vertices[offset + 1] = Float.floatToIntBits(pos.y());
            vertices[offset + 2] = Float.floatToIntBits(pos.z());
        }
        Direction direction = FaceBakery.calculateFacing(vertices);
        return new BakedQuad(vertices, quad.getTintIndex(), direction, quad.getSprite(), quad.isShade());
    }
}
