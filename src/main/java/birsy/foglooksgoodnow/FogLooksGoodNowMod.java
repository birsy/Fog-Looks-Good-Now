package birsy.foglooksgoodnow;

import birsy.foglooksgoodnow.config.FogLooksGoodNowConfig;
import com.mojang.logging.LogUtils;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(FogLooksGoodNowMod.MODID)
public class FogLooksGoodNowMod {
    public static final String MODID = "foglooksgoodnow";
    public static final Logger LOGGER = LogUtils.getLogger();

    public FogLooksGoodNowMod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, FogLooksGoodNowConfig.config, MODID + ".toml");
    }


}
