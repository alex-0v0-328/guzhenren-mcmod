package net.alex.guzhenren.gameplay.body;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.registry.effect.ModEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

/**
 * Reacts to the vanilla moments that change body [肉身] behavior: breathing, attacks, and effects.
 *
 * <p>{@code onBreathe} lets an undead life form breathe underwater. Effect changes refresh the
 * attack total immediately; {@code onAttack} refreshes the health-based Hardship Strength Gu
 * capacity before vanilla reads attack damage.
 *
 * <p>⚠ Left to the heartbeat alone the attack row would lag by up to a second, which is visible on
 * the panel.
 *
 * @author Alex
 * @version 1.0.0
 * @see BodyAttackService
 * @since 1.0.0
 */

@EventBusSubscriber(modid = Guzhenren.MOD_ID)
public final class BodyStateEvents {

    private BodyStateEvents() {}

    @SubscribeEvent
    public static void onBreathe(LivingBreatheEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!BodyService.isUndead(player)) return;

        event.setCanBreathe(true);
    }

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) { refreshAttack(event.getEntity()); }

    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Remove event) { refreshAttack(event.getEntity()); }

    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.Expired event) { refreshAttack(event.getEntity()); }

    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        if (event.getEntity() instanceof ServerPlayer player
                && player.hasEffect(ModEffects.HARDSHIP_STRENGTH_GU)) BodyAttackService.refresh(player);
    }

    private static void refreshAttack(LivingEntity entity) {
        if (entity instanceof ServerPlayer player) BodyAttackService.refresh(player);
    }
}
