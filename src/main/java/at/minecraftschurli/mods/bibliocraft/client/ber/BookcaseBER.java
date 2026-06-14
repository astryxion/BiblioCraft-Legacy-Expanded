package at.minecraftschurli.mods.bibliocraft.client.ber;

import at.minecraftschurli.mods.bibliocraft.client.model.BookcaseBookModels;
import at.minecraftschurli.mods.bibliocraft.content.bookcase.BookcaseBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BookcaseBER implements BlockEntityRenderer<BookcaseBlockEntity, BookcaseBER.State> {
    private static final RenderType BOOK_RENDER_TYPE = RenderTypes.solidMovingBlock();

    public BookcaseBER(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(BookcaseBlockEntity blockEntity, State state, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTick, cameraPosition, breakProgress);
        state.booksMask = blockEntity.getBooksMask();
        state.seed = blockEntity.getBlockPos().asLong();
    }

    @Override
    public void submit(State state, PoseStack stack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.booksMask == 0) return;
        RandomSource random = RandomSource.create(state.seed);
        var facing = state.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        List<BlockStateModelPart> parts = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            if (((state.booksMask >> i) & 1) == 0) continue;
            BlockStateModelPart book = BookcaseBookModels.getBook(facing, random, i);
            if (book != null) parts.add(book);
        }
        if (parts.isEmpty()) return;
        collector.submitBlockModel(stack, BOOK_RENDER_TYPE, parts, new int[0], state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
    }

    public static class State extends BlockEntityRenderState {
        private short booksMask;
        private long seed;
    }
}
