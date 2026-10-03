package net.alex.guzhenren.path.strength;

import com.mojang.serialization.Codec;
import net.alex.guzhenren.EnumTranslatable;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/**
 * The four branches of Strength Path [力道], including Normal [基础力道] as the default classification.
 *
 * <p>Closed vocabulary enum. Beast Strength Phantom and Human Jun Strength own today's persistent
 * strength data; Atmospheric Heaven and Earth has no implementation yet; Normal classifies every
 * other Strength Path Gu without adding another data field.
 *
 * @author Alex
 * @version 1.0.0
 * @see HumanStrength
 * @see BeastStrength
 * @since 1.0.0
 */

public enum StrengthPathBranch implements StringRepresentable, EnumTranslatable {

    BEAST_STRENGTH_PHANTOM,
    HUMAN_JUN_STRENGTH,
    ATMOSPHERIC_HEAVEN_AND_EARTH,
    NORMAL;

    public static final Codec<StrengthPathBranch> CODEC = StringRepresentable.fromEnum(StrengthPathBranch::values);
    private static final String KEY_PREFIX = "guzhenren.enum.strength.strength_path_branch.";

    @Override
    public @NotNull String getSerializedName() { return name().toLowerCase(); }

    public String getTranslationKey() { return KEY_PREFIX + name().toLowerCase(); }
}
