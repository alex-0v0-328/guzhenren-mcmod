package net.alex.guzhenren.registry.world;

import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Level keys of the anchored dimensions this mod's travel acts on.
 *
 * <p>The dimensions themselves -- dimension type, biome, level stem and sky effects -- belong to the
 * companion mod Gu World ({@code guworld}), a sibling project that depends on this mod; this mod never
 * sees its classes. So each dimension is named here by its level key alone, a contract that Gu World's
 * level stem keeps. Without that mod installed the level is absent, and {@code /guworld enter} refuses every target
 * ({@link net.alex.guzhenren.dimension.DimensionTravelService#enter} finds no level).
 * The dimension's display name, {@code dimension.<namespace>.<path>}, is that mod's language key too.
 *
 * <p>{@link AnchoredDimension} is an anchored dimension: {@code /guworld enter} may target it, and a
 * player inside is expected to leave through {@code /guworld exit} so the recorded return point is
 * used. {@link AnchoredDimension#level} is the dimension's level key, {@link AnchoredDimension#spawn}
 * the fixed entry point every entrant arrives at, and {@link AnchoredDimension#rank} the dimension's
 * 转 (6..9: 6-7 blessed land [福地], 8-9 grotto-heaven [洞天]).
 *
 * <p>{@link #ANCHORED_DIMENSIONS} is the anchored dimension allow-list, by level key. Treasure
 * Yellow Heaven is a rank-8 grotto-heaven; future Blessed Land / Grotto-Heaven dimensions register
 * here.
 *
 * <p>⚠ {@link #TREASURE_YELLOW_HEAVEN} spells out Gu World's namespace because this mod cannot reach
 * that mod's id constant. Renaming the dimension there means renaming it here too, or entering it fails.
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

public final class ModDimensions {

    private ModDimensions() {}

    public static final ResourceKey<Level> TREASURE_YELLOW_HEAVEN = ResourceKey.create(
            Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath("guworld", "treasure_yellow_heaven"));
    public static final Vec3 TREASURE_YELLOW_HEAVEN_SPAWN = new Vec3(0.5, 64.0, 0.5);

    public record AnchoredDimension(ResourceKey<Level> level, Vec3 spawn, int rank) {}

    public static final Map<ResourceKey<Level>, AnchoredDimension> ANCHORED_DIMENSIONS = Map.of(
            TREASURE_YELLOW_HEAVEN,
            new AnchoredDimension(TREASURE_YELLOW_HEAVEN, TREASURE_YELLOW_HEAVEN_SPAWN, 8));
}
