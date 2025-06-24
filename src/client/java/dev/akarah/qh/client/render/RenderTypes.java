package dev.akarah.qh.client.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

// This code was adapted from Skyblocker.
// - Refactored to fit this mod's needs
// - Adapted to use Mojmap
//
// Thanks to the contributors for the helpful rendering code!
// https://github.com/SkyblockerMod/Skyblocker/
public class RenderTypes {
    static final RenderPipeline FILLED_THROUGH_WALLS_PIPELINE = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
            .withLocation(ResourceLocation.parse("qh:pipeline/debug_filled_box_through_walls"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLE_STRIP)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .build());

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

    public static final RenderType.CompositeRenderType FILLED_THROUGH_WALLS = RenderType.create(
            "filled_through_walls",
            RenderType.TRANSIENT_BUFFER_SIZE,
            false,
            true,
            FILLED_THROUGH_WALLS_PIPELINE,
            RenderType.CompositeState.builder()
                    .setLayeringState(RenderType.CompositeRenderType.VIEW_OFFSET_Z_LAYERING)
                    .createCompositeState(false)
    );
}
