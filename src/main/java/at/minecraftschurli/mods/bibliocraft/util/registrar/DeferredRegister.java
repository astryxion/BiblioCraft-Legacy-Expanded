package at.minecraftschurli.mods.bibliocraft.util.registrar;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class DeferredRegister<T> {
    protected final ResourceKey<Registry<T>> registryKey;
    protected final String namespace;
    protected final Map<DeferredHolder<T, ? extends T>, Supplier<? extends T>> entries = new LinkedHashMap<>();

    protected DeferredRegister(ResourceKey<Registry<T>> registryKey, String namespace) {
        this.registryKey = registryKey;
        this.namespace = namespace;
    }

    public static DeferredRegister.Blocks createBlocks(String namespace) {
        return new Blocks(namespace);
    }

    public static DeferredRegister.Items createItems(String namespace) {
        return new Items(namespace);
    }

    public static DeferredRegister.Entities createEntities(String namespace) {
        return new Entities(namespace);
    }

    public static DeferredRegister.DataComponents createDataComponents(ResourceKey<Registry<DataComponentType<?>>> registryKey, String namespace) {
        return new DataComponents(registryKey, namespace);
    }

    public static <T> DeferredRegister<T> create(ResourceKey<Registry<T>> registryKey, String namespace) {
        return new DeferredRegister<>(registryKey, namespace);
    }

    public ResourceKey<Registry<T>> getRegistryKey() {
        return registryKey;
    }

    public Collection<DeferredHolder<T, ? extends T>> getEntries() {
        return entries.keySet();
    }

    Supplier<? extends T> getSupplier(DeferredHolder<T, ? extends T> holder) {
        return entries.get(holder);
    }

    public <I extends T> DeferredHolder<T, I> register(String path, Supplier<? extends I> supplier) {
        DeferredHolder<T, I> holder = DeferredHolder.create(registryKey, Identifier.fromNamespaceAndPath(namespace, path));
        entries.put(holder, supplier);
        return holder;
    }

    public <I extends T> DeferredHolder<T, I> register(String path, Function<Identifier, ? extends I> factory) {
        DeferredHolder<T, I> holder = DeferredHolder.create(registryKey, Identifier.fromNamespaceAndPath(namespace, path));
        entries.put(holder, () -> factory.apply(holder.getId()));
        return holder;
    }

    public void registerAll(Registry<T> registry) {
        for (Map.Entry<DeferredHolder<T, ? extends T>, Supplier<? extends T>> entry : entries.entrySet()) {
            if (entry.getKey().isBound()) {
                continue;
            }
            @SuppressWarnings("unchecked")
            T value = (T) entry.getValue().get();
            Registry.register(registry, entry.getKey().getId(), value);
            bind(entry.getKey(), value);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void bind(DeferredHolder<T, ? extends T> holder, T value) {
        ((DeferredHolder<T, T>) holder).bind(value);
    }

    public static final class Blocks extends DeferredRegister<Block> {
        private Blocks(String namespace) {
            super(Registries.BLOCK, namespace);
        }

        public <B extends Block> DeferredBlock<B> registerBlock(String path, Function<BlockBehaviour.Properties, ? extends B> factory, UnaryOperator<BlockBehaviour.Properties> properties) {
            return registerBlock(path, factory, () -> properties.apply(BlockBehaviour.Properties.of()));
        }

        public <B extends Block> DeferredBlock<B> registerBlock(String path, Function<BlockBehaviour.Properties, ? extends B> factory, Supplier<BlockBehaviour.Properties> properties) {
            DeferredBlock<B> holder = new DeferredBlock<>(ResourceKey.create(registryKey, Identifier.fromNamespaceAndPath(namespace, path)));
            entries.put(holder, () -> factory.apply(properties.get().setId(holder.getKey())));
            return holder;
        }
    }

    public static final class Items extends DeferredRegister<Item> {
        private Items(String namespace) {
            super(Registries.ITEM, namespace);
        }

        public <I extends Item> DeferredItem<I> registerItem(String path, Function<Item.Properties, ? extends I> factory, UnaryOperator<Item.Properties> properties) {
            DeferredItem<I> holder = new DeferredItem<>(ResourceKey.create(registryKey, Identifier.fromNamespaceAndPath(namespace, path)));
            entries.put(holder, () -> factory.apply(properties.apply(new Item.Properties()).setId(holder.getKey())));
            return holder;
        }

        public <I extends Item> DeferredItem<I> registerItem(String path, Function<Item.Properties, ? extends I> factory) {
            return registerItem(path, factory, UnaryOperator.identity());
        }

        public DeferredItem<Item> registerSimpleItem(String path) {
            return registerItem(path, Item::new);
        }

        public DeferredItem<BlockItem> registerSimpleBlockItem(DeferredBlock<? extends Block> block) {
            return registerSimpleBlockItem(block, UnaryOperator.identity());
        }

        public DeferredItem<BlockItem> registerSimpleBlockItem(DeferredBlock<? extends Block> block, UnaryOperator<Item.Properties> properties) {
            return registerItem(block.getId().getPath(), props -> new BlockItem(block.get(), props), props -> properties.apply(props.useBlockDescriptionPrefix()));
        }
    }

    public static final class Entities extends DeferredRegister<EntityType<?>> {
        private Entities(String namespace) {
            super(Registries.ENTITY_TYPE, namespace);
        }

        public <E extends net.minecraft.world.entity.Entity> DeferredHolder<EntityType<?>, EntityType<E>> registerEntityType(
                String path,
                EntityType.EntityFactory<E> factory,
                MobCategory category,
                UnaryOperator<EntityType.Builder<E>> builder
        ) {
            DeferredHolder<EntityType<?>, EntityType<E>> holder = DeferredHolder.create(registryKey, Identifier.fromNamespaceAndPath(namespace, path));
            entries.put(holder, () -> builder.apply(EntityType.Builder.of(factory, category)).build(holder.getKey()));
            return holder;
        }
    }

    public static final class DataComponents extends DeferredRegister<DataComponentType<?>> {
        private DataComponents(ResourceKey<Registry<DataComponentType<?>>> registryKey, String namespace) {
            super(registryKey, namespace);
        }

        public <C> DeferredHolder<DataComponentType<?>, DataComponentType<C>> registerComponentType(String path, UnaryOperator<DataComponentType.Builder<C>> builder) {
            DeferredHolder<DataComponentType<?>, DataComponentType<C>> holder = DeferredHolder.create(registryKey, Identifier.fromNamespaceAndPath(namespace, path));
            entries.put(holder, () -> builder.apply(DataComponentType.builder()).build());
            return holder;
        }
    }
}
