package com.github.minecraftschurlimods.bibliocraft.content.clock;

import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;

public final class ClockTrigger implements Comparable<ClockTrigger> {
    private final int hour;
    private final int minute;
    private final boolean redstone;
    private final boolean sound;

    public ClockTrigger(int hour, int minute, boolean redstone, boolean sound) {
        this.hour = hour;
        this.minute = minute;
        this.redstone = redstone;
        this.sound = sound;
    }

    public int hour() { return this.hour; }
    public int minute() { return this.minute; }
    public boolean redstone() { return this.redstone; }
    public boolean sound() { return this.sound; }

    public static final Codec<ClockTrigger> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("hour").forGetter(ClockTrigger::hour),
            Codec.INT.fieldOf("minute").forGetter(ClockTrigger::minute),
            Codec.BOOL.fieldOf("redstone").forGetter(ClockTrigger::redstone),
            Codec.BOOL.fieldOf("sound").forGetter(ClockTrigger::sound)
    ).apply(inst, ClockTrigger::new));

    public void write(PacketBuffer buf) {
        buf.writeInt(hour);
        buf.writeInt(minute);
        buf.writeBoolean(redstone);
        buf.writeBoolean(sound);
    }

    public static ClockTrigger read(PacketBuffer buf) {
        return new ClockTrigger(buf.readInt(), buf.readInt(), buf.readBoolean(), buf.readBoolean());
    }

    public int getInGameTime(World level) {
        // 1 in-game hour is 1/24 day, 1 in-game minute is 1/24/60=1/1440 day, time starts at 6 AM so we offset by 18 hours.
        int day = BCUtil.getDayDuration(level);
        return (int) (hour * day / 24. + minute * day / 1440. + day * 0.75) % day;
    }

    @Override
    public int compareTo(ClockTrigger that) {
        int hour = Integer.compare(this.hour, that.hour);
        return hour != 0 ? hour : Integer.compare(this.minute, that.minute);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClockTrigger other = (ClockTrigger) o;
        return this.hour == other.hour && this.minute == other.minute && this.redstone == other.redstone && this.sound == other.sound;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(this.hour, this.minute, this.redstone, this.sound);
    }

    @Override
    public String toString() {
        return "ClockTrigger[" + "hour=" + this.hour + ", " + "minute=" + this.minute + ", " + "redstone=" + this.redstone + ", " + "sound=" + this.sound + "]";
    }

}
