package com.damagetracker;

import com.damagetracker.network.DamageNotificationPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.text.Text;

/**
 * Client-side entry point for the DamageTracker mod.
 *
 * <p>Registers a handler for {@link DamageNotificationPayload} packets that
 * originate from the server.  When a packet is received, an action-bar message
 * is displayed to the local player so they can see in real time which player
 * took damage and how much.</p>
 */
public class DamageTrackerClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(
                DamageNotificationPayload.ID,
                (payload, context) -> {
                    String playerName = payload.playerName();
                    int damage = Math.round(payload.damage());

                    // Schedule on the main client thread before touching client state.
                    context.client().execute(() -> {
                        if (context.client().player != null) {
                            // Use the translatable key so the message respects the
                            // client's selected language (fr_fr / en_us / etc.).
                            context.client().player.sendMessage(
                                    Text.translatable("damagetracker.notification", playerName, damage),
                                    true  // true = action bar overlay
                            );
                        }
                    });
                }
        );
    }
}
