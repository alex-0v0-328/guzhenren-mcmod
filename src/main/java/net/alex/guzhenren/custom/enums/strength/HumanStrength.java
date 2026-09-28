package net.alex.guzhenren.custom.enums.strength;

import com.mojang.serialization.Codec;
import net.alex.guzhenren.custom.enums.EnumTranslatable;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/**
 * The human strengths a body accumulates, measured in Jin [斤] and Jun [钧].
 *
 * <p>Closed vocabulary enum stored as {@code Map<HumanStrength, Integer>} on {@code PathStrengthData}.
 * {@code JUN == 30 斤} and the four layer caps sum to exactly 9,999 斤. No sibling mod may add a kind.
 *
 * <p>⚠ {@code ATTACK_PER_JIN} 0.125 is load-bearing: a kind not worth exactly {@code 0.125 × 斤} would
 * break the strength capacity ramp. What a body has accumulated and what it can bring to bear are
 * different questions; only the strength service answers the second.
 *
 * @author Alex
 * @version 1.0.0
 * @see StrengthPathBranch
 * @since 1.0.0
 */

public enum HumanStrength implements StringRepresentable, EnumTranslatable {

    JIN(1, 9),
    TEN_JIN(10, 9),
    JUN(30, 30),
    TEN_JUN(300, 30);

    public static final Codec<HumanStrength> CODEC = StringRepresentable.fromEnum(HumanStrength::values);
    private static final String KEY_PREFIX = "guzhenren.enum.strength.human_strength.";
    public static final int TEN_FACTOR = 10;
    public static final double ATTACK_PER_JIN = 0.125D;
    private final int jin;
    private final int maxLayers;

    HumanStrength(int jin, int maxLayers) {
        this.jin = jin;
        this.maxLayers = maxLayers;
    }

    public int getJin() { return jin; }

    public int getMaxLayers() { return maxLayers; }

    @Override
    public @NotNull String getSerializedName() { return name().toLowerCase(); }

    public String getTranslationKey() { return KEY_PREFIX + name().toLowerCase(); }
}
