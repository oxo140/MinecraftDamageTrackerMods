package com.example.network;

import com.example.ExampleMod;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SharedHungerPayload(boolean isEnabled) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SharedHungerPayload> TYPE = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "shared_hunger_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SharedHungerPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            SharedHungerPayload::isEnabled,
            SharedHungerPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}