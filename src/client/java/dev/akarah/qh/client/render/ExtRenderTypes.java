package dev.akarah.qh.client.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

// This code was adapted from Skyblocker.
// - Refactored to fit this mod's needs
// - Adapted to use Mojmap
//
// Thanks to the contributors for the helpful rendering code!
// https://github.com/SkyblockerMod/Skyblocker/
public class ExtRenderTypes {
    static final RenderPipeline FILLED_THROUGH_WALLS_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                .withLocation(Identifier.parse("qh:pipeline/debug_filled_box_through_walls"))
                .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLE_STRIP)
                .build()
    );

    public static final RenderType FILLED_CUBE = RenderType.create(
            "filled",
            RenderSetup.builder(RenderPipelines.DEBUG_FILLED_BOX).createRenderSetup()
    );

    public static final RenderType FILLED_THROUGH_WALLS = RenderType.create(
            "filled_through_walls",
                    RenderSetup.builder(FILLED_THROUGH_WALLS_PIPELINE)
                        .createRenderSetup()
    );
}
