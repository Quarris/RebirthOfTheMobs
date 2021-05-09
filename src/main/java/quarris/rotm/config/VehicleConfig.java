package quarris.rotm.config;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.Loader;
import quarris.rotm.ROTM;
import quarris.rotm.config.utils.StringConfig;
import quarris.rotm.config.utils.StringConfigException;
import quarris.rotm.utils.Settable;
import quarris.rotm.utils.Utils;

import java.util.HashSet;
import java.util.Set;

public class VehicleConfig implements ISubConfig {

    @Config.Name("Cancel Vehicle Mounting")
    @Config.Comment({
            "Allows you to prevent entities to mount these vehicle.",
            "You can block specific vehicle entities or entire mods.",
            "Format: <modid:vehicle> OR <modid>",
            "Where: <modid:vehicle> is the entity vehicle to disallow.",
            "<modid> is the modid name of the mod to disallow vehicles from."
    })
    public String[] rawVehicleCancels = new String[]{};

    @Config.Ignore
    public Set<ResourceLocation> vehicleEntityCancels = new HashSet<>();

    @Config.Ignore
    public Set<String> vehicleModCancels = new HashSet<>();

    @Config.Name("Cancel Vehicle Mounting Overrides")
    @Config.Comment({
            "This allows vehicles to be excluded from the Cancel Vehicle Mounting config.",
            "You can put the vehicle entity (to override a mod vehicle cancel) or a vehicle;entity pair (to override a specific vehicle cancel).",
            "Format: <modid:vehicle> OR <modid:vehicle>;<modid:entity>",
            "Where: <modid:vehicle> is the vehicle to override.",
            "<modid:entity> is the entity to override from the vehicle"
    })
    public String[] rawVehicleCancelOverrides = new String[]{};

    @Config.Ignore
    public SetMultimap<ResourceLocation, ResourceLocation> vehicleCancelOverrides = HashMultimap.create();

    @Config.Name("Global Cancel Vehicle Mounting Overrides")
    @Config.Comment({
            "This allows the passanger entities to be excluded globally from the Cancel Vehicle Mounting config.",
            "Format: <modid:entity>",
            "Where: <modid:entity> is the entity to globally exclude from the Cancel Vehicle Mounting."
    })
    public String[] rawGlobalCancelOverrides = new String[]{};

    @Config.Ignore
    public Set<ResourceLocation> globalCancelOverrides = new HashSet<>();

    @Config.Comment("Should the Cancel Vehicle Mounting list be treated as a blocklist instead")
    public boolean treatAtBlocklist = true;


    @Override
    public void onConfigChanged() {
        this.vehicleEntityCancels.clear();
        this.vehicleModCancels.clear();
        this.vehicleCancelOverrides.clear();
        this.globalCancelOverrides.clear();
        for (String s : this.rawVehicleCancels) {
            ResourceLocation res = new ResourceLocation(s);
            if (Utils.doesEntityExist(res)) {
                this.vehicleEntityCancels.add(res);
            } else if (Loader.isModLoaded(s)) {
                this.vehicleModCancels.add(s);
            } else {
                ROTM.logger.warn("Could not parse config; skipping {}", s);
            }
        }

        for (String s : this.rawVehicleCancelOverrides) {
            Settable<ResourceLocation> vehicle = Settable.create();
            Settable<ResourceLocation> entity = Settable.create();
            try {
                new StringConfig(s)
                        .next().parseAs(ResourceLocation::new).validate(Utils::doesEntityExist).accept(vehicle::set)
                        .next().optional(null).parseAs(ResourceLocation::new).validate(Utils::doesEntityExist).accept(entity::set);
                this.vehicleCancelOverrides.put(vehicle.get(), entity.get());
            } catch (StringConfigException e) {
                ROTM.logger.warn("Could not parse config; skipping {}", s);
                continue;
            }

        }

        for (String s : rawGlobalCancelOverrides) {
            ResourceLocation res = new ResourceLocation(s);
            if (Utils.doesEntityExist(res)) {
                this.globalCancelOverrides.add(res);
            } else {
                ROTM.logger.warn("Could not parse config; skipping {}", s);
            }
        }
    }
}
