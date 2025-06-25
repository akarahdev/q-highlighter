package dev.akarah.qh;

import dev.akarah.qh.server.ServerImpl;
import dev.akarah.qh.sim.VirtualAccess;
import dev.akarah.qh.util.Util;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.core.registries.BuiltInRegistries;

import java.net.UnknownHostException;

public class ServerInitializer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        BuiltInRegistries.bootStrap();
        Main.ACCESS = new VirtualAccess();
        Util.LOGGER.info("Starting server...");
        try {
            var serverImpl = new ServerImpl();
            Util.LOGGER.info("Running server off of {}", serverImpl.getAddress());
            serverImpl.run();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

    }
}
