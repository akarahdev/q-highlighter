package dev.akarah.qh.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class ClientUtil {
    public static Optional<LocalPlayer> localPlayer() {
        return Optional.ofNullable(Minecraft.getInstance().player);
    }

    public static Optional<ClientLevel> level() {
        return localPlayer().map(x -> x.clientLevel);
    }
}
