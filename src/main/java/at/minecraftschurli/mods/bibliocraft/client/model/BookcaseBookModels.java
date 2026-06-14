package at.minecraftschurli.mods.bibliocraft.client.model;

import at.minecraftschurli.mods.bibliocraft.util.BCUtil;
import at.minecraftschurli.mods.bibliocraft.util.ClientUtil;
import com.mojang.math.Quadrant;
import net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.UnbakedExtraModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class BookcaseBookModels {
    public static final String[] PRIDE_SETS = {"rainbow", "trans", "gay", "lesbian", "bisexual", "pansexual", "nonbinary", "asexual", "aromantic", "aroace"};
    private static final ExtraModelKey<Cache> CACHE_KEY = ExtraModelKey.create(() -> "bibliocraft_bookcase_books");
    private static volatile Cache cache;

    private BookcaseBookModels() {}

    public static void register(ModelLoadingPlugin.Context context) {
        context.addModel(CACHE_KEY, new UnbakedExtraModel<>() {
            @Override
            public void resolveDependencies(ResolvableModel.Resolver resolver) {
                for (String set : bookSetNames()) {
                    for (int i = 0; i < 16; i++) {
                        resolver.markDependency(bookModelId(set, i));
                    }
                }
            }

            @Override
            public Cache bake(ModelBaker baker) {
                Map<Direction, Map<String, BlockStateModelPart[]>> setsByFacing = new EnumMap<>(Direction.class);
                for (Direction facing : Direction.Plane.HORIZONTAL) {
                    Map<String, BlockStateModelPart[]> sets = new HashMap<>();
                    ModelState modelState = modelStateFor(facing).asModelState();
                    for (String set : bookSetNames()) {
                        sets.put(set, bakeSet(baker, set, modelState));
                    }
                    setsByFacing.put(facing, sets);
                }
                cache = new Cache(setsByFacing);
                return cache;
            }
        });
    }

    public static @Nullable BlockStateModelPart getBook(Direction facing, RandomSource random, int slot) {
        Cache loaded = getCache();
        if (loaded == null) return null;
        BlockStateModelPart[] set = loaded.pickSet(facing, random);
        return set == null ? null : set[slot];
    }

    private static @Nullable Cache getCache() {
        return cache;
    }

    private static List<String> bookSetNames() {
        return Stream.concat(Stream.of("default"), Arrays.stream(PRIDE_SETS)).toList();
    }

    private static Variant.SimpleModelState modelStateFor(Direction facing) {
        return switch (facing) {
            case EAST -> Variant.SimpleModelState.DEFAULT.withY(Quadrant.R90);
            case SOUTH -> Variant.SimpleModelState.DEFAULT.withY(Quadrant.R180);
            case WEST -> Variant.SimpleModelState.DEFAULT.withY(Quadrant.R270);
            default -> Variant.SimpleModelState.DEFAULT;
        };
    }

    private static BlockStateModelPart[] bakeSet(ModelBaker baker, String set, ModelState modelState) {
        BlockStateModelPart[] books = new BlockStateModelPart[16];
        Material.Baked particle = SimpleModelWrapper.bake(baker, bookModelId(set, 0), modelState).particleMaterial();
        for (int i = 0; i < books.length; i++) {
            ResolvedModel model = baker.getModel(bookModelId(set, i));
            TextureSlots textureSlots = model.getTopTextureSlots();
            QuadCollection quads = model.bakeTopGeometry(textureSlots, baker, modelState);
            books[i] = new SimpleModelWrapper(quads, false, particle);
        }
        return books;
    }

    private static Identifier bookModelId(String set, int index) {
        return BCUtil.bcLoc("block/bookcase/" + set + "/book_" + index);
    }

    public record Cache(Map<Direction, Map<String, BlockStateModelPart[]>> setsByFacing) {
        public BlockStateModelPart[] pickSet(Direction facing, RandomSource random) {
            Map<String, BlockStateModelPart[]> sets = setsByFacing.get(facing);
            if (sets == null) return null;
            BlockStateModelPart[] defaultSet = sets.get("default");
            if (defaultSet == null) return null;
            if (!ClientUtil.isPride()) return defaultSet;
            WeightedList.Builder<BlockStateModelPart[]> builder = WeightedList.builder();
            for (BlockStateModelPart[] set : sets.values()) {
                builder.add(set, 1);
            }
            return builder.build().getRandomOrThrow(random);
        }
    }
}
