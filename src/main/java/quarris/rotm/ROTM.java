package quarris.rotm;

import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;
import quarris.rotm.capability.SpawnSummonsCap;
import quarris.rotm.commands.CommandDumpRegistry;
import quarris.rotm.config.ModConfigs;
import quarris.rotm.items.ItemDebug;
import quarris.rotm.items.ModItems;
import quarris.rotm.proxy.CommonProxy;
import quarris.rotm.utils.Utils;

import java.util.function.BiPredicate;

@Mod(modid = ROTM.MODID, name = ROTM.NAME, version = ROTM.VERSION)
public class ROTM {
    public static final String MODID = "rotm";
    public static final String NAME = "Rebirth of the Mobs";
    public static final String VERSION = "1.1.3";

    public static Logger logger;

    @SidedProxy(clientSide = "quarris.rotm.proxy.ClientProxy", serverSide = "quarris.rotm.proxy.CommonProxy")
    public static CommonProxy proxy;

    public ROTM() { }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        logger.info("Loading ROTM");
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        SpawnSummonsCap.register();
        proxy.registerItemModels();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ModConfigs.updateConfigs();
        if (ModConfigs.miscConfigs.naturalSpawnBuff) {
            ObfuscationReflectionHelper.setPrivateValue(EntityLiving.SpawnPlacementType.class, EntityLiving.SpawnPlacementType.ON_GROUND,
                    (BiPredicate<IBlockAccess, BlockPos>) Utils::canEntitySpawn, "spawnPredicate");
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
                        ModItems.debugDamages = new ItemDebug(ItemDebug.DebugType.DAMAGE).setRegistryName(new ResourceLocation(MODID, "debug_damages")).setUnlocalizedName("debug_damages"),
                        ModItems.debugEntities = new ItemDebug(ItemDebug.DebugType.ENTITY).setRegistryName(new ResourceLocation(MODID, "debug_entities")).setUnlocalizedName("debug_entities"),
                        ModItems.debugPotions = new ItemDebug(ItemDebug.DebugType.POTION).setRegistryName(new ResourceLocation(MODID, "debug_potions")).setUnlocalizedName("debug_potions")
                );
            }
        }

        @SideOnly(Side.CLIENT)
        @SubscribeEvent
        public static void registerItemModels(ModelRegistryEvent event) {
            proxy.registerItemModels();
        }
    }
}
