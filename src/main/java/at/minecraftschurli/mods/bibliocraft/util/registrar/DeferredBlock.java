package at.minecraftschurli.mods.bibliocraft.util.registrar;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

public class DeferredBlock<T extends Block> extends DeferredHolder<Block, T> {
    DeferredBlock(ResourceKey<Block> key) {
        super(key);
    }
}
