package com.github.minecraftschurlimods.bibliocraft.init;

import com.github.minecraftschurlimods.bibliocraft.content.fancyarmorstand.FancyArmorStandEntity;
import com.github.minecraftschurlimods.bibliocraft.content.seat.SeatEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.RegistryObject;

public interface BCEntities {
    // @formatter:off
    RegistryObject<EntityType<FancyArmorStandEntity>> FANCY_ARMOR_STAND = BCRegistries.ENTITIES.register("fancy_armor_stand", () -> EntityType.Builder.<FancyArmorStandEntity>of(FancyArmorStandEntity::new, MobCategory.MISC).noSummon().build("fancy_armor_stand"));
    RegistryObject<EntityType<SeatEntity>>            SEAT              = BCRegistries.ENTITIES.register("seat",              () -> EntityType.Builder.<SeatEntity>of(SeatEntity::new, MobCategory.MISC).sized(0, 0.875f).noSummon().build("seat"));
    // @formatter:on

    /**
     * Empty method, called by {@link BCRegistries#init(net.minecraftforge.eventbus.api.IEventBus)} to classload this class.
     */
    static void init() {
    }
}
