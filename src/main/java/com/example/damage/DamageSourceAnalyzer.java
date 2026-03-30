package com.example.damage;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/**
 * Filtre les dégâts probablement « copiés » par un mod de vie partagée (même source lointaine)
 * et identifie le type de dégât pour l'affichage.
 */
public final class DamageSourceAnalyzer {

	/** Au-delà de cette distance, un dégât avec entité source est considéré comme non local (ex. miroir sync). */
	private static final double MAX_ENTITY_SOURCE_DISTANCE = 32.0;

	private DamageSourceAnalyzer() {
	}

	public static boolean shouldBroadcast(ServerPlayer victim, DamageSource source) {
		Identifier typeId = damageTypeId(source);
		if (typeId == null) {
			return false;
		}
		// Beaucoup de mods appliquent des dégâts artificiels en « generic » pour synchroniser la vie.
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
		if (e instanceof LivingEntity living) {
			return BuiltInRegistries.ENTITY_TYPE.getKey(living.getType()).toString();
		}
		return "";
	}

	private static Identifier damageTypeId(DamageSource source) {
		return source.typeHolder().unwrapKey().map(k -> k.identifier()).orElse(null);
	}
}
