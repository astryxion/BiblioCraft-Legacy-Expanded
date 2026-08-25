package com.github.minecraftschurlimods.bibliocraft.api.woodtype;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.WoodType;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Holds all information Bibliocraft needs about a wood type.
 *
 * @param id         The id of the wood type.
 * @param woodType   The corresponding vanilla {@link WoodType}.
 * @param properties A supplier for the wood type's {@link AbstractBlock.Properties}.
 * @param texture    The location of the wood type's plank texture. Used in datagen.
 * @param family     A supplier for the corresponding {@link BlockFamily}. Used in datagen.
 */
public final class BibliocraftWoodType  {
    private final ResourceLocation id;
    private final WoodType woodType;
    private final Supplier<AbstractBlock.Properties> properties;
    private final ResourceLocation texture;
    private final Supplier<BlockFamily> family;

    public BibliocraftWoodType(ResourceLocation id, WoodType woodType, Supplier<AbstractBlock.Properties> properties, ResourceLocation texture, Supplier<BlockFamily> family) {
        this.id = id;
        this.woodType = woodType;
        this.properties = properties;
        this.texture = texture;
        this.family = family;
    }

    public ResourceLocation id() { return this.id; }
    public WoodType woodType() { return this.woodType; }
    public Supplier<AbstractBlock.Properties> properties() { return this.properties; }
    public ResourceLocation texture() { return this.texture; }
    public Supplier<BlockFamily> family() { return this.family; }

    /**
     * @return The namespace of the id of this wood type.
     */
    public String getNamespace() {
        return id().getNamespace();
    }

    /**
     * @return The path of the id of this wood type.
     */
    public String getPath() {
        return id().getPath();
    }

    /**
     * @return The wood type prefix used for registration. Keeps the mod id for cases when two mods add identically named wood types.
     */
    public String getRegistrationPrefix() {
        return getNamespace().equals("minecraft") ? getPath() : id().toString().replace(':', '_');
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BibliocraftWoodType other = (BibliocraftWoodType) o;
        return java.util.Objects.equals(this.id, other.id) && java.util.Objects.equals(this.woodType, other.woodType) && java.util.Objects.equals(this.properties, other.properties) && java.util.Objects.equals(this.texture, other.texture) && java.util.Objects.equals(this.family, other.family);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.id, this.woodType, this.properties, this.texture, this.family);
    }

    @Override
    public String toString() {
        return "BibliocraftWoodType[" + "id=" + this.id + ", " + "woodType=" + this.woodType + ", " + "properties=" + this.properties + ", " + "texture=" + this.texture + ", " + "family=" + this.family + "]";
    }

    /**
     * 1.16.5 has no vanilla {@code net.minecraft.data.BlockFamily}. Nested here so wood-type datagen
     * still exposes the same {@code getBaseBlock()} / {@code get(Variant.SLAB)} accessors.
     */
    public static final class BlockFamily {
        private final Block baseBlock;
        private final Map<Variant, Block> variants = new EnumMap<Variant, Block>(Variant.class);

        public BlockFamily(Block baseBlock) {
            this.baseBlock = baseBlock;
        }

        public BlockFamily(Block baseBlock, Block slab) {
            this(baseBlock);
            this.variants.put(Variant.SLAB, slab);
        }

        public Block getBaseBlock() {
            return this.baseBlock;
        }

        public Block get(Variant variant) {
            return this.variants.get(variant);
        }

        public enum Variant {
            BUTTON,
            DOOR,
            FENCE,
            FENCE_GATE,
            SIGN,
            SLAB,
            STAIRS,
            PRESSURE_PLATE,
            TRAPDOOR,
            WALL
        }
    }

}
