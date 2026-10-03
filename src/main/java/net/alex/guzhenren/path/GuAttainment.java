package net.alex.guzhenren.path;

import com.mojang.serialization.Codec;
import net.alex.guzhenren.EnumTranslatable;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/**
 * Attainment [造诣] in a path, from nothing up to the highest grade.
 *
 * <p>Closed vocabulary enum paired with {@link GuPath}: stored once per path in {@code PathEntry}, and
 * no sibling mod may extend the grade ladder. {@code shift(int)} clamps at both ends, so callers never
 * touch {@code ordinal()}.
 *
 * <p>⚠ Each grade carries its own refinement bonus here, so adding one is a single line and no change
 * anywhere else. Do not move that table into the code that reads it.
 *
 * @author Alex
 * @version 1.0.0
 * @see GuPath
 * @since 1.0.0
 */

public enum GuAttainment implements StringRepresentable, EnumTranslatable {

    NONE(0, 0),
    ORDINARY(1, 2),
    QUASI_MASTER(2, 5),
    MASTER(3, 10),
    QUASI_GRANDMASTER(4, 20),
    GRANDMASTER(5, 40),
    QUASI_GREAT_GRANDMASTER(6, 50),
    GREAT_GRANDMASTER(7, 70),
    QUASI_SUPREME_GRANDMASTER(8, 80),
    SUPREME_GRANDMASTER(9, 100);

    public static final Codec<GuAttainment> CODEC = StringRepresentable.fromEnum(GuAttainment::values);
    private static final String KEY_PREFIX = "guzhenren.enum.path.attainment.";
    private final int level;
    private final int refinementBonus;

    GuAttainment(int level, int refinementBonus) {
        this.level = level;
        this.refinementBonus = refinementBonus;
    }

    public int getLevel() { return level; }

    public int getRefinementBonus() { return refinementBonus; }

    public GuAttainment shift(int d) { return values()[Math.clamp(ordinal() + d, 0, values().length - 1)]; }

    @Override
    public @NotNull String getSerializedName() { return name().toLowerCase(); }

    public String getTranslationKey() { return KEY_PREFIX + name().toLowerCase(); }
}
