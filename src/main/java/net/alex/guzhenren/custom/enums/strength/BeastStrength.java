package net.alex.guzhenren.custom.enums.strength;

import com.mojang.serialization.Codec;
import net.alex.guzhenren.custom.enums.EnumTranslatable;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/**
 * The beast strengths a body can hold, each declaring what it is worth.
 *
 * <p>Closed vocabulary enum stored as {@code Set<BeastStrength>} on {@code PathStrengthData}. Each constant
 * carries its own family and attack bonus, so adding a beast is one constant touching neither
 * the Gu item nor the attack sum. No sibling mod may add a species.
 *
 * <p>⚠ A species tag is per SPECIES, not per constant: {@code WHITE_BOAR} and {@code BLACK_BOAR} share
 * {@code BOAR}. Do not move the family or the bonus into the reader.
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

public enum BeastStrength implements StringRepresentable, EnumTranslatable {

    WHITE_BOAR(BeastStrengthFamily.BOAR, 1, 3.0D),
    BLACK_BOAR(BeastStrengthFamily.BOAR, 1, 3.0D),
    BEAR(BeastStrengthFamily.BEAR, 1, 4.0D);

    public static final Codec<BeastStrength> CODEC = StringRepresentable.fromEnum(BeastStrength::values);
    private static final String KEY_PREFIX = "guzhenren.enum.strength.beast_strength.";
    private final BeastStrengthFamily family;
    private final int reading;
    private final double attackBonus;
    BeastStrength(BeastStrengthFamily family, int reading, double attackBonus) {
        this.family = family;
        this.reading = reading;
        this.attackBonus = attackBonus;
    }
    public BeastStrengthFamily getFamily() {return family;}
    public int getReading() {return reading;}
    public double getAttackBonus() {return attackBonus;}
    @Override
    public @NotNull String getSerializedName() {return name().toLowerCase();}
    public String getTranslationKey() {return KEY_PREFIX + name().toLowerCase();}
}
