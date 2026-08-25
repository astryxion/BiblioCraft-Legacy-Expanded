package com.github.minecraftschurlimods.bibliocraft.api.datagen;

import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.data.TagsProvider;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraftforge.common.data.ExistingFileHelper;
import javax.annotation.Nullable;

/**
 * The default {@link ItemTagsProvider} implementation in 1.20.1 clears the builders before calling {@code addTags}.
 * 1.16.5 does not clear that way; this subclass still exposes {@link #getBuilder(ITag.INamedTag)} for datagen helpers.
 */
@SuppressWarnings("unused")
public abstract class NonClearingItemTagsProvider extends ItemTagsProvider {
    /**
     * See super constructor for information.
     */
    public NonClearingItemTagsProvider(DataGenerator output, BlockTagsProvider blockTags, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, blockTags, modId, existingFileHelper);
    }

    /** Public accessor for {@link #tag(ITag.INamedTag)} for use by datagen helpers. */
    public TagsProvider.Builder<Item> getBuilder(ITag.INamedTag<Item> key) {
        return tag(key);
    }
}
