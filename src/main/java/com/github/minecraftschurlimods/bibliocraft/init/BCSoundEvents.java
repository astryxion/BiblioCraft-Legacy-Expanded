package com.github.minecraftschurlimods.bibliocraft.init;

import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;

public interface BCSoundEvents {
    // @formatter:off
    RegistryObject<SoundEvent> CLOCK_CHIME          = BCRegistries.SOUND_EVENTS.register("clock_chime",          () -> new SoundEvent(com.github.minecraftschurlimods.bibliocraft.util.BCUtil.bcLoc("clock_chime")));
    RegistryObject<SoundEvent> CLOCK_TICK           = BCRegistries.SOUND_EVENTS.register("clock_tick",           () -> new SoundEvent(com.github.minecraftschurlimods.bibliocraft.util.BCUtil.bcLoc("clock_tick")));
    RegistryObject<SoundEvent> CLOCK_TOCK           = BCRegistries.SOUND_EVENTS.register("clock_tock",           () -> new SoundEvent(com.github.minecraftschurlimods.bibliocraft.util.BCUtil.bcLoc("clock_tock")));
    RegistryObject<SoundEvent> DESK_BELL            = BCRegistries.SOUND_EVENTS.register("desk_bell",            () -> new SoundEvent(com.github.minecraftschurlimods.bibliocraft.util.BCUtil.bcLoc("desk_bell")));
    RegistryObject<SoundEvent> DISPLAY_CASE_CLOSE   = BCRegistries.SOUND_EVENTS.register("display_case_close",   () -> new SoundEvent(com.github.minecraftschurlimods.bibliocraft.util.BCUtil.bcLoc("display_case_close")));
    RegistryObject<SoundEvent> DISPLAY_CASE_OPEN    = BCRegistries.SOUND_EVENTS.register("display_case_open",    () -> new SoundEvent(com.github.minecraftschurlimods.bibliocraft.util.BCUtil.bcLoc("display_case_open")));
    RegistryObject<SoundEvent> TAPE_MEASURE_CLOSE   = BCRegistries.SOUND_EVENTS.register("tape_measure_close",   () -> new SoundEvent(com.github.minecraftschurlimods.bibliocraft.util.BCUtil.bcLoc("tape_measure_close")));
    RegistryObject<SoundEvent> TAPE_MEASURE_OPEN   = BCRegistries.SOUND_EVENTS.register("tape_measure_open",    () -> new SoundEvent(com.github.minecraftschurlimods.bibliocraft.util.BCUtil.bcLoc("tape_measure_open")));
    RegistryObject<SoundEvent> TYPEWRITER_ADD_PAPER = BCRegistries.SOUND_EVENTS.register("typewriter_add_paper", () -> new SoundEvent(com.github.minecraftschurlimods.bibliocraft.util.BCUtil.bcLoc("typewriter_add_paper")));
    RegistryObject<SoundEvent> TYPEWRITER_CHIME     = BCRegistries.SOUND_EVENTS.register("typewriter_chime",     () -> new SoundEvent(com.github.minecraftschurlimods.bibliocraft.util.BCUtil.bcLoc("typewriter_chime")));
    RegistryObject<SoundEvent> TYPEWRITER_TAKE_PAGE = BCRegistries.SOUND_EVENTS.register("typewriter_take_page", () -> new SoundEvent(com.github.minecraftschurlimods.bibliocraft.util.BCUtil.bcLoc("typewriter_take_page")));
    RegistryObject<SoundEvent> TYPEWRITER_TYPE      = BCRegistries.SOUND_EVENTS.register("typewriter_type",      () -> new SoundEvent(com.github.minecraftschurlimods.bibliocraft.util.BCUtil.bcLoc("typewriter_type")));
    RegistryObject<SoundEvent> TYPEWRITER_TYPING    = BCRegistries.SOUND_EVENTS.register("typewriter_typing",    () -> new SoundEvent(com.github.minecraftschurlimods.bibliocraft.util.BCUtil.bcLoc("typewriter_typing")));
    // @formatter:on

    /** Empty method, called by {@link BCRegistries#init(net.minecraftforge.eventbus.api.IEventBus)} to classload this class. */
    static void init() {
    }
}
