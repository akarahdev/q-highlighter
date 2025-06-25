package dev.akarah.qh.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;
import java.util.UUID;

public record GroupMember(
        String username,
        UUID uuid,
        Optional<MemberStatus> memberStatus
) {
    public static StreamCodec<RegistryFriendlyByteBuf, GroupMember> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, GroupMember::username,
            UUIDUtil.STREAM_CODEC, GroupMember::uuid,
            ByteBufCodecs.optional(MemberStatus.STREAM_CODEC), GroupMember::memberStatus,
            GroupMember::new
    );
}
