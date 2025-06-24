package dev.akarah.qh.packets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.akarah.qh.packets.c2s.C2SClientDataPacket;
import dev.akarah.qh.packets.s2c.S2CGroupInfoPacket;
import dev.akarah.qh.server.S2CEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record S2CMessage(
        Optional<S2CGroupInfoPacket> groupInfo
) {
    public static S2CMessage empty() {
        return new S2CMessage(
                Optional.empty()
        );
    }

    public static S2CMessage of(S2CGroupInfoPacket packet) {
        return new S2CMessage(
                Optional.of(packet)
        );
    }

    public static StreamCodec<ByteBuf, S2CMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(S2CGroupInfoPacket.STREAM_CODEC), S2CMessage::groupInfo,
            S2CMessage::new
    );
}
