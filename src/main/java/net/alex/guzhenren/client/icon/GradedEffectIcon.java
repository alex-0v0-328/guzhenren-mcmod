package net.alex.guzhenren.client.icon;

import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;

/**
 * Draws a graded effect's icon from its amplifier, so one effect can wear a different face per grade.
 *
 * <p>An {@link EffectIconLayout} record carrying a name, a directory ({@code mob_effect} or {@code
 * item}) and the rank range. The texture path is {@code directory/name_rank}, where rank is
 * {@code amplifier + amplifierOffset} clamped to the range; the offset stays 1 unless the family
 * counts its amplifier from a later rank (the malicious thought Gu, whose grades start at rank two).
 *
 * <p>⚠ Keeps a family of grades one effect -- splitting it per grade would let two grades be held at once.
 *
 * @author Alex
 * @version 1.0.0
 * @see EffectIconLayout
 * @see ItemEffectIcon
 * @since 1.0.0
 */

public record GradedEffectIcon(String name, String directory, int lowestRank,
                               int highestRank, int amplifierOffset) implements EffectIconLayout {

    public static GradedEffectIcon mobEffect(String name, int lowestRank, int highestRank) {
        return new GradedEffectIcon(name, "mob_effect", lowestRank, highestRank, 1);
    }

    public static GradedEffectIcon item(String name, int lowestRank, int highestRank) {
        return new GradedEffectIcon(name, "item", lowestRank, highestRank, 1);
    }

    public static GradedEffectIcon item(String name, int lowestRank, int highestRank, int amplifierOffset) {
        return new GradedEffectIcon(name, "item", lowestRank, highestRank, amplifierOffset);
    }

    @Override
    public String textureFor(MobEffectInstance instance) {
        int rank = Mth.clamp(instance.getAmplifier() + amplifierOffset, lowestRank, highestRank);
        return directory + "/" + name + "_" + rank;
    }
}
