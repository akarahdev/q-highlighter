package dev.akarah.qh.sim;

import dev.akarah.qh.registry.ExtBuiltInRegistries;
import dev.akarah.qh.registry.ExtRegistries;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;

import java.util.Optional;
import java.util.stream.Stream;

public class VirtualAccess implements RegistryAccess {
    @Override
    @SuppressWarnings("unchecked")
    public <E> Optional<Registry<E>> lookup(ResourceKey<? extends Registry<? extends E>> resourceKey) {
        if (resourceKey.equals(ExtRegistries.S2C_MESSAGES)) {
            return Optional.of((Registry<E>) ExtBuiltInRegistries.S2C_MESSAGES);
        }
        if (resourceKey.equals(ExtRegistries.C2S_MESSAGES)) {
            return Optional.of((Registry<E>) ExtBuiltInRegistries.C2S_MESSAGES);
        }
        return Optional.empty();
    }

    @Override
    public Stream<RegistryEntry<?>> registries() {
        return Stream.of(
                new RegistryEntry<>(
                        ExtRegistries.C2S_MESSAGES,
                        ExtBuiltInRegistries.C2S_MESSAGES
                ),
                new RegistryEntry<>(
                        ExtRegistries.S2C_MESSAGES,
                        ExtBuiltInRegistries.S2C_MESSAGES
                )
        );
    }

    public static void bootStrap() {
        ExtBuiltInRegistries.bootStrap();
    }
}
