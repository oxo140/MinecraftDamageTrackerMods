package com.example.client;

import com.example.ExampleMod;
import com.example.network.DamageNotifyPayload;
import com.example.network.SharedDeathPayload;
import com.example.network.SharedHealthPayload;
import com.example.network.SharedHungerPayload;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public final class DamageNotifyClient {

    private static final Identifier HUD_ID = Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "damage_notify_hud");

    private DamageNotifyClient() {
    }

    public static void register() {
        // 1. Initialisation de la configuration
        ModConfig.load();

        // 2. Enregistrement de la commande locale /sharedlanguage
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("sharedlanguage")
                .executes(context -> {
                    Minecraft.getInstance().execute(() -> {
                        Minecraft.getInstance().setScreen(new ConfigScreen());
                    });
                    return 1;
                }));
        });

        // 3. Réception du paquet réseau (Damage Tracker)
        ClientPlayNetworking.registerGlobalReceiver(DamageNotifyPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> DamageHud.show(
                    payload.victimName(),
                    payload.damageTypeId(),
                    payload.attackerTypeId(),
                    payload.amount()));
        });

        // 4. Réception du paquet réseau (Activation/Désactivation Shared Health)
        ClientPlayNetworking.registerGlobalReceiver(SharedHealthPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                if (context.client().player != null) {
                    String stateKey = payload.isEnabled() ? "command.modid.sharedhealth.enabled" : "command.modid.sharedhealth.disabled";
                    String stateTranslated = ModConfig.translate(stateKey, payload.isEnabled() ? "ENABLED" : "DISABLED");
                    
                    String coloredState = (payload.isEnabled() ? "§a" : "§c") + stateTranslated;
                    String format = ModConfig.translate("command.modid.sharedhealth.status", "[SharedGames] Health Sync: %s");
                    
                    String finalMessage = "§e" + String.format(format, coloredState);
                    context.client().player.displayClientMessage(net.minecraft.network.chat.Component.literal(finalMessage), false);
                }
            });
        });

        // 5. Réception du paquet réseau (Mort Partagée)
        ClientPlayNetworking.registerGlobalReceiver(SharedDeathPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                if (context.client().player != null) {
                    // On récupère la phrase dans la langue forcée par le joueur
                    String format = ModConfig.translate("message.modid.shared_death", "§c[SharedGames] Thanks to §e%s §cfor killing everyone!");
                    
                    // On remplace %s par le nom du joueur
                    String finalMessage = String.format(format, payload.victimName());
                    
                    context.client().player.displayClientMessage(net.minecraft.network.chat.Component.literal(finalMessage), false);
                }
            });
        });
        // Réception du paquet réseau (Activation/Désactivation Shared Hunger)
        ClientPlayNetworking.registerGlobalReceiver(SharedHungerPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                if (context.client().player != null) {
                    String stateKey = payload.isEnabled() ? "command.modid.sharedhunger.enabled" : "command.modid.sharedhunger.disabled";
                    String stateTranslated = ModConfig.translate(stateKey, payload.isEnabled() ? "ENABLED" : "DISABLED");
                    
                    String coloredState = (payload.isEnabled() ? "§a" : "§c") + stateTranslated;
                    String format = ModConfig.translate("command.modid.sharedhunger.status", "[SharedGames] Hunger Sync: %s");
                    
                    String finalMessage = "§e" + String.format(format, coloredState);
                    context.client().player.displayClientMessage(net.minecraft.network.chat.Component.literal(finalMessage), false);
                }
            });
        });
        
        // 6. Affichage du HUD au-dessus de la barre d'expérience
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.HOTBAR,
                HUD_ID,
                (graphics, tickDelta) -> {
                    Minecraft mc = Minecraft.getInstance();
                    int w = mc.getWindow().getGuiScaledWidth();
                    int h = mc.getWindow().getGuiScaledHeight();
                    DamageHud.render(graphics, mc.font, w, h);
                });
    }
}