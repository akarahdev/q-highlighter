package dev.akarah.qh.registry;

import dev.akarah.qh.packets.C2SPacket;
import dev.akarah.qh.packets.S2CPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class ExtRegistries {
    public static ResourceKey<? extends Registry<StreamCodec<RegistryFriendlyByteBuf, ? extends C2SPacket>>> C2S_MESSAGES
            = ResourceKey.createRegistryKey(ResourceLocation.parse("q-highlighter:messages/c2s"));
    public static ResourceKey<? extends Registry<StreamCodec<RegistryFriendlyByteBuf, ? extends S2CPacket>>> S2C_MESSAGES
            = ResourceKey.createRegistryKey(ResourceLocation.parse("q-highlighter:messages/s2c"));
}
