package net.alex.guzhenren.entity;

import java.util.function.Supplier;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * A wild Rhinoceros Beetle Gu [甲虫蛊] that shares the boar Gu flight and rest behavior.
 *
 * <p>All four registered variants use this entity class. The registration supplies the caught item,
 * dimensions and attributes; the renderer supplies the variant texture. The model's animation contract is
 * {@code animation.idle} while resting, {@code animation.lift} for 0.8 seconds followed by looping
 * {@code animation.fly}, and {@code animation.land} for 0.8 seconds followed by looping {@code animation.idle}.
 * The exported {@code animation.walk} is intentionally never selected.
 *
 * <p>While flying or landing, a beetle whose horizontal speed has dropped below {@link #HOVER_ENTER_SPEED}
 * blocks per tick loops {@code animation.hover} instead of {@code animation.fly} until it is faster than
 * {@link #HOVER_EXIT_SPEED} again; the gap keeps a beetle near the threshold from flickering between the two.
 * The speed is the client's own per-tick position change, so the choice needs no synchronized state.
 *
 * <p>A hit that leaves a resting beetle alive plays {@code animation.hurt} (0.3 seconds, from the rest pose, so
 * never in flight). With one health point, only a hit below one damage leaves it alive. Death holds
 * {@code animation.death}, which rolls the beetle onto its back where it is -- in the air too -- and
 * {@link #tickDeath} removes it at {@link #DEATH_REMOVE_TICK}: the controller first blends into the death pose for {@code TRANSITION_TICKS} and only
 * then plays the animation's {@link #DEATH_TICKS}, so removing at the animation's own length would cut off the end
 * of the roll. A running lift or land is stopped first so the death starts at once.
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

public class RhinocerosBeetleGuEntity extends RestingFlyingGuEntity implements GeoEntity {

    public static final double HOVER_ENTER_SPEED = 0.02D;
    public static final double HOVER_EXIT_SPEED = 0.04D;
    public static final int DEATH_TICKS = 24;
    private static final int TRANSITION_TICKS = 5;
    public static final int DEATH_REMOVE_TICK = DEATH_TICKS + TRANSITION_TICKS;
    private static final String MAIN = "main";
    private static final RawAnimation IDLE_ANIM =
            RawAnimation.begin().thenLoop("animation.idle");
    private static final RawAnimation FLY_ANIM =
            RawAnimation.begin().thenLoop("animation.fly");
    private static final RawAnimation HOVER_ANIM =
            RawAnimation.begin().thenLoop("animation.hover");
    private static final RawAnimation LIFT_ANIM =
            RawAnimation.begin().thenPlay("animation.lift");
    private static final RawAnimation LAND_ANIM =
            RawAnimation.begin().thenPlay("animation.land");
    private static final RawAnimation HURT_ANIM =
            RawAnimation.begin().thenPlay("animation.hurt");
    private static final RawAnimation DEATH_ANIM =
            RawAnimation.begin().thenPlayAndHold("animation.death");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean hovering;

    public RhinocerosBeetleGuEntity(EntityType<? extends RhinocerosBeetleGuEntity> type, Level level,
                                    Supplier<Item> caughtGu) {
        super(type, level, caughtGu);
    }

    @Override
    public boolean seeks(Player player) { return false; }

    @Override
    protected void playLandingAnimation() { triggerAnim(MAIN, "land"); }

    @Override
    protected void playTakeoffAnimation() { triggerAnim(MAIN, "lift"); }

    public static boolean hovers(boolean hovering, double horizontalSpeed) {
        return hovering ? horizontalSpeed < HOVER_EXIT_SPEED : horizontalSpeed < HOVER_ENTER_SPEED;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            hovering = hovers(hovering, Math.hypot(getX() - xo, getZ() - zo));
        }
    }

    //region harm -- a flinch only on the ground, and a death that plays out before removal
    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt && !level().isClientSide() && isAlive() && phase() == FlightPhase.RESTING) triggerAnim(MAIN, "hurt");
        return hurt;
    }

    @Override
    public void die(@NotNull DamageSource damageSource) {
        super.die(damageSource);
        if (!level().isClientSide()) stopTriggeredAnim(MAIN, null);
    }

    @Override
    protected void tickDeath() {
        deathTime++;
        setDeltaMovement(Vec3.ZERO);
        if (deathTime >= DEATH_REMOVE_TICK && !level().isClientSide() && !isRemoved()) {
            level().broadcastEntityEvent(this, (byte) 60);
            remove(Entity.RemovalReason.KILLED);
        }
    }
    //endregion

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, MAIN, TRANSITION_TICKS, state -> {
            if (isDeadOrDying()) return state.setAndContinue(DEATH_ANIM);
            return switch (phase()) {
                case FLYING, LANDING -> state.setAndContinue(hovering ? HOVER_ANIM : FLY_ANIM);
                case RESTING -> state.setAndContinue(IDLE_ANIM);
            };
        }).triggerableAnim("lift", LIFT_ANIM).triggerableAnim("land", LAND_ANIM).triggerableAnim("hurt", HURT_ANIM));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
}
