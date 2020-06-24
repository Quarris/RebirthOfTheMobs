package quarris.rotm.event;

import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import quarris.rotm.ROTM;
import quarris.rotm.capability.SpawnSummonsCap;
import quarris.rotm.config.ModConfigs;

import java.util.Collection;
import java.util.Random;

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

            Collection<String> sourcesToCancel = ModConfigs.entityConfigs.damagesToCancel
                    .get(entity instanceof EntityPlayer ? PLAYER_RES : EntityList.getKey(entity));

            if (sourcesToCancel.contains(source.getDamageType())) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void summonSpawns(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving().world.isRemote || event.getEntityLiving().isDead || !event.getEntityLiving().hasCapability(SpawnSummonsCap.instance, null))
            return;

        SpawnSummonsCap cap = event.getEntityLiving().getCapability(SpawnSummonsCap.instance, null);
        cap.update();
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void summonSpawnsDespawnOnDeath(LivingDeathEvent event) {
        if (event.getEntityLiving().world.isRemote || !event.getEntityLiving().hasCapability(SpawnSummonsCap.instance, null))
            return;

        SpawnSummonsCap cap = event.getEntityLiving().getCapability(SpawnSummonsCap.instance, null);
        cap.despawnSummons();
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void disableXPOnDeath(LivingExperienceDropEvent event) {
        if (event.getEntityLiving().getEntityData().getBoolean("DisableXP")) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void disableLootOnDeath(LivingDropsEvent event) {
        if (event.getEntityLiving().getEntityData().getBoolean("DisableLoot")) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void deathSpawn(LivingDeathEvent event) {
        if (event.getEntityLiving().world.isRemote)
            return;

        EntityLivingBase entity = event.getEntityLiving();
        ModConfigs.entityConfigs.deathSpawns.get(EntityList.getKey(event.getEntityLiving())).stream()
                .forEach(spawn -> {
                    Random random = new Random();
                    int amount = spawn.minSpawn + random.nextInt(spawn.maxSpawn - spawn.minSpawn + 1);
                    boolean spawned = false;
                    for (int i = 0; i < amount; i++) {
                        Entity toSpawn = EntityList.createEntityByIDFromName(spawn.summon, entity.world);
                        if (toSpawn == null) {
                            continue;
                        }

                        if (toSpawn instanceof EntityLiving) {
                            ((EntityLiving) toSpawn).onInitialSpawn(entity.world.getDifficultyForLocation(toSpawn.getPosition()), null);
                        }

                        toSpawn.deserializeNBT(spawn.nbt);

                        if (spawn.disableLoot) {
                            toSpawn.getEntityData().setBoolean("DisableXP", true);
                        }

                        if (spawn.disableXP) {
                            toSpawn.getEntityData().setBoolean("DisableLoot", true);
                        }

                        BlockPos entityPos = entity.getPosition();
                        int tries = 50;
                        for (int j = 0; j < tries; j++) {
                            toSpawn.setLocationAndAngles(
                                    entityPos.getX() + random.nextFloat() * 8 - 4,
                                    entityPos.getY() + random.nextInt(5) - 2,
                                    entityPos.getZ() + random.nextFloat() * 8 - 4,
                                    random.nextFloat() * 360.0F,
                                    0.0F);

                            if (WorldEntitySpawner.canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.getPlacementForEntity(toSpawn.getClass()), entity.world, toSpawn.getPosition()) && entity.world.spawnEntity(toSpawn)) {
                                spawned = true;
                                break;
                            }
                        }
                    }
                    if (spawned) {
                        entity.world.playSound(null, entity.getPosition(), ForgeRegistries.SOUND_EVENTS.getValue(spawn.sound), SoundCategory.HOSTILE, 1, 1);
                    }
                });
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void applyMeleeEffect(LivingAttackEvent event) {

    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void applyMeleeDefenseEffect(LivingHurtEvent event) {

    }

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityLivingBase && ModConfigs.entityConfigs.summonSpawns.keySet().contains(EntityList.getKey(event.getObject()))) {
            event.addCapability(new ResourceLocation(ROTM.MODID, "summonspawn"), new SpawnSummonsCap((EntityLivingBase) event.getObject()));
        }
    }
}
