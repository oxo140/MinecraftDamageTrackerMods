package com.damagetracker;

import com.damagetracker.network.DamageNotificationPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Server-side (and common) entry point for the DamageTracker mod.
 *
 * <p>Registers the server→client network payload and listens for living-entity
 * damage events.  Whenever a {@link ServerPlayerEntity} takes damage, a
 * {@link DamageNotificationPayload} is broadcast to every connected player so
 * that each client can display an action-bar message.</p>
 */
public class DamageTrackerMod implements ModInitializer {

    public static final String MOD_ID = "damagetracker";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // Register the server→client payload type so the networking layer knows
        // how to encode / decode it.
        PayloadTypeRegistry.playS2C().register(
                DamageNotificationPayload.ID,
                DamageNotificationPayload.CODEC
        );

        // Listen for damage events on any living entity.  When the damaged entity
        // is a player and the damage amount is positive, broadcast a notification
        // to every player currently on the server.
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (entity instanceof ServerPlayerEntity player && amount > 0) {
                String playerName = player.getName().getString();
                DamageNotificationPayload payload = new DamageNotificationPayload(playerName, amount);

                player.getServer().getPlayerManager().getPlayerList()
                        .forEach(serverPlayer -> ServerPlayNetworking.send(serverPlayer, payload));
            }
            // Always return true: we only observe the event, never cancel it.
            return true;
        });

        LOGGER.info("DamageTracker mod initialized!");
    }
}
