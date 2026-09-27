package net.alex.guzhenren.attachment.service.aperture;

import java.util.ArrayList;
import java.util.List;
import net.alex.guzhenren.attachment.data.aperture.ApertureData;
import net.alex.guzhenren.item.gu.TendedGuItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * The day-rollover walk over Gu held inside apertures, both the stored ones and each Vital Gu [本命蛊].
 * Called from the heartbeat once a day with the elapsed day count; forwards to {@link
 * TendedGuItem#tickInContainer} per refined Gu and reports starvation through {@link TendedGuItem#starved}.
 *
 * <p>⚠ Every reader asks {@code refined()} first: an unrefined Gu's hunger is zero, and zero is also
 * what starvation looks like -- drop the test and the first rollover eats every wild Gu. ⚠ The Vital
 * slot write-back ({@code setVital}) runs EVEN WHEN nothing changed -- a single slot has no list for
 * the store loop's whole-list path to cover. ⚠ Imports {@code item/**} on purpose; do not "fix" it.
 *
 * @author Alex
 * @version 1.0.0
 * @see ApertureStorageService
 * @see TendedGuItem
 * @since 1.0.0
 */

public final class ApertureStorageTick {

    private ApertureStorageTick() {}
    public static void tickStored(@NotNull ServerPlayer player, long days) {
        for (int aperture = 0; aperture < ApertureData.MAX_APERTURES; aperture++) {
            tickStore(player, aperture, days);
            tickVital(player, aperture, days);
        }
    }
    private static void tickStore(ServerPlayer player, int aperture, long days) {
        List<ItemStack> items = ApertureStorageService.items(player, aperture);
        if (items.isEmpty()) return;

        List<ItemStack> next = new ArrayList<>(items);
        boolean changed = false;

        for (int i = 0; i < next.size(); i++) {
            ItemStack stack = next.get(i);
            if (!(stack.getItem() instanceof TendedGuItem gu) || !gu.refined(stack)) continue;

            ItemStack before = stack.copy();
            boolean starved = TendedGuItem.tickInContainer(player, stack, days);
            if (starved) {
                next.set(i, ItemStack.EMPTY);
                TendedGuItem.starved(player, stack);
            }
            changed |= starved || changed(before, stack);
        }
        if (changed) ApertureStorageService.set(player, aperture, next);
    }
    private static void tickVital(ServerPlayer player, int aperture, long days) {
        ItemStack stack = ApertureStorageService.vital(player, aperture);
        if (!(stack.getItem() instanceof TendedGuItem)) return;

        ItemStack before = stack.copy();
        if (TendedGuItem.tickInContainer(player, stack, days)) {
            ApertureStorageService.setVital(player, aperture, ItemStack.EMPTY);
            TendedGuItem.starved(player, stack);
            return;
        }
        if (changed(before, stack)) ApertureStorageService.setVital(player, aperture, stack);
    }
    private static boolean changed(ItemStack before, ItemStack after) {
        return before.getCount() != after.getCount()
                || !ItemStack.isSameItemSameComponents(before, after);
    }
}
