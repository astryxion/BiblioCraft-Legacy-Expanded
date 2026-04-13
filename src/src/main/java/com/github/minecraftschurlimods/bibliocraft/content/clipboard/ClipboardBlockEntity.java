package com.github.minecraftschurlimods.bibliocraft.content.clipboard;

import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.util.CodecUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("deprecation")
public class ClipboardBlockEntity extends BlockEntity {
    private static final String CONTENT_KEY = "clipboard_content";
    private ClipboardContent content = ClipboardContent.DEFAULT;

    public ClipboardBlockEntity(BlockPos pos, BlockState blockState) {
        super(BCBlockEntities.CLIPBOARD.get(), pos, blockState);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(CONTENT_KEY)) {
            setContent(CodecUtil.decodeNbt(ClipboardContent.CODEC, tag.get(CONTENT_KEY)));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put(CONTENT_KEY, CodecUtil.encodeNbt(ClipboardContent.CODEC, getContent()));
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        if (!content.equals(ClipboardContent.DEFAULT)) {
            tag.put(CONTENT_KEY, CodecUtil.encodeNbt(ClipboardContent.CODEC, getContent()));
        }
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        if (tag.contains(CONTENT_KEY)) {
            setContent(CodecUtil.decodeNbt(ClipboardContent.CODEC, tag.get(CONTENT_KEY)));
        }
    }

    public ClipboardContent getContent() {
        return content;
    }

    public void setContent(ClipboardContent content) {
        this.content = content;
        setChanged();
    }
}
