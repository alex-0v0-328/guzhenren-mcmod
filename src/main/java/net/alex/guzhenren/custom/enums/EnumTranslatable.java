package net.alex.guzhenren.custom.enums;

/**
 * The one thing every domain enum in this mod implements: it names its own translation key.
 *
 * <p>Contract for the closed vocabulary enums ({@code GuPath}, {@code Rank}, {@code Stage},
 * {@code Talent}, ...): both language providers take this interface rather than a {@code String}, so the
 * key an enum ships and the key it is registered under can never drift into two different literals.
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

public interface EnumTranslatable {

    String getTranslationKey();
}
