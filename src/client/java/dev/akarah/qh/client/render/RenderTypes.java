package dev.akarah.qh.client.render;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;

// This code was adapted from Skyblocker.
// - Refactored to fit this mod's needs
// - Adapted to use Mojmap
//
// Thanks to the contributors for the helpful rendering code!
// https://github.com/SkyblockerMod/Skyblocker/
public class RenderTypes {
    public static final RenderType.CompositeRenderType FILLED_CUBE = RenderType.create(
            "filled",
            RenderType.TRANSIENT_BUFFER_SIZE,
            false,
            true,
            RenderPipelines.DEBUG_FILLED_BOX,
            RenderType.CompositeState.builder()
                    .setLayeringState(RenderType.CompositeRenderType.VIEW_OFFSET_Z_LAYERING)
                    .createCompositeState(false)
    );
}
