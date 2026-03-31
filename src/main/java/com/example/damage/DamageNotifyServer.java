package com.example.damage;

import com.example.network.DamageNotifyPayload;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
public final class DamageNotifyServer {

	private DamageNotifyServer() {
	}

	/**
	 * Anti-"spam" lié aux mods de vie/sync :
	 * certains mods déclenchent des dégâts artificiels sur plusieurs joueurs en rafale.
	 * On ne diffuse donc qu'un message de dégâts global toutes les ~0.5s.
	 */
	private static final long GLOBAL_SUPPRESS_MS = 500L;
	private static volatile long lastGlobalBroadcastMs = 0L;

	public static void register() {
		ServerLivingEntityEvents.AFTER_DAMAGE.register(DamageNotifyServer::onAfterDamage);
		ServerLivingEntityEvents.AFTER_DEATH.register(DamageNotifyServer::onAfterDeath);
	}

	private static void onAfterDamage(LivingEntity entity, DamageSource source, float baseDamageTaken, float damageTaken,
			boolean blocked) {
		if (blocked || damageTaken <= 0f || !(entity instanceof ServerPlayer player)) {
			return;
		}
		if (!DamageSourceAnalyzer.shouldBroadcast(player, source)) {
			return;
		}
		broadcast(player, source, damageTaken);
	}

	private static void onAfterDeath(LivingEntity entity, DamageSource source) {
		if (!(entity instanceof ServerPlayer player)) {
			return;
		}
		if (!DamageSourceAnalyzer.shouldBroadcast(player, source)) {
			return;
		}
		// AFTER_DAMAGE n'est pas appelé sur un coup mortel ; pas de montant fiable ici.
		broadcast(player, source, -1f);
	}

	private static void broadcast(ServerPlayer victim, DamageSource source, float amount) {
		long now = System.currentTimeMillis();
		if (now - lastGlobalBroadcastMs < GLOBAL_SUPPRESS_MS) {
			return;
		}
		lastGlobalBroadcastMs = now;

		String name = victim.getScoreboardName();
		String typeId = DamageSourceAnalyzer.damageTypeIdString(source);
		String attackerId = DamageSourceAnalyzer.attackerTypeIdString(source);
		var payload = new DamageNotifyPayload(name, typeId, attackerId, amount);

		for (ServerPlayer receiver : PlayerLookup.all(victim.level().getServer())) {
			ServerPlayNetworking.send(receiver, payload);
		}
	}
}
