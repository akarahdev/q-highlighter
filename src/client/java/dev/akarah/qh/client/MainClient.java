package dev.akarah.qh.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.akarah.qh.Util;
import dev.akarah.qh.client.net.C2SEntity;
import dev.akarah.qh.client.net.ClientImpl;
import dev.akarah.qh.client.render.RenderColor;
import dev.akarah.qh.client.render.RenderUtils;
import dev.akarah.qh.packets.C2SPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;

public class MainClient implements ClientModInitializer {
    public static @Nullable ClientImpl clientImpl;
    public static KeyMapping waypointRaytraceKey;

    @Override
    public void onInitializeClient() {
        MainClient.waypointRaytraceKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.qhighlighter.waypoint.raytrace",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                "category.qhighlighter.waypoint"
        ));
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, context) -> {
            dispatcher.register(
                    ClientCommandManager.literal("qh").then(
                            ClientCommandManager.literal("join-group").then(
                                    ClientCommandManager.argument("uri", StringArgumentType.string()).then(
                                            ClientCommandManager.argument("code", StringArgumentType.string()).executes(ctx -> {
                                                var uri = ctx.getArgument("uri", String.class);
                                                var code = ctx.getArgument("code", String.class);
                                                if (!uri.contains(":")) {
                                                    uri += ":" + Util.PORT;
                                                }
                                                var address = URI.create("ws://" + uri);

                                                String finalUri = uri;
                                                Thread.startVirtualThread(() -> {
                                                    if (MainClient.clientImpl != null) {
                                                        ctx.getSource().sendFeedback(Component.literal("Disconnecting from old server..."));
                                                        MainClient.clientImpl.close();
                                                    }

                                                    ctx.getSource().sendFeedback(Component.literal("Connecting to " + finalUri + "..."));
                                                    Thread.startVirtualThread(() -> {
                                                        MainClient.clientImpl = new ClientImpl(address, code);
                                                        try {
                                                            if (MainClient.clientImpl.connectBlocking()) {
                                                                ctx.getSource().sendFeedback(Component.literal("Connected to " + finalUri + "!"));
                                                            } else {
                                                                ctx.getSource().sendFeedback(Component.literal("Failed to connect to " + finalUri + "."));
                                                            }
                                                        } catch (Exception e) {
                                                            ctx.getSource().sendFeedback(Component.literal("Failed to connect with exception: " + e.getMessage()));
                                                        }


                                                    });
                                                });
                                                return 0;
                                            })
                                    )
                            )
                    ).then(
                            ClientCommandManager.literal("list").executes(ctx -> {
                                if (MainClient.clientImpl == null) {
                                    ctx.getSource().sendError(Component.literal("nuh uh!!! LLLL"));
                                    return 1;
                                }
                                ctx.getSource().sendFeedback(
                                        Component.literal(MainClient.netClient().state().groupMembers().toString())
                                );
                                return 0;
                            })
                    )
            );
        }));

        WorldRenderEvents.AFTER_ENTITIES.register(ctx -> {
            if(clientImpl == null) {
                return;
            }
            if(Minecraft.getInstance().player == null) {
                return;
            }
            var p = Minecraft.getInstance().player;
            synchronized (MainClient.netClient().state().waypoints()) {
                for(var waypoint : clientImpl.state().waypoints()) {
                    RenderUtils.renderBox(
                            ctx,
                            waypoint.add(-0.5, -0.5, -0.5),
                            waypoint.add(0.5, 0.5, 0.5),
                            new RenderColor(200, 255, 0, 0)
                    );


                    RenderUtils.renderLine(
                            ctx,
                            ctx.gameRenderer().getMainCamera().getPosition()
                                    .add(Minecraft.getInstance().player.getLookAngle()),
                            waypoint,
                            new RenderColor(255, 255, 0, 0)
                    );
                }
                clientImpl.state().waypoints(
                        new ArrayList<>(
                                clientImpl.state().waypoints()
                                        .stream()
                                        .filter(vec -> {
                                            var p1 = (Math.abs(p.getX() - vec.x) >= 5) || (Math.abs(p.getZ() - vec.z) >= 5);
                                            var bp = new BlockPos(
                                                    (int) vec.x,
                                                    (int) vec.y,
                                                    (int) vec.z
                                            );
                                            return p1 && isAtSolid(bp);
                                        })
                                        .toList()
                        )
                );
            }

        });

        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            if(minecraft.cameraEntity != null) {
                while(waypointRaytraceKey.consumeClick()) {
                    var hit = minecraft.cameraEntity.pick(5000, 1.0f, true);
                    if (MainClient.netClient() != null && MainClient.netClient().state() != null) {
                        synchronized (MainClient.netClient().state().waypoints()) {
                            var entity = new C2SEntity(
                                    MainClient.netClient(),
                                    MainClient.netClient().state()
                            );
                            entity.writePacket(new C2SPacket.RequestWaypoint(hit.getLocation()));
                        }
                    }
                }
            }
        });
    }

    public static ClientImpl netClient() {
        return MainClient.clientImpl;
    }

    public boolean isAtSolid(BlockPos bp) {
        boolean isAir = true;
        for(int x = -1; x <= 1; x++) {
            for(int y = -1; y <= 1; y++) {
                for(int z = -1; z <= 1; z++) {
                    var mod = bp.offset(x, y, z);
                    if(Minecraft.getInstance().level != null && !Minecraft.getInstance().level.getBlockState(mod).isAir()) {
                        isAir = false;
                    }
                }
            }
        }
        return !isAir;
    }
}
