package com.github.minecraftschurlimods.bibliocraft.content.clock;

import com.github.minecraftschurlimods.bibliocraft.init.BCBlockEntities;
import com.github.minecraftschurlimods.bibliocraft.init.BCSoundEvents;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import com.github.minecraftschurlimods.bibliocraft.util.CodecUtil;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockState;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ClockBlockEntity extends TileEntity implements ITickableTileEntity {
    private static final String TICK_SOUND_KEY = "tick";
    private static final String TRIGGERS_KEY = "triggers";
    private final List<ClockTrigger> triggers = new ArrayList<>();
    private final Multimap<Integer, ClockTrigger> triggersMap = HashMultimap.create();
    private int redstoneTick = 0;
    private boolean tickSound = true;

    public ClockBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntities.CLOCK.get());
    }

    @Override
    public void tick() {
        if (level != null) {
            tick(level, getBlockPos(), getBlockState(), this);
        }
    }

    public static void tick(World level, BlockPos pos, BlockState state, ClockBlockEntity blockEntity) {
        blockEntity.ensureTriggersMapPopulated();
        if (state.getValue(AbstractClockBlock.POWERED)) {
            blockEntity.redstoneTick--;
            if (blockEntity.redstoneTick <= 0) {
                setPowered(level, pos, false);
            }
        }
        int time = (int) (level.getDayTime() % BCUtil.getDayDuration(level));
        if (blockEntity.triggersMap.containsKey(time)) {
            Collection<ClockTrigger> trigger = blockEntity.triggersMap.get(time);
            if (trigger.stream().anyMatch(ClockTrigger::sound)) {
                level.playSound(null, pos, BCSoundEvents.CLOCK_CHIME.get(), SoundCategory.BLOCKS, 1, 1);
            }
            if (trigger.stream().anyMatch(ClockTrigger::redstone)) {
                blockEntity.redstoneTick = 2;
                setPowered(level, pos, true);
            }
        }
        if (blockEntity.getTickSound() && level.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT) && time % 20 == 0) {
            level.playSound(null, pos, time % 40 == 0 ? BCSoundEvents.CLOCK_TICK.get() : BCSoundEvents.CLOCK_TOCK.get(), SoundCategory.BLOCKS, 1, 1);
        }
    }

    private static void setPowered(World level, BlockPos pos, boolean powered) {
        BlockState state = level.getBlockState(pos);
        if (!state.hasProperty(AbstractClockBlock.POWERED)) return;
        level.setBlock(pos, state.setValue(AbstractClockBlock.POWERED, powered), 3);
        pos = pos.below();
        state = level.getBlockState(pos);
        if (state.getBlock() instanceof GrandfatherClockBlock) {
            level.setBlock(pos, state.setValue(AbstractClockBlock.POWERED, powered), 3);
        }
    }

    public List<ClockTrigger> getTriggers() {
        return Collections.unmodifiableList(triggers);
    }

    public void setFromPacket(ClockSyncPacket packet) {
        tickSound = packet.tickSound();
        addTriggers(packet.triggers());
        if (level instanceof ServerWorld) {
            ServerWorld serverLevel = (ServerWorld) level;
            com.github.minecraftschurlimods.bibliocraft.BCEventHandler.getChannel().send(PacketDistributor.TRACKING_CHUNK.with(() -> serverLevel.getChunkAt(getBlockPos())), packet);
        }
    }

    private void addTriggers(Collection<ClockTrigger> triggers) {
        this.triggers.clear();
        this.triggersMap.clear();
        for (ClockTrigger trigger : triggers) {
            this.triggers.add(trigger);
            this.triggersMap.put(trigger.getInGameTime(getLevel()), trigger);
        }
        this.triggers.sort(ClockTrigger::compareTo);
        setChanged();
    }

    /**
     * Repopulates triggersMap from triggers when level is available.
     * Needed because load() can run before the block entity has a level, so we defer map build until first tick.
     */
    private void ensureTriggersMapPopulated() {
        World l = getLevel();
        if (l == null || triggers.isEmpty() || !triggersMap.isEmpty()) return;
        triggersMap.clear();
        for (ClockTrigger trigger : triggers) {
            triggersMap.put(trigger.getInGameTime(l), trigger);
        }
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        tickSound = tag.getBoolean(TICK_SOUND_KEY);
        List<ClockTrigger> list = new ArrayList<>();
        for (INBT trigger : tag.getList(TRIGGERS_KEY, net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND)) {
            list.add(CodecUtil.decodeNbt(ClockTrigger.CODEC, trigger));
        }
        this.triggers.clear();
        this.triggersMap.clear();
        this.triggers.addAll(list);
        this.triggers.sort(ClockTrigger::compareTo);
        World l = getLevel();
        if (l != null) {
            for (ClockTrigger t : this.triggers) {
                this.triggersMap.put(t.getInGameTime(l), t);
            }
        }
        setChanged();
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        super.save(tag);
        tag.putBoolean(TICK_SOUND_KEY, tickSound);
        ListNBT list = new ListNBT();
        for (ClockTrigger trigger : triggers) {
            list.add(CodecUtil.encodeNbt(ClockTrigger.CODEC, trigger));
        }
        tag.put(TRIGGERS_KEY, list);
            return tag;
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = super.getUpdateTag();
        save(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.handleUpdateTag(state, tag);
        load(getBlockState(), tag);
    }

    public boolean getTickSound() {
        return tickSound;
    }
}
