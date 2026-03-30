package com.example.client;

import com.example.ExampleMod;
import com.example.network.DamageNotifyPayload;
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
		ClientPlayNetworking.registerGlobalReceiver(DamageNotifyPayload.TYPE, (payload, context) -> {
			context.client().execute(() -> DamageHud.show(
					payload.victimName(),
					payload.damageTypeId(),
					payload.attackerTypeId(),
					payload.amount()));
		});

		// Après la hotbar (barre d'XP incluse) pour rester visible ; même condition hideGui que la hotbar.
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
