package dev.akarah.qh.client.render;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RenderUtils {
    // partially borrowed from Skyblocker mod
    public static void renderShape(
            WorldRenderContext ctx,
            VoxelShape voxelShape,
            RenderType renderType,
            double x,
            double y,
            double z,
            int argbColor
    ) {
        try {
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
                    argbColor
            );

            poseStack.popPose();
        } catch (IllegalStateException exception) {
            System.out.println("Error while rendering shape of type: " + renderType + ", of shape: " + voxelShape + ", of color:" + argbColor);
            System.out.println(exception.getMessage());
        }
    }
}
