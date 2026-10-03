package net.alex.guzhenren.item.gu.mortal;

import net.alex.guzhenren.gameplay.aperture.ApertureService;
import net.alex.guzhenren.gameplay.aperture.Stage;
import net.alex.guzhenren.item.gu.GuSpec;
import net.alex.guzhenren.item.gu.OneShotGuItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * A one-shot Relics Gu [舍利蛊] that advances the holder's stage [阶段], only at its own rank.
 *
 * <p>Extends {@link net.alex.guzhenren.item.gu.OneShotGuItem}. The gate refuses a rank mismatch and
 * a holder already at {@link Stage#HIGHEST}; the apply
 * delegates to {@link ApertureService#shiftStage}.
 *
 * @author Alex
 * @version 1.0.0
 * @see net.alex.guzhenren.item.gu.OneShotGuItem
 * @since 1.0.0
 */

public class RelicsGuItem extends OneShotGuItem {

    private static final String FAILED_RANK_MISMATCH = "guzhenren.item.failed.rank_mismatch";
    private static final String FAILED_STAGE_PEAK = "guzhenren.item.failed.stage_peak";

    public RelicsGuItem(Properties properties, GuSpec spec) {
        super(properties, spec);
    }

    @Override
    protected @Nullable Refusal useGate(Player player, ItemStack stack) {
        if (ApertureService.rank(player) != rank()) {
            return new Refusal(FAILED_RANK_MISMATCH, Component.translatable(rank().getTranslationKey()));
        }
        return ApertureService.stage(player) == Stage.HIGHEST ? new Refusal(FAILED_STAGE_PEAK) : null;
    }

    @Override
    protected int useApply(ServerPlayer player, ItemStack stack) {
        ApertureService.shiftStage(player, 1);
        return 1;
    }
}
