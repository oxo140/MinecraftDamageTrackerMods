package com.example;

import com.example.client.DamageNotifyClient;
import com.example.network.ModNetworking;
import net.fabricmc.api.ClientModInitializer;

public class ExampleModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModNetworking.registerPlayS2COnce();
		DamageNotifyClient.register();
	}
}