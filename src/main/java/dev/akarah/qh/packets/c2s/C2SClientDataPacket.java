package dev.akarah.qh.packets.c2s;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.akarah.qh.server.S2CEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public record C2SClientDataPacket(
        String username,
        String groupName,
        UUID uuid,
        int protocolVersion
) {
    public static StreamCodec<ByteBuf, C2SClientDataPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, C2SClientDataPacket::username,
            ByteBufCodecs.STRING_UTF8, C2SClientDataPacket::groupName,
            UUIDUtil.STREAM_CODEC, C2SClientDataPacket::uuid,
            ByteBufCodecs.VAR_INT, C2SClientDataPacket::protocolVersion,
            C2SClientDataPacket::new
    );
}
