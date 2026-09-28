package net.alex.guzhenren.effect.timed;

import net.alex.guzhenren.Ticks;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * Hardship Strength Gu [苦力蛊] effect: the timed carrier -- the bonus itself is read from the
 * holder's health fraction, not from here.
 *
 * <p>This class carries only the duration and the icon; the 0..120 capacity bonus is computed in
 * {@link net.alex.guzhenren.attachment.service.path.PathStrengthService#capacity} off missing
 * health, so the effect staying present IS what keeps that ramp alive.
 *
 * @author Alex
 * @version 1.0.0
 * @see net.alex.guzhenren.attachment.service.path.PathStrengthService
 * @since 1.0.0
 */

public final class HardshipStrengthGuEffect extends MobEffect {

    public static final int DURATION_TICKS = 120 * Ticks.SECOND;

    public HardshipStrengthGuEffect(MobEffectCategory category, int color) {
        super(category, color);
    }
}
