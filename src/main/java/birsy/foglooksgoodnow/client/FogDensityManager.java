package birsy.foglooksgoodnow.client;

import birsy.foglooksgoodnow.FogLooksGoodNowMod;
import birsy.foglooksgoodnow.config.FogLooksGoodNowConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FogDensityManager {
    @Nullable
    public static FogDensityManager densityManager;
    public static FogDensityManager getDensityManager() {
        return Objects.requireNonNull(densityManager, "Attempted to call getDensityManager before it finished loading!");
    }
    public static Optional<FogDensityManager> getDensityManagerOptional() {
        return Optional.ofNullable(densityManager);
    }

    private final Minecraft mc;
    public InterpolatedValue fogStart;
    public InterpolatedValue fogDensity;

    private Map<String, BiomeFogDensity> configMap;

    public FogDensityManager() {
        this.mc = Minecraft.getInstance();
        this.fogStart = new InterpolatedValue(0.0F);
        this.fogDensity = new InterpolatedValue(1.0F);
        this.configMap = new HashMap<>();
        if (FogLooksGoodNowConfig.config.isLoaded()) {
            initializeConfig();
        }
    }

    public void initializeConfig() {
        FogLooksGoodNowMod.LOGGER.info("Initialized Config Values");
        this.fogStart.setDefaultValue(FogLooksGoodNowConfig.CLIENT_CONFIG.defaultFogStart.get());
        this.fogDensity.setDefaultValue(FogLooksGoodNowConfig.CLIENT_CONFIG.defaultFogDensity.get());
        this.configMap = new HashMap<>();

        List<Pair<String, BiomeFogDensity>> densityConfigs = FogLooksGoodNowConfig.getDensityConfigs();
        for (Pair<String, BiomeFogDensity> densityConfig : densityConfigs) {
            this.configMap.put(densityConfig.getLeft(), densityConfig.getRight());
        }
    }

    public void tick() {
        BlockPos pos = this.mc.gameRenderer.getMainCamera().getBlockPosition();
        Biome biome = this.mc.level.getBiome(pos).value();
        ResourceLocation key = this.mc.level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getKey(biome);
        if (key == null)
            return;

        BiomeFogDensity currentDensity = configMap.get(key.toString());
        float density = this.mc.level.effects().isFoggyAt(pos.getX(), pos.getZ()) ? 1.5F : 1.0F;
        if (currentDensity != null) {
            this.fogStart.interpolate(currentDensity.fogStart());
            this.fogDensity.interpolate(currentDensity.fogDensity() * density);
        } else {
            this.fogStart.interpolate();
            this.fogDensity.interpolate(this.fogDensity.defaultValue * density);
        }
    }

    public void close() {}

    public record BiomeFogDensity(float fogStart, float fogDensity) {};

    public class InterpolatedValue {
        public float defaultValue;

        private final float interpolationSpeed;
        private float previousValue;
        private float currentValue;

        public InterpolatedValue(float defaultValue, float interpolationSpeed) {
            this.defaultValue = defaultValue;
            this.currentValue = defaultValue;
            this.interpolationSpeed = interpolationSpeed;
        }

        public InterpolatedValue(float defaultValue) {
            this(defaultValue, 0.05f);
        }

        public void set(float value) {
            this.previousValue = this.currentValue;
            this.currentValue = value;
        }

        public void setDefaultValue(float value) {
            this.defaultValue = value;
        }
        public void setDefaultValue(double value) {
            this.defaultValue = (float)value;
        }

        public void interpolate(float value) {
            this.set(Float.isNaN(value) ? Mth.lerp(interpolationSpeed, currentValue, defaultValue) : Mth.lerp(interpolationSpeed, currentValue, value));
        }
        public void interpolate() {
            this.set(Mth.lerp(interpolationSpeed, currentValue, defaultValue));
        }

        public float get(float partialTick) {
            return Mth.lerp(partialTick, previousValue, currentValue);
        }
    }
}
