package net.alex.guzhenren.effect.pool;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * Half-Zombie [半生半僵] effect — a pool projection of the form stored on {@link
 * net.alex.guzhenren.body.BodyData}, never a truth of its own.
 *
 * <p>Pool effects are rebuilt every heartbeat — the qi ones by {@code PathQiService.syncEffects}, this
 * one by {@code PlayerTickEvents.projectHalfZombie} — so milk, {@code /effect clear} and death cannot
 * strand a player wearing a form they are no longer in; the next tick re-applies or removes it.
 *
 * <p>☠ The class body is empty on purpose: the form is the truth, the effect is only the vanilla
 * icon the HUD needs to display it.
 *
 * @author Alex
 * @version 1.0.0
 * @see net.alex.guzhenren.body.BodyService
 * @since 1.0.0
 */

public class HalfZombieEffect extends MobEffect {

    public HalfZombieEffect(MobEffectCategory category, int color) {
        super(category, color);
    }
}
