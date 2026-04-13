package com.github.minecraftschurlimods.bibliocraft.init;

import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;

public interface BCSoundEvents {
    // @formatter:off
    RegistryObject<SoundEvent> CLOCK_CHIME          = BCRegistries.SOUND_EVENTS.register("clock_chime",          () -> SoundEvent.createVariableRangeEvent(com.github.minecraftschurlimods.bibliocraft.util.BCUtil.bcLoc("clock_chime")));
    RegistryObject<SoundEvent> CLOCK_TICK           = BCRegistries.SOUND_EVENTS.register("clock_tick",           () -> SoundEvent.createVariableRangeEvent(com.github.minecraftschurlimods.bibliocraft.util.BCUtil.bcLoc("clock_tick")));
    RegistryObject<SoundEvent> CLOCK_TOCK           = BCRegistries.SOUND_EVENTS.register("clock_tock",           () -> SoundEvent.createVariableRangeEvent(com.github.minecraftschurlimods.bibliocraft.util.BCUtil.bcLoc("clock_tock")));
    RegistryObject<SoundEvent> DESK_BELL            = BCRegistries.SOUND_EVENTS.register("desk_bell",            () -> SoundEvent.createVariableRangeEvent(com.github.minecraftschurlimods.bibliocraft.util.BCUtil.bcLoc("desk_bell")));
    RegistryObject<SoundEvent> DISPLAY_CASE_CLOSE   = BCRegistries.SOUND_EVENTS.register("display_case_close",   () -> SoundEvent.createVariableRangeEvent(com.github.minecraftschurlimods.bibliocraft.util.BCUtil.bcLoc("display_case_close")));
    RegistryObject<SoundEvent> DISPLAY_CASE_OPEN    = BCRegistries.SOUND_EVENTS.register("display_case_open",    () -> SoundEvent.createVariableRangeEvent(com.github.minecraftschurlimods.bibliocraft.util.BCUtil.bcLoc("display_case_open")));
    RegistryObject<SoundEvent> TAPE_MEASURE_CLOSE   = BCRegistries.SOUND_EVENTS.register("tape_measure_close",   () -> SoundEvent.createVariableRangeEvent(com.github.minecraftschurlimods.bibliocraft.util.BCUtil.bcLoc("tape_measure_close")));
    RegistryObject<SoundEvent> TAPE_MEASURE_OPEN   = BCRegistries.SOUND_EVENTS.register("tape_measure_open",    () -> SoundEvent.createVariableRangeEvent(com.github.minecraftschurlimods.bibliocraft.util.BCUtil.bcLoc("tape_measure_open")));
    RegistryObject<SoundEvent> TYPEWRITER_ADD_PAPER = BCRegistries.SOUND_EVENTS.register("typewriter_add_paper", () -> SoundEvent.createVariableRangeEvent(com.github.minecraftschurlimods.bibliocraft.util.BCUtil.bcLoc("typewriter_add_paper")));
    RegistryObject<SoundEvent> TYPEWRITER_CHIME     = BCRegistries.SOUND_EVENTS.register("typewriter_chime",     () -> SoundEvent.createVariableRangeEvent(com.github.minecraftschurlimods.bibliocraft.util.BCUtil.bcLoc("typewriter_chime")));
    RegistryObject<SoundEvent> TYPEWRITER_TAKE_PAGE = BCRegistries.SOUND_EVENTS.register("typewriter_take_page", () -> SoundEvent.createVariableRangeEvent(com.github.minecraftschurlimods.bibliocraft.util.BCUtil.bcLoc("typewriter_take_page")));
    RegistryObject<SoundEvent> TYPEWRITER_TYPE      = BCRegistries.SOUND_EVENTS.register("typewriter_type",      () -> SoundEvent.createVariableRangeEvent(com.github.minecraftschurlimods.bibliocraft.util.BCUtil.bcLoc("typewriter_type")));
    RegistryObject<SoundEvent> TYPEWRITER_TYPING    = BCRegistries.SOUND_EVENTS.register("typewriter_typing",    () -> SoundEvent.createVariableRangeEvent(com.github.minecraftschurlimods.bibliocraft.util.BCUtil.bcLoc("typewriter_typing")));
    // @formatter:on

    /** Empty method, called by {@link BCRegistries#init(net.minecraftforge.eventbus.api.IEventBus)} to classload this class. */
    static void init() {
    }
}
