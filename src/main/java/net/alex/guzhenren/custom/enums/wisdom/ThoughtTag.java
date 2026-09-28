package net.alex.guzhenren.custom.enums.wisdom;

import com.mojang.serialization.Codec;
import net.alex.guzhenren.custom.enums.EnumTranslatable;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/**
 * The composition of a thought [念] pool: which kind a portion is tagged as.
 *
 * <p>Closed vocabulary enum; the tagged map on {@code MindData} keys off it. No sibling mod may add a tag.
 *
 * <p>⚠ {@code NATURAL} is derived, never stored -- it is what remains once every tagged portion is
 * subtracted from the pool's current. Only {@code EVIL} (and future tags) live in the map, so
 * {@code NATURAL} has no writer.
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

public enum ThoughtTag implements StringRepresentable, EnumTranslatable {

    NATURAL,
    EVIL;

    public static final Codec<ThoughtTag> CODEC = StringRepresentable.fromEnum(ThoughtTag::values);
    private static final String KEY_PREFIX = "guzhenren.enum.wisdom.tag.";

    @Override
    public @NotNull String getSerializedName() { return name().toLowerCase(); }

    @Override
    public String getTranslationKey() { return KEY_PREFIX + name().toLowerCase(); }
}
