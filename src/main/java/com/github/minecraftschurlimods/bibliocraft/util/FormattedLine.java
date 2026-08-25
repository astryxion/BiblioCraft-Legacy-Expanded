package com.github.minecraftschurlimods.bibliocraft.util;

import net.minecraft.util.text.StringTextComponent;
import com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;

public final class FormattedLine  {
    private final String text;
    private final Style style;
    private final int size;
    private final Mode mode;
    private final Alignment alignment;

    public FormattedLine(String text, Style style, int size, Mode mode, Alignment alignment) {
        this.text = text;
        this.style = style;
        this.size = size;
        this.mode = mode;
        this.alignment = alignment;
    }

    public String text() { return this.text; }
    public Style style() { return this.style; }
    public int size() { return this.size; }
    public Mode mode() { return this.mode; }
    public Alignment alignment() { return this.alignment; }

    public static final int MIN_SIZE = 5;
    public static final int MAX_SIZE = 35;
    private static final Codec<Style> STYLE_CODEC = Codec.STRING.xmap(
            s -> ITextComponent.Serializer.fromJson(s).getStyle(),
            style -> ITextComponent.Serializer.toJson(new StringTextComponent("").withStyle(style)));
    public static final Codec<FormattedLine> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.fieldOf("text").forGetter(FormattedLine::text),
            STYLE_CODEC.fieldOf("style").forGetter(FormattedLine::style),
            com.mojang.serialization.Codec.INT.flatXmap(v -> v >= MIN_SIZE && v <= MAX_SIZE ? com.mojang.serialization.DataResult.success(v) : com.mojang.serialization.DataResult.error("Value " + v + " outside of range [MIN_SIZE;MAX_SIZE]"), com.mojang.serialization.DataResult::success).fieldOf("size").forGetter(FormattedLine::size),
            Mode.CODEC.fieldOf("mode").forGetter(FormattedLine::mode),
            Alignment.CODEC.fieldOf("alignment").forGetter(FormattedLine::alignment)
    ).apply(inst, FormattedLine::new));

    public static final FormattedLine DEFAULT = new FormattedLine("", Style.EMPTY, 10, Mode.NORMAL, Alignment.LEFT);

    public FormattedLine withText(String text) {
        return new FormattedLine(text, style, size, mode, alignment);
    }

    public FormattedLine withStyle(Style style) {
        return new FormattedLine(text, style, size, mode, alignment);
    }

    public FormattedLine withSize(int size) {
        return new FormattedLine(text, style, size, mode, alignment);
    }

    public FormattedLine withMode(Mode mode) {
        return new FormattedLine(text, style, size, mode, alignment);
    }

    public FormattedLine withAlignment(Alignment alignment) {
        return new FormattedLine(text, style, size, mode, alignment);
    }

    public enum Mode implements StringRepresentableEnum {
        NORMAL, SHADOW, GLOWING;
        public static final Codec<Mode> CODEC = CodecUtil.enumCodec(Mode::values);

        public String getTranslationKey() {
            return "gui." + BibliocraftApi.MOD_ID + ".formatted_line.mode." + getSerializedName();
        }
    }

    public enum Alignment implements StringRepresentableEnum {
        LEFT, CENTER, RIGHT;
        public static final Codec<Alignment> CODEC = CodecUtil.enumCodec(Alignment::values);

        public String getTranslationKey() {
            return "gui." + BibliocraftApi.MOD_ID + ".formatted_line.alignment." + getSerializedName();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FormattedLine other = (FormattedLine) o;
        return java.util.Objects.equals(this.text, other.text) && java.util.Objects.equals(this.style, other.style) && this.size == other.size && java.util.Objects.equals(this.mode, other.mode) && java.util.Objects.equals(this.alignment, other.alignment);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.text, this.style, this.size, this.mode, this.alignment);
    }

    @Override
    public String toString() {
        return "FormattedLine[" + "text=" + this.text + ", " + "style=" + this.style + ", " + "size=" + this.size + ", " + "mode=" + this.mode + ", " + "alignment=" + this.alignment + "]";
    }

}
