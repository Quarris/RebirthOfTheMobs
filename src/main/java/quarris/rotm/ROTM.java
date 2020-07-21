package quarris.rotm;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModContainerFactory;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;
import quarris.rotm.capability.SpawnSummonsCap;
import quarris.rotm.commands.CommandDumpRegistry;
import quarris.rotm.config.ModConfigs;
import quarris.rotm.event.MiscEventHandler;
import quarris.rotm.items.ItemDebug;
import quarris.rotm.items.ModItems;
import quarris.rotm.utils.Utils;

import java.util.function.BiPredicate;

@Mod(modid = ROTM.MODID, name = ROTM.NAME, version = ROTM.VERSION, guiFactory = "net.minecraftforge.client.gui.ForgeGuiFactory")
public class ROTM {
    public static final String MODID = "rotm";
    public static final String NAME = "Rebirth of the Mobs";
    public static final String VERSION = "1.0";

    public static Logger logger;

    public ROTM() {
        ObfuscationReflectionHelper.setPrivateValue(EntityLiving.SpawnPlacementType.class, EntityLiving.SpawnPlacementType.ON_GROUND,
                (BiPredicate<IBlockAccess, BlockPos>) Utils::canEntitySpawn, "spawnPredicate");
    }

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
        if (FMLCommonHandler.instance().getSide().isClient()) {
            ModelLoader.setCustomModelResourceLocation(ModItems.debugDamages, 0, new ModelResourceLocation(ModItems.debugDamages.getRegistryName(), "inventory"));
        }
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
