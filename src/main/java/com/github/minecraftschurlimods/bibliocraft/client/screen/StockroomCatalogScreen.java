package com.github.minecraftschurlimods.bibliocraft.client.screen;

import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.StringTextComponent;
import com.github.minecraftschurlimods.bibliocraft.client.widget.SpriteButton;
import com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog.StockroomCatalogContent;
import com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog.StockroomCatalogItemEntry;
import com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog.StockroomCatalogListPacket;
import com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog.StockroomCatalogRequestListPacket;
import com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog.StockroomCatalogSorting;
import com.github.minecraftschurlimods.bibliocraft.content.stockroomcatalog.StockroomCatalogSyncPacket;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.github.minecraftschurlimods.bibliocraft.util.Translations;
import com.github.minecraftschurlimods.bibliocraft.util.lectern.LecternUtil;
import com.github.minecraftschurlimods.bibliocraft.util.lectern.TakeLecternBookPacket;
import com.mojang.datafixers.util.Either;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.ChangePageButton;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.ResourceLocation;
import java.util.Random;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import com.github.minecraftschurlimods.bibliocraft.BCEventHandler;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

public class StockroomCatalogScreen extends Screen {
    private static final ResourceLocation BACKGROUND = BCUtil.bcLoc("textures/gui/stockroom_catalog.png");
    private static final ResourceLocation LOCATE_ICON = BCUtil.bcLoc("textures/gui/sprites/locate.png");
    private static final ResourceLocation LOCATE_ICON_HIGHLIGHTED = BCUtil.bcLoc("textures/gui/sprites/locate_highlighted.png");
    private static final ResourceLocation REMOVE_ICON = BCUtil.bcLoc("textures/gui/sprites/remove.png");
    private static final ResourceLocation REMOVE_ICON_HIGHLIGHTED = BCUtil.bcLoc("textures/gui/sprites/remove_highlighted.png");
    private static final int ROWS_PER_PAGE = 11;
    private static final int PARTICLE_COUNT = 16;
    private final ItemStack stack;
    private final PlayerEntity player;
    private final Hand hand;
    private final BlockPos lectern;
    private final Random random = new Random();
    private final List<Button> removeButtons = new ArrayList<>();
    private final List<Button> locateButtons = new ArrayList<>();
    private StockroomCatalogContent data;
    private boolean showContainerList = false;
    private int page = 0;
    private String search = "";
    private ChangePageButton forwardButton;
    private ChangePageButton backButton;
    private List<BlockPos> containers = java.util.Collections.emptyList();
    private List<BlockPos> visibleContainers = java.util.Collections.emptyList();
    private List<StockroomCatalogItemEntry> items = java.util.Collections.emptyList();
    private List<StockroomCatalogItemEntry> visibleItems = java.util.Collections.emptyList();
    private StockroomCatalogSorting.Container containerSorting = StockroomCatalogSorting.Container.ALPHABETICAL_ASC;
    private StockroomCatalogSorting.Item itemSorting = StockroomCatalogSorting.Item.ALPHABETICAL_ASC;

    public StockroomCatalogScreen(ItemStack stack, PlayerEntity player, Hand hand) {
        this(stack, player, hand, null);
    }

    public StockroomCatalogScreen(ItemStack stack, PlayerEntity player, BlockPos lectern) {
        this(stack, player, null, lectern);
    }

    private StockroomCatalogScreen(ItemStack stack, PlayerEntity player, Hand hand, BlockPos lectern) {
        super(stack.getHoverName());
        this.stack = stack;
        this.player = player;
        this.hand = hand;
        this.lectern = lectern;
        this.data = StockroomCatalogContent.getFromStack(stack);
        requestPacket();
    }

    @Override
    public void render(MatrixStack graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        int x = (width - 256) / 2;
        int y = mouseY - 29;
        int i = 0;
        if (showContainerList) {
            for (BlockPos pos : visibleContainers) {
                ItemStack blockItem = new ItemStack(ClientUtil.getLevel().getBlockState(pos).getBlock().asItem());
                this.minecraft.getItemRenderer().renderGuiItem(blockItem, x + 34, i * 19 + 29);
                this.minecraft.getItemRenderer().renderGuiItemDecorations(font, blockItem, x + 34, i * 19 + 29);
                String itemText = font.plainSubstrByWidth(blockItem.getHoverName().getString(), 137);
                font.draw(graphics, itemText, x + 51, i * 19 + 33, 0);
                i++;
            }
            if (mouseX >= x + 34 && mouseX < x + 50) {
                if (y > 0 && y % 19 < 16 && y / 19 < visibleContainers.size()) {
                    BlockPos container = visibleContainers.get(y / 19);
                    int distance = (int) (lectern != null ? Math.sqrt(lectern.distSqr(container)) : ClientUtil.getPlayer().position().distanceTo(BCUtil.toVec3(container)));
                    this.renderTooltip(graphics, new TranslationTextComponent(Translations.STOCKROOM_CATALOG_DISTANCE_KEY, distance), mouseX, mouseY);
                }
            }
            if (mouseX >= x + 189 && mouseX < x + 205) {
                if (y > 0 && y % 19 < 16 && y / 19 < visibleContainers.size()) {
                    this.renderTooltip(graphics, Translations.STOCKROOM_CATALOG_REMOVE, mouseX, mouseY);
                }
            }
            if (mouseX >= x + 206 && mouseX < x + 222) {
                if (y > 0 && y % 19 < 16 && y / 19 < visibleContainers.size()) {
                    this.renderTooltip(graphics, Translations.STOCKROOM_CATALOG_LOCATE, mouseX, mouseY);
                }
            }
        } else {
            for (StockroomCatalogItemEntry entry : visibleItems) {
                this.minecraft.getItemRenderer().renderGuiItem(entry.item(), x + 34, i * 19 + 29);
                this.minecraft.getItemRenderer().renderGuiItemDecorations(font, entry.item(), x + 34, i * 19 + 29);
                String countText = new TranslationTextComponent(Translations.STOCKROOM_CATALOG_COUNT_KEY, entry.count()).getString();
                int countWidth = font.width(countText);
                font.draw(graphics, countText, x + 205 - countWidth, i * 19 + 33, 0);
                String itemText = font.plainSubstrByWidth(entry.item().getHoverName().getString(), 153 - countWidth);
                font.draw(graphics, itemText, x + 51, i * 19 + 33, 0);
                i++;
            }
            if (mouseX >= x + 34 && mouseX < x + 50) {
                if (y > 0 && y % 19 < 16 && y / 19 < visibleItems.size()) {
                    ItemStack item = visibleItems.get(y / 19).item();
                    this.renderTooltip(graphics, item, mouseX, mouseY);
                }
            }
            if (mouseX >= x + 206 && mouseX < x + 222) {
                if (y > 0 && y % 19 < 16 && y / 19 < visibleItems.size()) {
                    this.renderTooltip(graphics, Translations.STOCKROOM_CATALOG_LOCATE, mouseX, mouseY);
                }
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return lectern == null;
    }

    @Override
    public void onClose() {
        super.onClose();
        setDataOnStack();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void init() {
        locateButtons.clear();
        removeButtons.clear();
        int x = (width - 256) / 2;
        ITextComponent switchComponent = showContainerList ? Translations.STOCKROOM_CATALOG_SHOW_ITEMS : Translations.STOCKROOM_CATALOG_SHOW_CONTAINERS;
        if (lectern != null) {
            addButton(new Button(width / 2 - 151, 260, 98, 20, Translations.VANILLA_TAKE_BOOK, $ -> {
                LecternUtil.takeLecternBook(player, player.level, lectern);
                BCEventHandler.getChannel().sendToServer(new TakeLecternBookPacket(lectern));
                onClose();
            }));
            addButton(new Button(width / 2 - 49, 260, 98, 20, switchComponent, $ -> toggleMode()));
            addButton(new Button(width / 2 + 53, 260, 98, 20, net.minecraft.client.gui.DialogTexts.GUI_DONE, $ -> onClose()));
        } else {
            addButton(new Button(width / 2 - 100, 260, 98, 20, switchComponent, $ -> toggleMode()));
            addButton(new Button(width / 2 + 2, 260, 98, 20, net.minecraft.client.gui.DialogTexts.GUI_DONE, $ -> onClose()));
        }
        TextFieldWidget searchBox = addButton(new TextFieldWidget(getMinecraft().font, x + 33, 15, 140, 8, new StringTextComponent("")));
        searchBox.setTextColor(0);
        searchBox.setBordered(false);
        setEditBoxTextShadow(searchBox);
        searchBox.setSuggestion(search.isEmpty() ? Translations.STOCKROOM_CATALOG_SEARCH.getString() : null);
        searchBox.setValue(search);
        searchBox.setResponder(e -> {
            searchBox.setSuggestion(e.isEmpty() ? Translations.STOCKROOM_CATALOG_SEARCH.getString() : null);
            search = e.toLowerCase(Locale.ROOT);
            updateContents();
        });
        forwardButton = addButton(new ChangePageButton(x + 194, 236, true, $ -> {
            page++;
            updateContents();
        }, false));
        backButton = addButton(new ChangePageButton(x + 30, 236, false, $ -> {
            page--;
            updateContents();
        }, false));
        if (showContainerList) {
            addButton(new SortButton<>(StockroomCatalogSorting.Container.ALPHABETICAL_ASC, x + 172, 11, 52, 14, b -> {
                SortButton<StockroomCatalogSorting.Container> button = (SortButton<StockroomCatalogSorting.Container>) b;
                StockroomCatalogSorting.Container next;
                switch (button.getValue()) {
                    case ALPHABETICAL_ASC:
                        next = StockroomCatalogSorting.Container.ALPHABETICAL_DESC;
                        break;
                    case ALPHABETICAL_DESC:
                        next = StockroomCatalogSorting.Container.DISTANCE_ASC;
                        break;
                    case DISTANCE_ASC:
                        next = StockroomCatalogSorting.Container.DISTANCE_DESC;
                        break;
                    case DISTANCE_DESC:
                    default:
                        next = StockroomCatalogSorting.Container.ALPHABETICAL_ASC;
                        break;
                }
                button.setValue(next);
                containerSorting = button.getValue();
                requestPacket();
                updateContents();
            }));
            for (int i = 0; i < ROWS_PER_PAGE; i++) {
                final int j = i; // I love Java
                removeButtons.add(addButton(new SpriteButton.RegularAndHighlightSprite(REMOVE_ICON, REMOVE_ICON_HIGHLIGHTED, x + 189, i * 19 + 29, 16, 16, p -> {
                    updateData(data -> data.remove(GlobalPos.of(ClientUtil.getLevel().dimension(), visibleContainers.get(j))));
                    setDataOnStack();
                    requestPacket();
                    updateContents();
                })));
                locateButtons.add(addButton(new SpriteButton.RegularAndHighlightSprite(LOCATE_ICON, LOCATE_ICON_HIGHLIGHTED, x + 206, i * 19 + 29, 16, 16, p -> addParticles(visibleContainers.get(j)))));
            }
        } else {
            addButton(new SortButton<>(StockroomCatalogSorting.Item.ALPHABETICAL_ASC, x + 172, 11, 52, 14, b -> {
                SortButton<StockroomCatalogSorting.Item> button = (SortButton<StockroomCatalogSorting.Item>) b;
                StockroomCatalogSorting.Item next;
                switch (button.getValue()) {
                    case ALPHABETICAL_ASC:
                        next = StockroomCatalogSorting.Item.ALPHABETICAL_DESC;
                        break;
                    case ALPHABETICAL_DESC:
                        next = StockroomCatalogSorting.Item.COUNT_ASC;
                        break;
                    case COUNT_ASC:
                        next = StockroomCatalogSorting.Item.COUNT_DESC;
                        break;
                    case COUNT_DESC:
                    default:
                        next = StockroomCatalogSorting.Item.ALPHABETICAL_ASC;
                        break;
                }
                button.setValue(next);
                itemSorting = button.getValue();
                requestPacket();
                updateContents();
            }));
            for (int i = 0; i < ROWS_PER_PAGE; i++) {
                final int j = i; // I love Java
                locateButtons.add(addButton(new SpriteButton.RegularAndHighlightSprite(LOCATE_ICON, LOCATE_ICON_HIGHLIGHTED, x + 206, i * 19 + 29, 16, 16, p -> addParticles(visibleItems.get(j)))));
            }
        }
        updateContents();
    }

    /** 1.20.1: TextFieldWidget.setTextShadow not available; use reflection to set shadow field. */
    private static void setEditBoxTextShadow(TextFieldWidget editBox) {
        try {
            java.lang.reflect.Field shadow = editBox.getClass().getDeclaredField("f_94117_");
            shadow.setAccessible(true);
            shadow.setBoolean(editBox, false);
        } catch (Exception e) {
            try {
                java.lang.reflect.Field shadow = editBox.getClass().getDeclaredField("shadow");
                shadow.setAccessible(true);
                shadow.setBoolean(editBox, false);
            } catch (Exception ignored) { /* obf name may vary */ }
        }
    }

    @Override
    public void renderBackground(MatrixStack graphics) {
        super.renderBackground(graphics);
        this.minecraft.getTextureManager().bind(BACKGROUND);
        this.blit(graphics, (width - 256) / 2 + 18, 2, 0, 0, 256, 256);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        if (scrollAmount < 0 && forwardButton.visible) {
            forwardButton.onPress();
            return true;
        }
        if (scrollAmount > 0 && backButton.visible) {
            backButton.onPress();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) return true;
        switch (keyCode) {
case GLFW.GLFW_KEY_PAGE_UP: {
                backButton.onPress();
                return true;
            }
case GLFW.GLFW_KEY_PAGE_DOWN: {
                forwardButton.onPress();
                return true;
            }
default: return false;
}
    }

    public void setFromPacket(StockroomCatalogListPacket packet) {
        World level = ClientUtil.getLevel();
        containers = packet.containers()
                .stream()
                .filter(e -> level.getBlockEntity(e) != null && level.getBlockEntity(e).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).isPresent())
                .collect(java.util.stream.Collectors.toList());
        items = packet.items();
        updateContents();
    }

    private void requestPacket() {
        if (hand != null) {
            BCEventHandler.getChannel().sendToServer(new StockroomCatalogRequestListPacket(containerSorting, itemSorting, Either.left(hand)));
        } else if (lectern != null) {
            BCEventHandler.getChannel().sendToServer(new StockroomCatalogRequestListPacket(containerSorting, itemSorting, Either.right(lectern)));
        }
    }

    private void setDataOnStack() {
        StockroomCatalogContent.setOnStack(stack, data);
        if (hand != null) {
            BCEventHandler.getChannel().sendToServer(new StockroomCatalogSyncPacket(data, Either.left(hand)));
        } else if (lectern != null) {
            BCEventHandler.getChannel().sendToServer(new StockroomCatalogSyncPacket(data, Either.right(lectern)));
        }
    }

    private void toggleMode() {
        showContainerList = !showContainerList;
        page = 0;
        search = "";
        init(this.minecraft, this.width, this.height);
        updateContents();
    }

    private void updateData(UnaryOperator<StockroomCatalogContent> operator) {
        data = operator.apply(data);
    }

    private void updateContents() {
        buildVisibleCache();
        forwardButton.visible = page < (showContainerList ? containers.size() - 1 : items.size() - 1) / ROWS_PER_PAGE;
        backButton.visible = page > 0;
        removeButtons.forEach(e -> e.visible = true);
        locateButtons.forEach(e -> e.visible = true);
        if (showContainerList && visibleContainers.size() < ROWS_PER_PAGE) {
            IntStream.range(visibleContainers.size(), ROWS_PER_PAGE).forEach(i -> removeButtons.get(i).visible = false);
            IntStream.range(visibleContainers.size(), ROWS_PER_PAGE).forEach(i -> locateButtons.get(i).visible = false);
        }
        if (!showContainerList && visibleItems.size() < ROWS_PER_PAGE) {
            IntStream.range(visibleItems.size(), ROWS_PER_PAGE).forEach(i -> locateButtons.get(i).visible = false);
        }
    }

    private void buildVisibleCache() {
        if (showContainerList) {
            buildVisibleContainersCache();
        } else {
            buildVisibleItemsCache();
        }
    }

    private void buildVisibleContainersCache() {
        visibleContainers = containers.stream()
                .filter(e -> BCUtil.getNameAtPos(ClientUtil.getLevel(), e).getString().toLowerCase(Locale.ROOT).contains(search))
                .skip((long) page * ROWS_PER_PAGE)
                .limit(ROWS_PER_PAGE)
                .collect(java.util.stream.Collectors.toList());
    }

    private void buildVisibleItemsCache() {
        visibleItems = items.stream()
                .filter(e -> e.item().getHoverName().getString().toLowerCase(Locale.ROOT).contains(search))
                .skip((long) page * ROWS_PER_PAGE)
                .limit(ROWS_PER_PAGE)
                .collect(java.util.stream.Collectors.toList());
    }

    private void addParticles(StockroomCatalogItemEntry entry) {
        for (BlockPos pos : entry.containers()) {
            addParticles(pos);
        }
    }

    private void addParticles(BlockPos pos) {
        ParticleManager particles = ClientUtil.getMc().particleEngine;
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            Particle particle = particles.createParticle(ParticleTypes.POOF, pos.getX() + random.nextDouble(), pos.getY() + random.nextDouble(), pos.getZ() + random.nextDouble(), 0, 0, 0);
            if (particle != null) {
                particle.setLifetime((int) (random.nextDouble() * 20) + 60);
            }
        }
    }

    private static class SortButton<E extends Enum<E> & StockroomCatalogSorting> extends Button {
        private E value;

        public SortButton(E initialValue, int x, int y, int width, int height, Button.IPressable onPress) {
            super(x, y, width, height, message(initialValue), onPress);
            value = initialValue;
        }

        public E getValue() {
            return value;
        }

        public void setValue(E value) {
            this.value = value;
            setMessage(message(value));
        }

        private static <T extends StockroomCatalogSorting> ITextComponent message(T t) {
            return new TranslationTextComponent(Translations.STOCKROOM_CATALOG_SORT_KEY, new TranslationTextComponent(t.getTranslationKey()));
        }
    }
}
