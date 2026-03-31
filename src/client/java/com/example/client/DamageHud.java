package com.example.client;

import java.util.Locale;

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

	private DamageHud() {
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
		// 1. Si une entité (mob/joueur) est responsable
		if (attackerTypeId != null && !attackerTypeId.isEmpty()) {
			try {
				Identifier loc = Identifier.parse(attackerTypeId);
				// Le nom des mobs vanilla reste dans la langue du jeu (plus naturel)
				Component name = Component.translatable("entity." + loc.getNamespace() + "." + loc.getPath());
				String mob = name.getString();
				
				if ("explosion".equals(Identifier.parse(damageTypeId).getPath())) {
					// ON UTILISE NOTRE CONFIG POUR LA TRADUCTION DE L'EXPLOSION
					String format = ModConfig.translate("damage.modid.explosion_by", "%s (explosion)");
					return format.replace("%s", mob);
				}
				return mob;
			} catch (Exception ignored) {}
		}
		
		// 2. Traduction de notre mod (chute, feu, lave...)
		try {
			String path = Identifier.parse(damageTypeId).getPath();
			String translationKey = "damage.modid." + path;
			
			// ON UTILISE NOTRE CONFIG AU LIEU DU JEU NATIF
			String translated = ModConfig.translate(translationKey, translationKey);
			
			// Si la traduction n'a pas été trouvée, on met le nom brut (secours)
			if (translated.equals(translationKey)) {
				return shortId(damageTypeId);
			}
			return translated;
		} catch (Exception e) {
			return damageTypeId;
		}
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