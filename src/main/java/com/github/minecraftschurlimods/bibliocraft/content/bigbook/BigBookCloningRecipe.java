package com.github.minecraftschurlimods.bibliocraft.content.bigbook;

import com.github.minecraftschurlimods.bibliocraft.init.BCItems;
import com.github.minecraftschurlimods.bibliocraft.init.BCRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.CraftingInventory;

import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.world.World;

public class BigBookCloningRecipe extends SpecialRecipe {
    public BigBookCloningRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory container, World level) {
        int books = 0;
        ItemStack stack = ItemStack.EMPTY;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack written = container.getItem(i);
            if (written.isEmpty()) continue;
            if (written.getItem() == BCItems.WRITTEN_BIG_BOOK.get()) {
                if (!stack.isEmpty()) return false;
                stack = written;
            } else if (written.getItem() == BCItems.BIG_BOOK.get()) {
                books++;
            } else return false;
        }
        return !stack.isEmpty() && books > 0;
    }

    @Override
    public ItemStack assemble(CraftingInventory container) {
        int books = 0;
        ItemStack stack = ItemStack.EMPTY;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack written = container.getItem(i);
            if (written.isEmpty()) continue;
            if (written.getItem() == BCItems.WRITTEN_BIG_BOOK.get()) {
                if (!stack.isEmpty()) return ItemStack.EMPTY;
                stack = written;
            } else if (written.getItem() == BCItems.BIG_BOOK.get()) {
                books++;
            } else return ItemStack.EMPTY;
        }
        WrittenBigBookContent content = WrittenBigBookContent.getFromStack(stack);
        if (stack.isEmpty() || books < 1 || content.equals(WrittenBigBookContent.DEFAULT)) return ItemStack.EMPTY;
        WrittenBigBookContent contentCopy = content.tryCraftCopy();
        if (contentCopy == null) return ItemStack.EMPTY;
        ItemStack result = stack.copy();
        result.setCount(books);
        WrittenBigBookContent.setOnStack(result, contentCopy);
        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory container) {
        NonNullList<ItemStack> list = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < list.size(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.hasContainerItem()) {
                list.set(i, stack.getContainerItem());
            } else if (stack.getItem() == BCItems.WRITTEN_BIG_BOOK.get()) {
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
        return BCRecipes.BIG_BOOK_CLONING.get();
    }
}
