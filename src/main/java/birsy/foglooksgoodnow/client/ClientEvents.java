package birsy.foglooksgoodnow.client;

import birsy.foglooksgoodnow.FogLooksGoodNowMod;
import birsy.foglooksgoodnow.config.FogLooksGoodNowConfig;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
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
        FogManager.getDensityManagerOptional().ifPresent((fogDensityManager -> fogDensityManager.initializeConfig()));
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        if (event.getCamera().getFluidInCamera() == FogType.NONE) {
            FogManager densityManager = FogManager.getDensityManager();
            Minecraft mc = Minecraft.getInstance();
            float renderDistance = event.getRenderer().getRenderDistance();

            float undergroundFogMultiplier = 1.0F;
            if (FogManager.shouldRenderCaveFog()) {
                undergroundFogMultiplier = Mth.lerp(densityManager.getUndergroundFactor((float) event.getPartialTick()), 2.2F, 1.0F);
            }
            float darkness = densityManager.darkness.get((float) event.getPartialTick());
            undergroundFogMultiplier = Mth.lerp(darkness, 1.0F, undergroundFogMultiplier);

            RenderSystem.setShaderFogStart(renderDistance * densityManager.fogStart.get((float) event.getPartialTick()));
            RenderSystem.setShaderFogEnd(renderDistance / (densityManager.fogDensity.get((float) event.getPartialTick()) * undergroundFogMultiplier));
            RenderSystem.setShaderFogShape(FogShape.SPHERE);
        }
    }

    @SubscribeEvent
    public static void onRenderFogColors(ViewportEvent.ComputeFogColor event) {
        if (event.getCamera().getFluidInCamera() == FogType.NONE && FogManager.shouldRenderCaveFog()) {
            FogManager densityManager = FogManager.getDensityManager();

            Minecraft mc = Minecraft.getInstance();
            Vec3 fogColor = FogManager.getCaveFogColor(mc.level, event.getCamera());

            float undergroundFactor = 1 - densityManager.getUndergroundFactor((float) event.getPartialTick());
            event.setRed((float) Mth.lerp(undergroundFactor, event.getRed(), fogColor.x));
            event.setGreen((float) Mth.lerp(undergroundFactor, event.getGreen(), fogColor.y));
            event.setBlue((float) Mth.lerp(undergroundFactor, event.getBlue(), fogColor.z));
        }
    }
}
