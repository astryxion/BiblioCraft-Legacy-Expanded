package com.github.minecraftschurlimods.bibliocraft.init;

import com.github.minecraftschurlimods.bibliocraft.fabric.FabricRegistrar;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import net.minecraft.sounds.SoundEvent;

public interface BCSoundEvents {
    // @formatter:off
    FabricRegistrar.RegistryRef<SoundEvent> CLOCK_CHIME          = BCRegistries.SOUND_EVENTS.register("clock_chime",          () -> SoundEvent.createVariableRangeEvent(BCUtil.bcLoc("clock_chime")));
    FabricRegistrar.RegistryRef<SoundEvent> CLOCK_TICK           = BCRegistries.SOUND_EVENTS.register("clock_tick",           () -> SoundEvent.createVariableRangeEvent(BCUtil.bcLoc("clock_tick")));
    FabricRegistrar.RegistryRef<SoundEvent> CLOCK_TOCK           = BCRegistries.SOUND_EVENTS.register("clock_tock",           () -> SoundEvent.createVariableRangeEvent(BCUtil.bcLoc("clock_tock")));
    FabricRegistrar.RegistryRef<SoundEvent> DESK_BELL            = BCRegistries.SOUND_EVENTS.register("desk_bell",            () -> SoundEvent.createVariableRangeEvent(BCUtil.bcLoc("desk_bell")));
    FabricRegistrar.RegistryRef<SoundEvent> DISPLAY_CASE_CLOSE   = BCRegistries.SOUND_EVENTS.register("display_case_close",   () -> SoundEvent.createVariableRangeEvent(BCUtil.bcLoc("display_case_close")));
    FabricRegistrar.RegistryRef<SoundEvent> DISPLAY_CASE_OPEN    = BCRegistries.SOUND_EVENTS.register("display_case_open",    () -> SoundEvent.createVariableRangeEvent(BCUtil.bcLoc("display_case_open")));
    FabricRegistrar.RegistryRef<SoundEvent> TAPE_MEASURE_CLOSE   = BCRegistries.SOUND_EVENTS.register("tape_measure_close",   () -> SoundEvent.createVariableRangeEvent(BCUtil.bcLoc("tape_measure_close")));
    FabricRegistrar.RegistryRef<SoundEvent> TAPE_MEASURE_OPEN    = BCRegistries.SOUND_EVENTS.register("tape_measure_open",    () -> SoundEvent.createVariableRangeEvent(BCUtil.bcLoc("tape_measure_open")));
    FabricRegistrar.RegistryRef<SoundEvent> TYPEWRITER_ADD_PAPER = BCRegistries.SOUND_EVENTS.register("typewriter_add_paper", () -> SoundEvent.createVariableRangeEvent(BCUtil.bcLoc("typewriter_add_paper")));
    FabricRegistrar.RegistryRef<SoundEvent> TYPEWRITER_CHIME     = BCRegistries.SOUND_EVENTS.register("typewriter_chime",     () -> SoundEvent.createVariableRangeEvent(BCUtil.bcLoc("typewriter_chime")));
    FabricRegistrar.RegistryRef<SoundEvent> TYPEWRITER_TAKE_PAGE = BCRegistries.SOUND_EVENTS.register("typewriter_take_page", () -> SoundEvent.createVariableRangeEvent(BCUtil.bcLoc("typewriter_take_page")));
    FabricRegistrar.RegistryRef<SoundEvent> TYPEWRITER_TYPE      = BCRegistries.SOUND_EVENTS.register("typewriter_type",      () -> SoundEvent.createVariableRangeEvent(BCUtil.bcLoc("typewriter_type")));
    FabricRegistrar.RegistryRef<SoundEvent> TYPEWRITER_TYPING    = BCRegistries.SOUND_EVENTS.register("typewriter_typing",    () -> SoundEvent.createVariableRangeEvent(BCUtil.bcLoc("typewriter_typing")));
    // @formatter:on

    /**
     * Empty method, called by {@link BCRegistries#init()} to classload this class.
     */
    static void init() {
    }
}
