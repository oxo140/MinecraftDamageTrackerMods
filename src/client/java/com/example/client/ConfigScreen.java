package com.example.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends Screen {

    public ConfigScreen() {
        // Le titre utilisera la langue du jeu
        super(Component.translatable("damage.modid.menu.title"));
    }

    @Override
    protected void init() {
        int startY = this.height / 4;
        int centerX = this.width / 2 - 100;

        // Bouton 1 : Automatique (Suit la langue du jeu)
        this.addRenderableWidget(Button.builder(Component.translatable("damage.modid.menu.auto"), btn -> {
            ModConfig.save("auto");
            this.minecraft.setScreen(null);
        }).bounds(centerX, startY, 200, 20).build());

        // Bouton 2 : Français (Fixe)
        this.addRenderableWidget(Button.builder(Component.literal("Langue : Français"), btn -> {
            ModConfig.save("fr_fr");
            this.minecraft.setScreen(null);
        }).bounds(centerX, startY + 25, 200, 20).build());

        // Bouton 3 : English (Fixe)
        this.addRenderableWidget(Button.builder(Component.literal("Language : English"), btn -> {
            ModConfig.save("en_us");
            this.minecraft.setScreen(null);
        }).bounds(centerX, startY + 50, 200, 20).build());

        // Bouton 4 : Español (Fixe)
        this.addRenderableWidget(Button.builder(Component.literal("Idioma : Español"), btn -> {
            ModConfig.save("es_es");
            this.minecraft.setScreen(null);
        }).bounds(centerX, startY + 75, 200, 20).build());
        
        // Bouton Fermer (Suit la langue du jeu)
        this.addRenderableWidget(Button.builder(Component.translatable("damage.modid.menu.close"), btn -> {
            this.minecraft.setScreen(null);
        }).bounds(centerX, startY + 115, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
    }
}