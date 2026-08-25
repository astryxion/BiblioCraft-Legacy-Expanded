package com.github.minecraftschurlimods.bibliocraft.client.jei;

import net.minecraft.util.text.TranslationTextComponent;
import com.github.minecraftschurlimods.bibliocraft.content.printingtable.PrintingTableRecipe;
import com.github.minecraftschurlimods.bibliocraft.init.BCItems;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.Translations;
import com.mojang.datafixers.util.Pair;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PrintingTableRecipeCategory implements IRecipeCategory<PrintingTableRecipe> {
    public static final ResourceLocation UID = BCUtil.bcLoc("printing_table");
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
    private final IGuiHelper guiHelper;
    private final int width = 147;
    private final int height = 54;

    public PrintingTableRecipeCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
    }

    @Override
    public ResourceLocation getUid() { return UID; }
    @Override
    public Class<? extends PrintingTableRecipe> getRecipeClass() { return PrintingTableRecipe.class; }
    @Override
    public String getTitle() { return Translations.PRINTING_TABLE_CATEGORY.getString(); }
    @Override
    public IDrawable getBackground() { return guiHelper.createBlankDrawable(width, height); }
    @Override
    public IDrawable getIcon() { return guiHelper.createDrawableIngredient(new ItemStack(BCItems.PRINTING_TABLE.get())); }

    @Override
    public void setIngredients(PrintingTableRecipe recipe, IIngredients ingredients) {
        Pair<List<Ingredient>, Ingredient> display = recipe.getDisplayIngredients();
        List<Ingredient> inputs = new ArrayList<Ingredient>(display.getFirst());
        inputs.add(display.getSecond());
        ingredients.setInputIngredients(inputs);
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
    }

    @Override
    public void setRecipe(IRecipeLayout layout, PrintingTableRecipe recipe, IIngredients ingredients) {
        Pair<List<Ingredient>, Ingredient> display = recipe.getDisplayIngredients();
        List<Ingredient> left = display.getFirst();
        IGuiItemStackGroup stacks = layout.getItemStacks();
        int slot = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                stacks.init(slot, true, j * 18, i * 18);
                if (i * 3 + j < left.size()) {
                    stacks.set(slot, Arrays.asList(left.get(i * 3 + j).getItems()));
                }
                slot++;
            }
        }
        stacks.init(9, true, 73, 18);
        stacks.set(9, Arrays.asList(display.getSecond().getItems()));
        stacks.init(10, false, 125, 18);
        stacks.set(10, recipe.getResultItem());
    }

    @Override
    public void draw(PrintingTableRecipe recipe, MatrixStack guiGraphics, double mouseX, double mouseY) {
        IDrawable plusDrawable = guiHelper.createDrawable(JEI_RECIPE_GUI, PLUS_U, PLUS_V, 16, 14);
        plusDrawable.draw(guiGraphics, 57, 21);
        IDrawable arrowDrawable = guiHelper.createDrawable(JEI_RECIPE_GUI, ARROW_U, ARROW_V, ARROW_W, ARROW_H);
        arrowDrawable.draw(guiGraphics, 95, 20);
        if (recipe.canHaveExperienceCost()) {
            Minecraft.getInstance().getTextureManager().bind(LEVEL);
            AbstractGui.blit(guiGraphics, LEVEL_X, LEVEL_Y, 0, 0, 0, LEVEL_SIZE, LEVEL_SIZE, LEVEL_SIZE, LEVEL_SIZE);
        }
        FontRenderer font = Minecraft.getInstance().font;
        if (font != null) {
            font.draw(guiGraphics, new TranslationTextComponent(Translations.JEI_SECONDS_KEY, recipe.getDuration() / 20).getString(), width - 57, 10, 0xFF808080);
            font.draw(guiGraphics, new TranslationTextComponent(Translations.PRINTING_TABLE_MODE_KEY, new TranslationTextComponent(recipe.getMode().getTranslationKey())).getString(), width - 57, 22, 0xFF808080);
        }
    }

    @Override
    public List<ITextComponent> getTooltipStrings(PrintingTableRecipe recipe, double mouseX, double mouseY) {
        if (recipe.canHaveExperienceCost() && mouseX >= LEVEL_X && mouseX < LEVEL_X + LEVEL_SIZE && mouseY >= LEVEL_Y && mouseY < LEVEL_Y + LEVEL_SIZE) {
            return Collections.singletonList(Translations.REQUIRES_EXPERIENCE);
        }
        return Collections.emptyList();
    }
}
