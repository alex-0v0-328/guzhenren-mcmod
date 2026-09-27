package net.alex.guzhenren.effect.pool;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * Death Qi [死气] effect — a pool projection of the 死气 in {@link
 * net.alex.guzhenren.attachment.data.path.PathQiData}: burns lifespan [寿元] and floors health while above zero.
 *
 * <p>Pool effects are rebuilt every heartbeat by {@code PathQiService.syncEffects}, so milk cannot cure
 * them. A {@link net.minecraft.world.effect.MobEffect} has no expiry hook, so the burning runs on the
 * heartbeat in {@code PlayerTickEvents.tickDeathQi}, settling the debt by reading the level.
 *
 * <p>⚠ {@code YEAR_INTERVAL_TICKS} is 120 = 6 × 20 — it must divide the heartbeat's 20, or the burning
 * silently stops. 生气 [Life Qi] pays 死气 down 1:1; clearing to 0 refunds {@code REFUND_NUMERATOR / REFUND_DENOMINATOR}.
 *
 * @author Alex
 * @version 1.0.0
 * @see net.alex.guzhenren.attachment.service.path.PathQiService
 * @since 1.0.0
 */

public class DeathQiEffect extends MobEffect {

    public static final int YEAR_INTERVAL_TICKS = 120;
    public static final long YEARS_PER_INTERVAL = 1L;
    public static final float HEALTH_FLOOR = 2.0F;
    public static final float HEALTH_PER_HEARTBEAT = 1.0F;
    public static final int REFUND_NUMERATOR = 3;
    public static final int REFUND_DENOMINATOR = 4;
    public DeathQiEffect(MobEffectCategory category, int color) {
        super(category, color);
    }
}
