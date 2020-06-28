package quarris.rotm.config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import quarris.rotm.ROTM;

import java.util.ArrayList;
import java.util.List;

public class GuiModConfigs extends GuiConfig {

    public GuiModConfigs(GuiScreen parentScreen) {
        super(parentScreen, getConfigElements(), ROTM.MODID, false, false, ROTM.NAME);
    }

    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> list = new ArrayList<>();
        return list;
    }
}
