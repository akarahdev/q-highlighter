package dev.akarah.qh;

import dev.akarah.qh.sim.VirtualAccess;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.impl.launch.FabricLauncher;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;

public class Main implements ModInitializer {
    public static RegistryAccess ACCESS;

    @Override
    public void onInitialize() {
        VirtualAccess.bootStrap();
    }

    public static RegistryAccess getRegistryAccess() {
        return ACCESS;
    }
}
