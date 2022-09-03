package birsy.foglooksgoodnow.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class FoggySkyRenderer {
    public static void renderSky(ClientLevel level, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        if (FogManager.shouldRenderCaveFog()) {
            FogManager densityManager = FogManager.getDensityManager();

            Vec3 fogColor = FogManager.getCaveFogColor(level, camera);

            float undergroundFactor = 1 - densityManager.getUndergroundFactor(partialTick);
            undergroundFactor *= undergroundFactor * undergroundFactor * undergroundFactor;

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();

            RenderSystem.depthMask(false);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.disableTexture();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            float radius = 5.0F;
            renderCone(poseStack, bufferbuilder, 32, true, radius, -30.0F, (float) fogColor.x, (float) fogColor.y, (float) fogColor.z, undergroundFactor, 0.0F, (float) fogColor.x, (float) fogColor.y, (float) fogColor.z, undergroundFactor);
            renderCone(poseStack, bufferbuilder, 32, false, radius, 30.0F, (float) fogColor.x, (float) fogColor.y, (float) fogColor.z, undergroundFactor * 0.2F, 0.0F, (float) fogColor.x, (float) fogColor.y, (float) fogColor.z, undergroundFactor);

            RenderSystem.depthMask(true);
        }
    }

    private static void renderCone(PoseStack poseStack, BufferBuilder bufferBuilder, int resolution, boolean normal, float radius, float topVertexHeight, float topR, float topG, float topB, float topA, float bottomVertexHeight, float bottomR, float bottomG, float bottomB, float bottomA) {
        Matrix4f matrix = poseStack.last().pose();
        bufferBuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        bufferBuilder.vertex(matrix, 0.0F, topVertexHeight, 0.0F).color(topR, topG, topB, topA).endVertex();
        for(int vertex = 0; vertex <= resolution; ++vertex) {
            float angle = (float)vertex * ((float)Math.PI * 2F) / ((float)resolution);
            float x = Mth.sin(angle) * radius;
            float z = Mth.cos(angle) * radius;

            bufferBuilder.vertex(matrix, x, bottomVertexHeight, normal ? z : -z).color(bottomR, bottomG, bottomB, bottomA).endVertex();
        }

        BufferUploader.drawWithShader(bufferBuilder.end());
    }
}
