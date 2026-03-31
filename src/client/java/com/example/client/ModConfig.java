package com.example.client;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {
    
    // Chemin vers le fichier config/damagenotify.json
    private static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("damagenotify.json");
    private static final Gson GSON = new Gson();

    public static String selectedLang = "auto"; // Valeurs possibles: "auto", "fr_fr", "en_us", "es_es"
    private static JsonObject currentTranslations = new JsonObject();

    public static void load() {
        if (Files.exists(CONFIG_FILE)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_FILE, StandardCharsets.UTF_8)) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                if (json != null && json.has("lang")) {
                    selectedLang = json.get("lang").getAsString();
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la lecture de la configuration DamageNotify: " + e.getMessage());
            }
        }
        loadTranslations();
    }

    public static void save(String lang) {
        selectedLang = lang;
        try (Writer writer = Files.newBufferedWriter(CONFIG_FILE, StandardCharsets.UTF_8)) {
            JsonObject json = new JsonObject();
            json.addProperty("lang", selectedLang);
            GSON.toJson(json, writer);
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde de la configuration DamageNotify: " + e.getMessage());
        }
        // On recharge les textes immédiatement après avoir sauvegardé
        loadTranslations();
    }

    private static void loadTranslations() {
        if ("auto".equals(selectedLang)) {
            return; // Laisse Minecraft gérer nativement
        }

        try {
            String path = "/assets/modid/lang/" + selectedLang + ".json";
            try (InputStream is = ModConfig.class.getResourceAsStream(path)) {
                if (is != null) {
                    try (Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                        currentTranslations = GSON.fromJson(reader, JsonObject.class);
                    }
                } else {
                    System.err.println("Fichier de langue introuvable dans le mod : " + path);
                    currentTranslations = new JsonObject();
                }
            }
        } catch (Exception e) {
            System.err.println("Impossible de charger la langue " + selectedLang + ": " + e.getMessage());
            currentTranslations = new JsonObject();
        }
    }

    public static String translate(String key, String fallback) {
        if ("auto".equals(selectedLang)) {
            // Mode automatique : système natif du jeu
            return Component.translatable(key).getString();
        }
        // Mode forcé : on cherche dans notre JSON chargé manuellement
        if (currentTranslations != null && currentTranslations.has(key)) {
            return currentTranslations.get(key).getAsString();
        }
        return fallback;
    }
}