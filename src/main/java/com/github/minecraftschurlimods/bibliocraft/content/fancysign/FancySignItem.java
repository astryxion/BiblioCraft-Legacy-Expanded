package com.github.minecraftschurlimods.bibliocraft.content.fancysign;

import com.github.minecraftschurlimods.bibliocraft.api.woodtype.BibliocraftWoodType;
import com.github.minecraftschurlimods.bibliocraft.init.BCBlocks;
import com.github.minecraftschurlimods.bibliocraft.util.block.WoodTypeBlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.block.BlockState;
import javax.annotation.Nullable;

public class FancySignItem extends WoodTypeBlockItem {
    public FancySignItem(BibliocraftWoodType woodType) {
        super(BCBlocks.FANCY_SIGN, woodType);
    }

    @Override
    @Nullable
    protected BlockState getPlacementState(BlockItemUseContext context) {
        BlockState state;
        switch (context.getClickedFace()) {
            case UP:
                state = BCBlocks.FANCY_SIGN.get(woodType).defaultBlockState();
                break;
            case DOWN:
                state = BCBlocks.FANCY_SIGN.get(woodType).defaultBlockState().setValue(FancySignBlock.HANGING, true);
                break;
            default:
                state = BCBlocks.WALL_FANCY_SIGN.get(woodType).defaultBlockState();
                break;
        }
        state = state.setValue(FancySignBlock.FACING, context.getHorizontalDirection().getOpposite());
        return canPlace(context, state) ? state : null;
    }
}
