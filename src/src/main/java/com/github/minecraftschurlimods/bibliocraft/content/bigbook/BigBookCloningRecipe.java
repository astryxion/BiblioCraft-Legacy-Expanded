package com.github.minecraftschurlimods.bibliocraft.content.bigbook;

import com.github.minecraftschurlimods.bibliocraft.init.BCItems;
import com.github.minecraftschurlimods.bibliocraft.init.BCRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class BigBookCloningRecipe extends CustomRecipe {
    public BigBookCloningRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    /** 1.20.1: SimpleCraftingRecipeSerializer may only pass id; use MISC category. */
    public BigBookCloningRecipe(ResourceLocation id) {
        this(id, CraftingBookCategory.MISC);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        int books = 0;
        ItemStack stack = ItemStack.EMPTY;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack written = container.getItem(i);
            if (written.isEmpty()) continue;
            if (written.is(BCItems.WRITTEN_BIG_BOOK.get())) {
                if (!stack.isEmpty()) return false;
                stack = written;
            } else if (written.is(BCItems.BIG_BOOK.get())) {
                books++;
            } else return false;
        }
        return !stack.isEmpty() && books > 0;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, net.minecraft.core.RegistryAccess registries) {
        int books = 0;
        ItemStack stack = ItemStack.EMPTY;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack written = container.getItem(i);
            if (written.isEmpty()) continue;
            if (written.is(BCItems.WRITTEN_BIG_BOOK.get())) {
                if (!stack.isEmpty()) return ItemStack.EMPTY;
                stack = written;
            } else if (written.is(BCItems.BIG_BOOK.get())) {
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
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        NonNullList<ItemStack> list = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < list.size(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.getCraftingRemainingItem().isEmpty()) {
                list.set(i, stack.getCraftingRemainingItem());
            } else if (stack.is(BCItems.WRITTEN_BIG_BOOK.get())) {
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
    public RecipeSerializer<?> getSerializer() {
        return BCRecipes.BIG_BOOK_CLONING.get();
    }
}
