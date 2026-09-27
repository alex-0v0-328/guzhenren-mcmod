package net.alex.guzhenren.custom.enums.path;

import com.mojang.serialization.Codec;
import net.alex.guzhenren.custom.enums.EnumTranslatable;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/**
 * Where a Dao mark [道痕] came from, so a quantity can later be revoked exactly.
 *
 * <p>Closed vocabulary enum; no sibling mod may add a tag. That closure is what lets
 * {@code PathEntry} keep a plain {@code EnumMap} keyed by it.
 *
 * @author Alex
 * @version 1.0.0
 * @see GuPath
 * @since 1.0.0
 */

public enum MarkTag implements StringRepresentable, EnumTranslatable {

    NATURAL,
    RACE,
    EXTREME_PHYSIQUE;

    public static final Codec<MarkTag> CODEC = StringRepresentable.fromEnum(MarkTag::values);
    private static final String KEY_PREFIX = "guzhenren.enum.path.tag.";
    @Override
    public @NotNull String getSerializedName() {return name().toLowerCase();}
    public String getTranslationKey() {return KEY_PREFIX + name().toLowerCase();}
}
