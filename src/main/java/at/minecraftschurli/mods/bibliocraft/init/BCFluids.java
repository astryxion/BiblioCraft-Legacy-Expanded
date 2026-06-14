package at.minecraftschurli.mods.bibliocraft.init;

import at.minecraftschurli.mods.bibliocraft.content.fluid.ExperienceFluid;
import at.minecraftschurli.mods.bibliocraft.util.registrar.DeferredHolder;
import net.minecraft.world.level.material.Fluid;

public interface BCFluids {
    DeferredHolder<Fluid, ExperienceFluid.Source> EXPERIENCE = BCRegistries.FLUIDS.register("experience", ExperienceFluid.Source::new);
    DeferredHolder<Fluid, ExperienceFluid.Flowing> EXPERIENCE_FLOWING = BCRegistries.FLUIDS.register("flowing_experience", ExperienceFluid.Flowing::new);

    static void init() {}
}
