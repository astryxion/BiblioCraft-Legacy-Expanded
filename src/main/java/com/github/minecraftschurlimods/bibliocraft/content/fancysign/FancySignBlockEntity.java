package com.github.minecraftschurlimods.bibliocraft.content.fancysign;

import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.util.CodecUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FancySignBlockEntity extends BlockEntity {
    private static final String FRONT_CONTENT_KEY = "front_content";
    private static final String BACK_CONTENT_KEY = "back_content";
    private FancySignContent frontContent = FancySignContent.withSize(16);
    private FancySignContent backContent = FancySignContent.withSize(16);

    public FancySignBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntities.FANCY_SIGN.get(), pos, state);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(FRONT_CONTENT_KEY)) {
            setFrontContent(CodecUtil.decodeNbt(FancySignContent.CODEC, tag.get(FRONT_CONTENT_KEY)));
        }
        if (tag.contains(BACK_CONTENT_KEY)) {
            setBackContent(CodecUtil.decodeNbt(FancySignContent.CODEC, tag.get(BACK_CONTENT_KEY)));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(FRONT_CONTENT_KEY, CodecUtil.encodeNbt(FancySignContent.CODEC, getFrontContent()));
        tag.put(BACK_CONTENT_KEY, CodecUtil.encodeNbt(FancySignContent.CODEC, getBackContent()));
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        if (!getFrontContent().lines().isEmpty()) {
            tag.put(FRONT_CONTENT_KEY, CodecUtil.encodeNbt(FancySignContent.CODEC, getFrontContent()));
        }
        if (!getBackContent().lines().isEmpty()) {
            tag.put(BACK_CONTENT_KEY, CodecUtil.encodeNbt(FancySignContent.CODEC, getBackContent()));
        }
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
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
