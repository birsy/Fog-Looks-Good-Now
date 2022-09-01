package birsy.foglooksgoodnow.client;

import birsy.foglooksgoodnow.FogLooksGoodNowMod;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = FogLooksGoodNowMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Reloading event) {
        FogDensityManager.getDensityManagerOptional().ifPresent((fogDensityManager -> fogDensityManager.initializeConfig()));
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        FogDensityManager densityManager = FogDensityManager.getDensityManager();
        float renderDistance = event.getRenderer().getRenderDistance();
        RenderSystem.setShaderFogStart(renderDistance * densityManager.fogStart.get((float) event.getPartialTick()));
        RenderSystem.setShaderFogEnd(renderDistance / densityManager.fogDensity.get((float) event.getPartialTick()));
        RenderSystem.setShaderFogShape(FogShape.SPHERE);
    }

    @SubscribeEvent
    public static void onRenderFogColors(ViewportEvent.ComputeFogColor event) {

    }

    public static float calculateInterpolatedLight(Level world, Vec3 playerVecPos, LightLayer lightType) {
        BlockPos pos = new BlockPos(Mth.floor(playerVecPos.x()), Mth.floor(playerVecPos.y()), Mth.floor(playerVecPos.z()));

        float xLerp = (float) Mth.frac(playerVecPos.x());
        float yLerp = (float) Mth.frac(playerVecPos.y());
        float zLerp = (float) Mth.frac(playerVecPos.z());

        float light000 = getLight(world, pos.offset(0, 0, 0), lightType);
        light000 = light000 == -1 ? 0 : light000;
        float light001 = getLight(world, pos.offset(0, 0, 1), lightType);
        light001 = light001 == -1 ? light000 : light001;
        float light010 = getLight(world, pos.offset(0, 1, 0), lightType);
        light010 = light010 == -1 ? light000 : light010;
        float light011 = getLight(world, pos.offset(0, 1, 1), lightType);
        light011 = light011 == -1 ? light000 : light011;
        float light100 = getLight(world, pos.offset(1, 0, 0), lightType);
        light100 = light100 == -1 ? light000 : light100;
        float light101 = getLight(world, pos.offset(1, 0, 1), lightType);
        light101 = light101 == -1 ? light000 : light101;
        float light110 = getLight(world, pos.offset(1, 1, 0), lightType);
        light110 = light110 == -1 ? light000 : light110;
        float light111 = getLight(world, pos.offset(1, 1, 1), lightType);
        light111 = light111 == -1 ? light000 : light111;

        float light00 = Mth.lerp(xLerp, light000, light100);
        float light01 = Mth.lerp(xLerp, light001, light101);
        float light10 = Mth.lerp(xLerp, light010, light110);
        float light11 = Mth.lerp(xLerp, light011, light111);

        float light0 = Mth.lerp(zLerp, light00, light01);
        float light1 = Mth.lerp(zLerp, light10, light11);

        float light = Mth.lerp(yLerp, light0, light1);

        return -1 * (light - 15);
    }
    public static float calculateInterpolatedLight(Level world, Vec3 playerVecPos, LightLayer lightType, boolean returnsNormalized) {
        return calculateInterpolatedLight(world, playerVecPos, lightType) / (returnsNormalized ? 15.0F : 1.0F);
    }
    private static float getLight(Level worldIn, BlockPos posIn, LightLayer lightType) {
        if (worldIn.getBlockState(posIn).canOcclude()) {
            return -1;
        }
        return lightType != null ? worldIn.getBrightness(lightType, posIn) : worldIn.getRawBrightness(posIn, 0);
    }
}
