package net.alex.guzhenren.custom.enums.aperture;

import com.mojang.serialization.Codec;
import net.alex.guzhenren.custom.enums.EnumTranslatable;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/**
 * The aperture status [空窍状态]: what an aperture [空窍] still can and cannot do, derived never stored.
 *
 * <p>Closed vocabulary enum. NORMAL is a living aperture; DEAD [死窍] covers zombie [僵], half-zombie
 * [半僵] and stone apertures [石窍] -- no natural regen, nourishing or striking.
 *
 * <p>⚠ The derivation lives on {@code ApertureService#status}, not here -- enums in this package are
 * vocabulary, and the check every gate wants is {@code status == NORMAL}.
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

public enum ApertureStatus implements StringRepresentable, EnumTranslatable {

    NORMAL, DEAD;
    public static final Codec<ApertureStatus> CODEC = StringRepresentable.fromEnum(ApertureStatus::values);
    private static final String KEY_PREFIX = "guzhenren.enum.aperture.status.";

    @Override
    public @NotNull String getSerializedName() { return name().toLowerCase(); }

    public String getTranslationKey() { return KEY_PREFIX + name().toLowerCase(); }
}
