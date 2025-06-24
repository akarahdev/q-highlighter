package dev.akarah.qh.client;

import com.mojang.brigadier.arguments.StringArgumentType;
import dev.akarah.qh.Util;
import dev.akarah.qh.client.net.ClientImpl;
import dev.akarah.qh.client.render.RenderColor;
import dev.akarah.qh.client.render.RenderTypes;
import dev.akarah.qh.client.render.RenderUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.Nullable;

import java.net.URI;

public class MainClient implements ClientModInitializer {
    public static @Nullable ClientImpl clientImpl;

    @Override
    public void onInitializeClient() {
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
            if (Minecraft.getInstance().player == null) {
                return;
            }

            if(MainClient.netClient() == null) {
                return;
            }

            var state = MainClient.netClient().state();
            for (var entity : ctx.world().entitiesForRendering()) {
                if (state.groupMembers().contains(entity.getUUID())
                        && Minecraft.getInstance().player != null
                        && !entity.getUUID().equals(Minecraft.getInstance().player.getUUID())) {
                    RenderUtils.renderBox(
                            ctx,
                            entity.position().add(new Vec3(-0.5, 0, -0.5)),
                            entity.position().add(new Vec3(0.5, 2, 0.5)),
                            new RenderColor(100, 255, 255, 0)
                    );
                }
            }
        });
    }

    public static ClientImpl netClient() {
        return MainClient.clientImpl;
    }
}
