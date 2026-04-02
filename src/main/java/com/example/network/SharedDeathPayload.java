package com.example.network;

import com.example.ExampleMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SharedDeathPayload(String victimName) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SharedDeathPayload> TYPE = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(ExampleMod.MOD_ID, "shared_death_msg"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SharedDeathPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SharedDeathPayload::victimName,
            SharedDeathPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}