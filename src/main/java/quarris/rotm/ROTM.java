package quarris.rotm;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.Logger;
import quarris.rotm.config.ModConfigs;

@Mod(modid = ROTM.MODID, name = ROTM.NAME, version = ROTM.VERSION)
public class ROTM {
    public static final String MODID = "rotm";
    public static final String NAME = "Rebirth of the Mobs";
    public static final String VERSION = "1.0";

    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ModConfigs.updateConfigs();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }
}
