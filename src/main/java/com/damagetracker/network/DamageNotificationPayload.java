package com.damagetracker.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * Network payload sent from the server to all clients whenever a player
 * takes damage, carrying the player's display name and the damage amount.
 */
public record DamageNotificationPayload(String playerName, float damage) implements CustomPayload {

    public static final CustomPayload.Id<DamageNotificationPayload> ID =
            new CustomPayload.Id<>(Identifier.of("damagetracker", "damage_notification"));

    public static final PacketCodec<ByteBuf, DamageNotificationPayload> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.STRING, DamageNotificationPayload::playerName,
                    PacketCodecs.FLOAT, DamageNotificationPayload::damage,
                    DamageNotificationPayload::new
            );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
