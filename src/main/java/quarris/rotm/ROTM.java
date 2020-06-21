package quarris.rotm;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;
import quarris.rotm.capability.SpawnSummonsCap;
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
        ModConfigs.updateConfigs();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        SpawnSummonsCap.register();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }
}
