package dev.akarah.qh.packets;

import dev.akarah.qh.registry.ExtRegistries;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.WritableRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public sealed interface S2CPacket {
    StreamCodec<RegistryFriendlyByteBuf, ? extends S2CPacket> streamCodec();

    StreamCodec<RegistryFriendlyByteBuf, S2CPacket> STREAM_CODEC =
            ByteBufCodecs.registry(ExtRegistries.S2C_MESSAGES).dispatch(
                    S2CPacket::streamCodec,
                    Function.identity()
            );

    record GroupInfoPacket(
            List<GroupMember> clients
    ) implements S2CPacket {
        public static StreamCodec<RegistryFriendlyByteBuf, GroupInfoPacket> STREAM_CODEC = StreamCodec.composite(
                GroupMember.STREAM_CODEC.apply(ByteBufCodecs.list()), GroupInfoPacket::clients,
                GroupInfoPacket::new
        );

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends S2CPacket> streamCodec() {
            return STREAM_CODEC;
        }
    }

    record RegisterWaypointPacket(
            String registrar,
            Vec3 waypoint
    ) implements S2CPacket {
        public static StreamCodec<RegistryFriendlyByteBuf, RegisterWaypointPacket> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, RegisterWaypointPacket::registrar,
                Vec3.STREAM_CODEC, RegisterWaypointPacket::waypoint,
                RegisterWaypointPacket::new
        );

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends S2CPacket> streamCodec() {
            return STREAM_CODEC;
        }
    }

    record ChatMessagePacket(
            String username,
            String message
    ) implements S2CPacket {
        public static StreamCodec<RegistryFriendlyByteBuf, ChatMessagePacket> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, ChatMessagePacket::username,
                ByteBufCodecs.STRING_UTF8, ChatMessagePacket::message,
                ChatMessagePacket::new
        );

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends S2CPacket> streamCodec() {
            return STREAM_CODEC;
        }
    }

    static StreamCodec<RegistryFriendlyByteBuf, ? extends S2CPacket> bootStrap(WritableRegistry<StreamCodec<RegistryFriendlyByteBuf, ? extends S2CPacket>> registry) {
        registry.register(
                ResourceKey.create(ExtRegistries.S2C_MESSAGES, ResourceLocation.withDefaultNamespace("group_info")),
                GroupInfoPacket.STREAM_CODEC,
                RegistrationInfo.BUILT_IN
        );
        registry.register(
                ResourceKey.create(ExtRegistries.S2C_MESSAGES, ResourceLocation.withDefaultNamespace("register_waypoint")),
                RegisterWaypointPacket.STREAM_CODEC,
                RegistrationInfo.BUILT_IN
        );
        registry.register(
                ResourceKey.create(ExtRegistries.S2C_MESSAGES, ResourceLocation.withDefaultNamespace("chat_message")),
                ChatMessagePacket.STREAM_CODEC,
                RegistrationInfo.BUILT_IN
        );
        return GroupInfoPacket.STREAM_CODEC;
    }
}
