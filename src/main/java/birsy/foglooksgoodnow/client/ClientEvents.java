package birsy.foglooksgoodnow.client;

import birsy.foglooksgoodnow.FogLooksGoodNowMod;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.material.FogType;
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
        if (event.getCamera().getFluidInCamera() == FogType.NONE) {
            FogDensityManager densityManager = FogDensityManager.getDensityManager();
            Minecraft mc = Minecraft.getInstance();
            float renderDistance = event.getRenderer().getRenderDistance();
            float undergroundFogMultiplier = Mth.lerp(densityManager.getUndergroundFactor((float) event.getPartialTick()), 2.2F, 1.0F);

            RenderSystem.setShaderFogStart(renderDistance * densityManager.fogStart.get((float) event.getPartialTick()));
            RenderSystem.setShaderFogEnd(renderDistance / (densityManager.fogDensity.get((float) event.getPartialTick()) * undergroundFogMultiplier));
            RenderSystem.setShaderFogShape(FogShape.SPHERE);
        }
    }

    @SubscribeEvent
    public static void onRenderFogColors(ViewportEvent.ComputeFogColor event) {
        if (event.getCamera().getFluidInCamera() == FogType.NONE) {
            FogDensityManager densityManager = FogDensityManager.getDensityManager();

            Minecraft mc = Minecraft.getInstance();
            BiomeManager biomemanager = mc.level.getBiomeManager();
            Vec3 biomePos = event.getRenderer().getMainCamera().getPosition().subtract(2.0D, 2.0D, 2.0D).scale(0.25D);
            Vec3 fogColor = CubicSampler.gaussianSampleVec3(biomePos, (x, y, z) -> Vec3.fromRGB24(biomemanager.getNoiseBiomeAtQuart(x, y, z).get().getFogColor()));
            fogColor = fogColor.multiply(0.2F, 0.2F, 0.2F);

            float undergroundFactor = 1 - densityManager.getUndergroundFactor((float) event.getPartialTick());
            event.setRed((float) Mth.lerp(undergroundFactor, event.getRed(), fogColor.x));
            event.setGreen((float) Mth.lerp(undergroundFactor, event.getGreen(), fogColor.y));
            event.setBlue((float) Mth.lerp(undergroundFactor, event.getBlue(), fogColor.z));
        }
    }
}
