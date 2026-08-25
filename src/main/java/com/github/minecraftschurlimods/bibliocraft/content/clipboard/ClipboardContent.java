package com.github.minecraftschurlimods.bibliocraft.content.clipboard;

import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.CodecUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class ClipboardContent  {
    private final String title;
    private final int active;
    private final List<Page> pages;

    public ClipboardContent(String title, int active, List<Page> pages) {
        this.title = title;
        this.active = active;
        this.pages = pages;
    }

    public String title() { return this.title; }
    public int active() { return this.active; }
    public List<Page> pages() { return this.pages; }

    public static final String NBT_KEY = "ClipboardContent";
    public static final int MAX_PAGES = 50;
    public static final int MAX_LINES = 9;
    public static final ClipboardContent DEFAULT = new ClipboardContent("", 0, java.util.Collections.singletonList(Page.DEFAULT));
    public static final Codec<ClipboardContent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.fieldOf("title").forGetter(ClipboardContent::title),
            Codec.INT.fieldOf("active").forGetter(ClipboardContent::active),
            Page.CODEC.listOf().fieldOf("pages").forGetter(ClipboardContent::pages)
    ).apply(inst, ClipboardContent::new));

    public void write(PacketBuffer buf) {
        buf.writeUtf(title);
        buf.writeInt(active);
        buf.writeVarInt(pages.size());
        for (Page _e : pages) { Page.write(buf, _e); }
    }

    public static ClipboardContent read(PacketBuffer buf) {
        String title = buf.readUtf();
        int active = buf.readInt();
        int n = buf.readVarInt();
        List<Page> pages = new ArrayList<Page>(n);
        for (int i = 0; i < n; i++) {
            pages.add(Page.read(buf));
        }
        return new ClipboardContent(title, active, pages);
    }

    public static ClipboardContent getFromStack(ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTag();
        if (tag.contains(NBT_KEY)) return CodecUtil.decodeNbt(CODEC, tag.get(NBT_KEY));
        // 1.20.1: items from block break may have content in BlockEntityTag (legacy or alternate loot)
        CompoundNBT beTag = tag.getCompound("BlockEntityTag");
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

    public static final class Page  {
    private final List<CheckboxState> checkboxes;
    private final List<String> lines;

    public List<CheckboxState> checkboxes() { return this.checkboxes; }
    public List<String> lines() { return this.lines; }

        public Page(List<CheckboxState> checkboxes, List<String> lines) {
            this.checkboxes = BCUtil.extend(checkboxes, MAX_LINES, CheckboxState.EMPTY);
            this.lines = BCUtil.extend(lines, MAX_LINES, "");
        }

        public static final Page DEFAULT = new Page(new ArrayList<>(MAX_LINES), new ArrayList<>(MAX_LINES));
        public static final Codec<Page> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                CheckboxState.CODEC.listOf().fieldOf("checkboxes").forGetter(Page::checkboxes),
                Codec.STRING.listOf().fieldOf("lines").forGetter(Page::lines)
        ).apply(inst, Page::new));

        public static void write(PacketBuffer buf, Page page) {
            buf.writeVarInt(page.checkboxes.size());
            for (CheckboxState _e : page.checkboxes) { buf.writeByte(_e.ordinal()); }
            buf.writeVarInt(page.lines.size());
            for (String _e : page.lines) { buf.writeUtf(_e); }
        }

        public static Page read(PacketBuffer buf) {
            int _nCb = buf.readVarInt();
            List<CheckboxState> checkboxes = new java.util.ArrayList<CheckboxState>(_nCb);
            for (int _i = 0; _i < _nCb; _i++) { checkboxes.add(CheckboxState.values()[buf.readByte()]); }
            int _nLines = buf.readVarInt();
        List<String> lines = new java.util.ArrayList<String>(_nLines);
        for (int _i = 0; _i < _nLines; _i++) { lines.add(buf.readUtf()); }
            return new Page(checkboxes, lines);
        }

        public Page setCheckboxes(List<CheckboxState> checkboxes) {
            return new Page(checkboxes, new ArrayList<>(lines));
        }

        public Page setLines(List<String> lines) {
            return new Page(checkboxes, new ArrayList<>(lines));
        }
    

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Page other = (Page) o;
        return java.util.Objects.equals(this.checkboxes, other.checkboxes) && java.util.Objects.equals(this.lines, other.lines);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.checkboxes, this.lines);
    }

    @Override
    public String toString() {
        return "Page[" + "checkboxes=" + this.checkboxes + ", " + "lines=" + this.lines + "]";
    }

}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClipboardContent other = (ClipboardContent) o;
        return java.util.Objects.equals(this.title, other.title) && this.active == other.active && java.util.Objects.equals(this.pages, other.pages);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.title, this.active, this.pages);
    }

    @Override
    public String toString() {
        return "ClipboardContent[" + "title=" + this.title + ", " + "active=" + this.active + ", " + "pages=" + this.pages + "]";
    }

}
