package quarris.rotm.config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.DefaultGuiFactory;
import quarris.rotm.ROTM;

public class ModGuiFactory extends DefaultGuiFactory {

    public ModGuiFactory() {
        super(ROTM.MODID, ROTM.NAME);
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        return new GuiModConfigs(parentScreen);
    }
}
