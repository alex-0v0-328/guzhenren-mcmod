package net.alex.guzhenren.item.material;

import net.alex.guzhenren.attachment.service.aperture.ApertureEssenceService;
import net.alex.guzhenren.attachment.service.aperture.ApertureService;
import net.alex.guzhenren.custom.enums.aperture.Rank;
import net.alex.guzhenren.custom.enums.path.GuPath;
import net.alex.guzhenren.item.gu.mortal.PrimevalElderGuItem;
import net.alex.guzhenren.registry.item.ModItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Primeval stone [元石]: a right click pours its essence [真元] into the holder's aperture [空窍].
 *
 * <p>Extends {@link net.alex.guzhenren.item.material.GuMaterialItem}. The essence value comes from registration.
 * The gate refuses an unawakened player (the service write is a silent no-op there) and a full pool. ☠ It also
 * owns every automatic draw on carried stones -- the top-up line (refill below 50%, stop at 80%) and the
 * {@code drawStones} path used by the refinement menu and the Elder Gu vault. {@link #topUp} refills
 * from carried stones, and only once the pool has fallen below the line. A second copy of the top-up
 * line drifts, and the pool silently clamps whatever a caller pours past the cap.
 *
 * <p>⚠ It refuses the unawakened instead of quietly doing nothing: the stone would otherwise be eaten for free.
 *
 * @author Alex
 * @version 1.0.0
 * @see net.alex.guzhenren.item.material.GuMaterialItem
 * @since 1.0.0
 */

public class PrimevalStoneItem extends GuMaterialItem {

    private static final String FAILED_UNAWAKENED = "guzhenren.item.failed.unawakened";
    private static final String FAILED_FULL = "guzhenren.item.failed.essence_full";
    public static final int REFILL_BELOW_PERCENT = 50;
    public static final int REFILL_UP_TO_PERCENT = 80;
    private final long essence;

    public PrimevalStoneItem(Properties properties, long essence) {
        super(properties, Rank.ONE, GuPath.HEAVEN);
        this.essence = essence;
    }

    public long essence() { return essence; }

    @Override
    protected boolean hasUse() { return true; }

    @Override
    protected @Nullable Refusal gate(Player player, ItemStack stack) {
        if (!ApertureService.hasAperture(player)) return new Refusal(FAILED_UNAWAKENED);
        return ApertureEssenceService.currentEssence(player) >= ApertureEssenceService.maxEssence(player)
                ? new Refusal(FAILED_FULL) : null;
    }

    @Override
    protected int apply(ServerPlayer player, ItemStack stack) {
        int used = used(player, stack);
        ApertureEssenceService.add(player, essence * used);
        return used;
    }

    public int used(Player player, ItemStack stack) {
        long deficit = ApertureEssenceService.maxEssence(player) - ApertureEssenceService.currentEssence(player);
        return (int) Math.min(stack.getCount(), (deficit + essence - 1) / essence);
    }

    //region 元石补给 [the stone top-up] -- one line, so two callers cannot drift apart
    public static long essencePerStone() {
        return ModItems.PRIMEVAL_STONE.get() instanceof PrimevalStoneItem stone ? stone.essence() : 0L;
    }

    public static boolean needsTopUp(Player p) {
        long max = ApertureEssenceService.maxEssence(p);
        return max > 0L && ApertureEssenceService.currentEssence(p) * 100L < max * REFILL_BELOW_PERCENT;
    }

    public static long topUpDeficit(Player p) {
        return ApertureEssenceService.maxEssence(p) * REFILL_UP_TO_PERCENT / 100L
                - ApertureEssenceService.currentEssence(p);
    }

    public static void topUp(ServerPlayer player) {
        if (needsTopUp(player)) pourInto(player, topUpDeficit(player));
    }
    //endregion

    //region sourcing stones for something else -- the walk lives here, not in a service
    public static void pourInto(ServerPlayer player, long wanted) {
        long each = essencePerStone();
        if (wanted <= 0L || each <= 0L) return;
        int taken = draw(player, (int) Math.min(Integer.MAX_VALUE, (wanted + each - 1) / each));
        if (taken > 0) ApertureEssenceService.add(player, taken * each);
    }

    private static int draw(ServerPlayer player, int wanted) {
        int left = wanted;
        int taken = 0;

        ItemStack offhand = player.getItemInHand(InteractionHand.OFF_HAND);
        if (offhand.getItem() instanceof PrimevalElderGuItem elder) {
            int drawn = elder.drawStones(offhand, left);
            taken += drawn;
            left -= drawn;
        }
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize() && left > 0; i++) {
            ItemStack stack = inventory.getItem(i);
            if (!(stack.getItem() instanceof PrimevalStoneItem)) continue;
            int drawn = Math.min(left, stack.getCount());
            stack.shrink(drawn);
            taken += drawn;
            left -= drawn;
        }
        return taken;
    }
    //endregion

    //region ☠ a lump bigger than the pool -- these stones burn straight, never through the cap
    public static long worthOnHand(Player player) {
        long stones = 0L;
        ItemStack offhand = player.getItemInHand(InteractionHand.OFF_HAND);
        if (offhand.getItem() instanceof PrimevalElderGuItem) stones += PrimevalElderGuItem.stored(offhand);

        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.getItem() instanceof PrimevalStoneItem) stones += stack.getCount();
        }
        return stones * essencePerStone();
    }

    public static boolean canAfford(Player p, long cost) {
        return ApertureEssenceService.spendable(p) + worthOnHand(p) >= cost;
    }

    public static boolean spend(ServerPlayer player, long cost) {
        if (cost <= 0L) return true;
        long each = essencePerStone();
        if (each <= 0L || !canAfford(player, cost)) return false;

        long fromPool = Math.min(ApertureEssenceService.spendable(player), cost);
        ApertureEssenceService.consume(player, fromPool);

        long owed = cost - fromPool;
        if (owed <= 0L) return true;
        long drawn = draw(player, (int) Math.min(Integer.MAX_VALUE, (owed + each - 1) / each)) * each;
        if (drawn > owed) ApertureEssenceService.add(player, drawn - owed);
        return true;
    }
    //endregion
}
