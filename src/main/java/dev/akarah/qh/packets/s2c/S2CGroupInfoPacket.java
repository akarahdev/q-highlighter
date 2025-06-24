package dev.akarah.qh.packets.s2c;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;

import java.util.List;
import java.util.UUID;

public record S2CGroupInfoPacket(
        List<UUID> clients
) {
    public static Codec<S2CGroupInfoPacket> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.listOf().fieldOf("clients").forGetter(S2CGroupInfoPacket::clients)
    ).apply(instance, S2CGroupInfoPacket::new));
}
