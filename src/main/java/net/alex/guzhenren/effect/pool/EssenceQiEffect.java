package net.alex.guzhenren.effect.pool;

import net.alex.guzhenren.gameplay.aperture.ApertureEssenceService;
import net.alex.guzhenren.gameplay.path.qi.PathQiData;
import net.alex.guzhenren.gameplay.path.qi.PathQiService;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * Essence Qi [元气] effect — a pool projection of the 元气 held in {@link
 * PathQiData}, which lifts essence [真元] regeneration.
 *
 * <p>Pool effects are rebuilt every heartbeat by {@code PathQiService.syncEffects}, so milk cannot cure
 * them — the pool is the truth. The {@code REGEN_BONUS} table is read by {@link
 * ApertureEssenceService#regenStep} rather than applied
 * from here, so essence regeneration stays a single formula in a single place.
 *
 * <p>⚠ Death Qi [死气] outranks this: the regen step checks {@code isChoked} first and returns.
 *
 * @author Alex
 * @version 1.0.0
 * @see PathQiService
 * @since 1.0.0
 */

public class EssenceQiEffect extends MobEffect {

    private static final double[] REGEN_BONUS = { 0.20, 0.40, 0.60, 0.80, 1.00 };

    public EssenceQiEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public static double bonus(int amplifier) {
        return REGEN_BONUS[Math.clamp(amplifier, 0, REGEN_BONUS.length - 1)];
    }
}
