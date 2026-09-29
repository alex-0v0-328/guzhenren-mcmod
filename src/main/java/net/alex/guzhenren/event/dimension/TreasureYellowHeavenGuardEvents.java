package net.alex.guzhenren.event.dimension;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.attachment.service.dimension.DimensionTravelService;
import net.alex.guzhenren.entity.SoulTraderEntity;
import net.alex.guzhenren.registry.world.ModDimensions;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Server-authority guard for the Treasure Yellow Heaven [宝黄天] anchored dimension. While inside,
 * a player may only move, fly, chat, and use the exit command; every other interaction is cancelled
 * here. Void damage is also cancelled so the rescue handler below can return the player to spawn.
 *
 * <p>All checks are server-side. The few events that also fire on the client are guarded with
 * {@code !level.isClientSide()} so action is taken exactly once per interaction.
 *
 * <p>In {@link #onPlayerTick}, leaving by any route -- exit, vanilla {@code /tp}, another mod's
 * teleport -- drops the flight grant.
 *
 * <p>The one entity a player may right-click here is a {@link SoulTraderEntity}: trading is what this heaven is
 * for (Alex, 2026-09-29). Attacks on it stay cancelled like every other attack.
 *
 * <p>⚠ A cancelled toss goes back into the bag through {@link #stowTossed}, which never drops. Putting it back
 * with {@code placeItemBackInInventory} dropped whatever did not fit, the drop fired this toss event again, and a
 * full bag recursed until the server crashed -- a cursor stack or the 2x2 crafting grid falling back on a
 * closing screen was enough. What does not fit now stays out as an item that hovers without gravity where it
 * was dropped, so it never falls into the void and can be picked up once there is room; it despawns like any
 * dropped item, and the player is told the bag is full (Alex, 2026-09-29).
 *
 * @author Alex
 * @version 1.0.0
 * @see net.alex.guzhenren.attachment.service.dimension.DimensionTravelService
 * @since 1.0.0
 */

@EventBusSubscriber(modid = Guzhenren.MOD_ID)
public final class TreasureYellowHeavenGuardEvents {

    private static final String INVENTORY_FULL = "guzhenren.dimension.treasure_yellow_heaven.inventory_full";

    private TreasureYellowHeavenGuardEvents() {}

    private static boolean inTyh(Entity entity) {
        return entity.level().dimension().equals(ModDimensions.TREASURE_YELLOW_HEAVEN);
    }

    private static boolean inTyh(Player player) {
        return player.level().dimension().equals(ModDimensions.TREASURE_YELLOW_HEAVEN);
    }

    @SubscribeEvent
    public static void onBreakBlock(BlockEvent.BreakEvent event) {
        if (inTyh(event.getPlayer())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlaceBlock(BlockEvent.EntityPlaceEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (event.getEntity() instanceof Player player && inTyh(player)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide()) return;
        if (inTyh(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getLevel().isClientSide()) return;
        if (inTyh(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide()) return;
        if (inTyh(event.getEntity()) && !tradesWith(event.getTarget())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.getLevel().isClientSide()) return;
        if (inTyh(event.getEntity()) && !tradesWith(event.getTarget())) {
            event.setCanceled(true);
        }
    }

    public static boolean tradesWith(Entity target) {
        return target instanceof SoulTraderEntity;
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        if (inTyh(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onItemToss(ItemTossEvent event) {
        if (event.getPlayer().level().isClientSide()) return;
        if (!inTyh(event.getPlayer())) return;

        if (stowTossed(event.getPlayer(), event.getEntity())) event.setCanceled(true);
    }

    public static boolean stowTossed(Player player, ItemEntity tossed) {
        ItemStack rest = tossed.getItem().copy();
        player.getInventory().add(rest);
        if (rest.isEmpty()) return true;

        tossed.setItem(rest);
        tossed.setNoGravity(true);
        tossed.setDeltaMovement(Vec3.ZERO);
        player.displayClientMessage(Component.translatable(INVENTORY_FULL).withStyle(ChatFormatting.RED), true);
        return false;
    }

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity().level().isClientSide()) return;

        if (event.getSource().getEntity() instanceof Player attacker && inTyh(attacker)) {
            event.setCanceled(true);
            return;
        }

        if (event.getEntity() instanceof Player victim && inTyh(victim)
                && event.getSource().is(DamageTypes.FELL_OUT_OF_WORLD)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!inTyh(player)) {
            if (!ModDimensions.ANCHORED_DIMENSIONS.containsKey(player.level().dimension())) {
                DimensionTravelService.revokeFlight(player);
            }
            return;
        }

        DimensionTravelService.ensureFlight(player);
        DimensionTravelService.rescueIfBelowVoid(player, ModDimensions.TREASURE_YELLOW_HEAVEN_SPAWN);
    }
}
