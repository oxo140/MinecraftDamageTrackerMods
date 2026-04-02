package com.example.command;

import com.example.network.SharedHealthPayload;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class SharedHealthCommand {
    
    public static boolean isHealthSyncEnabled = true;

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("sharedhealth")
                // Pas de .requires() pour le moment pour faciliter tes tests en solo
                .executes(context -> {
                    isHealthSyncEnabled = !isHealthSyncEnabled;
                    
                    // On vérifie que c'est bien un joueur qui a tapé la commande
                    if (context.getSource().getEntity() instanceof ServerPlayer player) {
                        // On envoie l'état (true/false) via le réseau à ce joueur
                        ServerPlayNetworking.send(player, new SharedHealthPayload(isHealthSyncEnabled));
                    }
                    
                    return 1; 
                })
            );
        });
    }
}