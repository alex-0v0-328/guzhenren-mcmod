package net.alex.guzhenren.client.hud;

import net.alex.guzhenren.item.GuItem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * The bar drawn over the hotbar while a charged Gu use is running.
 *
 * <p>Extends {@link net.alex.guzhenren.client.hud.HotbarHud}. Registered above
 * {@code VanillaGuiLayers.AIR_LEVEL}. Reads the held item's {@code getUseDuration} and
 * {@code chargeFraction} from {@link net.alex.guzhenren.item.GuItem}; if the item provides its own
 * fraction the bar reads that, otherwise it falls back to the vanilla remaining-ticks ratio.
 *
 * <p>⚠ Any Gu whose use has a duration gets this for free, so adding a charged Gu needs no change here.
 * Whenever a leaf class's caption reads {@code a / b}, that class owes a {@code chargeFraction} over
 * the same pair.
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

public final class ChargeHud extends HotbarHud {

    public static final ChargeHud INSTANCE = new ChargeHud();

    private ChargeHud() {}

    @Override
    public void render(@NotNull GuiGraphics graphics, @NotNull DeltaTracker delta) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.options.hideGui || player.isSpectator()) return;
        if (!player.isUsingItem()) return;

        ItemStack stack = player.getUseItem();
        if (!(stack.getItem() instanceof GuItem gu)) return;

        int total = stack.getUseDuration(player);
        if (total <= 0) return;

        int remaining = player.getUseItemRemainingTicks();
        Float own = gu.chargeFraction(stack, remaining);
        float progress = own != null ? own : 1.0F - remaining / (float) total;

        int x = (minecraft.getWindow().getGuiScaledWidth() - BAR_WIDTH) / 2;
        int y = barTop(minecraft);

        drawBar(graphics, x, y, progress, gu.chargeColor(stack, remaining));

        Component label = gu.chargeCaption(stack, remaining);
        if (label == null) return;

        drawLabel(graphics, minecraft, x, y, label);
    }
}
