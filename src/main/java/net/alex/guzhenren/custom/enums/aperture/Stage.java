package net.alex.guzhenren.custom.enums.aperture;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import net.alex.guzhenren.custom.enums.EnumTranslatable;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/**
 * Stage [阶段] within a rank, and the multiplier it contributes to the essence [真元] cap.
 *
 * <p>Closed vocabulary enum: the multiplier lives here and the formula lives on the record, so the cap
 * has exactly one expression. {@code NONE} is outside the settable range; {@code shift(int)} clamps at
 * {@code INIT..PEAK}. No sibling mod may add a stage.
 *
 * <p>⚠ A second place that multiplies is a second answer -- do not re-declare the multiplier at a call
 * site. {@code NONE}'s multiplier is {@code 0}, which is the mortal's empty pool, not a fallback.
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

public enum Stage implements StringRepresentable, EnumTranslatable {

    NONE(0),
    INIT(1),
    MIDDLE(2),
    UPPER(4),
    PEAK(8);

    public static final Codec<Stage> CODEC = StringRepresentable.fromEnum(Stage::values);
    private static final String KEY_PREFIX = "guzhenren.enum.aperture.stage.";
    public static final Stage LOWEST = INIT;
    public static final Stage HIGHEST = PEAK;
    private final int essenceMultiplier;
    Stage(int essenceMultiplier) {
        this.essenceMultiplier = essenceMultiplier;
    }
    public int getEssenceMultiplier() {return essenceMultiplier;}
    public Stage shift(int d) {return values()[Math.clamp(ordinal() + d, LOWEST.ordinal(), HIGHEST.ordinal())];}
    public static Stage[] settable() {return Arrays.copyOfRange(values(), LOWEST.ordinal(), HIGHEST.ordinal() + 1);}
    @Override
    public @NotNull String getSerializedName() {return name().toLowerCase();}
    public String getTranslationKey() {return KEY_PREFIX + name().toLowerCase();}
}
