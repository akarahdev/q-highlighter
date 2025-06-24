package dev.akarah.qh.packets;

import dev.akarah.qh.registry.ExtRegistries;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.WritableRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;
import java.util.function.Function;

public sealed interface C2SPacket {
    StreamCodec<ByteBuf, ? extends C2SPacket> streamCodec();

    StreamCodec<RegistryFriendlyByteBuf, C2SPacket> STREAM_CODEC =
            ByteBufCodecs.registry(ExtRegistries.C2S_MESSAGES).dispatch(
                    C2SPacket::streamCodec,
                    Function.identity()
            );

    record C2SClientDataPacket(
            String username,
            String groupName,
            UUID uuid,
            int protocolVersion
    ) implements C2SPacket {
        public static StreamCodec<ByteBuf, C2SClientDataPacket> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, C2SClientDataPacket::username,
                ByteBufCodecs.STRING_UTF8, C2SClientDataPacket::groupName,
                UUIDUtil.STREAM_CODEC, C2SClientDataPacket::uuid,
                ByteBufCodecs.VAR_INT, C2SClientDataPacket::protocolVersion,
                C2SClientDataPacket::new
        );

        @Override
        public StreamCodec<ByteBuf, ? extends C2SPacket> streamCodec() {
            return STREAM_CODEC;
        }
    }

    record RequestWaypoint(
            Vec3 waypoint
    ) implements C2SPacket {
        public static StreamCodec<ByteBuf, RequestWaypoint> STREAM_CODEC = StreamCodec.composite(
                Vec3.STREAM_CODEC, RequestWaypoint::waypoint,
                RequestWaypoint::new
        );

        @Override
        public StreamCodec<ByteBuf, ? extends C2SPacket> streamCodec() {
            return STREAM_CODEC;
        }
    }

    static StreamCodec<ByteBuf, ? extends C2SPacket> bootStrap(WritableRegistry<StreamCodec<ByteBuf, ? extends C2SPacket>> registry) {
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
        return C2SClientDataPacket.STREAM_CODEC;
    }
}
