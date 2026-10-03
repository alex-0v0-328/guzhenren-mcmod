package net.alex.guzhenren.effect.timed;

import net.alex.guzhenren.gameplay.aperture.ApertureEssenceService;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * The effect worn while a Liquor Worm [酒虫] distills — a marker for the vanilla HUD icon, not the state itself.
 *
 * <p>Timed effects own their truth on vanilla's timer, but this one is exceptional: it marks the
 * state, it does not hold it. {@link ApertureEssenceService}
 * owns the three phases, so removing this effect does not end the distillation. The 1:2 payback on
 * expiry is watched by {@code PlayerTickEvents.closeDistilling}, which reads the level — the only thing
 * that catches milk, {@code /effect clear} and death.
 *
 * <p>☠ Hanging the payback on this effect's expiry would be wrong: MobEffect has no expiry hook.
 *
 * @author Alex
 * @version 1.0.0
 * @see ApertureEssenceService
 * @since 1.0.0
 */

public class LiquorWormEffect extends MobEffect {

    public LiquorWormEffect(MobEffectCategory category, int color) {
        super(category, color);
    }
}
