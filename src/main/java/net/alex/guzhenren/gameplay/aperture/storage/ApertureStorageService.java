package net.alex.guzhenren.gameplay.aperture.storage;

import java.util.List;

import net.alex.guzhenren.gameplay.aperture.ApertureData;
import net.alex.guzhenren.gameplay.aperture.ApertureService;
import net.alex.guzhenren.gameplay.aperture.Rank;
import net.alex.guzhenren.gameplay.body.BodyHealthService;
import net.alex.guzhenren.item.GuItem;
import net.alex.guzhenren.item.gu.MortalGuItem;
import net.alex.guzhenren.registry.attachment.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * The only writer of what an Aperture [空窍] holds, including the Vital Gu [本命蛊] bound to each.
 * {@code setVital} also rewrites the aperture's primary path via {@link ApertureService#setPrimaryPath}
 * -- binding a Gu IS what sets primary path [主修] (the store is not synced; the aperture is).
 *
 * <p>⚠ Reaches into {@code item/} on purpose ({@link GuItem}) -- binding a Vital Gu reads that Gu's
 * declared path; do not "fix" those imports. ⚠ Writes NEVER go through {@code ApertureService.store}:
 * {@link BodyHealthService#refresh} hangs off that. ⚠
 * {@code setPrimaryPath} no-ops when unchanged; keep the call, or a rebind loses the path.
 *
 * <p>{@link #shiftForFirstAperture} is the storage-side twin of
 * {@link ApertureData#insertFirst}: when Hope Gu opens
 * the first aperture ahead of a lone second one, every stored list and the Vital Gu slot slides up
 * one position.
 *
 * @author Alex
 * @version 1.0.0
 * @see ApertureService
 * @see ApertureStorageTick
 * @since 1.0.0
 */

public final class ApertureStorageService {

    private ApertureStorageService() {}

    public static final int MAX_LOAD = 256;

    public static @NotNull ApertureStorage get(@NotNull Player p) { return p.getData(ModAttachments.APERTURE_STORAGE); }

    public static @NotNull List<ItemStack> items(@NotNull Player p, int aperture) { return get(p).get(aperture); }

    public static @NotNull List<ItemStack> page(@NotNull Player p, int aperture, int from, int size) {
        return get(p).page(aperture, from, size);
    }

    public static boolean pageMatches(@NotNull Player p, int aperture, int from, @NotNull List<ItemStack> page) {
        return get(p).matchesPage(aperture, from, page);
    }

    public static int count(@NotNull Player p, int aperture) { return get(p).count(aperture); }

    public static @NotNull ItemStack vital(@NotNull Player p, int aperture) { return get(p).getVital(aperture); }

    public static int load(@NotNull Player p, int aperture) { return load(p, get(p), aperture); }

    public static int maxStackSize(@NotNull Player p, int aperture, int currentLoad,
            @NotNull ItemStack current, @NotNull ItemStack incoming) {
        if (!(incoming.getItem() instanceof MortalGuItem gu)) return 0;

        Rank holder = ApertureService.aperture(p, aperture).rank();
        int limit = Math.max(MAX_LOAD, currentLoad);
        int existingCount = 0;
        if (!current.isEmpty()) {
            if (ItemStack.isSameItemSameComponents(current, incoming)) existingCount = current.getCount();
            else currentLoad -= cost(holder, current);
        }
        int freeLoad = Math.max(0, limit - currentLoad);
        return Math.min(incoming.getMaxStackSize(), existingCount + freeLoad / costPerItem(holder, gu));
    }

    public static void set(@NotNull ServerPlayer p, int aperture, @NotNull List<ItemStack> items) {
        ApertureStorage current = get(p);
        ApertureStorage next = current.with(aperture, items);
        if (exceedsLoad(load(p, current, aperture), load(p, next, aperture))) return;

        p.setData(ModAttachments.APERTURE_STORAGE, next);
    }

    public static boolean setVital(@NotNull ServerPlayer p, int aperture, @NotNull ItemStack stack) {
        ApertureStorage current = get(p);
        ApertureStorage next = current.withVital(aperture, stack);
        if (exceedsLoad(load(p, current, aperture), load(p, next, aperture))) return false;

        p.setData(ModAttachments.APERTURE_STORAGE, next);
        if (stack.getItem() instanceof GuItem gu) ApertureService.setPrimaryPath(p, aperture, gu.path());
        return true;
    }

    public static boolean setPage(@NotNull ServerPlayer p, int aperture, int from, @NotNull List<ItemStack> page) {
        ApertureStorage current = get(p);
        ApertureStorage next = current.withPage(aperture, from, page);
        if (exceedsLoad(load(p, current, aperture), load(p, next, aperture))) return false;

        p.setData(ModAttachments.APERTURE_STORAGE, next);
        return true;
    }

    private static int load(Player p, ApertureStorage storage, int aperture) {
        Rank holder = ApertureService.aperture(p, aperture).rank();
        int total = 0;
        if (aperture >= 0 && aperture < storage.byAperture().size()) {
            total += load(holder, storage.byAperture().get(aperture));
        }
        if (aperture >= 0 && aperture < storage.vital().size()) {
            total += cost(holder, storage.vital().get(aperture));
        }
        return total;
    }

    public static void shiftForFirstAperture(@NotNull ServerPlayer p) {
        p.setData(ModAttachments.APERTURE_STORAGE, get(p).shiftRight());
    }

    private static int load(Rank holder, List<ItemStack> stacks) {
        int total = 0;
        for (ItemStack stack : stacks) total += cost(holder, stack);
        return total;
    }

    private static boolean exceedsLoad(int current, int next) { return next > Math.max(MAX_LOAD, current); }

    private static int cost(Rank holder, ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof MortalGuItem gu)) return 0;

        return costPerItem(holder, gu) * stack.getCount();
    }

    private static int costPerItem(Rank holder, MortalGuItem gu) {
        int gap = gu.rank().ordinal() - holder.ordinal();
        return gap < 0 ? 1 : gap == 0 ? 2 : 2 << gap;
    }
}
