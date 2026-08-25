package com.github.minecraftschurlimods.bibliocraft.content.fancylight;

import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.StringRepresentableEnum;
import com.github.minecraftschurlimods.bibliocraft.util.block.BCFacingBlock;
import net.minecraft.util.Direction;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;

public abstract class AbstractFancyLightBlock extends BCFacingBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);

    public AbstractFancyLightBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(LIT, true).setValue(TYPE, Type.STANDING).setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LIT, TYPE);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction direction = context.getClickedFace();
        return BCUtil.nonNull(super.getStateForPlacement(context)).setValue(TYPE, direction == Direction.UP ? Type.STANDING : direction == Direction.DOWN ? Type.HANGING : Type.WALL);
    }

    public enum Type implements StringRepresentableEnum {
        STANDING, HANGING, WALL
    }
}
