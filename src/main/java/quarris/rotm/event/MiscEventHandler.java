package quarris.rotm.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import quarris.rotm.ROTM;
import quarris.rotm.config.ModConfigs;
import quarris.rotm.utils.Utils;

@Mod.EventBusSubscriber(modid = ROTM.MODID)
public class MiscEventHandler {

    @SubscribeEvent
    public static void addSwimSpeedAttribute(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityLivingBase) {
            EntityLivingBase entity = (EntityLivingBase) event.getEntity();
            float multiplier = ModConfigs.miscConfigs.swimSpeedMultipliers.getOrDefault(Utils.getEntityName(entity), 1.0f);

            if (ModConfigs.miscConfigs.treatGlobalSwimSpeedAsAllowlist == ModConfigs.miscConfigs.globalSwimSpeedBlocklist.contains(Utils.getEntityName(entity))) {
                multiplier *= ModConfigs.miscConfigs.globalSwimSpeedMultiplier;
            }

            if (multiplier != 1.0f) {
                entity.getEntityAttribute(EntityLivingBase.SWIM_SPEED).applyModifier(new AttributeModifier("SwimSpeedModifier", multiplier, 1));
            }
        }
    }

    @SubscribeEvent
    public static void updateSwimmingSpeed(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();

        if (entity.isInWater()) {
            float multiplier = ModConfigs.miscConfigs.swimSpeedMultipliers.getOrDefault(Utils.getEntityName(entity), 1.0f);

            if (ModConfigs.miscConfigs.treatGlobalSwimSpeedAsAllowlist == ModConfigs.miscConfigs.globalSwimSpeedBlocklist.contains(Utils.getEntityName(entity))) {
                multiplier *= ModConfigs.miscConfigs.globalSwimSpeedMultiplier;
            }

            entity.motionY /= multiplier;
        }
    }
}
