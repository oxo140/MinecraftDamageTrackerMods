package com.example.damage;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player; // <-- IMPORTANT

public final class DamageSourceAnalyzer {

	private static final double MAX_ENTITY_SOURCE_DISTANCE = 32.0;

	private DamageSourceAnalyzer() {
	}

	public static boolean shouldBroadcast(ServerPlayer victim, DamageSource source) {
		Identifier typeId = damageTypeId(source);
		if (typeId == null) {
			return false;
		}
		if (Identifier.DEFAULT_NAMESPACE.equals(typeId.getNamespace())
				&& "generic".equals(typeId.getPath())) {
			return false;
		}

		Entity direct = source.getDirectEntity();
		Entity causing = source.getEntity();
		Entity ref = direct != null ? direct : causing;
		if (ref != null && ref != victim) {
			return victim.distanceTo(ref) <= MAX_ENTITY_SOURCE_DISTANCE;
		}
		return true;
	}

	public static String damageTypeIdString(DamageSource source) {
		Identifier id = damageTypeId(source);
		return id != null ? id.toString() : "unknown";
	}

	public static String attackerTypeIdString(DamageSource source) {
		Entity e = source.getEntity();
        
        // 👇 NOUVEAU : Si l'entité est un joueur, on renvoie "player:SonPseudo"
        if (e instanceof Player player) {
            return "player:" + player.getScoreboardName();
        }
        // Sinon (zombie, squelette...), on renvoie l'identifiant classique
		if (e instanceof LivingEntity living) {
			return BuiltInRegistries.ENTITY_TYPE.getKey(living.getType()).toString();
		}
		return "";
	}

	private static Identifier damageTypeId(DamageSource source) {
		return source.typeHolder().unwrapKey().map(k -> k.identifier()).orElse(null);
	}
}