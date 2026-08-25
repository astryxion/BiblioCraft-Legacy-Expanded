package com.github.minecraftschurlimods.bibliocraft.api.woodtype;

import com.github.minecraftschurlimods.bibliocraft.api.woodtype.BibliocraftWoodType.BlockFamily;
import net.minecraft.util.ResourceLocation;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.WoodType;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Register your own {@link BibliocraftWoodType}s here.
 * <p>
 * This event is not cancelable. This event is fired on the mod event bus.
 */
public class RegisterBibliocraftWoodTypesEvent extends Event implements IModBusEvent {
    private final Map<ResourceLocation, BibliocraftWoodType> values;

    
    public RegisterBibliocraftWoodTypesEvent(Map<ResourceLocation, BibliocraftWoodType> values) {
        this.values = values;
    }

    /**
     * Registers a new {@link BibliocraftWoodType}.
     *
     * @param id         The id of the wood type. Should be the id of the mod the wood type comes from, and the name of the wood type.
     * @param woodType   The vanilla {@link WoodType} associated with this wood type.
     * @param properties A supplier for the {@link AbstractBlock.Properties} associated with this wood type. Typically, this is a copy of the wood type's planks' properties.
     * @param texture    The location of the wood type's planks texture, for use in datagen.
     * @param family     A supplier for the {@link BlockFamily} for the associated wood type, for use in datagen.
     */
    public void register(ResourceLocation id, WoodType woodType, Supplier<AbstractBlock.Properties> properties, ResourceLocation texture, Supplier<BlockFamily> family) {
        if (values.containsKey(id))
            throw new IllegalStateException("Wood type " + id + " is already registered");
        values.put(id, new BibliocraftWoodType(id, woodType, properties, texture, family));
    }
}
