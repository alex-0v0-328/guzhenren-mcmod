package net.alex.guzhenren.registry.fluid;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.block.SpiritSpringFluid;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * The Spirit Spring [元泉] fluid pair: the registry entries of the source and its flowing arm.
 *
 * <p>DeferredRegister holder only; the behavior both entries share lives in {@link SpiritSpringFluid}.
 *
 * @author Alex
 * @version 1.0.0
 * @see ModFluidTypes
 * @see SpiritSpringFluid
 * @since 1.0.0
 */

public final class ModFluids {

    private ModFluids() {}

    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(Registries.FLUID, Guzhenren.MOD_ID);
    public static final DeferredHolder<Fluid, SpiritSpringFluid.Source> SPIRIT_SPRING =
            FLUIDS.register("spirit_spring", SpiritSpringFluid.Source::new);
    public static final DeferredHolder<Fluid, SpiritSpringFluid.Flowing> FLOWING_SPIRIT_SPRING =
            FLUIDS.register("flowing_spirit_spring", SpiritSpringFluid.Flowing::new);

    public static void register(IEventBus modEventBus) { FLUIDS.register(modEventBus); }
}
