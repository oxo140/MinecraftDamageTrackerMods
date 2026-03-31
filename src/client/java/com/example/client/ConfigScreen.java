package com.example.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends Screen {

    public ConfigScreen() {
        super(Component.translatable("damage.modid.menu.title"));
    }

    @Override
    protected void init() {
        int startY = this.height / 4;
        int centerX = this.width / 2 - 100;

        // Bouton 1 : Automatique
        this.addRenderableWidget(Button.builder(Component.translatable("damage.modid.menu.auto"), btn -> {
            setLanguageAndClose("auto");
        }).bounds(centerX, startY, 200, 20).build());

        // Bouton 2 : Français
        this.addRenderableWidget(Button.builder(Component.literal("Langue : Français"), btn -> {
            setLanguageAndClose("fr_fr");
        }).bounds(centerX, startY + 25, 200, 20).build());

        // Bouton 3 : English
        this.addRenderableWidget(Button.builder(Component.literal("Language : English"), btn -> {
            setLanguageAndClose("en_us");
        }).bounds(centerX, startY + 50, 200, 20).build());

        // Bouton 4 : Español
        this.addRenderableWidget(Button.builder(Component.literal("Idioma : Español"), btn -> {
            setLanguageAndClose("es_es");
        }).bounds(centerX, startY + 75, 200, 20).build());
        
        // Bouton Fermer
        this.addRenderableWidget(Button.builder(Component.translatable("damage.modid.menu.close"), btn -> {
            this.minecraft.setScreen(null);
        }).bounds(centerX, startY + 115, 200, 20).build());
    }

    /**
     * Méthode maison pour sauvegarder, envoyer un message chat, et fermer l'écran.
     */
    private void setLanguageAndClose(String langCode) {
        // 1. On sauvegarde (ce qui recharge immédiatement les traductions en mémoire)
        ModConfig.save(langCode);
        
        // 2. On envoie un message dans le chat uniquement visible par le joueur local
        if (this.minecraft != null && this.minecraft.player != null) {
            // On traduit le message avec la nouvelle langue sélectionnée
            String translatedMsg = ModConfig.translate("damage.modid.menu.saved", "Language saved!");
            
            // Le "§a" permet d'écrire le texte en vert clair dans le chat
            this.minecraft.player.displayClientMessage(Component.literal("§a" + translatedMsg), false);
        }
        
        // 3. On ferme le menu
        this.minecraft.setScreen(null);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
    }
}