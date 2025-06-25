package dev.akarah.qh.registry;

import com.mojang.serialization.Lifecycle;
import dev.akarah.qh.packets.C2SPacket;
import dev.akarah.qh.packets.S2CPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class ExtBuiltInRegistries {
    public static WritableRegistry<StreamCodec<RegistryFriendlyByteBuf, ? extends C2SPacket>> C2S_MESSAGES;
    public static WritableRegistry<StreamCodec<RegistryFriendlyByteBuf, ? extends S2CPacket>> S2C_MESSAGES;

    public static void bootStrap() {
        C2S_MESSAGES = new DefaultedMappedRegistry<>(
                ExtRegistries.C2S_MESSAGES.location().toString(),
                ExtRegistries.C2S_MESSAGES,
                Lifecycle.stable(),
                false
        );
        C2SPacket.bootStrap(C2S_MESSAGES);
        S2C_MESSAGES = new DefaultedMappedRegistry<>(
                ExtRegistries.S2C_MESSAGES.location().toString(),
                ExtRegistries.S2C_MESSAGES,
                Lifecycle.stable(),
                false
        );
        S2CPacket.bootStrap(S2C_MESSAGES);
    }
}
