package quarris.rotm.capability;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import quarris.rotm.config.ModConfigs;
import quarris.rotm.config.types.SummonSpawnType;
import quarris.rotm.utils.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class SpawnSummonsCap implements ICapabilitySerializable<NBTTagCompound> {

    @CapabilityInject(SpawnSummonsCap.class)
    public static Capability<SpawnSummonsCap> instance;

    public static void register() {
        CapabilityManager.INSTANCE.register(SpawnSummonsCap.class, new Capability.IStorage<SpawnSummonsCap>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<SpawnSummonsCap> capability, SpawnSummonsCap instance, EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<SpawnSummonsCap> capability, SpawnSummonsCap instance, EnumFacing side, NBTBase nbt) {

            }
        }, () -> null);
    }

    public final EntityLivingBase entity;
    public final Table<ResourceLocation, Integer, SummonSpawn> spawns;

    public SpawnSummonsCap(EntityLivingBase entity) {
        this.entity = entity;
        this.spawns = HashBasedTable.create();

        ModConfigs.entityConfigs.summonSpawns.get(EntityList.getKey(entity)).stream()
                .filter(Objects::nonNull)
                .forEach(entry -> this.spawns.put(entry.summon, entry.id, new SummonSpawn(entry, entity.world.getTotalWorldTime())));
    }

    public void update() {
        for (SummonSpawn summonSpawn : this.spawns.values()) {
            summonSpawn.updateSummonList();
            summonSpawn.attemptSpawn();
        }
    }

    public void despawnSummons() {
        for (SummonSpawn summonSpawn : this.spawns.values()) {
            summonSpawn.despawnSummons();
        }
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        for (Table.Cell<ResourceLocation, Integer, SummonSpawn> cell : this.spawns.cellSet()) {
            NBTTagCompound summonSpawnNBT = new NBTTagCompound();
            summonSpawnNBT.setString("StringID", cell.getRowKey().toString());
            summonSpawnNBT.setInteger("IntID", cell.getColumnKey());
            summonSpawnNBT.setTag("SummonData", cell.getValue().serializeNBT());
            list.appendTag(summonSpawnNBT);
        }
        nbt.setTag("SummonSpawns", list);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        NBTTagList list = nbt.getTagList("SummonSpawns", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound summonNbt = list.getCompoundTagAt(i);
            ResourceLocation stringID = new ResourceLocation(summonNbt.getString("StringID"));
            int intID = summonNbt.getInteger("IntID");
            NBTTagCompound summonData = summonNbt.getCompoundTag("SummonData");
            if (this.spawns.contains(stringID, intID)) {
                this.spawns.get(stringID, intID).deserializeNBT(summonData);
            }
        }
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == instance;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == instance) {
            return (T) this;
        }
        return null;
    }

    public class SummonSpawn implements INBTSerializable<NBTTagCompound> {

        public final Random random = new Random();
        public final SummonSpawnType entry;
        private long nextSpawnTime;
        private int totalSpawned;
        private final List<UUID> summonedEntities;
        private final List<UUID> attemptedSummons;

        private boolean madeSound;

        public SummonSpawn(SummonSpawnType entry, long time) {
            this.entry = entry;
            this.nextSpawnTime = time;
            this.summonedEntities = new LinkedList<>();
            this.attemptedSummons = new LinkedList<>();
        }

        public boolean canSpawn(World world, EntityLivingBase master) {
            if (world.getTotalWorldTime() >= this.nextSpawnTime &&
                    (master.getHealth() / master.getMaxHealth()) <= this.entry.health &&
                    (this.entry.bypassMaxSpawns || this.summonedEntities.size() < this.entry.maxSpawn) &&
                    (this.entry.cap <= 0 || this.totalSpawned < this.entry.cap)) {
                return !this.entry.requireTarget || !(master instanceof EntityLiving) || ((EntityLiving) master).getAttackTarget() != null;
            }

            return false;
        }

        /**
         * This method calculates the amount of summons which can spawn in the next cycle.
         * It takes into account whether it can bypass the maxSpawns and if it has a cap.
         *
         * @return The amount of summons that can spawn in the next cycle.
         */
        public int getNextSpawnAmount() {
            int nextRandom = this.entry.minSpawn + this.random.nextInt(this.entry.maxSpawn - this.entry.minSpawn + 1);

            if (!this.entry.bypassMaxSpawns) {
                int currentCount = this.summonedEntities.size();
                if (currentCount < this.entry.maxSpawn) {
                    int toSpawn = Math.max(nextRandom, this.entry.maxSpawn - currentCount);
                    if (this.entry.cap > 0) {
                        return Math.min(toSpawn, this.entry.cap - this.totalSpawned);
                    }

                    return toSpawn;
                }

                return 0;
            } else {
                if (this.entry.cap > 0) {
                    return Math.min(nextRandom, this.entry.cap - this.totalSpawned);
                }

                return nextRandom;
            }
        }

        public void setRandomCooldownFrom(long time) {
            long cooldown = this.entry.minCooldownTicks + this.random.nextInt(this.entry.maxCooldownTicks - this.entry.minCooldownTicks + 1);
            this.nextSpawnTime = time + cooldown;
        }

        public void updateSummonList() {
            EntityLivingBase master = SpawnSummonsCap.this.entity;
            World world = master.world;
            // See if any of the attempted summons are actually in the world
            boolean spawned = false;
            for (int i = this.attemptedSummons.size() - 1; i >= 0; i--) {
                UUID attemptedSummonUUID = this.attemptedSummons.get(i);
                if (world.loadedEntityList.stream().anyMatch(e -> attemptedSummonUUID.equals(e.getUniqueID()))) {
                    spawned = true;
                    this.summonedEntities.add(attemptedSummonUUID);
                    this.totalSpawned++;
                }
            }
            if (spawned) {
                if (this.entry.sound != null && !this.madeSound) {
                    world.playSound(null, master.getPosition(), ForgeRegistries.SOUND_EVENTS.getValue(this.entry.sound), SoundCategory.HOSTILE, 1, 1);
                }
                this.setRandomCooldownFrom(master.world.getTotalWorldTime());
            }
            this.attemptedSummons.clear();
            // Update the current summons list if the entity has died or otherwise.
            for (int i = this.summonedEntities.size() - 1; i >= 0; i--) {
                UUID summonedUUID = this.summonedEntities.get(i);
                if (!SpawnSummonsCap.this.entity.world.loadedEntityList.stream().anyMatch(e -> summonedUUID.equals(e.getUniqueID()))) {
                    this.summonedEntities.remove(i);
                }
            }

            this.madeSound = false;
        }

        public void attemptSpawn() {
            EntityLivingBase master = SpawnSummonsCap.this.entity;
            if (!master.world.isRemote && this.canSpawn(master.world, master)) {
                int amount = this.getNextSpawnAmount();
                boolean spawned = false;
                for (int i = 0; i < amount; i++) {
                    Entity toSpawn = EntityList.createEntityByIDFromName(this.entry.summon, master.world);
                    if (toSpawn == null) {
                        continue;
                    }

                    if (toSpawn instanceof EntityLiving) {
                        ((EntityLiving) toSpawn).onInitialSpawn(master.world.getDifficultyForLocation(toSpawn.getPosition()), null);
                    }

                    toSpawn.deserializeNBT(this.entry.nbt);

                    if (this.entry.disableLoot) {
                        toSpawn.getEntityData().setBoolean("DisableXP", true);
                    }

                    if (this.entry.disableXP) {
                        toSpawn.getEntityData().setBoolean("DisableLoot", true);
                    }

                    if (toSpawn instanceof EntityLiving && master instanceof EntityLiving && this.entry.autoAggro) {
                        ((EntityLiving) toSpawn).setAttackTarget(((EntityLiving) master).getAttackTarget());
                    }

                    int tries = 50;
                    for (int j = 0; j < tries; j++) {
                        toSpawn.setLocationAndAngles(
                                master.posX + this.random.nextFloat() * 8 - 4,
                                master.posY + this.random.nextInt(5) - 2,
                                master.posZ + this.random.nextFloat() * 8 - 4,
                                this.random.nextFloat() * 360.0F,
                                0.0F);

                        if (!Utils.isEntityInCollision(master.world, toSpawn) && master.world.spawnEntity(toSpawn)) {
                            spawned = true;
                            this.summonedEntities.add(toSpawn.getUniqueID());
                            this.totalSpawned++;
                            break;
                        }
                    }
                    // Some mods cancel the EntityJoinWorldEvent in the spawnEntity(Entity) method, and then add the entity themselves which could cause severe problems
                    // Keep track of the uuids we tried to spawn, and then in the next tick update our summoned list accordingly
                    this.attemptedSummons.add(toSpawn.getUniqueID());
                }
                if (spawned) {
                    if (this.entry.sound != null) {
                        master.world.playSound(null, master.getPosition(), ForgeRegistries.SOUND_EVENTS.getValue(this.entry.sound), SoundCategory.HOSTILE, 1, 1);
                        this.madeSound = true;
                    }
                    this.setRandomCooldownFrom(master.world.getTotalWorldTime());
                }
            }
        }

        public void despawnSummons() {
            EntityLivingBase master = SpawnSummonsCap.this.entity;
            List<Entity> loadedEntityList = master.world.loadedEntityList;
            for (int i = 0; i < loadedEntityList.size(); i++) {
                Entity e = loadedEntityList.get(i);
                if (!e.isDead && this.summonedEntities.contains(e.getUniqueID())) {
                    e.setDead();
                }
            }
        }

        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound nbt = new NBTTagCompound();
            NBTTagList summonedList = new NBTTagList();
            for (UUID uuid : this.summonedEntities) {
                summonedList.appendTag(NBTUtil.createUUIDTag(uuid));
            }

            nbt.setTag("SummonedList", summonedList);
            nbt.setLong("NextSpawnTime", this.nextSpawnTime);
            nbt.setInteger("TotalSpawned", this.totalSpawned);
            return nbt;
        }

        @Override
        public void deserializeNBT
                (NBTTagCompound
                         nbt) {
            this.summonedEntities.clear();
            NBTTagList summonedList = nbt.getTagList("SummonedList", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < summonedList.tagCount(); i++) {
                this.summonedEntities.add(NBTUtil.getUUIDFromTag(summonedList.getCompoundTagAt(i)));
            }

            this.nextSpawnTime = nbt.getLong("NextSpawnTime");
            this.totalSpawned = nbt.getInteger("TotalSpawned");
        }

        @Override
        public String toString
                () {
            final StringBuilder sb = new StringBuilder("SummonSpawn{");
            sb.append("entry=").append(entry);
            sb.append('}');
            return sb.toString();
        }
    }
}
