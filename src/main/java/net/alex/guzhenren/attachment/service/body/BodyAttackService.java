package net.alex.guzhenren.attachment.service.body;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.attachment.data.body.BodyData;
import net.alex.guzhenren.attachment.data.path.PathStrengthData;
import net.alex.guzhenren.attachment.service.path.PathStrengthService;
import net.alex.guzhenren.custom.enums.strength.BeastStrength;
import net.alex.guzhenren.custom.enums.strength.HumanStrength;
import net.alex.guzhenren.effect.AttackContributor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

/**
 * The only thing in the mod that touches the {@code ATTACK_DAMAGE} attribute -- {@code bonus()} is the
 * whole sum: {@link AttackContributor} timed effects [力道], beast strengths [兽力], the usable-jin
 * ramp, and the zombie [僵] tier bonus.
 *
 * <p>⚠ The modifier MUST stay transient (a permanent one double-stacks from attribute NBT on the next
 * login). ⚠ No effect may declare its own {@code addAttributeModifier} -- {@code bonus()} already
 * counts it via {@link AttackContributor#attackBonus}. ⚠ The zombie bonus rides {@code
 * BodyData.zombieTier}, NOT a MobEffect (permanent 僵 has none; command tier -1 gets NO attack).
 *
 * <p>{@link #showsImpactRing(double)} tests the attack-panel value against
 * {@link #IMPACT_RING_ATTACK_THRESHOLD}, at and above which a landed Epic Fight bare-hand/fist punch
 * spawns the shockwave ring (Alex, 2026-09-19); read as the panel number, so only fist-category
 * weapon damage ever rides along with the strength bonus.
 *
 * @author Alex
 * @version 1.0.0
 * @see BodyHealthService
 * @see PathStrengthService
 * @since 1.0.0
 */

public final class BodyAttackService {

    private BodyAttackService() {}

    public static final double VANILLA_ATTACK_DAMAGE = 1.0D;
    private static final ResourceLocation MODIFIER_ID =
            Guzhenren.id("strength_attack_damage");
    public static final double ZOMBIE_ATTACK_BASE = 5.0D;
    public static final double IMPACT_RING_ATTACK_THRESHOLD = 16.0D;

    public static boolean showsImpactRing(double attackDamage) {
        return attackDamage >= IMPACT_RING_ATTACK_THRESHOLD;
    }

    public static double bonus(@NotNull Player player) {
        PathStrengthData data = PathStrengthService.get(player);
        double total = 0.0D;

        for (BeastStrength beast : BeastStrength.values()) {
            if (data.has(beast)) total += beast.getAttackBonus();
        }
        return total + PathStrengthService.usableJin(player) * HumanStrength.ATTACK_PER_JIN
                + zombieBonus(player) + effectBonus(player);
    }

    public static double effectBonus(@NotNull Player player) {
        double total = 0.0D;
        for (MobEffectInstance instance : player.getActiveEffects()) {
            if (instance.getEffect().value() instanceof AttackContributor contributor) {
                total += contributor.attackBonus(instance.getAmplifier());
            }
        }
        return total;
    }

    public static double zombieBonus(@NotNull Player player) {
        BodyData body = BodyService.get(player);
        if (!body.isZombieOrHalfZombie() || body.zombieTier() < 0) return 0.0D;

        return ZOMBIE_ATTACK_BASE * (1 << body.zombieTier());
    }

    static void swapTransientModifier(@NotNull AttributeInstance instance, @NotNull ResourceLocation id, double bonus) {
        AttributeModifier held = instance.getModifier(id);
        if (held == null ? bonus == 0.0D : held.amount() == bonus) return;

        instance.removeModifier(id);
        if (bonus != 0.0D) {
            instance.addTransientModifier(new AttributeModifier(id, bonus, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    public static void refresh(@NotNull ServerPlayer player) {
        AttributeInstance instance = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (instance == null) return;
        swapTransientModifier(instance, MODIFIER_ID, bonus(player));
    }
}
