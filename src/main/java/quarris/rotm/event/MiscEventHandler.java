package quarris.rotm.event;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
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

    /*
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void canEntitySpawn(LivingSpawnEvent.CheckSpawn event) {
        if (ModConfigs.miscConfigs.naturalSpawnBuff) {
            if (!event.isSpawner()) {
                if (ModConfigs.miscConfigs.ignoreBlockSpawns.contains(Utils.getEntityName(event.getEntity())))
                    return;

                EntityLivingBase entity = event.getEntityLiving();
                World world = entity.world;
                BlockPos pos = new BlockPos(event.getX(), event.getY(), event.getZ());

                BlockPos downPos = pos.down();
                IBlockState downState = world.getBlockState(downPos);

                boolean isOnSolidGround = downState.getMaterial().isSolid();
                boolean isEntityInCollision = Utils.isEntityInCollision(world, entity);

                if (!isOnSolidGround || isEntityInCollision) {
                    event.setResult(Event.Result.DENY);
                }
            }
        }
    }
    */
}
