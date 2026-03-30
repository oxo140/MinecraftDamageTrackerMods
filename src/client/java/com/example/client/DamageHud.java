package com.example.client;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * Affichage au-dessus de la zone hotbar / XP (largeur type inventaire). Police vanilla {@link Font}.
 */
public final class DamageHud {

	private static final int MAX_WIDTH = 182;
	private static final int DISPLAY_MS = 4000;
	/** Rouge type Minecraft (proche §c). */
	private static final int COLOR_NAME = 0xFFFF5555;
	private static final int COLOR_REST = 0xFFFFFFFF;
	/** Remonte le texte (~1 cm à l’échelle GUI habituelle). */
	private static final int OFFSET_EXTRA_UP = 14;

	/** Cœur : ❤ (U+2764) ; la police vanilla le colore en rouge via {@link #COLOR_NAME}. */
	private static final String HEART_GLYPH = "\u2764";

	private static volatile String fullText = "";
	private static volatile int nameLengthChars = 0;
	/** Index dans {@link #fullText} où commence la partie « — N ♥♥… » (rouge). */
	private static volatile int damageStartCharIndex = 0;
	private static volatile long hideAtMs = 0L;

	private static final Map<String, String> DAMAGE_TYPE_FR = new HashMap<>();

	static {
		put("minecraft:fall", "Chute");
		put("minecraft:fly_into_wall", "Mur");
		put("minecraft:stalagmite", "Stalagmite");
		put("minecraft:fell_out_of_world", "Chute dans le vide");
		put("minecraft:out_of_world", "Void");
		put("minecraft:fireball", "Boule de feu");
		put("minecraft:on_fire", "Feu");
		put("minecraft:in_fire", "Feu");
		put("minecraft:lava", "Lave");
		put("minecraft:hot_floor", "Sol brûlant");
		put("minecraft:campfire", "Feu de camp");
		put("minecraft:lightning_bolt", "Foudre");
		put("minecraft:explosion", "Explosion");
		put("minecraft:player_attack", "Joueur");
		put("minecraft:mob_attack", "Mob");
		put("minecraft:mob_attack_no_aggro", "Mob");
		put("minecraft:player_explosion", "Explosion (joueur)");
		put("minecraft:arrow", "Flèche");
		put("minecraft:trident", "Trident");
		put("minecraft:fireworks", "Feu d'artifice");
		put("minecraft:sting", "Piqûre");
		put("minecraft:spit", "Crachat");
		put("minecraft:freeze", "Gel");
		put("minecraft:falling_anvil", "Enclume");
		put("minecraft:falling_block", "Bloc tombant");
		put("minecraft:falling_stalactite", "Stalactite");
		put("minecraft:dragon_breath", "Souffle du dragon");
		put("minecraft:cactus", "Cactus");
		put("minecraft:sweet_berry_bush", "Baies");
		put("minecraft:drown", "Noyade");
		put("minecraft:starve", "Faim");
		put("minecraft:suffocation", "Suffocation");
		put("minecraft:in_wall", "Dans un mur");
		put("minecraft:magic", "Magie");
		put("minecraft:indirect_magic", "Magie");
		put("minecraft:thorns", "Épines");
		put("minecraft:sonic_boom", "Warden");
		put("minecraft:unattributed_fireball", "Boule de feu");
		put("minecraft:wither", "Wither");
		put("minecraft:mob_projectile", "Projectile");
		put("minecraft:bad_respawn_point", "Lit / ancre");
		put("minecraft:outside_border", "Bordure");
		put("minecraft:generic_kill", "Mort");
	}

	private DamageHud() {
	}

	private static void put(String id, String fr) {
		DAMAGE_TYPE_FR.put(id, fr);
	}

	public static void show(String victimName, String damageTypeId, String attackerTypeId, float amount) {
		String typeLabel = labelFor(damageTypeId, attackerTypeId);
		String whiteMid = " : " + typeLabel;
		String redDamage = amount >= 0f ? formatHeartsSuffix(amount) : "";
		nameLengthChars = victimName.length();
		fullText = victimName + whiteMid + redDamage;
		damageStartCharIndex = victimName.length() + whiteMid.length();
		hideAtMs = System.currentTimeMillis() + DISPLAY_MS;
	}

	/** Dégâts jeu → demi-cœurs affichés : 10 pts = 5 cœurs. */
	private static String formatHeartsSuffix(float damagePoints) {
		float hearts = damagePoints / 2f;
		String num = (Math.abs(hearts - Math.rint(hearts)) < 1e-3f)
				? String.valueOf((int) Math.rint(hearts))
				: String.format(Locale.ROOT, "%.1f", hearts);
		int heartCount = Math.min(20, Math.max(1, Math.round(hearts)));
		return " - " + num + " " + HEART_GLYPH.repeat(heartCount);
	}

	private static String labelFor(String damageTypeId, String attackerTypeId) {
		if (attackerTypeId != null && !attackerTypeId.isEmpty()) {
			try {
				Identifier loc = Identifier.parse(attackerTypeId);
				Component name = Component.translatable(
						"entity." + loc.getNamespace() + "." + loc.getPath());
				String mob = name.getString();
				if ("explosion".equals(Identifier.parse(damageTypeId).getPath())) {
					return mob + " (explosion)";
				}
				return mob;
			} catch (Exception ignored) {
				// fallback below
			}
		}
		return DAMAGE_TYPE_FR.getOrDefault(damageTypeId, shortId(damageTypeId));
	}

	private static String shortId(String damageTypeId) {
		try {
			return Identifier.parse(damageTypeId).getPath().replace('_', ' ');
		} catch (Exception e) {
			return damageTypeId;
		}
	}

	public static void render(GuiGraphics graphics, Font font, int screenWidth, int screenHeight) {
		long now = System.currentTimeMillis();
		if (now > hideAtMs || fullText.isEmpty()) {
			return;
		}
		String text = truncateToWidth(font, fullText, MAX_WIDTH);
		int nl = nameLengthChars;
		int d = damageStartCharIndex;
		int x = screenWidth / 2;
		int y = screenHeight - 32 - 3 - font.lineHeight - 2 - OFFSET_EXTRA_UP;

		String namePart;
		String whitePart;
		String damagePart;
		if (text.length() <= nl) {
			namePart = text;
			whitePart = "";
			damagePart = "";
		} else if (text.length() <= d) {
			namePart = text.substring(0, nl);
			whitePart = text.substring(nl);
			damagePart = "";
		} else {
			namePart = text.substring(0, nl);
			whitePart = text.substring(nl, d);
			damagePart = text.substring(d);
		}

		int totalW = font.width(namePart) + font.width(whitePart) + font.width(damagePart);
		x -= totalW / 2;

		graphics.drawString(font, namePart, x, y, COLOR_NAME, false);
		x += font.width(namePart);
		if (!whitePart.isEmpty()) {
			graphics.drawString(font, whitePart, x, y, COLOR_REST, false);
			x += font.width(whitePart);
		}
		if (!damagePart.isEmpty()) {
			graphics.drawString(font, damagePart, x, y, COLOR_NAME, false);
		}
	}

	private static String truncateToWidth(Font font, String text, int maxWidth) {
		if (font.width(text) <= maxWidth) {
			return text;
		}
		String ellipsis = "…";
		String s = text;
		while (s.length() > 1 && font.width(s + ellipsis) > maxWidth) {
			s = s.substring(0, s.length() - 1);
		}
		return s + ellipsis;
	}
}
