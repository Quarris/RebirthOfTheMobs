package quarris.rotm.config;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import org.apache.commons.lang3.tuple.MutablePair;
import quarris.rotm.ROTM;
import quarris.rotm.config.utils.StringConfig;
import quarris.rotm.config.utils.StringConfigException;
import quarris.rotm.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MiscConfig implements ISubConfig {

    @Config.Comment({
            "This multiplier is applied to all entities when they swim that are not on the blocklist.",
    })
    public float globalSwimSpeedMultiplier = 1.0f;

    @Config.Comment("This blocklist prevents the given entities from receiving the Global Swim Speed Multiplier")
    public String[] rawGlobalSwimSpeedBlocklist = new String[]{};

    @Config.Ignore
    public final List<ResourceLocation> globalSwimSpeedBlocklist = new ArrayList<>();

    @Config.Comment("Uses the rawGlobalSwimSpeedBlocklist as an allowlist instead")
    public boolean treatGlobalSwimSpeedAsAllowlist = false;

    @Config.Comment({
            "This multiplier increases the swim speed for the specified entity by a specified amount. It stacks with the global speed multiplier.",
            "Format: <modid:entity>;<multiplier>",
            "Where: <modid:entity> is the entity to apply the multiplier for, and",
            "<multiplier> is the value to multiply the speed by."
    })
    public String[] rawSwimSpeedMultipliers = new String[]{};

    @Config.Ignore
    public final Map<ResourceLocation, Float> swimSpeedMultipliers = new HashMap<>();

    @Override
    public void onConfigChanged() {
        this.updateGlobalSwimSpeedList();
        this.updateSwimSpeedList();
    }

    public void updateGlobalSwimSpeedList() {
        this.globalSwimSpeedBlocklist.clear();
        for (String s : this.rawGlobalSwimSpeedBlocklist) {
            try {
                new StringConfig(s)
                        .next().parseAs(ResourceLocation::new).validate(Utils::doesEntityExist).<ResourceLocation>accept(globalSwimSpeedBlocklist::add);
            } catch (StringConfigException e) {
                ROTM.logger.warn("Could not parse config; skipping {}\n{}", s, e.getLocalizedMessage());
            }
        }
    }

    public void updateSwimSpeedList() {
        this.swimSpeedMultipliers.clear();
        for (String s : this.rawSwimSpeedMultipliers) {
            try {
                MutablePair<ResourceLocation, Float> pair = new MutablePair<>();
                new StringConfig(s)
                        .next().parseAs(ResourceLocation::new).validate(Utils::doesEntityExist).accept(pair::setLeft)
                        .next().parseAs(Float::parseFloat).accept(pair::setRight);

                this.swimSpeedMultipliers.put(pair.left, pair.right);
            } catch (StringConfigException e) {
                ROTM.logger.warn("Could not parse config; skipping {}\n{}", s, e.getLocalizedMessage());
            }
        }
    }
}
