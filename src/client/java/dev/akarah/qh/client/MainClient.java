package dev.akarah.qh.client;

import com.mojang.brigadier.arguments.StringArgumentType;
import dev.akarah.qh.Util;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.rmi.server.UnicastRemoteObject;

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
                                                if(!uri.contains(":")) {
                                                    uri += ":" + Util.PORT;
                                                }
                                                var address = URI.create("ws://" + uri);

                                                String finalUri = uri;
                                                Thread.startVirtualThread(() -> {
                                                    if(MainClient.clientImpl != null) {
                                                        ctx.getSource().sendFeedback(Component.literal("Disconnecting from old server..."));
                                                        MainClient.clientImpl.close();
                                                    }

                                                    ctx.getSource().sendFeedback(Component.literal("Connecting to " + finalUri + "..."));
                                                    Thread.startVirtualThread(() -> {
                                                        MainClient.clientImpl = new ClientImpl(address, code);
                                                        try {
                                                            if(MainClient.clientImpl.connectBlocking()) {
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
                                if(MainClient.clientImpl == null) {
                                    ctx.getSource().sendError(Component.literal("nuh uh!!! LLLL"));
                                    return 1;
                                }
                                ctx.getSource().sendFeedback(
                                        Component.literal(MainClient.clientImpl.clientState.groupMembers.toString())
                                );
                                return 0;
                            })
                    )
            );
        }));
    }
}
