package com.example.client;

import com.example.ExampleMod;
import com.example.network.DamageNotifyPayload;
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

        // 2. Enregistrement de la commande locale /confighealthlanguage
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("confighealthlanguage")
                .executes(context -> {
                    // L'ouverture d'un écran doit absolument se faire sur le thread du Client
                    Minecraft.getInstance().execute(() -> {
                        Minecraft.getInstance().setScreen(new ConfigScreen());
                    });
                    return 1;
                }));
        });

        // 3. Réception du paquet réseau serveur -> client
        ClientPlayNetworking.registerGlobalReceiver(DamageNotifyPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> DamageHud.show(
                    payload.victimName(),
                    payload.damageTypeId(),
                    payload.attackerTypeId(),
                    payload.amount()));
        });

        // 4. Affichage du HUD au-dessus de la barre d'expérience
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