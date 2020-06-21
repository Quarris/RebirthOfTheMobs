package quarris.rotm.event;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import quarris.rotm.ROTM;

@Mod.EventBusSubscriber(modid = ROTM.MODID)
public class MiscEventHandler {

    @SubscribeEvent
    public static void increaseSwimSpeed(LivingEvent.LivingUpdateEvent event) {

    }
}
