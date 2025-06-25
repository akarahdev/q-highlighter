package dev.akarah.qh.client.data;

import dev.akarah.qh.client.ClientUtil;
import dev.akarah.qh.client.MainClient;
import dev.akarah.qh.client.render.RenderColor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public record SweepData(
        BaseValues baseValues,
        Optional<AxeThrow> axeThrowModifier,
        Optional<WrongStyle> styleModifier,
        double logs
) {
    public record BaseValues(double sweep, double treeToughness) {

    }

    public record AxeThrow(double multiplier) {

    }

    public record WrongStyle(String error, double multiplier) {

    }

    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        var font = Minecraft.getInstance().font;

        var components = new ArrayList<Component>();
        components.add(
                Component.literal("Sweep: ")
                        .withColor(ARGB.color(255, 133, 133, 133))
                        .append(
                                Component.literal(this.baseValues().sweep() + "∮")
                                        .withColor(ARGB.color(255, 0, 133, 0))
                        )
        );
        this.axeThrowModifier().ifPresent(axeThrow -> components.add(
                Component.literal("Axe Throw: ")
                        .withColor(ARGB.color(255, 133, 133, 133))
                        .append(
                                Component.literal(String.valueOf(axeThrow.multiplier()))
                                        .withColor(ARGB.color(255, 255, 133, 133))
                        )
                        .append(
                                Component.literal("%")
                                        .withColor(ARGB.color(255, 255, 133, 133))
                        )
        ));
        this.styleModifier().ifPresent(styleModifier -> components.add(
                Component.literal("Wrong Style: ")
                        .withColor(ARGB.color(255, 133, 133, 133))
                        .append(
                                Component.literal(String.valueOf(styleModifier.multiplier()))
                                        .withColor(ARGB.color(255, 255, 133, 133))
                        )
                        .append(
                                Component.literal("%")
                                        .withColor(ARGB.color(255, 255, 133, 133))
                        )
        ));
        components.add(
                Component.literal("Logs: ")
                        .withColor(ARGB.color(255, 133, 133, 133))
                        .append(
                                Component.literal(this.logs() + " Logs")
                                        .withColor(ARGB.color(255, 0, 133, 0))
                        )
        );

        var maxLength = components.stream()
                .map(Component::getString)
                .map(String::length)
                .max(Comparator.naturalOrder())
                .orElse(0);

        for(int i = 0; i < components.size(); i++) {
            var component = components.get(i);
            graphics.drawString(
                    font,
                    component,
                    (graphics.guiWidth() - maxLength * 8),
                    (graphics.guiHeight() / 8) + (i * font.lineHeight),
                    new RenderColor(255, 255, 0, 0).argb()
            );
        }

    }
}