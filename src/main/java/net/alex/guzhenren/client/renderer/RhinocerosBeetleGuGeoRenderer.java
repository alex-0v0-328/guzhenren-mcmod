package net.alex.guzhenren.client.renderer;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.entity.RhinocerosBeetleGuEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * Shares beetle geometry and animations while each registered type selects its fixed texture: dark silver for
 * both rank-three Gu (one shared file), dark gold for rank four and dark amethyst for rank five. The death
 * tilt is off because {@code animation.death} rolls the beetle onto its back by itself.
 */

public final class RhinocerosBeetleGuGeoRenderer extends GeoEntityRenderer<RhinocerosBeetleGuEntity> {

    public static final ResourceLocation SILVER_TEXTURE = Guzhenren.id("textures/entity/crash_gu.png");
    public static final ResourceLocation GOLD_TEXTURE = Guzhenren.id("textures/entity/charging_crash_gu.png");
    public static final ResourceLocation AMETHYST_TEXTURE = Guzhenren.id("textures/entity/charging_crash_gu_5.png");
    private final ResourceLocation texture;

    public RhinocerosBeetleGuGeoRenderer(EntityRendererProvider.Context context,
                                         GeoModel<RhinocerosBeetleGuEntity> model, ResourceLocation texture) {
        super(context, model);
        this.shadowRadius = 0.2F;
        this.texture = texture;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull RhinocerosBeetleGuEntity entity) { return texture; }

    @Override
    protected float getDeathMaxRotation(@NotNull RhinocerosBeetleGuEntity entity) { return 0.0F; }
}
