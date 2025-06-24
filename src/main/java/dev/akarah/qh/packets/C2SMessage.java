package dev.akarah.qh.packets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.akarah.qh.packets.c2s.C2SClientDataPacket;
import dev.akarah.qh.server.S2CEntity;

import java.util.Optional;

public record C2SMessage(
        Optional<C2SClientDataPacket> clientData
) {
    public static C2SMessage empty() {
        return new C2SMessage(
                Optional.empty()
        );
    }

    public static C2SMessage of(C2SClientDataPacket packet) {
        return new C2SMessage(
                Optional.of(packet)
        );
    }

    public static Codec<C2SMessage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            C2SClientDataPacket.CODEC.optionalFieldOf("client_data").forGetter(C2SMessage::clientData)
    ).apply(instance, C2SMessage::new));
}
