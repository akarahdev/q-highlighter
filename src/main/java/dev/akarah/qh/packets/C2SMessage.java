package dev.akarah.qh.packets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.akarah.qh.packets.c2s.C2SClientDataPacket;
import dev.akarah.qh.server.S2CEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

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

    public static StreamCodec<ByteBuf, C2SMessage> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.optional(C2SClientDataPacket.STREAM_CODEC), C2SMessage::clientData,
        C2SMessage::new
    );
}
