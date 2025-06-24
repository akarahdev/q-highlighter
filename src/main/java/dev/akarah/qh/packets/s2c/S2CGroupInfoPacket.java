package dev.akarah.qh.packets.s2c;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.UUID;

public record S2CGroupInfoPacket(
        List<UUID> clients
) {

    public static StreamCodec<ByteBuf, S2CGroupInfoPacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()), S2CGroupInfoPacket::clients,
            S2CGroupInfoPacket::new
    );
}
