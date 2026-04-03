package com.example.command;

import com.example.network.SharedHungerPayload;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class SharedHungerCommand {
    
    // Activé par défaut
    public static boolean isHungerSyncEnabled = true;

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("sharedhunger")
                .executes(context -> {
                    isHungerSyncEnabled = !isHungerSyncEnabled;
                    
                    if (context.getSource().getEntity() instanceof ServerPlayer player) {
                        ServerPlayNetworking.send(player, new SharedHungerPayload(isHungerSyncEnabled));
                    }
                    
                    return 1; 
                })
            );
        });
    }
}