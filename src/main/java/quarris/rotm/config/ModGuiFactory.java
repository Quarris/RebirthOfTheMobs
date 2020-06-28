package quarris.rotm.config;

import net.minecraftforge.fml.client.DefaultGuiFactory;
import quarris.rotm.ROTM;

public class ModGuiFactory extends DefaultGuiFactory {

    public ModGuiFactory() {
        super(ROTM.MODID, ROTM.NAME);
    }

}
