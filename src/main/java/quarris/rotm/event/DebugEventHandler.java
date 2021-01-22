package quarris.rotm.event;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import quarris.rotm.ROTM;
import quarris.rotm.config.ModConfigs;
import quarris.rotm.items.ModItems;
import quarris.rotm.utils.Utils;

@Mod.EventBusSubscriber(modid = ROTM.MODID)
public class DebugEventHandler {

    @SubscribeEvent
    public static void onDamageTaken(LivingDamageEvent event) {
        if (ModConfigs.debugConfigs.enableDebugMode) {
            DamageSource source = event.getSource();
            String damageType = source.getDamageType();
            String immediateSource = source.getImmediateSource() == null ? "null" : Utils.getEntityName(source.getImmediateSource()).toString();
            String trueSource = source.getTrueSource() == null ? "null" : Utils.getEntityName(source.getTrueSource()).toString();
            ITextComponent text = new TextComponentTranslation("debug_damages.print", damageType, immediateSource, trueSource);
            event.getEntity().getEntityData().setString("LastDamageSource", text.getFormattedText());
            if (event.getEntityLiving() instanceof EntityPlayerMP && event.getEntityLiving().getHeldItemMainhand().getItem() == ModItems.debugDamages) {
                event.getEntityLiving().sendMessage(text);
            }
        }
    }
}
