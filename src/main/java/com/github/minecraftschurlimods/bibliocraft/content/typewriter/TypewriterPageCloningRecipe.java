package com.github.minecraftschurlimods.bibliocraft.content.typewriter;

import com.github.minecraftschurlimods.bibliocraft.init.BCItems;
import com.github.minecraftschurlimods.bibliocraft.init.BCRecipes;
import com.github.minecraftschurlimods.bibliocraft.init.BCTags;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.CraftingInventory;

import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.world.World;

public class TypewriterPageCloningRecipe extends SpecialRecipe {
    public TypewriterPageCloningRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory container, World level) {
        int paper = 0;
        ItemStack stack = ItemStack.EMPTY;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack written = container.getItem(i);
            if (written.isEmpty()) continue;
            if (written.getItem() == BCItems.TYPEWRITER_PAGE.get()) {
                if (!stack.isEmpty()) return false;
                stack = written;
            } else if (BCTags.Items.contains(BCTags.Items.TYPEWRITER_PAPER, written.getItem())) {
                paper++;
            } else return false;
        }
        return !stack.isEmpty() && paper > 0;
    }

    @Override
    public ItemStack assemble(CraftingInventory container) {
        int paper = 0;
        ItemStack stack = ItemStack.EMPTY;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack written = container.getItem(i);
            if (written.isEmpty()) continue;
            if (written.getItem() == BCItems.TYPEWRITER_PAGE.get()) {
                if (!stack.isEmpty()) return ItemStack.EMPTY;
                stack = written;
            } else if (BCTags.Items.contains(BCTags.Items.TYPEWRITER_PAPER, written.getItem())) {
                paper++;
            } else return ItemStack.EMPTY;
        }
        TypewriterPage page = TypewriterPage.getFromStack(stack);
        if (stack.isEmpty() || paper < 1) return ItemStack.EMPTY;
        ItemStack result = stack.copy();
        result.setCount(paper);
        TypewriterPage.setOnStack(result, page);
        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory container) {
        NonNullList<ItemStack> list = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < list.size(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.hasContainerItem()) {
                list.set(i, stack.getContainerItem());
            } else if (stack.getItem() == BCItems.TYPEWRITER_PAGE.get()) {
                ItemStack one = stack.copy();
                one.setCount(1);
                list.set(i, one);
            }
        }
        return list;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return BCRecipes.TYPEWRITER_PAGE_CLONING.get();
    }
}
