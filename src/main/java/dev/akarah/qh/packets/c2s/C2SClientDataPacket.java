package dev.akarah.qh.packets.c2s;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.akarah.qh.server.S2CEntity;
import net.minecraft.core.UUIDUtil;

import java.util.UUID;

public record C2SClientDataPacket(
        String username,
        String groupName,
        UUID uuid,
        int protocolVersion
) {
    public static Codec<C2SClientDataPacket> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("username").forGetter(C2SClientDataPacket::username),
            Codec.STRING.fieldOf("group_name").forGetter(C2SClientDataPacket::groupName),
            UUIDUtil.CODEC.fieldOf("uuid").forGetter(C2SClientDataPacket::uuid),
            Codec.INT.fieldOf("protocol_version").forGetter(C2SClientDataPacket::protocolVersion)
    ).apply(instance, C2SClientDataPacket::new));
}
