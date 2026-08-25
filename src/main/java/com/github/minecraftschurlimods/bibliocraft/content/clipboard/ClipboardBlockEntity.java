package com.github.minecraftschurlimods.bibliocraft.content.clipboard;

import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.util.CodecUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockState;

@SuppressWarnings("deprecation")
public class ClipboardBlockEntity extends TileEntity {
    private static final String CONTENT_KEY = "clipboard_content";
    private ClipboardContent content = ClipboardContent.DEFAULT;

    public ClipboardBlockEntity(BlockPos pos, BlockState blockState) {
        super(BCBlockEntities.CLIPBOARD.get());
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        if (tag.contains(CONTENT_KEY)) {
            setContent(CodecUtil.decodeNbt(ClipboardContent.CODEC, tag.get(CONTENT_KEY)));
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        super.save(tag);
        tag.put(CONTENT_KEY, CodecUtil.encodeNbt(ClipboardContent.CODEC, getContent()));
            return tag;
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = super.getUpdateTag();
        if (!content.equals(ClipboardContent.DEFAULT)) {
            tag.put(CONTENT_KEY, CodecUtil.encodeNbt(ClipboardContent.CODEC, getContent()));
        }
        return tag;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.handleUpdateTag(state, tag);
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
