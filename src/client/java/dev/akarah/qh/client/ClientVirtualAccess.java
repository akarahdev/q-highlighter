package dev.akarah.qh.client;

import dev.akarah.qh.registry.ExtBuiltInRegistries;
import dev.akarah.qh.registry.ExtRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Stream;

public class ClientVirtualAccess implements RegistryAccess {
    @Override
    @SuppressWarnings("unchecked")
    public <E> @NotNull Optional<Registry<E>> lookup(ResourceKey<? extends Registry<? extends E>> resourceKey) {
        if (resourceKey.equals(ExtRegistries.S2C_MESSAGES)) {
            return Optional.of((Registry<E>) ExtBuiltInRegistries.S2C_MESSAGES);
        }
        if (resourceKey.equals(ExtRegistries.C2S_MESSAGES)) {
            return Optional.of((Registry<E>) ExtBuiltInRegistries.C2S_MESSAGES);
        }

        return ((Optional<Registry<E>>) BuiltInRegistries.REGISTRY.get(resourceKey.location()).map(Holder.Reference::value))
                .or(() -> (Optional<? extends Registry<E>>) ClientUtil.level().map(Level::registryAccess).flatMap(x -> x.get(resourceKey).map(Holder.Reference::value)));
    }

    @Override
    public @NotNull Stream<RegistryEntry<?>> registries() {
//        return Stream.concat(
//                Stream.of(
//                        new RegistryEntry<>(
//                                ExtRegistries.C2S_MESSAGES,
//                                ExtBuiltInRegistries.C2S_MESSAGES
//                        ),
//                        new RegistryEntry<>(
//                                ExtRegistries.S2C_MESSAGES,
//                                ExtBuiltInRegistries.S2C_MESSAGES
//                        )
//                ),
//                BuiltInRegistries.REGISTRY
//                        .entrySet()
//                        .stream()
//                        .map(x -> new RegistryEntry(x.getKey(), x.getValue()))
//        );
        return Stream.empty();
    }

    public static void bootStrap() {
        ExtBuiltInRegistries.bootStrap();
    }
}
