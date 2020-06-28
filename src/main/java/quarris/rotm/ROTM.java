package quarris.rotm;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.DefaultGuiFactory;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.Logger;
import quarris.rotm.capability.SpawnSummonsCap;
import quarris.rotm.commands.CommandDumpRegistry;
import quarris.rotm.config.ModConfigs;
import quarris.rotm.items.ItemDebug;

@Mod(modid = ROTM.MODID, name = ROTM.NAME, version = ROTM.VERSION, guiFactory = "quarris.rotm.config.ModGuiFactory")
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

    @EventHandler
    public void initCommands(FMLServerStartingEvent event) {
        if (ModConfigs.debugConfigs.enableDebugMode) {
            event.registerServerCommand(new CommandDumpRegistry<>("dumpEntities", ForgeRegistries.ENTITIES));
            event.registerServerCommand(new CommandDumpRegistry<>("dumpSounds", ForgeRegistries.SOUND_EVENTS));
            event.registerServerCommand(new CommandDumpRegistry<>("dumpPotions", ForgeRegistries.POTIONS));
        }
    }

    @Mod.EventBusSubscriber
    public static class RegistryEvents {

        @SubscribeEvent
        public static void registerDebugItems(RegistryEvent.Register<Item> event) {
            if (ModConfigs.debugConfigs.enableDebugMode) {
                event.getRegistry().registerAll(
                        new ItemDebug().setRegistryName(new ResourceLocation(MODID, "debugDamages"))
                );
            }
        }

    }
}
