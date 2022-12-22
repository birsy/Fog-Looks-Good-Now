package birsy.foglooksgoodnow.config;

import birsy.foglooksgoodnow.FogLooksGoodNowMod;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

import java.awt.*;
import java.util.List;

public class FogLooksGoodNowOptions {
    public boolean useFog;
    public boolean useCaveFog;
    public boolean caveFogChangesSky;

    public boolean daylightCycleDependent;

    FogInstance defaultFogInstance;
    List<BiomeFogInstance> biomeFogInstances;

    public FogLooksGoodNowOptions() {}

    public class FogInstance {
        public Color fogColor;
        public boolean useDefaultFogColor;
        public float fogStart;
        public float fogEnd;
        public Curve fogStartDaylightCycleDependent;
        public Curve fogEndDaylightCycleDependent;

        public Color caveFogColor;
        public float caveFogStart;
        public float caveFogEnd;

        public FogInstance() {
            fogColor = new Color(175, 208, 255);
            useDefaultFogColor = true;
            fogStart = 0.0F;
            fogEnd = 1.0F;
            fogStartDaylightCycleDependent = new Curve(0, 23999, -0.25F, 1.0F);
            fogEndDaylightCycleDependent = new Curve(0, 23999, -0.25F, 1.0F);

            caveFogColor = new Color(40, 40, 51);
            fogStart = -0.1F;
            fogEnd = 0.7F;
        }

        public FogInstance(FogInstance copy) {
            this.fogColor = copy.fogColor;
            this.useDefaultFogColor = copy.useDefaultFogColor;
            this.fogStart = copy.fogStart;
            this.fogEnd = copy.fogEnd;
            this.fogStartDaylightCycleDependent = copy.fogStartDaylightCycleDependent;
            this.fogEndDaylightCycleDependent = copy.fogEndDaylightCycleDependent;

            this.caveFogColor = copy.caveFogColor;
            this.caveFogStart = copy.caveFogStart;
            this.caveFogEnd = copy.caveFogEnd;
        }
    }

    public class BiomeFogInstance extends FogInstance {
        public String biomeLocation;

        public BiomeFogInstance(String biomeLocation) {
            super();
            this.biomeLocation = biomeLocation;
        }

        public BiomeFogInstance(String biomeLocation, FogInstance copy) {
            super(copy);
            this.biomeLocation = biomeLocation;
        }
    }

    public class Curve {
        private List<Vec2> controlPoints;
        private final float minX, maxX;
        private final float minY, maxY;

        public Curve(float minX, float maxX, float minY, float maxY) {
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
        }

        // Not really a curve. Linearly interpolated until I can find a good algorithm.
        public float getValueAtPoint(float x) {
            if (controlPoints.isEmpty())  { return Float.NaN; }
            if (controlPoints.size() < 2) { return controlPoints.get(1).y; }

            for (int i = 0; i < controlPoints.size() - 1; i++) {
                Vec2 controlPoint = controlPoints.get(i);
                Vec2 nextControlPoint = controlPoints.get(i + 1);

                if (x > controlPoint.x && x < nextControlPoint.x) {
                    float xDist = nextControlPoint.x - controlPoint.x;
                    float newX = x - controlPoint.x;
                    newX /= xDist;

                    return Mth.lerp(newX, controlPoint.y, nextControlPoint.y);
                }
            }

            FogLooksGoodNowMod.LOGGER.info("Value outside of curve range!");
            return Float.NaN;
        }

        public Curve addControlPoint(Vec2 point) {
            point = new Vec2(Mth.clamp(point.x, minX, maxX), Mth.clamp(point.y, minY, maxY));
            controlPoints.add(point);
            controlPoints.sort((a, b) -> (int) ((b.x - a.x) * (maxX - minX)));
            return this;
        }

        public Curve addControlPoint(float x, float y) {
            return addControlPoint(new Vec2(x, y));
        }
    }
}
