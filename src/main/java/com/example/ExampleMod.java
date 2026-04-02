package com.example;

import com.example.command.SharedHealthCommand; 
import com.example.damage.DamageNotifyServer;
import com.example.network.ModNetworking;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleMod implements ModInitializer {
	public static final String MOD_ID = "modid";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModNetworking.registerPlayS2COnce();
		DamageNotifyServer.register();
		SharedHealthCommand.register(); 
        
		// 👇 NOUVELLE LIGNE : On lance l'horloge de synchronisation absolue 👇
		com.example.sync.SharedHealthManager.register();
        
		LOGGER.info("Damage notify : logique serveur chargée (solo = serveur intégré + ce même chargement)");
	}
}