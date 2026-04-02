package com.example.network;

import com.example.ExampleMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SharedHealthPayload(boolean isEnabled) implements CustomPacketPayload {

    // Identifiant unique de notre paquet
    public static final CustomPacketPayload.Type<SharedHealthPayload> TYPE = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "shared_health_sync"));

    // Codec pour transformer notre boolean (true/false) en données réseau
    public static final StreamCodec<RegistryFriendlyByteBuf, SharedHealthPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            SharedHealthPayload::isEnabled,
            SharedHealthPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}