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
            // Enregistrement du Damage Tracker
            PayloadTypeRegistry.playS2C().register(DamageNotifyPayload.TYPE, DamageNotifyPayload.STREAM_CODEC);
            
            // Enregistrement de la commande Shared Health
            PayloadTypeRegistry.playS2C().register(SharedHealthPayload.TYPE, SharedHealthPayload.STREAM_CODEC);
            
            // 👇 CORRECTION : Enregistrement du message de mort partagée 👇
            PayloadTypeRegistry.playS2C().register(SharedDeathPayload.TYPE, SharedDeathPayload.STREAM_CODEC);
            
            playS2cRegistered = true;
        }
    }
}