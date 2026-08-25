package com.github.minecraftschurlimods.bibliocraft.datagen.assets;

import com.github.minecraftschurlimods.bibliocraft.api.BibliocraftApi;
import com.github.minecraftschurlimods.bibliocraft.init.BCSoundEvents;
import com.github.minecraftschurlimods.bibliocraft.util.BCUtil;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinition;
import net.minecraftforge.common.data.SoundDefinitionsProvider;

import java.util.stream.IntStream;

public class BCSoundDefinitionsProvider extends SoundDefinitionsProvider {
    public BCSoundDefinitionsProvider(DataGenerator output, ExistingFileHelper helper) {
        super(output, BibliocraftApi.MOD_ID, helper);
    }

    @Override
    public void registerSounds() {
        add(BCSoundEvents.CLOCK_CHIME.get(), "clock/chime", "clock.chime");
        add(BCSoundEvents.CLOCK_TICK.get(), "clock/tick_", 5, "clock.tick");
        add(BCSoundEvents.CLOCK_TOCK.get(), "clock/tock_", 5, "clock.tock");
        add(BCSoundEvents.DESK_BELL.get(), "desk_bell/", 4, "desk_bell");
        add(BCSoundEvents.DISPLAY_CASE_CLOSE.get(), "display_case/close", "display_case.close");
        add(BCSoundEvents.DISPLAY_CASE_OPEN.get(), "display_case/open", "display_case.open");
        add(BCSoundEvents.TAPE_MEASURE_CLOSE.get(), "tape_measure/close", "tape_measure.close");
        add(BCSoundEvents.TAPE_MEASURE_OPEN.get(), "tape_measure/open", "tape_measure.open");
        add(BCSoundEvents.TYPEWRITER_ADD_PAPER.get(), "typewriter/add_paper", "typewriter.add_paper");
        add(BCSoundEvents.TYPEWRITER_CHIME.get(), "typewriter/chime", "typewriter.chime");
        add(BCSoundEvents.TYPEWRITER_TAKE_PAGE.get(), "typewriter/take_page", "typewriter.take_page");
        add(BCSoundEvents.TYPEWRITER_TYPE.get(), "typewriter/type", "typewriter.type");
        add(BCSoundEvents.TYPEWRITER_TYPING.get(), "typewriter/typing_", 5, "typewriter.typing");
    }

    private void add(SoundEvent sound, String path, String subtitle) {
        add(sound, SoundDefinition.definition()
                .with(sound(BCUtil.bcLoc(path)))
                .subtitle("subtitles." + BibliocraftApi.MOD_ID + "." + subtitle));
    }

    private void add(SoundEvent sound, String path, int fileCount, String subtitle) {
        add(sound, SoundDefinition.definition()
                .with(IntStream.range(0, fileCount).mapToObj(e -> sound(BCUtil.bcLoc(path + e))).toArray(SoundDefinition.Sound[]::new))
                .subtitle("subtitles." + BibliocraftApi.MOD_ID + "." + subtitle));
    }
}
