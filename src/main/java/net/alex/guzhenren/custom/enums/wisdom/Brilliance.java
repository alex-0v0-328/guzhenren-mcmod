package net.alex.guzhenren.custom.enums.wisdom;

import com.mojang.serialization.Codec;
import net.alex.guzhenren.custom.enums.EnumTranslatable;
import net.alex.guzhenren.custom.enums.WeightedPick;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/**
 * Brilliance [才情]: the grade deciding how fast thought [念] refills.
 *
 * <p>Closed vocabulary enum, rolled once at birth by {@code onBirth} and independent of aptitude.
 * Rates 1/4/16/64/256 念/s with weights 15/25/25/25/10; {@code shift(int)} clamps at both ends.
 * No sibling mod may add a grade.
 *
 * <p>⚠ There is deliberately no {@code NONE} grade, because a mortal still thinks. The lowest grade is
 * a real value rather than an absence, and code reading it as "unset" stops a mortal thinking at all.
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

public enum Brilliance implements StringRepresentable, EnumTranslatable {

    ORDINARY(1, 15),
    DECENT(4, 25),
    DISTINCTIVE(16, 25),
    OUTSTANDING(64, 25),
    UNRIVALED(256, 10);

    public static final Codec<Brilliance> CODEC = StringRepresentable.fromEnum(Brilliance::values);
    private static final String KEY_PREFIX = "guzhenren.enum.wisdom.brilliance.";
    public static final Brilliance LOWEST = ORDINARY;
    public static final Brilliance HIGHEST = UNRIVALED;
    private final long thoughtsPerSecond;
    private final int weight;

    Brilliance(long thoughtsPerSecond, int weight) {
        this.thoughtsPerSecond = thoughtsPerSecond;
        this.weight = weight;
    }

    public long getThoughtsPerSecond() { return thoughtsPerSecond; }

    public int getWeight() { return weight; }

    public Brilliance shift(int d) { return values()[Math.clamp(ordinal() + d, LOWEST.ordinal(), HIGHEST.ordinal())]; }

    @Override
    public @NotNull String getSerializedName() { return name().toLowerCase(); }

    public String getTranslationKey() { return KEY_PREFIX + name().toLowerCase(); }

    public static Brilliance randomBrilliance() { return WeightedPick.pick(values(), b -> b.weight); }
}
