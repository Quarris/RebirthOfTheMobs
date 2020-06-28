package quarris.rotm.config;

import net.minecraftforge.common.config.Config;

public class DebugConfig implements ISubConfig {

    @Config.Comment({
            "Enables the debug mode.",
            "Debug mode enables debug items to register as well as commands to help with finding out certain values to use in the configs, such as entity/potion names or damage types."
    })
    public boolean enableDebugMode = false;

    @Override
    public void onConfigChanged() {

    }
}

