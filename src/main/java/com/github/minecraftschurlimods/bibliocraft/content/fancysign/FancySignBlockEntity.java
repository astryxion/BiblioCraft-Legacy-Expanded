package com.github.minecraftschurlimods.bibliocraft.content.fancysign;

import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.util.CodecUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockState;

public class FancySignBlockEntity extends TileEntity {
    private static final String FRONT_CONTENT_KEY = "front_content";
    private static final String BACK_CONTENT_KEY = "back_content";
    private FancySignContent frontContent = FancySignContent.withSize(16);
    private FancySignContent backContent = FancySignContent.withSize(16);

    public FancySignBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntities.FANCY_SIGN.get());
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        if (tag.contains(FRONT_CONTENT_KEY)) {
            setFrontContent(CodecUtil.decodeNbt(FancySignContent.CODEC, tag.get(FRONT_CONTENT_KEY)));
        }
        if (tag.contains(BACK_CONTENT_KEY)) {
            setBackContent(CodecUtil.decodeNbt(FancySignContent.CODEC, tag.get(BACK_CONTENT_KEY)));
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        super.save(tag);
        tag.put(FRONT_CONTENT_KEY, CodecUtil.encodeNbt(FancySignContent.CODEC, getFrontContent()));
        tag.put(BACK_CONTENT_KEY, CodecUtil.encodeNbt(FancySignContent.CODEC, getBackContent()));
            return tag;
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = super.getUpdateTag();
        if (!getFrontContent().lines().isEmpty()) {
            tag.put(FRONT_CONTENT_KEY, CodecUtil.encodeNbt(FancySignContent.CODEC, getFrontContent()));
        }
        if (!getBackContent().lines().isEmpty()) {
            tag.put(BACK_CONTENT_KEY, CodecUtil.encodeNbt(FancySignContent.CODEC, getBackContent()));
        }
        return tag;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.handleUpdateTag(state, tag);
        if (tag.contains(FRONT_CONTENT_KEY)) {
            setFrontContent(CodecUtil.decodeNbt(FancySignContent.CODEC, tag.get(FRONT_CONTENT_KEY)));
        }
        if (tag.contains(BACK_CONTENT_KEY)) {
            setBackContent(CodecUtil.decodeNbt(FancySignContent.CODEC, tag.get(BACK_CONTENT_KEY)));
        }
    }

    public FancySignContent getFrontContent() {
        return frontContent;
    }

    public void setFrontContent(FancySignContent frontContent) {
        this.frontContent = frontContent;
        setChanged();
    }

    public FancySignContent getBackContent() {
        return backContent;
    }

    public void setBackContent(FancySignContent backContent) {
        this.backContent = backContent;
        setChanged();
    }
}
