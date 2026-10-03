package net.alex.guzhenren.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.List;
import net.alex.guzhenren.client.ModPalette;
import net.alex.guzhenren.gameplay.aperture.ApertureEssenceService;
import net.alex.guzhenren.gameplay.refinement.GuRecipe;
import net.alex.guzhenren.gameplay.refinement.GuRecipeInput;
import net.alex.guzhenren.gameplay.refinement.RefinementMenu;
import net.alex.guzhenren.gameplay.soul.SoulData;
import net.alex.guzhenren.gameplay.soul.SoulService;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The refinement [炼蛊] screen: the ring grid, the phase bar, and the recipe picker.
 *
 * <p>Extends {@link net.minecraft.client.gui.screens.inventory.AbstractContainerScreen} for
 * {@link RefinementMenu}. Draws the 5×5 grid (corners cut), the craft
 * button in three states, the phase bar, the stone slot, and the recipe picker modal. Ghosts for
 * missing ingredients are drawn from {@code renderLabels} so the carried item paints last. The picker
 * modal translates Z to 500 to stay above slot items. In {@link #mouseClicked(double, double, int)},
 * the open picker swallows every click; only the left button acts on it.
 *
 * <p>⚠ Every cell position comes from the menu's own helpers, so a drawn cell and the real slot
 * cannot drift apart. Two different pitches are in play; do not reuse the inventory's for the grid.
 *
 * @author Alex
 * @version 1.0.0
 * @since 1.0.0
 */

public class RefinementScreen extends AbstractContainerScreen<RefinementMenu> {

    private static final int SLOT = RefinementMenu.SLOT;
    private static final int GRID_SLOT = RefinementMenu.GRID_SLOT;
    private static final int CELL = 16;
    private static final int INVENTORY_COLS = 9;
    private static final int CRAFT_X = 140;
    private static final int CRAFT_Y = 76;
    private static final int CRAFT_W = 52;
    private static final int CRAFT_H = 20;
    private static final int BAR_X = 140;
    private static final int BAR_Y = 102;
    private static final int BAR_W = 52;
    private static final int BAR_H = 6;
    private static final int RECIPE_X = 140;
    private static final int RECIPE_Y = 114;
    private static final int RECIPE_W = 52;
    private static final int RECIPE_H = 20;
    private static final int LEGEND_Y = 142;
    private static final int CORE_FILL = 0x4DFFFFFF;
    private static final int LEGEND_TEXT = 0xFFA0A0A0;
    private static final int SHORT_RED = 0x99FF5555;
    private static final int TRACK = 0x33000000;
    private static final int BAR_WINDOW = 0xFF81C784;
    private static final int BAR_GAP = 0x6681C784;
    private static final int BAR_SHORT = 0xFFFF5555;
    private static final int PICK_FILL = 0xF0000000;
    private static final int GHOST_OVERLAY = 0x1AFFFFFF;
    private static final float GHOST_ALPHA = 0.35F;
    private static final int POOL_X = 18;
    private static final int POOL_W = 230;
    private static final int POOL_H = 5;
    private static final int POOL_Y = 156;
    private static final int POOL_STRIDE = 19;
    private static final String CRAFT_KEY = "guzhenren.menu.refinement.craft";
    private static final String STOP_KEY = "guzhenren.menu.refinement.stop";
    private static final String POOL_KEY = "guzhenren.menu.refinement.pool";
    private static final String LEGEND_KEY = "guzhenren.menu.refinement.rings";
    private static final String WINDOW_KEY = "guzhenren.menu.refinement.window";
    private static final String GAP_KEY = "guzhenren.menu.refinement.gap";
    private static final String RECIPE_KEY = "guzhenren.menu.refinement.recipes";
    private static final String SELECTED_KEY = "guzhenren.menu.refinement.selected";
    private static final String EXTRA_KEY = "guzhenren.menu.refinement.extra";
    private static final String PICK_TITLE_KEY = "guzhenren.menu.refinement.pick.title";
    private static final String PICK_AUTO_KEY = "guzhenren.menu.refinement.pick.auto";
    private static final String PICK_EMPTY_KEY = "guzhenren.menu.refinement.pick.empty";
    private static final String PICK_NEEDS_KEY = "guzhenren.menu.refinement.pick.needs";
    private static final String PICK_ITEM_KEY = "guzhenren.menu.refinement.pick.item";
    private static final String PICK_WINDOWS_KEY = "guzhenren.menu.refinement.pick.windows";
    private static final String PICK_STONES_KEY = "guzhenren.menu.refinement.pick.stones";
    private static final String PICK_COST_KEY = "guzhenren.menu.refinement.pick.cost";
    private static final String PICK_SOUL_KEY = "guzhenren.menu.refinement.pick.soul";
    private static final String PICK_CHANCE_KEY = "guzhenren.menu.refinement.pick.chance";
    private static final String PICK_SUCCESS_KEY = "guzhenren.menu.refinement.pick.success";
    private static final int PICK_W = 200;
    private static final int PICK_PAD = 6;
    private static final int PICK_HEADER_H = 14;
    private static final int PICK_ROW_H = 20;
    private static final int PICK_MAX_ROWS = 5;
    private static final float PICK_Z = 500.0F;
    private static final String STONE_SEPARATOR = " · ";
    private static final int BACK_W = 16;
    private static final int BACK_H = 14;
    private static final String BACK_GLYPH = "<-";
    private static final int TITLE_X_WITH_BACK = 32;
    private static final int MARGIN = 18;
    private static final int HEADER_H = 20;
    private boolean picking;
    private int pickScroll;

    public RefinementScreen(RefinementMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 266;
        this.imageHeight = 321;
        this.inventoryLabelY = RefinementMenu.INVENTORY_Y - 11;
        this.inventoryLabelX = RefinementMenu.INVENTORY_X;
        this.titleLabelX = TITLE_X_WITH_BACK;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;
        g.fill(x, y, x + imageWidth, y + imageHeight, ModPalette.PANEL_FILL);
        g.renderOutline(x, y, imageWidth, imageHeight, ModPalette.BORDER);
        g.fill(x + MARGIN, y + HEADER_H, x + imageWidth - MARGIN, y + HEADER_H + 1, ModPalette.REFINEMENT);

        drawInput(g, x, y);
        drawCell(g, x + RefinementMenu.STONE_X, y + RefinementMenu.STONE_Y, ModPalette.SLOT_FILL);
        drawCells(g, x + RefinementMenu.OUTPUT_X, y + RefinementMenu.OUTPUT_Y,
                RefinementMenu.OUTPUT_COLS, RefinementMenu.OUTPUT_ROWS, GRID_SLOT, ModPalette.SLOT_FILL);
        drawCells(g, x + RefinementMenu.INVENTORY_X, y + RefinementMenu.INVENTORY_Y,
                INVENTORY_COLS, 3, SLOT, ModPalette.SLOT_FILL);
        drawCells(g, x + RefinementMenu.INVENTORY_X, y + RefinementMenu.HOTBAR_Y,
                INVENTORY_COLS, 1, SLOT, ModPalette.SLOT_FILL);
        drawBar(g, x, y);
    }

    //region the two rings -- the 内圈 is marked by a brighter cell and an accent frame around the block
    private void drawInput(GuiGraphics g, int x, int y) {
        for (int i = 0; i < RefinementMenu.RING_SIZE; i++) {
            drawCell(g, x + RefinementMenu.ringX(i), y + RefinementMenu.ringY(i), ModPalette.SLOT_FILL);
        }
        drawCells(g, x + RefinementMenu.coreX(0), y + RefinementMenu.coreY(0),
                RefinementMenu.CORE_COLS, RefinementMenu.CORE_ROWS, GRID_SLOT, CORE_FILL);
        g.renderOutline(x + RefinementMenu.coreX(0) - 3, y + RefinementMenu.coreY(0) - 3,
                (RefinementMenu.CORE_COLS - 1) * GRID_SLOT + CELL + 6,
                (RefinementMenu.CORE_ROWS - 1) * GRID_SLOT + CELL + 6, ModPalette.REFINEMENT);
    }

    private void drawCells(GuiGraphics g, int x, int y, int cols, int rows, int pitch, int fill) {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) drawCell(g, x + col * pitch, y + row * pitch, fill);
        }
    }

    private void drawCell(GuiGraphics g, int x, int y, int fill) { g.fill(x, y, x + CELL, y + CELL, fill); }
    //endregion

    //region the phase bar -- it empties over the 5s window, then over the 2s gap
    private void drawBar(GuiGraphics g, int x, int y) {
        int bx = x + BAR_X;
        int by = y + BAR_Y;
        g.fill(bx, by, bx + BAR_W, by + BAR_H, TRACK);
        if (!menu.running()) return;

        int span = menu.inWindow() ? GuRecipe.WINDOW_TICKS : GuRecipe.GAP_TICKS;
        int filled = BAR_W * menu.phaseLeft() / span;
        g.fill(bx, by, bx + filled, by + BAR_H, barColour());
    }

    private int barColour() {
        if (!menu.inWindow()) return BAR_GAP;
        return menu.stonesIn() < menu.stonesNeeded() ? BAR_SHORT : BAR_WINDOW;
    }
    //endregion

    @Override
    protected void renderLabels(@NotNull GuiGraphics g, int mouseX, int mouseY) {
        g.drawString(font, title, titleLabelX, titleLabelY, ModPalette.REFINEMENT, false);
        g.drawString(font, statusLine(), RefinementMenu.INPUT_X, LEGEND_Y,
                statusColour(), false);
        g.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, ModPalette.TEXT, false);
        renderGhosts(g);
        renderPools(g);
    }

    //region the pools -- all three ride the synced attachments, so none of them needs a packet
    private void renderPools(GuiGraphics g) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        long maxEssence = ApertureEssenceService.maxEssence(player);
        int unit = 0;
        drawPool(g, unit++, ApertureEssenceService.currentEssence(player), maxEssence, ModPalette.APERTURE);

        long distilled = ApertureEssenceService.distilledEssence(player);
        if (distilled > 0L) drawPool(g, unit++, distilled, maxEssence, ModPalette.DISTILLED_FILL);

        SoulData soul = SoulService.get(player);
        drawPool(g, unit, soul.currentSoul(), soul.maxSoul(), ModPalette.SOUL);
    }

    private void drawPool(GuiGraphics g, int unit, long value, long max, int fill) {
        int y = POOL_Y + unit * POOL_STRIDE;
        Component reading = Component.translatable(POOL_KEY, value, max);
        g.drawString(font, reading, POOL_X + POOL_W - font.width(reading), y, LEGEND_TEXT, false);

        int barY = y + font.lineHeight;
        g.fill(POOL_X, barY, POOL_X + POOL_W, barY + POOL_H, TRACK);
        if (max <= 0L || value <= 0L) return;

        int filled = (int) Math.min(POOL_W, POOL_W * value / max);
        g.fill(POOL_X, barY, POOL_X + filled, barY + POOL_H, fill);
    }
    //endregion

    //region the status line -- the ring legend until a 蛊方 is picked, then what that pick still wants
    private Component statusLine() {
        if (menu.running()) {
            int shown = menu.stage() + 1;
            int seconds = (menu.phaseLeft() + 19) / 20;
            return menu.inWindow()
                    ? Component.translatable(WINDOW_KEY, shown, menu.stages(), seconds,
                    menu.stonesIn(), menu.stonesNeeded())
                    : Component.translatable(GAP_KEY, shown, menu.stages());
        }
        GuRecipe recipe = selectedRecipe();
        if (recipe == null) return Component.translatable(LEGEND_KEY);
        if (crowded(recipe)) return Component.translatable(EXTRA_KEY);
        return Component.translatable(SELECTED_KEY, resultName(recipe));
    }

    private int statusColour() {
        if (menu.running()) return ModPalette.TEXT;

        GuRecipe recipe = selectedRecipe();
        if (recipe == null) return LEGEND_TEXT;
        return crowded(recipe) ? BAR_SHORT : ModPalette.TEXT;
    }

    private boolean crowded(GuRecipe recipe) {
        if (menu.ready()) return false;

        for (int missing : recipe.shortfall(menu.grid())) {
            if (missing > 0) return false;
        }
        return true;
    }
    //endregion

    @Override
    public void render(@NotNull GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        knownCache = null;
        super.render(g, mouseX, mouseY, partialTick);
        renderCraft(g, mouseX, mouseY);
        renderRecipe(g, mouseX, mouseY);
        renderBack(g, mouseX, mouseY);
        if (picking) {
            renderPicker(g, mouseX, mouseY);
            return;
        }
        renderTooltip(g, mouseX, mouseY);
    }

    //region the cells a picked 蛊方 still wants -- drawn from renderLabels, so the carried item stays on top
    private void renderGhosts(GuiGraphics g) {
        GuRecipe recipe = selectedRecipe();
        if (recipe == null || menu.running()) return;

        GuRecipeInput grid = menu.grid();
        int[] missing = recipe.shortfall(grid);

        for (int n = 0; n < missing.length; n++) {
            int slot = recipe.slots().get(n);
            if (missing[n] <= 0 || slot < 0 || slot >= grid.size()) continue;
            if (!grid.getItem(slot).isEmpty()) continue;

            ItemStack shown = option(recipe.ingredients().get(n));
            if (!shown.isEmpty()) drawGhost(g, shown, missing[n], slot);
        }
    }

    private void drawGhost(GuiGraphics g, ItemStack shown, int count, int slot) {
        int x = slotX(slot);
        int y = slotY(slot);

        g.setColor(1.0F, 1.0F, 1.0F, GHOST_ALPHA);
        g.renderFakeItem(shown, x, y);
        g.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        g.fill(RenderType.guiGhostRecipeOverlay(), x, y, x + CELL, y + CELL, GHOST_OVERLAY);
        g.renderItemDecorations(font, shown, x, y, count > 1 ? String.valueOf(count) : null);
    }

    private static int slotX(int slot) {
        return slot < RefinementMenu.RING_SIZE ? RefinementMenu.ringX(slot)
                : RefinementMenu.coreX((slot - RefinementMenu.RING_SIZE) % RefinementMenu.CORE_COLS);
    }

    private static int slotY(int slot) {
        return slot < RefinementMenu.RING_SIZE ? RefinementMenu.ringY(slot)
                : RefinementMenu.coreY((slot - RefinementMenu.RING_SIZE) / RefinementMenu.CORE_COLS);
    }

    private static ItemStack option(SizedIngredient need) {
        ItemStack[] options = need.ingredient().getItems();
        if (options.length == 0) return ItemStack.EMPTY;
        return options[(int) (Util.getMillis() / 1000L % options.length)];
    }
    //endregion

    //region the 炼制 button -- three states off the ritual, and 停止 while it runs
    private void renderCraft(GuiGraphics g, int mouseX, int mouseY) {
        int x = craftX();
        int y = craftY();
        boolean stopping = menu.running();
        boolean live = stopping || (clickable() && menu.affords());
        boolean shortOfEssence = !stopping && clickable() && !menu.affords();
        boolean hover = live && inCraft(mouseX, mouseY);

        g.fill(x, y, x + CRAFT_W, y + CRAFT_H,
                live ? (hover ? ModPalette.BUTTON_HOVER : ModPalette.BUTTON_IDLE) : ModPalette.BUTTON_DEAD);
        if (live || shortOfEssence) {
            g.renderOutline(x, y, CRAFT_W, CRAFT_H,
                    stopping ? BAR_SHORT : live ? ModPalette.REFINEMENT : SHORT_RED);
        }

        Component label = Component.translatable(stopping ? STOP_KEY : CRAFT_KEY);
        g.drawString(font, label, x + (CRAFT_W - font.width(label)) / 2,
                y + (CRAFT_H - font.lineHeight) / 2 + 1, live ? ModPalette.TEXT : ModPalette.BUTTON_IDLE, false);
    }

    private boolean clickable() { return menu.ready() && !menu.running(); }

    private int craftX() { return leftPos + CRAFT_X; }

    private int craftY() { return topPos + CRAFT_Y; }

    private boolean inCraft(double mx, double my) {
        return mx >= craftX() && mx < craftX() + CRAFT_W
                && my >= craftY() && my < craftY() + CRAFT_H;
    }
    //endregion

    //region the 蛊方 button -- dead while the ritual runs, because the grid is locked anyway
    private void renderRecipe(GuiGraphics g, int mouseX, int mouseY) {
        int x = recipeX();
        int y = recipeY();
        boolean live = !menu.running();
        boolean hover = live && inRecipe(mouseX, mouseY);

        g.fill(x, y, x + RECIPE_W, y + RECIPE_H,
                live ? (hover ? ModPalette.BUTTON_HOVER : ModPalette.BUTTON_IDLE) : ModPalette.BUTTON_DEAD);
        if (live) g.renderOutline(x, y, RECIPE_W, RECIPE_H, ModPalette.REFINEMENT);

        Component label = Component.translatable(RECIPE_KEY);
        g.drawString(font, label, x + (RECIPE_W - font.width(label)) / 2,
                y + (RECIPE_H - font.lineHeight) / 2 + 1, live ? ModPalette.TEXT : ModPalette.BUTTON_IDLE, false);
    }

    private int recipeX() { return leftPos + RECIPE_X; }

    private int recipeY() { return topPos + RECIPE_Y; }

    private boolean inRecipe(double mx, double my) {
        return mx >= recipeX() && mx < recipeX() + RECIPE_W
                && my >= recipeY() && my < recipeY() + RECIPE_H;
    }
    //endregion

    //region the picker -- rows windowed whole like the info panel's modal; PICK_Z clears the item layers
    private void renderPicker(GuiGraphics g, int mouseX, int mouseY) {
        g.pose().pushPose();
        g.pose().translate(0.0F, 0.0F, PICK_Z);
        drawPicker(g, mouseX, mouseY);
        g.pose().popPose();
    }

    private void drawPicker(GuiGraphics g, int mouseX, int mouseY) {
        List<RecipeHolder<GuRecipe>> known = known();
        int rows = known.size() + 1;
        int visible = Math.min(PICK_MAX_ROWS, rows);
        pickScroll = Mth.clamp(pickScroll, 0, rows - visible);

        int x0 = pickLeft();
        int y0 = pickTop(visible);
        int h = pickHeight(visible);

        g.fill(x0, y0, x0 + PICK_W, y0 + h, PICK_FILL);
        g.renderOutline(x0, y0, PICK_W, h, ModPalette.BORDER);
        g.drawString(font, Component.translatable(PICK_TITLE_KEY), x0 + PICK_PAD,
                y0 + (PICK_HEADER_H - font.lineHeight) / 2, ModPalette.REFINEMENT, false);
        g.fill(x0 + PICK_PAD, y0 + PICK_HEADER_H, x0 + PICK_W - PICK_PAD, y0 + PICK_HEADER_H + 1,
                ModPalette.BORDER);

        int hovered = -1;
        for (int i = 0; i < visible; i++) {
            int row = pickScroll + i;
            int ry = pickRowY(y0, i);
            if (mouseX >= x0 && mouseX < x0 + PICK_W && mouseY >= ry && mouseY < ry + PICK_ROW_H) {
                g.fill(x0 + 1, ry, x0 + PICK_W - 1, ry + PICK_ROW_H, ModPalette.BUTTON_IDLE);
                hovered = row;
            }
            if (row - 1 == menu.selected()) g.renderOutline(x0 + 1, ry, PICK_W - 2, PICK_ROW_H,
                    ModPalette.REFINEMENT);
            drawPickRow(g, known, row, x0, ry);
        }
        if (hovered >= 1) g.renderComponentTooltip(font, details(known.get(hovered - 1).value()), mouseX, mouseY);
    }

    private void drawPickRow(GuiGraphics g, List<RecipeHolder<GuRecipe>> known, int row, int x0, int y) {
        int textY = y + (PICK_ROW_H - font.lineHeight) / 2;
        if (row == 0) {
            Component auto = Component.translatable(known.isEmpty() ? PICK_EMPTY_KEY : PICK_AUTO_KEY);
            g.drawString(font, auto, x0 + PICK_PAD, textY, ModPalette.TEXT, false);
            return;
        }
        GuRecipe recipe = known.get(row - 1).value();
        ItemStack icon = result(recipe);
        g.renderFakeItem(icon, x0 + PICK_PAD, y + (PICK_ROW_H - CELL) / 2);
        g.drawString(font, icon.getHoverName(), x0 + PICK_PAD + CELL + 4, textY, ModPalette.TEXT, false);

        Component rate = Component.translatable(PICK_CHANCE_KEY, recipe.baseSuccess());
        g.drawString(font, rate, x0 + PICK_W - PICK_PAD - font.width(rate), textY, LEGEND_TEXT, false);
    }

    private List<Component> details(GuRecipe recipe) {
        List<Component> lines = new ArrayList<>();
        lines.add(resultName(recipe));
        lines.add(Component.translatable(PICK_NEEDS_KEY));
        for (SizedIngredient need : recipe.ingredients()) {
            lines.add(Component.translatable(PICK_ITEM_KEY, need.count(), option(need).getHoverName()));
        }
        lines.add(Component.translatable(PICK_WINDOWS_KEY, recipe.windowCount(), recipe.totalSeconds()));
        lines.add(Component.translatable(PICK_STONES_KEY, stoneList(recipe)));
        lines.add(Component.translatable(PICK_COST_KEY, recipe.essencePerSecond()));
        lines.add(Component.translatable(PICK_SOUL_KEY, recipe.soulPerSecond()));
        lines.add(Component.translatable(PICK_SUCCESS_KEY, recipe.baseSuccess()));
        return lines;
    }

    private static String stoneList(GuRecipe recipe) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < recipe.windowCount(); i++) {
            if (i > 0) text.append(STONE_SEPARATOR);
            text.append(recipe.stonesFor(i));
        }
        return text.toString();
    }

    private int pickHeight(int visible) { return PICK_HEADER_H + PICK_PAD * 2 + visible * PICK_ROW_H; }

    private int pickLeft() { return leftPos + (imageWidth - PICK_W) / 2; }

    private int pickTop(int visible) { return topPos + (imageHeight - pickHeight(visible)) / 2; }

    private int pickRowY(int y0, int i) { return y0 + PICK_HEADER_H + PICK_PAD + i * PICK_ROW_H; }

    private void clickPicker(double mx, double my) {
        List<RecipeHolder<GuRecipe>> known = known();
        int visible = Math.min(PICK_MAX_ROWS, known.size() + 1);
        int x0 = pickLeft();
        int y0 = pickTop(visible);

        for (int i = 0; i < visible; i++) {
            int ry = pickRowY(y0, i);
            if (mx < x0 || mx >= x0 + PICK_W || my < ry || my >= ry + PICK_ROW_H) continue;

            int row = pickScroll + i;
            send(row == 0 ? RefinementMenu.BUTTON_CLEAR_RECIPE
                    : RefinementMenu.BUTTON_RECIPE_BASE + row - 1);
            break;
        }
        picking = false;
    }
    //endregion

    //region the 蛊方 a player may attempt -- the client holds the whole synced table, so it needs no packet
    private @Nullable List<RecipeHolder<GuRecipe>> knownCache;

    private List<RecipeHolder<GuRecipe>> known() {
        if (knownCache == null) {
            ClientLevel level = Minecraft.getInstance().level;
            knownCache = level == null ? List.of() : GuRecipe.known(level.getRecipeManager());
        }
        return knownCache;
    }

    private @Nullable GuRecipe selectedRecipe() {
        int index = menu.selected();
        List<RecipeHolder<GuRecipe>> known = known();
        return index >= 0 && index < known.size() ? known.get(index).value() : null;
    }

    private static ItemStack result(GuRecipe recipe) {
        return recipe.results().isEmpty() ? ItemStack.EMPTY : recipe.results().getFirst();
    }

    private static Component resultName(GuRecipe recipe) { return result(recipe).getHoverName(); }
    //endregion

    private void renderBack(GuiGraphics g, int mouseX, int mouseY) {
        int x = backX();
        int y = backY();
        boolean hover = inBack(mouseX, mouseY);
        g.fill(x, y, x + BACK_W, y + BACK_H, hover ? ModPalette.BUTTON_HOVER : ModPalette.BUTTON_IDLE);
        g.drawString(font, BACK_GLYPH, x + (BACK_W - font.width(BACK_GLYPH)) / 2,
                y + (BACK_H - font.lineHeight) / 2 + 1, ModPalette.TEXT, false);
    }

    private int backX() { return leftPos + 11; }

    private int backY() { return topPos + 4; }

    private boolean inBack(double mx, double my) {
        return mx >= backX() && mx < backX() + BACK_W && my >= backY() && my < backY() + BACK_H;
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (picking) {
            if (button == 0) clickPicker(mx, my);
            return true;
        }
        if (button == 0) {
            if (inBack(mx, my)) return clickBack();
            if (inRecipe(mx, my) && !menu.running()) return openPicker();
            if (inCraft(mx, my) && menu.running()) return send(RefinementMenu.BUTTON_STOP);
            if (inCraft(mx, my) && clickable()) return send(RefinementMenu.BUTTON_CRAFT);
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double dx, double dy) {
        if (!picking) return super.mouseScrolled(mx, my, dx, dy);
        if (dy != 0.0) pickScroll = Math.max(0, pickScroll - (int) Math.signum(dy));
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!picking) return super.keyPressed(keyCode, scanCode, modifiers);

        if (keyCode == InputConstants.KEY_ESCAPE
                || Minecraft.getInstance().options.keyInventory.matches(keyCode, scanCode)) {
            picking = false;
        }
        return true;
    }

    private boolean openPicker() {
        picking = true;
        pickScroll = 0;
        return true;
    }

    private boolean clickBack() {
        onClose();
        Minecraft.getInstance().setScreen(new PlayerInfoScreen());
        return true;
    }

    private boolean send(int button) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameMode == null) return false;
        mc.gameMode.handleInventoryButtonClick(menu.containerId, button);
        return true;
    }
}
