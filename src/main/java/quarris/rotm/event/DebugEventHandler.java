package quarris.rotm.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import quarris.rotm.ROTM;
import quarris.rotm.items.ModItems;

@Mod.EventBusSubscriber(modid = ROTM.MODID)
public class DebugEventHandler {

    @SubscribeEvent
    public static void onDamageTaken(LivingDamageEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = ((EntityPlayer) event.getEntityLiving());
            if (player.getHeldItemMainhand().getItem() == ModItems.debugDamages || player.getHeldItemOffhand().getItem() == ModItems.debugDamages) {
                player.sendMessage(new TextComponentString(event.getSource().getDamageType()));
            }
        }
    }
}
