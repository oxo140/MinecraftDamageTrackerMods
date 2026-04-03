package com.example.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

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
            // 1. Enregistrement du Damage Tracker
            PayloadTypeRegistry.playS2C().register(DamageNotifyPayload.TYPE, DamageNotifyPayload.STREAM_CODEC);
            
            // 2. Enregistrement de la commande Shared Health
            PayloadTypeRegistry.playS2C().register(SharedHealthPayload.TYPE, SharedHealthPayload.STREAM_CODEC);
            
            // 3. Enregistrement du message de mort partagée
            PayloadTypeRegistry.playS2C().register(SharedDeathPayload.TYPE, SharedDeathPayload.STREAM_CODEC);
            
            // 👇 4. LE MANQUANT : Enregistrement de la commande Shared Hunger 👇
            PayloadTypeRegistry.playS2C().register(SharedHungerPayload.TYPE, SharedHungerPayload.STREAM_CODEC);
            
            playS2cRegistered = true;
        }
    }
}