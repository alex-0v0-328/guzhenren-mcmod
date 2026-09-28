package net.alex.guzhenren.client.screen;

import net.alex.guzhenren.attachment.service.aperture.ApertureStorageService;
import net.alex.guzhenren.client.ModPalette;
import net.alex.guzhenren.menu.ApertureStorageMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * The screen for one aperture's [空窍] store, a page at a time.
 *
 * <p>Extends {@link net.minecraft.client.gui.screens.inventory.AbstractContainerScreen} for
 * {@link net.alex.guzhenren.menu.ApertureStorageMenu}. Draws 54 slots per page, prev/next pager
 * buttons, and the Vital Gu [本命蛊] slot past the right edge of the panel. A back button ({@code <-})
 * closes the container first, then opens the B panel. All drawing is {@code g.fill}, no textures.
 *
 * @author Alex
 * @version 1.0.0
 * @see net.alex.guzhenren.menu.ApertureStorageMenu
 * @since 1.0.0
 */

public class ApertureStorageScreen extends AbstractContainerScreen<ApertureStorageMenu> {

    private static final int PAGE_BUTTON_W = 16;
    private static final int PAGE_BUTTON_H = 14;
    private static final int PAGE_LABEL_W = 40;
    private static final int VITAL_LEFT = 178;
    private static final int VITAL_RIGHT = 210;
    private static final int VITAL_BOTTOM = 44;
    private static final String VITAL_KEY = "guzhenren.menu.vital";
    private static final int BACK_W = 16;
    private static final String BACK_GLYPH = "<-";
    private static final int TITLE_X_WITH_BACK = 26;
    private static final String LOAD_KEY = "guzhenren.menu.aperture_load";

    public ApertureStorageScreen(ApertureStorageMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 222;
        this.inventoryLabelY = this.imageHeight - 94;
        this.titleLabelX = TITLE_X_WITH_BACK;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        g.fill(x, y, x + imageWidth, y + imageHeight, ModPalette.PANEL_FILL);
        g.renderOutline(x, y, imageWidth, imageHeight, ModPalette.BORDER);
        g.fill(x + 7, y + 15, x + imageWidth - 7, y + 16, ModPalette.APERTURE);

        for (int row = 0; row < ApertureStorageMenu.ROWS; row++) {
            for (int col = 0; col < ApertureStorageMenu.COLS; col++) {
                int sx = x + ApertureStorageMenu.STORAGE_X + col * ApertureStorageMenu.SLOT;
                int sy = y + ApertureStorageMenu.STORAGE_Y + row * ApertureStorageMenu.SLOT;
                g.fill(sx, sy, sx + 16, sy + 16, ModPalette.SLOT_FILL);
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < ApertureStorageMenu.COLS; col++) {
                int sx = x + ApertureStorageMenu.STORAGE_X + col * ApertureStorageMenu.SLOT;
                int sy = y + ApertureStorageMenu.INVENTORY_Y + row * ApertureStorageMenu.SLOT;
                g.fill(sx, sy, sx + 16, sy + 16, ModPalette.SLOT_FILL);
            }
        }
        for (int col = 0; col < ApertureStorageMenu.COLS; col++) {
            int sx = x + ApertureStorageMenu.STORAGE_X + col * ApertureStorageMenu.SLOT;
            g.fill(sx, y + ApertureStorageMenu.HOTBAR_Y, sx + 16, y + ApertureStorageMenu.HOTBAR_Y + 16,
                    ModPalette.SLOT_FILL);
        }
        renderVital(g, x, y);
    }

    private void renderVital(GuiGraphics g, int x, int y) {
        g.fill(x + VITAL_LEFT, y, x + VITAL_RIGHT, y + VITAL_BOTTOM, ModPalette.PANEL_FILL);
        g.renderOutline(x + VITAL_LEFT, y, VITAL_RIGHT - VITAL_LEFT, VITAL_BOTTOM, ModPalette.BORDER);
        g.fill(x + VITAL_LEFT + 4, y + 15, x + VITAL_RIGHT - 4, y + 16, ModPalette.APERTURE);

        Component label = Component.translatable(VITAL_KEY);
        int width = VITAL_RIGHT - VITAL_LEFT;
        g.drawString(font, label, x + VITAL_LEFT + (width - font.width(label)) / 2, y + 5,
                ModPalette.APERTURE, false);
        g.fill(x + ApertureStorageMenu.VITAL_X, y + ApertureStorageMenu.VITAL_Y,
                x + ApertureStorageMenu.VITAL_X + 16, y + ApertureStorageMenu.VITAL_Y + 16,
                ModPalette.SLOT_FILL);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics g, int mouseX, int mouseY) {
        g.drawString(font, title, titleLabelX, titleLabelY, ModPalette.APERTURE, false);
        g.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, ModPalette.TEXT, false);
    }

    @Override
    public void render(@NotNull GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        renderPager(g, mouseX, mouseY);
        renderTooltip(g, mouseX, mouseY);
    }

    private void renderPager(GuiGraphics g, int mouseX, int mouseY) {
        renderBack(g, mouseX, mouseY);
        renderPageButton(g, mouseX, mouseY, prevX(), pagerBottomY(), menu.pageIndex() > 0, "<");
        renderPageButton(g, mouseX, mouseY, nextX(), pagerBottomY(), menu.pageIndex() + 1 < menu.pageCount(), ">");

        Component page = Component.literal((menu.pageIndex() + 1) + " / " + menu.pageCount());
        g.drawString(font, page, labelX() + (PAGE_LABEL_W - font.width(page)) / 2,
                pagerBottomY() + (PAGE_BUTTON_H - font.lineHeight) / 2 + 1, ModPalette.TEXT, false);

        Component load = Component.translatable(LOAD_KEY, menu.load(), ApertureStorageService.MAX_LOAD);
        g.drawString(font, load, leftPos + imageWidth - font.width(load), pagerBottomY() + 1,
                ModPalette.APERTURE, false);
    }

    private void renderPageButton(GuiGraphics g, int mouseX, int mouseY, int x, int y, boolean live, String glyph) {
        boolean hover = live && inButton(mouseX, mouseY, x, y);
        g.fill(x, y, x + PAGE_BUTTON_W, y + PAGE_BUTTON_H,
                live ? (hover ? ModPalette.BUTTON_HOVER : ModPalette.BUTTON_IDLE) : ModPalette.BUTTON_DEAD);
        g.drawString(font, glyph, x + (PAGE_BUTTON_W - font.width(glyph)) / 2,
                y + (PAGE_BUTTON_H - font.lineHeight) / 2 + 1, live ? ModPalette.TEXT : ModPalette.BUTTON_IDLE,
                false);
    }

    private void renderBack(GuiGraphics g, int mouseX, int mouseY) {
        int x = backX();
        int y = pagerY();
        boolean hover = inButton(mouseX, mouseY, x);
        g.fill(x, y, x + BACK_W, y + PAGE_BUTTON_H,
                hover ? ModPalette.BUTTON_HOVER : ModPalette.BUTTON_IDLE);
        g.drawString(font, BACK_GLYPH, x + (BACK_W - font.width(BACK_GLYPH)) / 2,
                y + (PAGE_BUTTON_H - font.lineHeight) / 2 + 1, ModPalette.TEXT, false);
    }

    private int pagerY() { return topPos + 3; }

    private int pagerBottomY() { return topPos + imageHeight + 4; }

    private int backX() { return leftPos + 7; }

    private int prevX() { return leftPos + 7; }

    private int labelX() { return prevX() + PAGE_BUTTON_W; }

    private int nextX() { return labelX() + PAGE_LABEL_W; }

    private boolean inButton(double mx, double my, int x) {
        return inButton(mx, my, x, pagerY());
    }

    private boolean inButton(double mx, double my, int x, int y) {
        return mx >= x && mx < x + PAGE_BUTTON_W && my >= y && my < y + PAGE_BUTTON_H;
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0) {
            if (inButton(mx, my, backX())) return clickBack();
            if (inButton(mx, my, prevX(), pagerBottomY())) return clickPage(ApertureStorageMenu.BUTTON_PREV);
            if (inButton(mx, my, nextX(), pagerBottomY())) return clickPage(ApertureStorageMenu.BUTTON_NEXT);
        }
        return super.mouseClicked(mx, my, button);
    }

    private boolean clickBack() {
        onClose();
        Minecraft.getInstance().setScreen(new PlayerInfoScreen());
        return true;
    }

    private boolean clickPage(int id) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameMode == null) return false;
        mc.gameMode.handleInventoryButtonClick(menu.containerId, id);
        return true;
    }
}
