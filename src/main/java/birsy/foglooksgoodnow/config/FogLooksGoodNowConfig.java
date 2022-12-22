package birsy.foglooksgoodnow.config;

import birsy.foglooksgoodnow.client.FogManager;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
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

        public final ForgeConfigSpec.BooleanValue useCaveFog;
        public final ForgeConfigSpec.DoubleValue caveFogDensity;
        public final ForgeConfigSpec.IntValue caveFogColor;

        private ClientConfig(ForgeConfigSpec.Builder builder) {
            builder.push("Client");

            this.defaultFogStart = builder.comment("Defines the global default fog start value").defineInRange("globalfogstart", 0.0, 0.01, 100.0);
            this.defaultFogDensity = builder.comment("Defines the global default fog end value, as a percentage of render distance. At 1.0, the fog end is at render distance. At 0, there is no fog").defineInRange("fogend", 1.0, 0.0, 1.0);

            this.useCaveFog = builder.comment("Defines if fog will darken and get more dense when underground.").define("usecavefog", true);
            this.caveFogDensity = builder.comment("Defines the density of fog in caves. If cave fog is active, this will be multiplied with the current fog end.").defineInRange("cavefogdensity", 0.8, 0.0, 1.0);
            this.caveFogColor = builder.comment("Defines the color of cave fog, in the decimal color format. If cave fog is active, this will be multiplied with the current fog color.").defineInRange("caveFogColor",  3355443, 0, 16777215);

            this.biomeFogs = builder.comment("Defines a specific fog start, fog end, and cave fog end per biome. Entries are comma separated, structured like \"<biomeid>,<fog start>,<fog end>,<cave fog color>\"",
                    "Example: [\"minecraft:plains,0.1,1.2,5066351\", \"minecraft:nether_wastes,0,0.5,5056548\"]")
                    .defineListAllowEmpty(Arrays.stream(new String[]{"biomeFogMap"}).toList(), () -> new ArrayList<>(), o -> o instanceof String);

            builder.pop();
        }
    }

    public static List<Pair<String, FogManager.BiomeFogDensity>> getDensityConfigs() {
        List<Pair<String, FogManager.BiomeFogDensity>> list = new ArrayList<>();
        List<? extends String> densityConfigs = CLIENT_CONFIG.biomeFogs.get();

        for (String densityConfig : densityConfigs) {
            String[] options = densityConfig.split(".*");
            try {
                list.add(Pair.of(options[0], new FogManager.BiomeFogDensity(Float.parseFloat(options[1]), Float.parseFloat(options[2]), Integer.parseInt(options[3]))));
            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }
        }

        return list;
    }
}
