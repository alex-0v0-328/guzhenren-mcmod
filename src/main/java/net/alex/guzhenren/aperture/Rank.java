package net.alex.guzhenren.aperture;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import net.alex.guzhenren.EnumTranslatable;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/**
 * Rank [转数], the coarse measure of a cultivator, and the tables each grade carries.
 *
 * <p>Closed vocabulary enum: {@code rankBase}, {@code maxHealth} and the {@link EssenceColor} all live
 * here as the single source. {@code NONE} is outside the settable range; {@code shift(int)} clamps at
 * {@code ONE..FIVE}. No sibling mod may add a rank.
 *
 * <p>⚠ {@code NONE} translates to the empty string on purpose -- the mortal's word belongs to
 * {@link Title} alone, and a word with two owners drifts apart. {@code SIX..NINE} carry deliberate
 * zeroes (phase 3); do not "fix" them.
 *
 * @author Alex
 * @version 1.0.0
 * @see Title
 * @since 1.0.0
 */

public enum Rank implements StringRepresentable, EnumTranslatable {

    NONE(0L, 20, EssenceColor.NONE),
    ONE(1L, 20, EssenceColor.GREEN_COPPER),
    TWO(10L, 40, EssenceColor.RED_STEEL),
    THREE(100L, 60, EssenceColor.WHITE_SILVER),
    FOUR(1_000L, 80, EssenceColor.YELLOW_GOLDEN),
    FIVE(10_000L, 100, EssenceColor.PURPLE_CRYSTAL),

    SIX(0L, 0, EssenceColor.GREEN_GRAPE),
    SEVEN(0L, 0, EssenceColor.RED_DATE),
    EIGHT(0L, 0, EssenceColor.WHITE_LITCHI),
    NINE(0L, 0, EssenceColor.YELLOW_APRICOT);

    public static final Codec<Rank> CODEC = StringRepresentable.fromEnum(Rank::values);
    private static final String KEY_PREFIX = "guzhenren.enum.aperture.rank.";
    public static final Rank LOWEST = ONE;
    public static final Rank HIGHEST = FIVE;
    private final long rankBase;
    private final int maxHealth;
    private final EssenceColor essenceColor;

    Rank(long rankBase, int maxHealth, EssenceColor essenceColor) {
        this.rankBase = rankBase;
        this.maxHealth = maxHealth;
        this.essenceColor = essenceColor;
    }

    public long getRankBase() { return rankBase; }

    public int getMaxHealth() { return maxHealth; }

    public EssenceColor getEssenceColor() { return essenceColor; }

    public Rank shift(int d) { return values()[Math.clamp(ordinal() + d, LOWEST.ordinal(), HIGHEST.ordinal())]; }

    public static Rank[] settable() { return Arrays.copyOfRange(values(), LOWEST.ordinal(), HIGHEST.ordinal() + 1); }

    @Override
    public @NotNull String getSerializedName() { return name().toLowerCase(); }

    public String getTranslationKey() { return KEY_PREFIX + name().toLowerCase(); }
}
