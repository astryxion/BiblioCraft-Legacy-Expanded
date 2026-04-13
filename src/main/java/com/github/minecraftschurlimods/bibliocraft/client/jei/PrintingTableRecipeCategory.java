package com.github.minecraftschurlimods.bibliocraft.client.jei;

import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableRecipe;
import com.github.minecraftschurlimods.bibliocraft.init.BCItems;
import com.github.minecraftschurlimods.bibliocraft.init.BCRecipes;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.Translations;
import com.mojang.datafixers.util.Pair;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class PrintingTableRecipeCategory implements IRecipeCategory<PrintingTableRecipe> {
    public static final RecipeType<PrintingTableRecipe> TYPE = RecipeType.create(BCRecipes.PRINTING_TABLE.getId().getNamespace(), BCRecipes.PRINTING_TABLE.getId().getPath(), PrintingTableRecipe.class);
    private static final ResourceLocation LEVEL = BCUtil.bcLoc("textures/gui/sprites/level.png");
    private static final ResourceLocation JEI_RECIPE_GUI = new ResourceLocation("jei", "textures/jei/gui/gui_vanilla.png");
    private static final int LEVEL_X = 98;
    private static final int LEVEL_Y = 24;
    private static final int LEVEL_SIZE = 9;
    private static final int PLUS_U = 60;
    private static final int PLUS_V = 18;
    private static final int ARROW_U = 82;
    private static final int ARROW_V = 128;
    private static final int ARROW_W = 24;
    private static final int ARROW_H = 17;
    private final mezz.jei.api.helpers.IGuiHelper guiHelper;
    private final int width = 147;
    private final int height = 54;

    public PrintingTableRecipeCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
    }

    @Override
    public RecipeType<PrintingTableRecipe> getRecipeType() { return TYPE; }
    @Override
    public Component getTitle() { return Translations.PRINTING_TABLE_CATEGORY; }
    @Override
    public mezz.jei.api.gui.drawable.IDrawable getBackground() { return guiHelper.createBlankDrawable(width, height); }
    @Override
    public mezz.jei.api.gui.drawable.IDrawable getIcon() { return guiHelper.createDrawableItemStack(new net.minecraft.world.item.ItemStack(BCItems.PRINTING_TABLE.get())); }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PrintingTableRecipe recipe, IFocusGroup focuses) {
        Pair<List<Ingredient>, Ingredient> ingredients = recipe.getDisplayIngredients();
        List<Ingredient> left = ingredients.getFirst();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, j * 18 + 1, i * 18 + 1).setBackground(guiHelper.getSlotDrawable(), 0, 0);
                if (i * 3 + j < left.size()) {
                    slot.addIngredients(left.get(i * 3 + j));
                }
            }
        }
        builder.addSlot(RecipeIngredientRole.INPUT, 74, 19).setBackground(guiHelper.getSlotDrawable(), 0, 0).addIngredients(ingredients.getSecond());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 126, 19).setBackground(guiHelper.getSlotDrawable(), 0, 0).addItemStack(recipe.getResultItem(net.minecraft.core.RegistryAccess.EMPTY));
    }

    /** 1.20.1: IRecipeExtrasBuilder has no addDrawable/addRecipePlusSign; plus/arrow/text drawn in draw() only. */
    public void createRecipeExtras(IRecipeExtrasBuilder builder, PrintingTableRecipe recipe, IFocusGroup focuses) {
    }

    @Override
    public void draw(PrintingTableRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        IDrawable plusDrawable = guiHelper.createDrawable(JEI_RECIPE_GUI, PLUS_U, PLUS_V, 16, 14);
        plusDrawable.draw(guiGraphics, 57, 21);
        IDrawable arrowDrawable = guiHelper.createDrawable(JEI_RECIPE_GUI, ARROW_U, ARROW_V, ARROW_W, ARROW_H);
        arrowDrawable.draw(guiGraphics, 95, 20);
        if (recipe.canHaveExperienceCost()) {
            guiGraphics.blit(LEVEL, LEVEL_X, LEVEL_Y, 0, 0, LEVEL_SIZE, LEVEL_SIZE, LEVEL_SIZE, LEVEL_SIZE);
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.font != null) {
            guiGraphics.drawString(mc.font, Component.translatable(Translations.JEI_SECONDS_KEY, recipe.getDuration() / 20).getString(), width - 57, 10, 0xFF808080, false);
            guiGraphics.drawString(mc.font, Component.translatable(Translations.PRINTING_TABLE_MODE_KEY, Component.translatable(recipe.getMode().getTranslationKey())).getString(), width - 57, 22, 0xFF808080, false);
        }
    }

    public void getTooltip(ITooltipBuilder tooltip, PrintingTableRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (mouseX >= LEVEL_X && mouseX < LEVEL_X + LEVEL_SIZE && mouseY >= LEVEL_Y && mouseY < LEVEL_Y + LEVEL_SIZE) {
            tooltip.add(Translations.REQUIRES_EXPERIENCE);
        }
    }
}
