package quarris.rotm.event;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import quarris.rotm.ROTM;
import quarris.rotm.config.ModConfigs;
import quarris.rotm.config.VehicleConfig;
import quarris.rotm.utils.Utils;

import java.util.Set;

@Mod.EventBusSubscriber(modid = ROTM.MODID)
public class VehicleEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void cancelMount(EntityMountEvent event) {
        if (!event.getEntity().world.isRemote && event.isMounting()) {
            ResourceLocation vehicleName = Utils.getEntityName(event.getEntityBeingMounted());
            ResourceLocation entityName = Utils.getEntityName(event.getEntityMounting());
            VehicleConfig config = ModConfigs.vehicleConfigs;

            Set<ResourceLocation> overrides = config.vehicleCancelOverrides.get(vehicleName);
            boolean cancelVehicle = config.vehicleEntityCancels.contains(vehicleName);
            boolean cancelMod = config.vehicleModCancels.contains(vehicleName.getResourceDomain());
            boolean isOverriden = config.globalCancelOverrides.contains(entityName) || overrides.contains(null) || overrides.contains(entityName);

            if (((cancelVehicle || cancelMod) && !isOverriden) == config.treatAtBlocklist)
                event.setCanceled(true);

        }
    }

}
