package dev.akarah.qh.client.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

// This code was adapted from Skyblocker.
// - Refactored to fit this mod's needs
// - Adapted to use Mojmap
//
// Thanks to the contributors for the helpful rendering code!
// https://github.com/SkyblockerMod/Skyblocker/
public class RenderUtils {
    public static void renderShape(
            LevelRenderContext ctx,
            VoxelShape voxelShape,
            RenderType renderType,
            double x,
            double y,
            double z,
            RenderColor color
    ) {
        var poseStack = ctx.poseStack();

        var camera = ctx.gameRenderer().getMainCamera().position();

        poseStack.pushPose();
        poseStack.translate(camera.multiply(-1.0, -1.0, -1.0));

        var consumer = ctx.bufferSource().getBuffer(renderType);

        ShapeRenderer.renderShape(
                poseStack,
                consumer,
                voxelShape,
                x,
                y,
                z,
                color.argb(),
                1
        );

        poseStack.popPose();
    }

    public static void renderBox(
            LevelRenderContext ctx,
            Vec3 c1,
            Vec3 c2,
            RenderColor argbColor
    ) {
        var poseStack = ctx.poseStack();

        var camera = ctx.gameRenderer().getMainCamera().position();

        poseStack.pushPose();
        poseStack.translate(camera.multiply(-1.0, -1.0, -1.0));

        var consumer = ctx.bufferSource().getBuffer(ExtRenderTypes.FILLED_THROUGH_WALLS);

        ShapeRenderer.renderShape(
                poseStack,
                consumer,
                Shapes.box(
                        0,
                        0,
                        0,
                        c2.x - c1.x,
                        c2.y - c1.y,
                        c2.z - c1.z
                ),
                c1.x,
                c1.y,
                c1.z,
                ARGB.colorFromFloat(
                        argbColor.alphaFloat(),
                        argbColor.redFloat(),
                        argbColor.blueFloat(),
                        argbColor.greenFloat()
                ),
                1
        );

        poseStack.popPose();
    }

    public static void renderLine(
            LevelRenderContext ctx,
            Vec3 c1,
            Vec3 c2,
            RenderColor color,
            float width
    ) {
        var consumer = ctx.bufferSource().getBuffer(RenderTypes.LINES_TRANSLUCENT);

        var camera = ctx.gameRenderer().getMainCamera().position();

        Matrix4f positionMatrix = new Matrix4f()
                .translate((float) -camera.x, (float) -camera.y, (float) -camera.z);

        // Calculate normal vector from line direction
        Vector3f normal = c2.toVector3f()
                .sub((float) c1.x, (float) c1.y, (float) c1.z)
                .normalize();

        consumer.addVertex(positionMatrix, (float) c1.x, (float) c1.y, (float) c1.z)
                .setColor(color.redFloat(), color.greenFloat(), color.blueFloat(), color.alphaFloat())
                .setNormal(normal.x(), normal.y(), normal.z())
                .setLineWidth(width);

        consumer.addVertex(positionMatrix, (float) c2.x, (float) c2.y, (float) c2.z)
                .setColor(color.redFloat(), color.greenFloat(), color.blueFloat(), color.alphaFloat())
                .setNormal(normal.x(), normal.y(), normal.z())
                .setLineWidth(width);
    }
}
