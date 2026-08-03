package at.minecraftschurli.mods.bibliocraft.api.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

/// The default [ItemTagsProvider] implementation clears the builders before calling [ItemTagsProvider#addTags(HolderLookup.Provider)].
/// We don't want that, so we override [ItemTagsProvider#addTags(HolderLookup.Provider)] to not do that.
@SuppressWarnings("unused")
public abstract class NonClearingItemTagsProvider extends ItemTagsProvider {
    // Store the provider here because while the superclass has it, it is private there.
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;

    /// See super constructor for information.
    public NonClearingItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId) {
        super(output, lookupProvider, modId);
        this.lookupProvider = lookupProvider;
    }

    @Override
    protected CompletableFuture<HolderLookup.Provider> createContentsProvider() {
        return lookupProvider.thenApply(provider -> {
            addTags(provider);
            return provider;
        });
    }

    protected LazyItemTagAppender lazyTag(TagKey<Item> key) {
        TagBuilder tagbuilder = this.getOrCreateRawBuilder(key);
        return new LazyItemTagAppender(TagAppender.forBuilder(tagbuilder));
    }

    @Override
    public TagAppender<Item> tag(TagKey<Item> key) {
        return super.tag(key);
    }

    protected final class LazyItemTagAppender {
        private final TagAppender<Item> delegate;

        private LazyItemTagAppender(TagAppender<Item> delegate) {
            this.delegate = delegate;
        }

        public LazyItemTagAppender addOptional(Identifier id) {
            this.delegate.addOptional(ResourceKey.create(Registries.ITEM, id));
            return this;
        }

        public LazyItemTagAppender addOptionalTag(TagKey<Item> tag) {
            this.delegate.addOptionalTag(tag);
            return this;
        }
    }
}
