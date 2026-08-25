package com.github.minecraftschurlimods.bibliocraft.client.screen;

import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.StringTextComponent;
import com.github.minecraftschurlimods.bibliocraft.client.widget.ExperienceBarButton;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableBlockEntity;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableInputPacket;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableMenu;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableMode;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.ClientUtil;
import com.github.minecraftschurlimods.bibliocraft.util.Translations;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.player.PlayerInventory;
import com.github.minecraftschurlimods.bibliocraft.BCEventHandler;

public class PrintingTableScreen extends BCScreenWithToggleableSlots<PrintingTableMenu> {
    private static final ResourceLocation BACKGROUND = BCUtil.bcLoc("textures/gui/printing_table.png");
    private static final ResourceLocation EXPERIENCE_BAR_BACKGROUND = BCUtil.bcLoc("textures/gui/sprites/experience_bar_background.png");
    private static final ResourceLocation EXPERIENCE_BAR_PROGRESS = BCUtil.bcLoc("textures/gui/sprites/experience_bar_progress.png");
    private static final ResourceLocation PROGRESS = BCUtil.bcLoc("textures/gui/sprites/printing_table_progress.png");
    private Button modeButton;
    private Button experienceBarButton;

    public PrintingTableScreen(PrintingTableMenu menu, PlayerInventory inventory, ITextComponent title) {
        super(menu, inventory, title, BACKGROUND);
    }

    @Override
    protected void init() {
        leftPos = (width - 192) / 2;
        topPos = (height - 192) / 2;
        PrintingTableBlockEntity blockEntity = menu.getBlockEntity();
        modeButton = addButton(new Button(leftPos + 81, topPos + 6, 82, 20, new StringTextComponent(""), $ -> {
            PrintingTableMode mode = blockEntity.getMode();
            PrintingTableMode next;
            switch (mode) {
                case BIND:
                    next = PrintingTableMode.CLONE;
                    break;
                case CLONE:
                    next = PrintingTableMode.MERGE;
                    break;
                case MERGE:
                default:
                    next = PrintingTableMode.BIND;
                    break;
            }
            blockEntity.setMode(next);
            setModeButtonMessage();
            experienceBarButton.visible = blockEntity.getMode() == PrintingTableMode.CLONE;
            BCEventHandler.getChannel().sendToServer(new PrintingTableInputPacket(blockEntity.getBlockPos(), blockEntity.getMode()));
        }));
        setModeButtonMessage();
        experienceBarButton = addButton(new ExperienceBarButton(Translations.PRINTING_TABLE_ADD_EXPERIENCE, leftPos + 81, topPos + 65, 82, 5, EXPERIENCE_BAR_BACKGROUND, EXPERIENCE_BAR_PROGRESS,
                () -> BCUtil.getLevelForExperience(blockEntity.getExperience()),
                () -> {
                    if (blockEntity.getExperienceCost() <= 0) return 0f;
                    if (blockEntity.isExperienceFull()) return 1f;
                    int experience = blockEntity.getExperience();
                    int level = BCUtil.getLevelForExperience(experience);
                    float experienceForLevel = BCUtil.getExperienceForLevel(level);
                    float experienceForNextLevel = BCUtil.getExperienceForLevel(level + 1);
                    return MathHelper.clamp((experience - experienceForLevel) / (experienceForNextLevel - experienceForLevel), 0, 1);
                },
                $ -> {
                    if (blockEntity.isExperienceFull()) return;
                    int experienceCost = blockEntity.getExperienceCost();
                    ClientPlayerEntity player = ClientUtil.getPlayer();
                    int experienceToGive = player.isCreative() ? experienceCost : Math.min(player.totalExperience, experienceCost);
                    if (experienceToGive > 0) {
                        blockEntity.addExperience(experienceToGive);
                        player.giveExperiencePoints(-experienceToGive);
                        BCEventHandler.getChannel().sendToServer(new PrintingTableInputPacket(blockEntity.getBlockPos(), experienceToGive));
                    }
                }
        ));
        experienceBarButton.visible = blockEntity.getMode() == PrintingTableMode.CLONE;
    }

    @Override
    protected void renderBg(MatrixStack graphics, float partialTicks, int x, int y) {
        super.renderBg(graphics, partialTicks, x, y);
        float progress = menu.getBlockEntity().getProgress();
        int width = progress == 1f ? 0 : MathHelper.ceil(progress * 24);
        this.minecraft.getTextureManager().bind(PROGRESS);
        net.minecraft.client.gui.AbstractGui.blit(graphics, leftPos + 110, topPos + 35, 0, 0, 0, width, 16, 24, 16);
    }

    @Override
    public void render(MatrixStack graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        int experienceCost = menu.getBlockEntity().getLevelCost();
        if (experienceCost > 0) {
            ClientUtil.renderXpText(experienceCost + "", graphics, leftPos + 122, topPos + 39);
        }
    }

    private void setModeButtonMessage() {
        modeButton.setMessage(new TranslationTextComponent(Translations.PRINTING_TABLE_MODE_KEY, new TranslationTextComponent(menu.getBlockEntity().getMode().getTranslationKey())));
    }
}
