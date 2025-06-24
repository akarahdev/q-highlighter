package dev.akarah.qh.client.render;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.util.ARGB;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

// This code was adapted from Skyblocker.
// - Refactored to fit this mod's needs
// - Adapted to use Mojmap
//
// Thanks to the contributors for the helpful rendering code!
// https://github.com/SkyblockerMod/Skyblocker/
public class RenderUtils {
    public static void renderShape(
            WorldRenderContext ctx,
            VoxelShape voxelShape,
            RenderType renderType,
            double x,
            double y,
            double z,
            RenderColor color
    ) {
        var poseStack = ctx.matrixStack();
        assert poseStack != null;

        var camera = ctx.camera().getPosition();

        poseStack.pushPose();
        poseStack.translate(camera.multiply(-1.0, -1.0, -1.0));

        var consumers = ctx.consumers();
        assert consumers != null;
        var consumer = consumers.getBuffer(renderType);

        ShapeRenderer.renderShape(
                poseStack,
                consumer,
                voxelShape,
                x,
                y,
                z,
                color.argb()
        );

        poseStack.popPose();
    }

    public static void renderBox(
            WorldRenderContext ctx,
            Vec3 c1,
            Vec3 c2,
            RenderColor argbColor
    ) {
        var poseStack = ctx.matrixStack();
        assert poseStack != null;

        var camera = ctx.camera().getPosition();

        poseStack.pushPose();
        poseStack.translate(camera.multiply(-1.0, -1.0, -1.0));

        var consumers = ctx.consumers();
        assert consumers != null;
        var consumer = consumers.getBuffer(RenderTypes.FILLED_CUBE);

        ShapeRenderer.addChainedFilledBoxVertices(
                poseStack,
                consumer,
                c1.x,
                c1.y,
                c1.z,
                c2.x,
                c2.y,
                c2.z,
                argbColor.redFloat(),
                argbColor.blueFloat(),
                argbColor.greenFloat(),
                argbColor.alphaFloat()
        );

        poseStack.popPose();
    }
}
