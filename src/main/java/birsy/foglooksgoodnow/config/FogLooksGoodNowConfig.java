package birsy.foglooksgoodnow.config;

import birsy.foglooksgoodnow.FogLooksGoodNowMod;
import birsy.foglooksgoodnow.client.FogDensityManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class FogLooksGoodNowConfig {
    public static final ForgeConfigSpec config;
    public static final ClientConfig CLIENT_CONFIG;

    static {
        final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        config = specPair.getRight();
        CLIENT_CONFIG = specPair.getLeft();
    }

    public static class ClientConfig {
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> biomeFogs;
        public final ForgeConfigSpec.DoubleValue defaultFogStart;
        public final ForgeConfigSpec.DoubleValue defaultFogDensity;

        private ClientConfig(ForgeConfigSpec.Builder builder) {
            builder.push("Client");
            this.defaultFogStart = builder.comment("Defines the global default fog start value").defineInRange("globalfogstart", 0.0, 0.01, 100.0);
            this.defaultFogDensity = builder.comment("Defines the global default fog density value. At 1.0, the fog end is at render distance. At 0, there is no fog").defineInRange("globalfogdensity", 1.0, 0.01, 100.0);
            this.biomeFogs = builder.comment("Defines a specific fog start and density per biome. Entries are comma separated, structured like \"<biomeid>,<fog start>,<fog end>\"",
                    "\nExample: \"minecraft:plains,0.1,1.2\", \"minecraft:nether_wastes,0,0.5\"")
                    .defineListAllowEmpty(Arrays.stream(new String[]{"biomeFogMap"}).toList(), () -> new ArrayList<>(), o -> o instanceof String);
            builder.pop();
        }
    }

    public static List<Pair<String, FogDensityManager.BiomeFogDensity>> getDensityConfigs() {
        List<Pair<String, FogDensityManager.BiomeFogDensity>> list = new ArrayList<>();
        List<? extends String> densityConfigs = CLIENT_CONFIG.biomeFogs.get();

        for (String densityConfig : densityConfigs) {
            String[] options = densityConfig.split(".*");
            try {
                list.add(Pair.of(options[0], new FogDensityManager.BiomeFogDensity(Float.parseFloat(options[1]), Float.parseFloat(options[2]))));
            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }
        }

        return list;
    }
}
