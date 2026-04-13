package com.github.minecraftschurlimods.bibliocraft.content.typewriter;

import com.github.minecraftschurlimods.bibliocraft.init.BCItems;
import com.github.minecraftschurlimods.bibliocraft.init.BCRecipes;
import com.github.minecraftschurlimods.bibliocraft.init.BCTags;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class TypewriterPageCloningRecipe extends CustomRecipe {
    public TypewriterPageCloningRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    public TypewriterPageCloningRecipe(ResourceLocation id) {
        this(id, CraftingBookCategory.MISC);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        int paper = 0;
        ItemStack stack = ItemStack.EMPTY;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack written = container.getItem(i);
            if (written.isEmpty()) continue;
            if (written.is(BCItems.TYPEWRITER_PAGE.get())) {
                if (!stack.isEmpty()) return false;
                stack = written;
            } else if (written.is(BCTags.Items.TYPEWRITER_PAPER)) {
                paper++;
            } else return false;
        }
        return !stack.isEmpty() && paper > 0;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, net.minecraft.core.RegistryAccess registries) {
        int paper = 0;
        ItemStack stack = ItemStack.EMPTY;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack written = container.getItem(i);
            if (written.isEmpty()) continue;
            if (written.is(BCItems.TYPEWRITER_PAGE.get())) {
                if (!stack.isEmpty()) return ItemStack.EMPTY;
                stack = written;
            } else if (written.is(BCTags.Items.TYPEWRITER_PAPER)) {
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
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        NonNullList<ItemStack> list = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < list.size(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.getCraftingRemainingItem().isEmpty()) {
                list.set(i, stack.getCraftingRemainingItem());
            } else if (stack.is(BCItems.TYPEWRITER_PAGE.get())) {
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
        return BCRecipes.TYPEWRITER_PAGE_CLONING.get();
    }
}
