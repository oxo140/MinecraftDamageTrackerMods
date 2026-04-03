package com.example.sync;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.example.command.SharedHungerCommand;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodData;

public class SharedHungerManager {

    private static final Map<UUID, Integer> previousFoodLevels = new HashMap<>();

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(SharedHungerManager::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        if (!SharedHungerCommand.isHungerSyncEnabled) {
            previousFoodLevels.clear();
            return;
        }

        Collection<ServerPlayer> players = PlayerLookup.all(server);
        if (players.isEmpty()) return;

        int minFood = Integer.MAX_VALUE;
        int maxFood = -1;
        float targetSaturation = -1f;

        boolean foodDecreased = false;
        boolean foodIncreased = false;

        for (ServerPlayer player : players) {
            if (!player.isAlive()) continue;
            
            FoodData foodData = player.getFoodData();
            int currentFood = foodData.getFoodLevel();
            int previousFood = previousFoodLevels.getOrDefault(player.getUUID(), currentFood);

            // On détecte si quelqu'un a couru (perte) ou mangé (gain)
            if (currentFood < previousFood) foodDecreased = true;
            if (currentFood > previousFood) {
                foodIncreased = true;
                // Si quelqu'un mange, on copie sa saturation (les cœurs cachés de la faim) pour la donner aux autres
                if (foodData.getSaturationLevel() > targetSaturation) {
                    targetSaturation = foodData.getSaturationLevel();
                }
            }

            if (currentFood < minFood) minFood = currentFood;
            if (currentFood > maxFood) maxFood = currentFood;
        }

        // Si désynchronisation détectée (quelqu'un a mangé ou eu faim)
        if (minFood != maxFood && minFood != Integer.MAX_VALUE) {
            // Priorité : Si quelqu'un a eu faim, on s'aligne sur lui. Sinon, on s'aligne sur celui qui a mangé.
            int targetFood = foodDecreased ? minFood : (foodIncreased ? maxFood : minFood);

            for (ServerPlayer player : players) {
                if (player.isAlive()) {
                    FoodData foodData = player.getFoodData();
                    if (foodData.getFoodLevel() != targetFood) {
                        foodData.setFoodLevel(targetFood);
                        
                        // Si l'équipe a été nourrie, on applique aussi la saturation
                        if (foodIncreased && targetSaturation != -1f) {
                            foodData.setSaturation(targetSaturation);
                        }
                    }
                }
            }
        }

        // Sauvegarde pour le prochain tick
        previousFoodLevels.clear();
        for (ServerPlayer player : players) {
            if (player.isAlive()) {
                previousFoodLevels.put(player.getUUID(), player.getFoodData().getFoodLevel());
            }
        }
    }
}