package com.example.sync;

import com.example.command.SharedHealthCommand;
import com.example.network.SharedDeathPayload; // Importation du nouveau Payload
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

public class SharedHealthManager {
    private static boolean isSyncingDamage = false;
    private static boolean isSyncingDeath = false;
    private static final Map<UUID, Float> previousHealths = new HashMap<>();

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(SharedHealthManager::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        if (!SharedHealthCommand.isHealthSyncEnabled) {
            previousHealths.clear();
            return;
        }
        Collection<ServerPlayer> players = PlayerLookup.all(server);
        if (players.isEmpty()) return;

        float minHealth = Float.MAX_VALUE;
        float maxHealth = -1f;
        boolean damageHappened = false;
        boolean regenHappened = false;

        for (ServerPlayer player : players) {
            if (!player.isAlive()) continue;
            float current = player.getHealth();
            float previous = previousHealths.getOrDefault(player.getUUID(), current);
            if (current < previous) damageHappened = true;
            if (current > previous) regenHappened = true;
            if (current < minHealth) minHealth = current;
            if (current > maxHealth) maxHealth = current;
        }

        if (minHealth != maxHealth && minHealth != Float.MAX_VALUE) {
            float targetHealth = damageHappened ? minHealth : (regenHappened ? maxHealth : minHealth);
            for (ServerPlayer player : players) {
                if (player.isAlive() && player.getHealth() != targetHealth) {
                    player.setHealth(targetHealth);
                }
            }
        }
        previousHealths.clear();
        for (ServerPlayer player : players) {
            if (player.isAlive()) previousHealths.put(player.getUUID(), player.getHealth());
        }
    }

    public static void shareDamage(ServerPlayer victim, float damageAmount) {
        if (!SharedHealthCommand.isHealthSyncEnabled || isSyncingDamage) return;
        isSyncingDamage = true; 
        try {
            for (ServerPlayer player : PlayerLookup.all(victim.level().getServer())) {
                if (player != victim && player.isAlive()) {
                    player.hurt(player.damageSources().generic(), damageAmount);
                }
            }
        } finally {
            isSyncingDamage = false; 
        }
    }

    public static void shareDeath(ServerPlayer victim) {
        if (!SharedHealthCommand.isHealthSyncEnabled || isSyncingDeath) return;
        isSyncingDeath = true; 
        try {
            // 👇 AU LIEU DU MESSAGE, ON ENVOIE LE PAQUET À TOUT LE MONDE
            SharedDeathPayload payload = new SharedDeathPayload(victim.getScoreboardName());
            for (ServerPlayer player : PlayerLookup.all(victim.level().getServer())) {
                ServerPlayNetworking.send(player, payload);

                if (player != victim && player.isAlive()) {
                    player.hurt(player.damageSources().genericKill(), Float.MAX_VALUE);
                }
            }
        } finally {
            isSyncingDeath = false; 
        }
    }
}