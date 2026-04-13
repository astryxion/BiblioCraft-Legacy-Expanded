package com.github.minecraftschurlimods.bibliocraft.content.clipboard;

import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.CodecUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record ClipboardContent(String title, int active, List<Page> pages) {
    public static final String NBT_KEY = "ClipboardContent";
    public static final int MAX_PAGES = 50;
    public static final int MAX_LINES = 9;
    public static final ClipboardContent DEFAULT = new ClipboardContent("", 0, List.of(Page.DEFAULT));
    public static final Codec<ClipboardContent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.fieldOf("title").forGetter(ClipboardContent::title),
            Codec.INT.fieldOf("active").forGetter(ClipboardContent::active),
            Page.CODEC.listOf().fieldOf("pages").forGetter(ClipboardContent::pages)
    ).apply(inst, ClipboardContent::new));

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(title);
        buf.writeInt(active);
        buf.writeCollection(pages, (b, p) -> Page.write(b, p));
    }

    public static ClipboardContent read(FriendlyByteBuf buf) {
        return new ClipboardContent(buf.readUtf(), buf.readInt(), buf.readList(Page::read));
    }

    public static ClipboardContent getFromStack(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains(NBT_KEY)) return CodecUtil.decodeNbt(CODEC, tag.get(NBT_KEY));
        // 1.20.1: items from block break may have content in BlockEntityTag (legacy or alternate loot)
        CompoundTag beTag = tag.getCompound("BlockEntityTag");
        if (beTag.contains("clipboard_content")) return CodecUtil.decodeNbt(CODEC, beTag.get("clipboard_content"));
        return DEFAULT;
    }

    public static void setOnStack(ItemStack stack, ClipboardContent content) {
        stack.getOrCreateTag().put(NBT_KEY, CodecUtil.encodeNbt(CODEC, content));
    }

    public ClipboardContent setTitle(String title) {
        return new ClipboardContent(title, active, new ArrayList<>(pages));
    }

    public ClipboardContent setActive(int active) {
        return new ClipboardContent(title, active, new ArrayList<>(pages));
    }

    public boolean canHaveNewPage() {
        return active < pages.size() - 1 || pages.size() - 1 < MAX_PAGES;
    }

    public ClipboardContent nextPage() {
        if (active >= pages.size() - 1 && canHaveNewPage()) {
            List<Page> list = new ArrayList<>(pages);
            list.add(Page.DEFAULT);
            return setActive(list.size() - 1).setPages(list);
        } else return active >= pages.size() - 1 ? this : setActive(active + 1);
    }

    public ClipboardContent prevPage() {
        return active == 0 ? this : setActive(active - 1);
    }

    public ClipboardContent setPages(List<Page> pages) {
        return new ClipboardContent(title, active, new ArrayList<>(pages));
    }

    public record Page(List<CheckboxState> checkboxes, List<String> lines) {
        public Page(List<CheckboxState> checkboxes, List<String> lines) {
            this.checkboxes = BCUtil.extend(checkboxes, MAX_LINES, CheckboxState.EMPTY);
            this.lines = BCUtil.extend(lines, MAX_LINES, "");
        }

        public static final Page DEFAULT = new Page(new ArrayList<>(MAX_LINES), new ArrayList<>(MAX_LINES));
        public static final Codec<Page> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                CheckboxState.CODEC.listOf().fieldOf("checkboxes").forGetter(Page::checkboxes),
                Codec.STRING.listOf().fieldOf("lines").forGetter(Page::lines)
        ).apply(inst, Page::new));

        public static void write(FriendlyByteBuf buf, Page page) {
            buf.writeCollection(page.checkboxes, (b, c) -> b.writeByte(c.ordinal()));
            buf.writeCollection(page.lines, FriendlyByteBuf::writeUtf);
        }

        public static Page read(FriendlyByteBuf buf) {
            List<CheckboxState> checkboxes = buf.readList(b -> CheckboxState.values()[b.readByte()]);
            List<String> lines = buf.readList(FriendlyByteBuf::readUtf);
            return new Page(checkboxes, lines);
        }

        public Page setCheckboxes(List<CheckboxState> checkboxes) {
            return new Page(checkboxes, new ArrayList<>(lines));
        }

        public Page setLines(List<String> lines) {
            return new Page(checkboxes, new ArrayList<>(lines));
        }
    }
}
