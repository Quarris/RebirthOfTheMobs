package quarris.rotm.config;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import quarris.rotm.ROTM;
import quarris.rotm.config.utils.StringConfig;
import quarris.rotm.config.utils.StringConfigException;
import quarris.rotm.utils.Settable;
import quarris.rotm.utils.Utils;

public class VehicleConfig implements ISubConfig {

    @Config.Name("Cancel Vehicle Mounting")
    @Config.Comment({
            "This config allows you to disallow entities (such as players or mobs) to ride vehicles (such as minecarts or boats)",
            "Format: <modid:vehicleName>;<modid:entityName>",
            "Where: <modid:vehicleName> is the registry name for the vehicle entity",
            "and <modid:entityName> is the registry name for the entity to ride the vehicle"
    })
    public String[] rawVehicleCancels = new String[] {};

    @Config.Comment("Should the above list be treated as a blocklist")
    public boolean treatAtBlocklist = false;

    @Config.Ignore
    public Multimap<ResourceLocation, ResourceLocation> vehicleCancels = HashMultimap.create();

    @Override
    public void onConfigChanged() {
        this.vehicleCancels.clear();
        for (String s : this.rawVehicleCancels) {
            Settable<ResourceLocation> vehicle = Settable.create();
            Settable<ResourceLocation> entity = Settable.create();
            try {
                new StringConfig(s)
                        .next().parseAs(ResourceLocation::new).validate(Utils::doesEntityExist).accept(vehicle::set)
                        .next().parseAs(ResourceLocation::new).validate(Utils::doesEntityExist).accept(entity::set);
            } catch (StringConfigException e) {
                ROTM.logger.warn("Could not parse config; skipping {}\n{}", s, e.getLocalizedMessage());
                continue;
            }

            this.vehicleCancels.put(vehicle.get(), entity.get());
        }
    }
}
