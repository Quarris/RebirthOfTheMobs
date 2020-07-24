package quarris.rotm.event;

import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import quarris.rotm.ROTM;
import quarris.rotm.config.ModConfigs;

@Mod.EventBusSubscriber(modid = ROTM.MODID)
public class DebugEventHandler {

    @SubscribeEvent
    public static void onDamageTaken(LivingDamageEvent event) {
        if (ModConfigs.debugConfigs.enableDebugMode) {
            event.getEntity().getEntityData().setString("LastDamageSource", event.getSource().getDamageType());
        }
    }
}
