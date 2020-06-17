package quarris.rotm.events;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import quarris.rotm.ROTM;
import quarris.rotm.config.ModConfigs;

import java.util.Collection;

@Mod.EventBusSubscriber(modid = ROTM.MODID)
public class EntityEventHandler {

    public static final ResourceLocation PLAYER_RES = new ResourceLocation("player");

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void cancelPotionApply(PotionEvent.PotionApplicableEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (!entity.world.isRemote) {
            Collection<ResourceLocation> potionsToCancel = ModConfigs.entityConfigs.potionsToCancel
                    .get(entity instanceof EntityPlayer ? PLAYER_RES : EntityList.getKey(entity));

            if (potionsToCancel.contains(event.getPotionEffect().getPotion().getRegistryName())) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void cancelDamageSource(LivingAttackEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (!entity.world.isRemote) {
            DamageSource source = event.getSource();

            Collection<String> sourcesToCancel = ModConfigs.entityConfigs.damageSourcesToCancel
                    .get(entity instanceof EntityPlayer ? PLAYER_RES : EntityList.getKey(entity));

            if (sourcesToCancel.contains(source.getDamageType())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void aggroSpawn(LivingSetAttackTargetEvent event) {

    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void deathSpawn(LivingDeathEvent event) {

    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void applyMeleeEffect(LivingAttackEvent event) {

    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void applyMeleeDefenseEffect(LivingHurtEvent event) {

    }
}
