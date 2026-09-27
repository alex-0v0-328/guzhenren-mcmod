package net.alex.guzhenren.custom.enums.body;

import com.mojang.serialization.Codec;
import net.alex.guzhenren.custom.enums.EnumTranslatable;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/**
 * Cumulative body physiques [体质] that can coexist on a player.
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

public enum Physique implements StringRepresentable, EnumTranslatable {

    ZOMBIE,
    HALF_ZOMBIE,
    EXTREME;

    public static final Codec<Physique> CODEC = StringRepresentable.fromEnum(Physique::values);
    private static final String KEY_PREFIX = "guzhenren.enum.body.physique.";
    @Override
    public @NotNull String getSerializedName() {return name().toLowerCase();}
    public String getTranslationKey() {return KEY_PREFIX + name().toLowerCase();}
}
