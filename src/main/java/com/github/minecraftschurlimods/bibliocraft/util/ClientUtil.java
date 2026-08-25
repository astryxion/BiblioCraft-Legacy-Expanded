package com.github.minecraftschurlimods.bibliocraft.util;

import net.minecraft.client.renderer.model.ItemCameraTransforms;
import com.github.minecraftschurlimods.bibliocraft.BCConfig;
import com.github.minecraftschurlimods.bibliocraft.init.BCItems;
import com.github.minecraftschurlimods.bibliocraft.client.screen.BigBookScreen;
import com.github.minecraftschurlimods.bibliocraft.client.screen.ClipboardScreen;
import com.github.minecraftschurlimods.bibliocraft.client.screen.ClockScreen;
import com.github.minecraftschurlimods.bibliocraft.client.screen.FancySignScreen;
import com.github.minecraftschurlimods.bibliocraft.client.screen.RedstoneBookScreen;
import com.github.minecraftschurlimods.bibliocraft.client.screen.StockroomCatalogScreen;
import com.github.minecraftschurlimods.bibliocraft.client.screen.TypewriterPageScreen;
import com.github.minecraftschurlimods.bibliocraft.client.screen.TypewriterScreen;
import com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog.StockroomCatalogListPacket;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.math.BlockPos;
import java.util.Random;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.client.model.data.IModelData;

import java.util.Calendar;

/**
 * Utility class holding various helper methods. Kept separate from {@link BCUtil} for classloading reasons.
 */
public final class ClientUtil {
    /**
     * Helper to get the {@link Minecraft} instance.
     *
     * @return The {@link Minecraft} instance.
     */
    public static Minecraft getMc() {
        return Minecraft.getInstance();
    }

    /**
     * Helper to get the {@link ClientWorld} instance from the {@link Minecraft} instance.
     *
     * @return The {@link ClientWorld} instance.
     */
    public static ClientWorld getLevel() {
        return getMc().level;
    }

    /**
     * Helper to get the {@link ClientPlayerEntity} instance from the {@link Minecraft} instance.
     *
     * @return The {@link ClientPlayerEntity} instance.
     */
    public static ClientPlayerEntity getPlayer() {
        return getMc().player;
    }

    /**
     * Helper to get the {@link FontRenderer} instance from the {@link Minecraft} instance.
     *
     * @return The {@link FontRenderer} instance.
     */
    public static FontRenderer getFont() {
        return getMc().font;
    }

    /**
     * Opens a {@link BigBookScreen} on the client.
     *
     * @param stack  The owning {@link ItemStack} of the screen.
     * @param player The owning {@link PlayerEntity} of the screen.
     * @param hand   The {@link Hand} in which the big book is held.
     */
    public static void openBigBookScreen(ItemStack stack, PlayerEntity player, Hand hand) {
        getMc().setScreen(new BigBookScreen(stack, player, hand));
    }

    /**
     * Opens a {@link BigBookScreen} on the client.
     *
     * @param stack   The owning {@link ItemStack} of the screen.
     * @param player  The owning {@link PlayerEntity} of the screen.
     * @param lectern The owning lectern's {@link BlockPos}.
     */
    public static void openBigBookScreen(ItemStack stack, PlayerEntity player, BlockPos lectern) {
        getMc().setScreen(new BigBookScreen(stack, player, lectern));
    }

    /**
     * Opens a {@link ClipboardScreen} on the client.
     *
     * @param stack The owning {@link ItemStack} of the screen.
     * @param hand  The {@link Hand} in which the clipboard is held.
     */
    public static void openClipboardScreen(ItemStack stack, Hand hand) {
        getMc().setScreen(new ClipboardScreen(stack, hand));
    }

    /**
     * Opens a {@link ClockScreen} on the client.
     *
     * @param pos The {@link BlockPos} of the clock owning the screen.
     */
    public static void openClockScreen(BlockPos pos) {
        getMc().setScreen(new ClockScreen(pos));
    }

    /**
     * Opens a {@link FancySignScreen} on the client.
     *
     * @param pos  The {@link BlockPos} of the clock owning the screen.
     * @param back Whether the back of the sign was clicked or not.
     */
    public static void openFancySignScreen(BlockPos pos, boolean back) {
        getMc().setScreen(new FancySignScreen(pos, back));
    }

    /**
     * Opens a {@link RedstoneBookScreen} on the client.
     */
    public static void openRedstoneBookScreen() {
        getMc().setScreen(new RedstoneBookScreen());
    }

    /**
     * Opens a {@link StockroomCatalogScreen} on the client.
     *
     * @param stack  The owning {@link ItemStack} of the screen.
     * @param player The owning {@link PlayerEntity} of the screen.
     * @param hand   The {@link Hand} in which the stockroom catalog is held.
     */
    public static void openStockroomCatalogScreen(ItemStack stack, PlayerEntity player, Hand hand) {
        getMc().setScreen(new StockroomCatalogScreen(stack, player, hand));
    }

    /**
     * Opens a {@link StockroomCatalogScreen} on the client.
     *
     * @param stack   The owning {@link ItemStack} of the screen.
     * @param player  The owning {@link PlayerEntity} of the screen.
     * @param lectern The owning lectern's {@link BlockPos}.
     */
    public static void openStockroomCatalogScreen(ItemStack stack, PlayerEntity player, BlockPos lectern) {
        getMc().setScreen(new StockroomCatalogScreen(stack, player, lectern));
    }

    /**
     * Opens the appropriate screen for a Bibliocraft book in a lectern. Call only on the client.
     *
     * @param stack  The {@link ItemStack} (book) in the lectern.
     * @param player The {@link PlayerEntity} viewing the lectern.
     * @param pos    The {@link BlockPos} of the lectern.
     */
    public static void openScreenForLectern(ItemStack stack, PlayerEntity player, BlockPos pos) {
        if (stack.getItem() == BCItems.BIG_BOOK.get() || stack.getItem() == BCItems.WRITTEN_BIG_BOOK.get()) {
            openBigBookScreen(stack, player, pos);
        } else if (stack.getItem() == BCItems.STOCKROOM_CATALOG.get()) {
            openStockroomCatalogScreen(stack, player, pos);
        }
    }

    /**
     * Opens a {@link TypewriterScreen} on the client.
     *
     * @param pos The typewriter's {@link BlockPos}.
     */
    public static void openTypewriterScreen(BlockPos pos) {
        getMc().setScreen(new TypewriterScreen(pos));
    }

    /**
     * Opens a {@link TypewriterPageScreen} on the client.
     *
     * @param stack The typewriter page's {@link ItemStack}.
     */
    public static void openTypewriterPageScreen(ItemStack stack) {
        getMc().setScreen(new TypewriterPageScreen(stack));
    }

    /**
     * Translates the {@link MatrixStack} into the block center and rotates it according to the block entity's rotation.
     *
     * @param stack       The pose stack to transform.
     * @param blockEntity The block entity to get the rotation from.
     */
    public static void setupCenteredBER(MatrixStack stack, TileEntity blockEntity) {
        stack.translate(0.5, 0.5, 0.5);
        BlockState state = blockEntity.getBlockState();
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            float rot;
            switch (state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
                case SOUTH:
                    rot = 0;
                    break;
                case EAST:
                    rot = 90;
                    break;
                case WEST:
                    rot = 270;
                    break;
                default:
                    rot = 180;
                    break;
            }
            stack.mulPose(Vector3f.YP.rotationDegrees(rot));
        }
    }

    /**
     * Renders an {@link ItemStack} in the {@link ItemCameraTransforms.TransformType#FIXED} pose.
     *
     * @param item    The {@link ItemStack} to render.
     * @param stack   The {@link MatrixStack} to use.
     * @param buffer  The {@link IRenderTypeBuffer} to use.
     * @param light   The light value to use.
     * @param overlay The overlay value to use.
     */
    public static void renderFixedItem(ItemStack item, MatrixStack stack, IRenderTypeBuffer buffer, int light, int overlay) {
        renderItem(item, stack, buffer, light, overlay, ItemCameraTransforms.TransformType.FIXED);
    }

    /**
     * Renders an {@link ItemStack} in the {@link ItemCameraTransforms.TransformType#GUI} pose.
     *
     * @param item    The {@link ItemStack} to render.
     * @param stack   The {@link MatrixStack} to use.
     * @param buffer  The {@link IRenderTypeBuffer} to use.
     * @param light   The light value to use.
     * @param overlay The overlay value to use.
     */
    public static void renderGuiItem(ItemStack item, MatrixStack stack, IRenderTypeBuffer buffer, int light, int overlay) {
        renderItem(item, stack, buffer, light, overlay, ItemCameraTransforms.TransformType.GUI);
    }

    /**
     * Renders an {@link ItemStack} for use in a BER or GUI.
     *
     * @param item    The {@link ItemStack} to render.
     * @param stack   The {@link MatrixStack} to use.
     * @param buffer  The {@link IRenderTypeBuffer} to use.
     * @param light   The light value to use.
     * @param overlay The overlay value to use.
     * @param context The {@link ItemCameraTransforms.TransformType} to use.
     */
    public static void renderItem(ItemStack item, MatrixStack stack, IRenderTypeBuffer buffer, int light, int overlay, ItemCameraTransforms.TransformType context) {
        Minecraft minecraft = getMc();
        ItemRenderer renderer = minecraft.getItemRenderer();
        renderer.render(item, context, context == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND || context == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, stack, buffer, light, overlay, renderer.getModel(item, minecraft.level, null));
    }

    /**
     * Renders the given {@link IBakedModel} in the world.
     *
     * @param model     The {@link IBakedModel} to render.
     * @param stack     The {@link MatrixStack} to use.
     * @param buffer    The {@link IRenderTypeBuffer} to use.
     * @param level     The {@link World} to render the model in.
     * @param pos       The {@link BlockPos} to render the model at.
     * @param state     The {@link BlockState} to render the model for.
     * @param random    The {@link Random} to use for random models.
     * @param modelData The {@link IModelData} to use.
     */
    public static void renderBakedModel(IBakedModel model, MatrixStack stack, IRenderTypeBuffer buffer, World level, BlockPos pos, BlockState state, Random random, IModelData modelData) {
        net.minecraft.client.renderer.BlockModelRenderer renderer = getMc().getBlockRenderer().getModelRenderer();
        int color = getMc().getBlockColors().getColor(state, level, pos, 0);
        float red = (float) (color >> 16 & 255) / 255f;
        float green = (float) (color >> 8 & 255) / 255f;
        float blue = (float) (color & 255) / 255f;
        int light = WorldRenderer.getLightColor(level, pos);
        net.minecraft.client.renderer.RenderType type = net.minecraft.client.renderer.RenderTypeLookup.getRenderType(state, false);
        renderer.renderModel(stack.last(), buffer.getBuffer(type), state, model, red, green, blue, light, OverlayTexture.NO_OVERLAY, modelData);
    }

    /**
     * Renders text in the formatting of the experience level number above the hotbar.
     *
     * @param text     The text to render.
     * @param graphics The {@link MatrixStack} to use.
     * @param centerX  The horizontal center of the text.
     * @param startY   The y coordinate of the text. Be aware that there will be a 1px outline above this position.
     * @see net.minecraft.client.gui.Gui#renderExperienceLevel(MatrixStack, net.minecraft.client.DeltaTracker)
     */
    public static void renderXpText(String text, MatrixStack graphics, int centerX, int startY) {
        FontRenderer font = getFont();
        int startX = centerX - font.width(text) / 2;
        font.draw(graphics, text, startX + 1, startY, 0);
        font.draw(graphics, text, startX - 1, startY, 0);
        font.draw(graphics, text, startX, startY + 1, 0);
        font.draw(graphics, text, startX, startY - 1, 0);
        font.draw(graphics, text, startX, startY, 0x80ff20);
    }

    /**
     * @return Whether pride-themed content should be displayed.
     */
    public static boolean isPride() {
        return BCConfig.ENABLE_PRIDE.get() && (BCConfig.ENABLE_PRIDE_ALWAYS.get() || Calendar.getInstance().get(Calendar.MONTH) == Calendar.JUNE);
    }

    /**
     * Classloading guard for setting the stockroom catalog contents from a packet.
     *
     * @param packet The packet containing the stockroom catalog contents.
     */
    public static void setStockroomCatalogList(StockroomCatalogListPacket packet) {
        if (getMc().screen instanceof StockroomCatalogScreen) {
            StockroomCatalogScreen screen = (StockroomCatalogScreen) getMc().screen;
            screen.setFromPacket(packet);
        }
    }
}
