package dev.akarah.qh.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.akarah.qh.Main;
import dev.akarah.qh.client.data.SweepData;
import dev.akarah.qh.client.net.ClientImpl;
import dev.akarah.qh.client.render.RenderColor;
import dev.akarah.qh.client.render.RenderUtils;
import dev.akarah.qh.packets.C2SPacket;
import dev.akarah.qh.util.Util;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;
import org.lwjgl.glfw.GLFW;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MainClient implements ClientModInitializer {
    public static @Nullable ClientImpl clientImpl;
    public static KeyMapping waypointRaytraceKey;
    public static SweepData SWEEP_DATA = new SweepData(
            new SweepData.BaseValues(0.0, 0.0),
            Optional.empty(),
            Optional.empty(),
            0.0
    );

    @Override
    public void onInitializeClient() {
        Main.ACCESS = new ClientVirtualAccess();

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
                            ClientCommandManager.literal("chat").then(
                                    ClientCommandManager.argument("message", StringArgumentType.greedyString()).executes(ctx -> {
                                        MainClient.netClient().map(ClientImpl::entity).ifPresent(entity -> {
                                            entity.writePacket(new C2SPacket.RequestMessage(
                                                    ctx.getArgument("message", String.class)
                                            ));
                                        });
                                        return 0;
                                    })
                            )
                    )
            );
        }));

        WorldRenderEvents.AFTER_ENTITIES.register(ctx -> {
            ClientUtil.localPlayer()
                    .ifPresent(p -> {
                        netClient().map(ClientImpl::state)
                                .ifPresent(state -> {
                                    state.waypoints().with(waypoints -> waypoints.forEach(waypoint -> {
                                        RenderUtils.renderBox(
                                                ctx,
                                                waypoint.add(-0.5, -0.5, -0.5),
                                                waypoint.add(0.5, 0.5, 0.5),
                                                new RenderColor(200, 255, 0, 0)
                                        );


                                        ClientUtil.localPlayer().ifPresent(localPlayer -> {
                                            RenderUtils.renderLine(
                                                    ctx,
                                                    ctx.gameRenderer().getMainCamera().getPosition()
                                                            .add(new Vec3(ctx.gameRenderer().getMainCamera().getLookVector())),
                                                    waypoint,
                                                    new RenderColor(255, 255, 0, 0)
                                            );
                                        });
                                    }));

                                    state.waypoints().map(x -> new ArrayList<>(x.stream()
                                            .filter(vec -> {
                                                var p1 = (Math.abs(p.getX() - vec.x) >= 5) || (Math.abs(p.getZ() - vec.z) >= 5);
                                                var bp = new BlockPos(
                                                        (int) vec.x,
                                                        (int) vec.y,
                                                        (int) vec.z
                                                );
                                                return p1 && isAtSolid(bp);
                                            })
                                            .toList())
                                    );
                                });
                    });
        });

        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            if (minecraft.cameraEntity != null) {
                while (waypointRaytraceKey.consumeClick()) {
                    var hit = minecraft.cameraEntity.pick(5000, 1.0f, true);
                    MainClient.netClient().ifPresent(netClient ->
                            netClient.entity().writePacket(new C2SPacket.RequestWaypoint(hit.getLocation())));
                }
            }
        });

        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                ResourceLocation.parse("qhighlighter:e"),
                (context, tickCounter) -> {
                    MainClient.sweepData().render(context, tickCounter);
                }
        );

        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                ResourceLocation.parse("qhighlighter:f"),
                (graphics, tickCounter) -> {
                    try {
                        MainClient.netClient().ifPresent(netClient -> {
                            var components = new ArrayList<Component>();
                            netClient.state().groupMembers().with(groupMembers -> {
                                for(var member : groupMembers) {
                                    components.add(
                                            Component.literal(member.username())
                                                    .withColor(ARGB.color(255, 133, 255, 133))
                                    );
                                    components.add(
                                            Component.literal("(uuid: " + member.uuid() + ")")
                                                    .withColor(ARGB.color(255, 80, 80, 80))
                                    );
                                    member.memberStatus().ifPresent(memberStatus -> {
                                        components.add(Component.empty());

                                        components.add(
                                                Component.literal("Logs per break: ")
                                                        .withColor(ARGB.color(255, 133, 133, 133))
                                                        .append(
                                                                Component.literal(String.valueOf(memberStatus.logs()))
                                                                        .withColor(ARGB.color(255, 0, 255, 0))
                                                        )
                                        );

                                        var coordinates = new Vec3i(
                                                (int) memberStatus.coordinates().x,
                                                (int) memberStatus.coordinates().y,
                                                (int) memberStatus.coordinates().z
                                        );
                                        components.add(
                                                Component.literal("Coordinates: ")
                                                        .withColor(ARGB.color(255, 133, 133, 133))
                                                        .append(
                                                                Component.literal(coordinates.toShortString())
                                                                        .withColor(ARGB.color(255, 0, 255, 0))
                                                        )
                                        );

                                        components.add(
                                                Component.literal("Equipment:")
                                                        .withColor(ARGB.color(255, 133, 133, 133))
                                        );

                                        memberStatus.equipment().stream().forEach(component -> components.add(
                                                Component.literal(" - ")
                                                        .withColor(ARGB.color(255, 80, 80, 80))
                                                        .append(component)
                                        ));

                                    });
                                    components.add(Component.empty());
                                }

                                var font = Minecraft.getInstance().font;
                                for(int i = 0; i < components.size(); i++) {
                                    var component = components.get(i);
                                    graphics.drawString(
                                            font,
                                            component,
                                            10,
                                            (i * font.lineHeight) + 10,
                                            new RenderColor(255, 255, 0, 0).argb()
                                    );
                                }
                            });
                        });
                    } catch (Exception e) {
                        // this is needed since minecraft is silly and equipment isn't always available
                        // in order for this to work we need non-null component contents so
                        // if a component is somehow null just... don't render ig
                    }
                }
        );
    }

    public static Optional<ClientImpl> netClient() {
        return Optional.ofNullable(MainClient.clientImpl);
    }

    public boolean isAtSolid(BlockPos bp) {
        boolean isAir = true;
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    var mod = bp.offset(x, y, z);
                    if (Minecraft.getInstance().level != null && !Minecraft.getInstance().level.getBlockState(mod).isAir()) {
                        isAir = false;
                    }
                }
            }
        }
        return !isAir;
    }

    public static SweepData sweepData() {
        return SWEEP_DATA;
    }

    public static void sweepData(SweepData data) {
        SWEEP_DATA = data;
    }
}
