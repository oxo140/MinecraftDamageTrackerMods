package com.example.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

/**
 * Enregistrement S2C idempotent : garantit le codec avant le receiver client (ordre d'entrypoints variable),
 * et fonctionne pareil en solo (serveur intégré) que sur serveur dédié.
 */
public final class ModNetworking {

	private static volatile boolean playS2cRegistered;

	private ModNetworking() {
	}

	public static void registerPlayS2COnce() {
		if (playS2cRegistered) {
			return;
		}
		synchronized (ModNetworking.class) {
			if (playS2cRegistered) {
				return;
			}
			PayloadTypeRegistry.playS2C().register(DamageNotifyPayload.TYPE, DamageNotifyPayload.STREAM_CODEC);
			playS2cRegistered = true;
		}
	}
}
