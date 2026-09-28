package net.alex.guzhenren.client.hud;

import net.alex.guzhenren.client.ModPalette;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;

/**
 * Shared layout for the bars drawn over the hotbar: geometry, the baseline that armor and mounts move,
 * and the three-fill draw every such bar reuses.
 *
 * <p>Package-private abstract base for {@link net.alex.guzhenren.client.hud.ChargeHud} and
 * {@link net.alex.guzhenren.client.hud.NourishHud}. The {@code barTop} method rides vanilla's
 * held-item-name baseline so armor and a mount's health push the bar up instead of hiding it. All
 * drawing is {@code g.fill}, no textures.
 *
 * @author Alex
 * @version 1.0.0
 * @see net.alex.guzhenren.client.hud.ChargeHud
 * @see net.alex.guzhenren.client.hud.NourishHud
 * @since 1.0.0
 */

abstract class HotbarHud implements LayeredDraw.Layer {

    static final int BAR_WIDTH = 182;
    static final int BAR_HEIGHT = 5;
    static final int TEXT_GAP = 3;
    static final int NAME_GAP = 4;
    static final int MIN_SHIFT = 59;
    static final int CREATIVE_LIFT = 14;

    static int barTop(Minecraft minecraft) {
        Gui gui = minecraft.gui;
        int shift = Math.max(Math.max(gui.leftHeight, gui.rightHeight), MIN_SHIFT);
        int baseline = minecraft.getWindow().getGuiScaledHeight() - shift;
        if (minecraft.gameMode != null && !minecraft.gameMode.canHurtPlayer()) baseline += CREATIVE_LIFT;
        return baseline - NAME_GAP - BAR_HEIGHT;
    }

    static void drawBar(GuiGraphics g, int x, int y, float fraction, int fill) {
        g.fill(x - 1, y - 1, x + BAR_WIDTH + 1, y + BAR_HEIGHT + 1, ModPalette.BAR_BORDER);
        g.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, ModPalette.BAR_TRACK);
        g.fill(x, y, x + Math.round(BAR_WIDTH * Math.clamp(fraction, 0.0F, 1.0F)), y + BAR_HEIGHT, fill);
    }

    static void drawLabel(GuiGraphics g, Minecraft mc, int x, int y, Component label) {
        g.drawString(mc.font, label, x + (BAR_WIDTH - mc.font.width(label)) / 2,
                y - TEXT_GAP - mc.font.lineHeight, ModPalette.TEXT, true);
    }
}
