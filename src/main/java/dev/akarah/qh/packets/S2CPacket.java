package dev.akarah.qh.packets;

import dev.akarah.qh.registry.ExtRegistries;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.WritableRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public sealed interface S2CPacket {
    StreamCodec<ByteBuf, ? extends S2CPacket> streamCodec();

    StreamCodec<RegistryFriendlyByteBuf, S2CPacket> STREAM_CODEC =
            ByteBufCodecs.registry(ExtRegistries.S2C_MESSAGES).dispatch(
                    S2CPacket::streamCodec,
                    Function.identity()
            );

    record S2CGroupInfoPacket(
            List<UUID> clients
    ) implements S2CPacket {
        public static StreamCodec<ByteBuf, S2CGroupInfoPacket> STREAM_CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()), S2CGroupInfoPacket::clients,
                S2CGroupInfoPacket::new
        );

        @Override
        public StreamCodec<ByteBuf, ? extends S2CPacket> streamCodec() {
            return STREAM_CODEC;
        }
    }

    static StreamCodec<ByteBuf, ? extends S2CPacket> bootStrap(WritableRegistry<StreamCodec<ByteBuf, ? extends S2CPacket>> registry) {
        registry.register(
                ResourceKey.create(ExtRegistries.S2C_MESSAGES, ResourceLocation.withDefaultNamespace("group_info")),
                S2CGroupInfoPacket.STREAM_CODEC,
                RegistrationInfo.BUILT_IN
        );
        return S2CGroupInfoPacket.STREAM_CODEC;
    }
}
