package net.alex.guzhenren.item.gu.mortal.earth;

import net.alex.guzhenren.attachment.data.aperture.ApertureData;
import net.alex.guzhenren.attachment.service.aperture.ApertureNourishService;
import net.alex.guzhenren.attachment.service.aperture.ApertureService;
import net.alex.guzhenren.custom.enums.aperture.ApertureStatus;
import net.alex.guzhenren.custom.enums.aperture.Stage;
import net.alex.guzhenren.item.gu.ConsumedGuItem;
import net.alex.guzhenren.item.gu.GuSpec;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A Stone Aperture Gu [石窍蛊]: its target hits this rank's peak and petrifies -- never nourished, never struck again.
 *
 * <p>Extends {@link net.alex.guzhenren.item.gu.ConsumedGuItem}, so it is tended, never feeds and
 * is taken by its own use. Three rungs register against this one class. The target is the PRIMARY
 * aperture whenever it is NORMAL; only a dead or stoned primary passes the gu on, and only to a
 * NORMAL second aperture. A rank mismatch on the primary does NOT pass it on -- the fall-through is
 * for a lost aperture, not for a wrong rank. The gate refuses no usable target, a rank mismatch on
 * the target, and a target already at the peak (that use would buy only the lock). The payout
 * delegates to {@link ApertureNourishService#petrify}.
 *
 * @author Alex
 * @version 1.0.0
 * @see net.alex.guzhenren.item.gu.ConsumedGuItem
 * @since 1.0.0
 */

public class StoneApertureGuItem extends ConsumedGuItem {

    public static final int NO_TARGET = -1;
    private static final String FAILED_RANK_MISMATCH = "guzhenren.item.failed.rank_mismatch";
    private static final String FAILED_UNAVAILABLE = "guzhenren.item.failed.aperture_unavailable";
    private static final String FAILED_STAGE_PEAK = "guzhenren.item.failed.stage_peak";
    public StoneApertureGuItem(Properties properties, GuSpec spec) {
        super(properties, spec);
    }
    /**
     * The pure seam the unit tests pin: PRIMARY wins while it is NORMAL, the second aperture answers
     * only when the primary is DEAD, and nobody usable answers {@code NO_TARGET}.
     */
    public static int stoneTarget(@NotNull ApertureStatus primary, @NotNull ApertureStatus secondary) {
        if (primary == ApertureStatus.NORMAL) return ApertureData.PRIMARY;
        if (secondary == ApertureStatus.NORMAL) return ApertureData.SECONDARY;
        return NO_TARGET;
    }
    /**
     * Maps the seam's semantic slots onto real list positions: a lone second aperture lives at
     * position 0, and a missing slot counts as DEAD -- a lost aperture is exactly what the
     * fall-through exists for.
     */
    private static int targetOf(Player player) {
        ApertureData data = ApertureService.get(player);
        int primary = data.firstIndex();
        int secondary = data.secondIndex();
        int target = stoneTarget(
                primary < 0 ? ApertureStatus.DEAD : ApertureService.status(player, primary),
                secondary < 0 ? ApertureStatus.DEAD : ApertureService.status(player, secondary));
        if (target == NO_TARGET) return NO_TARGET;
        return target == ApertureData.SECONDARY ? Math.max(secondary, 0) : Math.max(primary, 0);
    }
    @Override
    protected @Nullable Refusal payoutGate(Player player, ItemStack stack) {
        int target = targetOf(player);
        if (target == NO_TARGET) return new Refusal(FAILED_UNAVAILABLE);
        if (ApertureService.aperture(player, target).rank() != rank()) {
            return new Refusal(FAILED_RANK_MISMATCH, Component.translatable(rank().getTranslationKey()));
        }
        return ApertureService.aperture(player, target).stage() == Stage.HIGHEST
                ? new Refusal(FAILED_STAGE_PEAK) : null;
    }
    @Override
    protected void payout(ServerPlayer player, ItemStack stack) {
        int target = targetOf(player);
        if (target != NO_TARGET) ApertureNourishService.petrify(player, target);
    }
}
