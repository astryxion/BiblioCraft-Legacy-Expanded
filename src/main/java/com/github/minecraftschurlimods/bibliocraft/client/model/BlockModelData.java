package com.github.minecraftschurlimods.bibliocraft.client.model;

import java.util.Collections;
import java.util.Map;

/**
 * Minimal model data for block model rendering (Fabric replacement for NeoForge ModelData).
 * Used by custom baked models to receive block-entity-dependent data.
 */
public final class BlockModelData {
    public static final BlockModelData EMPTY = new BlockModelData(Collections.emptyMap());

    private final Map<?, ?> data;

    private BlockModelData(Map<?, ?> data) {
        this.data = data;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(BlockModelProperty<T> prop) {
        return (T) data.get(prop);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final java.util.Map<BlockModelProperty<?>, Object> map = new java.util.HashMap<>();

        public <T> Builder with(BlockModelProperty<T> prop, T value) {
            map.put(prop, value);
            return this;
        }

        public BlockModelData build() {
            return new BlockModelData(Map.copyOf(map));
        }
    }

    /** Property key for model data. */
    public static final class BlockModelProperty<T> {
        public BlockModelProperty() {}
    }
}
