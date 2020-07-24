package quarris.rotm.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
import quarris.rotm.config.types.HealthRegainType;
import quarris.rotm.config.types.MobDefenseType;
import quarris.rotm.config.types.MobOffenseType;
import quarris.rotm.utils.Utils;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = ROTM.MODID)
public class EntityEventHandler {


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void cancelPotionApply(PotionEvent.PotionApplicableEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (!entity.world.isRemote) {
            Collection<ResourceLocation> potionsToCancel = ModConfigs.entityConfigs.potionsToCancel
                    .get(Utils.getEntityName(entity));

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
                    .get(Utils.getEntityName(entity));

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
        ModConfigs.entityConfigs.deathSpawns.get(Utils.getEntityName(entity)).stream()
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
                                    entityPos.getY() + random.nextInt(5) - 1.5d,
                                    entityPos.getZ() + random.nextFloat() * 8 - 4,
                                    random.nextFloat() * 360.0F,
                                    0.0F);

                            if (isPositionValidForSpawningSummons(toSpawn, entityPos) && entity.world.spawnEntity(toSpawn)) {
                                spawned = true;
                                break;
                            }
                        }
                    }
                    if (spawned && spawn.sound != null) {
                        entity.world.playSound(null, entity.getPosition(), ForgeRegistries.SOUND_EVENTS.getValue(spawn.sound), SoundCategory.HOSTILE, 1, 1);
                    }
                });
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void applyMobOffenseEffect(LivingHurtEvent event) {
        if (!event.getEntityLiving().world.isRemote) {
            EntityLivingBase entity = event.getEntityLiving();
            DamageSource source = event.getSource();
            if (source.getTrueSource() instanceof EntityLivingBase) {
                EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
                Collection<MobOffenseType> offenseTypes = ModConfigs.entityConfigs.mobOffense.get(Utils.getEntityName(entity));
                System.out.println(offenseTypes);
                for (MobOffenseType type : offenseTypes) {
                    if (type.canApplyToEntity(entity) && (type.damageType.isEmpty() || type.damageType.equalsIgnoreCase(source.getDamageType()))) {
                        PotionEffect effect = new PotionEffect(ForgeRegistries.POTIONS.getValue(type.potion), type.duration, type.level);
                        attacker.addPotionEffect(effect);
                        if (type.sound != null) {
                            entity.world.playSound(null, entity.getPosition(), ForgeRegistries.SOUND_EVENTS.getValue(type.sound), SoundCategory.HOSTILE, 1, 1);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void applyMobDefenseEffect(LivingHurtEvent event) {
        if (!event.getEntityLiving().world.isRemote) {
            EntityLivingBase entity = event.getEntityLiving();
            DamageSource source = event.getSource();
            if (source.getTrueSource() instanceof EntityLivingBase) {
                EntityLivingBase attacker = (EntityLivingBase) source.getTrueSource();
                Collection<MobDefenseType> defenseTypes = ModConfigs.entityConfigs.mobDefenses.get(Utils.getEntityName(entity));
                for (MobDefenseType type : defenseTypes) {
                    if (type.canApplyToEntity(entity) && (type.damageType.isEmpty() || type.damageType.equalsIgnoreCase(source.getDamageType()))) {
                        PotionEffect effect = new PotionEffect(ForgeRegistries.POTIONS.getValue(type.potion), type.duration, type.level);
                        attacker.addPotionEffect(effect);
                        if (type.sound != null) {
                            entity.world.playSound(null, entity.getPosition(), ForgeRegistries.SOUND_EVENTS.getValue(type.sound), SoundCategory.HOSTILE, 1, 1);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void applyHealthRegain(LivingDeathEvent event) {
        World world = event.getEntityLiving().world;
        if (world.isRemote)
            return;

        EntityLivingBase dead = event.getEntityLiving();
        List<EntityLivingBase> loaded = world.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityLivingBase)
                .map(entity -> (EntityLivingBase) entity)
                .collect(Collectors.toList());

        for (EntityLivingBase entity : loaded) {
           List<HealthRegainType> types = ModConfigs.entityConfigs.healthRegains.get(EntityList.getKey(entity)).stream().filter(type -> type.target == null || type.target.equals(Utils.getEntityName(dead))).collect(Collectors.toList());
           for (HealthRegainType type : types) {
               if (type.radius <= 0 || dead.getDistanceSq(entity) <= type.radius * type.radius) {
                   if (!type.lastManStanding || loaded.stream().filter(e -> e != null && Utils.getEntityName(e).equals(type.target)).count() == 1) {
                       entity.heal(entity.getMaxHealth() * type.healthPercentage);
                   }
               }
           }
        }
    }

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityLivingBase && ModConfigs.entityConfigs.summonSpawns.keySet().contains(Utils.getEntityName(event.getObject()))) {
            event.addCapability(new ResourceLocation(ROTM.MODID, "summonspawn"), new SpawnSummonsCap((EntityLivingBase) event.getObject()));
        }
    }

    public static boolean isPositionValidForSpawningSummons(Entity entity, BlockPos pos) {
        if (!entity.world.getWorldBorder().contains(pos)) {
            return false;
        } else {
            return !Utils.isEntityInCollision(entity.world, entity);
        }
    }
}
