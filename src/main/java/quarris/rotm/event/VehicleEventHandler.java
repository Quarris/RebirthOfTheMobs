package quarris.rotm.event;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import quarris.rotm.ROTM;
import quarris.rotm.config.ModConfigs;
import quarris.rotm.utils.Utils;

import java.util.Collection;

@Mod.EventBusSubscriber(modid = ROTM.MODID)
public class VehicleEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void cancelMount(EntityMountEvent event) {
        if (!event.getEntity().world.isRemote && event.isMounting()) {
            Collection<ResourceLocation> entitiesToCancel = ModConfigs.vehicleConfigs.vehicleCancels.get(Utils.getEntityName(event.getEntityBeingMounted()));
            if (ModConfigs.vehicleConfigs.treatAtBlocklist == entitiesToCancel.contains(Utils.getEntityName(event.getEntityMounting()))) {
                event.setCanceled(true);
            }
        }
    }

}
