package dev.akarah.qh;

import dev.akarah.qh.sim.VirtualAccess;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.RegistryAccess;

public class Main implements ModInitializer {
    static VirtualAccess ACCESS = new VirtualAccess();

    @Override
    public void onInitialize() {
        VirtualAccess.bootStrap();
    }

    public static RegistryAccess getRegistryAccess() {
        return ACCESS;
    }
}
