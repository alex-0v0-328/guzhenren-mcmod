package net.alex.guzhenren.custom.enums.aperture;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import net.alex.guzhenren.custom.enums.EnumTranslatable;
import net.alex.guzhenren.custom.enums.WeightedPick;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/**
 * Aptitude [资质], derived from the aperture's base essence and never stored beside it.
 *
 * <p>Closed vocabulary enum: regen rate, Epic Fight stamina percentage and the roll weights all live here. {@code NONE}
 * is outside the settable range; {@code shift(int)} clamps at {@code EXTREME..FOURTH}. No sibling mod
 * may add a grade.
 *
 * <p>⚠ The constants run HIGH to LOW, so {@code shift(+1)} is {@code ordinal - 1}. Reading the direction
 * off the declaration order is exactly how that gets reversed by accident. {@code 1..19} is a hole, not
 * a value ({@code fromPercent} -> {@code NONE}).
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

public enum Talent implements StringRepresentable, EnumTranslatable {

    EXTREME(100, 100, 10, 20, 50),
    FIRST(80, 99, 20, 8, 20),
    SECOND(60, 79, 30, 4, 20),
    THIRD(40, 59, 30, 2, 10),
    FOURTH(20, 39, 10, 1, 10),

    NONE(0, 0, 0, 0, 0);

    public static final Codec<Talent> CODEC = StringRepresentable.fromEnum(Talent::values);
    private static final String KEY_PREFIX = "guzhenren.enum.aperture.talent.";
    public static final Talent HIGHEST = EXTREME;
    public static final Talent LOWEST = FOURTH;
    private final int minPercent;
    private final int maxPercent;
    private final int weight;
    private final int regenRate;
    private final int staminaMaxPercent;

    Talent(int minPercent, int maxPercent, int weight, int regenRate, int staminaMaxPercent) {
        this.minPercent = minPercent;
        this.maxPercent = maxPercent;
        this.weight = weight;
        this.regenRate = regenRate;
        this.staminaMaxPercent = staminaMaxPercent;
    }

    public int getMinPercent() { return minPercent; }

    public int getMaxPercent() { return maxPercent; }

    public int getWeight() { return weight; }

    public int getRegenRate() { return regenRate; }

    public int getStaminaMaxPercent() { return staminaMaxPercent; }

    public Talent shift(int d) { return values()[Math.clamp(ordinal() - d, HIGHEST.ordinal(), LOWEST.ordinal())]; }

    public static Talent[] settable() { return Arrays.copyOfRange(values(), HIGHEST.ordinal(), LOWEST.ordinal() + 1); }

    @Override
    public @NotNull String getSerializedName() { return name().toLowerCase(); }

    public String getTranslationKey() { return KEY_PREFIX + name().toLowerCase(); }

    public static Talent randomTalent() { return WeightedPick.pick(values(), t -> t.weight); }

    public static int randomPercent(Talent talent) {
        if (talent.minPercent == talent.maxPercent) return talent.minPercent;
        return ThreadLocalRandom.current().nextInt(talent.minPercent, talent.maxPercent + 1);
    }

    public static Talent fromPercent(int percent) {
        for (Talent t : values()) {
            if (percent >= t.minPercent && percent <= t.maxPercent) return t;
        }
        return NONE;
    }
}
