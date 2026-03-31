package com.example.network;

import com.example.ExampleMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record DamageNotifyPayload(String victimName, String damageTypeId, String attackerTypeId, float amount)
		implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<DamageNotifyPayload> TYPE = new CustomPacketPayload.Type<>(
			Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "damage_notify"));

	public static final StreamCodec<RegistryFriendlyByteBuf, DamageNotifyPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8,
			DamageNotifyPayload::victimName,
			ByteBufCodecs.STRING_UTF8,
			DamageNotifyPayload::damageTypeId,
			ByteBufCodecs.STRING_UTF8,
			DamageNotifyPayload::attackerTypeId,
			ByteBufCodecs.FLOAT,
			DamageNotifyPayload::amount,
			DamageNotifyPayload::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
