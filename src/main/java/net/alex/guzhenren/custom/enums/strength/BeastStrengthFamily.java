package net.alex.guzhenren.custom.enums.strength;

import net.alex.guzhenren.custom.enums.EnumTranslatable;

/**
 * The family a beast strength [兽力] belongs to: the two boars share one bracket, the bear owns the other.
 *
 * <p>Readings group by family rather than by species constant, so a second boar lands in the boar
 * bracket instead of opening a new one.
 *
 * @author Alex
 * @version 1.0.0
 * @see BeastStrength
 * @see net.alex.guzhenren.attachment.data.path.PathStrengthData
 * @since 1.0.0
 */

public enum BeastStrengthFamily implements EnumTranslatable {

    BOAR,
    BEAR;

    private static final String KEY_PREFIX = "guzhenren.enum.strength.beast_family.";

    public String getTranslationKey() { return KEY_PREFIX + name().toLowerCase(); }
}
