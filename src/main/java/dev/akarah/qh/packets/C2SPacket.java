package dev.akarah.qh.packets;

import dev.akarah.qh.registry.ExtRegistries;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.WritableRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

public sealed interface C2SPacket {
    StreamCodec<RegistryFriendlyByteBuf, ? extends C2SPacket> streamCodec();

    StreamCodec<RegistryFriendlyByteBuf, C2SPacket> STREAM_CODEC =
            ByteBufCodecs.registry(ExtRegistries.C2S_MESSAGES).dispatch(
                    C2SPacket::streamCodec,
                    Function.identity()
            );

    record C2SClientDataPacket(
            GroupMember memberData,
            String groupName,
            int protocolVersion
    ) implements C2SPacket {
        public static StreamCodec<RegistryFriendlyByteBuf, C2SClientDataPacket> STREAM_CODEC = StreamCodec.composite(
                GroupMember.STREAM_CODEC, C2SClientDataPacket::memberData,
                ByteBufCodecs.STRING_UTF8, C2SClientDataPacket::groupName,
                ByteBufCodecs.VAR_INT, C2SClientDataPacket::protocolVersion,
                C2SClientDataPacket::new
        );

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends C2SPacket> streamCodec() {
            return STREAM_CODEC;
        }
    }

    record RequestWaypoint(
            Vec3 waypoint
    ) implements C2SPacket {
        public static StreamCodec<RegistryFriendlyByteBuf, RequestWaypoint> STREAM_CODEC = StreamCodec.composite(
                Vec3.STREAM_CODEC, RequestWaypoint::waypoint,
                RequestWaypoint::new
        );

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends C2SPacket> streamCodec() {
            return STREAM_CODEC;
        }
    }

    record RequestMessage(
            String message
    ) implements C2SPacket {
        public static StreamCodec<RegistryFriendlyByteBuf, RequestMessage> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, RequestMessage::message,
                RequestMessage::new
        );

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends C2SPacket> streamCodec() {
            return STREAM_CODEC;
        }
    }

    record UpdateStatus(
            MemberStatus status
    ) implements C2SPacket {
        public static StreamCodec<RegistryFriendlyByteBuf, UpdateStatus> STREAM_CODEC = StreamCodec.composite(
                MemberStatus.STREAM_CODEC, UpdateStatus::status,
                UpdateStatus::new
        );

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ? extends C2SPacket> streamCodec() {
            return STREAM_CODEC;
        }
    }

    static StreamCodec<RegistryFriendlyByteBuf, ? extends C2SPacket> bootStrap(WritableRegistry<StreamCodec<RegistryFriendlyByteBuf, ? extends C2SPacket>> registry) {
        registry.register(
                ResourceKey.create(ExtRegistries.C2S_MESSAGES, ResourceLocation.withDefaultNamespace("client_data")),
                C2SClientDataPacket.STREAM_CODEC,
                RegistrationInfo.BUILT_IN
        );
        registry.register(
                ResourceKey.create(ExtRegistries.C2S_MESSAGES, ResourceLocation.withDefaultNamespace("request_waypoint")),
                RequestWaypoint.STREAM_CODEC,
                RegistrationInfo.BUILT_IN
        );
        registry.register(
                ResourceKey.create(ExtRegistries.C2S_MESSAGES, ResourceLocation.withDefaultNamespace("request_message")),
                RequestMessage.STREAM_CODEC,
                RegistrationInfo.BUILT_IN
        );
        registry.register(
                ResourceKey.create(ExtRegistries.C2S_MESSAGES, ResourceLocation.withDefaultNamespace("update_log_count")),
                UpdateStatus.STREAM_CODEC,
                RegistrationInfo.BUILT_IN
        );
        return C2SClientDataPacket.STREAM_CODEC;
    }
}
