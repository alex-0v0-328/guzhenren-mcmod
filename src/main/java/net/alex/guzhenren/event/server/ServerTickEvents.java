package net.alex.guzhenren.event.server;

import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.attachment.service.aperture.AperturePressureExplosionTask;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * Drives the world-level background work that outlives the player who caused it: the staged
 * pressure-explosion [空窍压力爆炸] craters. Unlike the one-second heartbeat in {@link
 * net.alex.guzhenren.event.player.PlayerTickEvents} these runs every server tick and keeps running
 * after the player died -- the crater finishes on its own. The task list is wiped when the server
 * stops, so a singleplayer world switch never carries stale level references.
 *
 * @author Alex
 * @version 1.0.0
 * @see net.alex.guzhenren.attachment.service.aperture.AperturePressureExplosionTask
 * @since 1.0.0
 */

@EventBusSubscriber(modid = Guzhenren.MOD_ID)
public final class ServerTickEvents {

    private ServerTickEvents() {}

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) { AperturePressureExplosionTask.tickAll(); }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) { AperturePressureExplosionTask.clear(); }
}
