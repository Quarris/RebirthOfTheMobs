package quarris.rotm.config;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class SummonSpawnType {

    public final ResourceLocation summon;   // Entity being summoned
    public final int id;                    // ID to differentiate between master:summon entries
    public final float health;              // The percentage of health the master needs to be for this summon to trigger
    public final int minSpawn;              // Minimum amount to spawn per cycle
    public final int maxSpawn;              // Maximum amount to spawn per cycle
    public final int minCooldownTicks;      // Minimum cooldown after spawn
    public final int maxCooldownTicks;      // Maximum cooldown after spawn
    public final boolean bypassMaxSpawns;   // Can bypass the max amount spawned
    public final boolean despawnOnDeath;    // Do the summoned entities die when the master entity dies
    public final int cap;                   // Hard cap on the total amount of this summon that can spawn by the master entity
    public final boolean disableXP;         // Should XP be dropped by the summons on death
    public final boolean disableLoot;       // Should Loot be dropped by the summons on death
    public final ResourceLocation sound;    // The sound to be played when the summon happens
    public final NBTTagCompound nbt;        // NBT to apply to the summoned entity on spawn


    SummonSpawnType(ResourceLocation summon, int id, float health, int minSpawn, int maxSpawn, int minCooldown, int maxCooldown, boolean bypassMaxSpawns, boolean despawnOnDeath, int cap, boolean disableXP, boolean disableLoot, ResourceLocation sound, NBTTagCompound nbt) {
        this.summon = summon;
        this.id = id;
        this.health = health;
        this.minSpawn = minSpawn;
        this.maxSpawn = maxSpawn;
        this.minCooldownTicks = minCooldown * 20;
        this.maxCooldownTicks = maxCooldown * 20;
        this.bypassMaxSpawns = bypassMaxSpawns;
        this.despawnOnDeath = despawnOnDeath;
        this.cap = cap;
        this.disableXP = disableXP;
        this.disableLoot = disableLoot;
        this.sound = sound;
        this.nbt = nbt;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SummonType{");
        sb.append("summon=").append(summon);
        sb.append(", id=").append(id);
        sb.append(", health=").append(health);
        sb.append(", minSpawn=").append(minSpawn);
        sb.append(", maxSpawn=").append(maxSpawn);
        sb.append(", minCooldownTicks=").append(minCooldownTicks);
        sb.append(", maxCooldownTicks=").append(maxCooldownTicks);
        sb.append(", bypassMaxSpawns=").append(bypassMaxSpawns);
        sb.append(", despawnOnDeath=").append(despawnOnDeath);
        sb.append(", cap=").append(cap);
        sb.append(", disableXP=").append(disableXP);
        sb.append(", disableLoot=").append(disableLoot);
        sb.append(", sound=").append(sound);
        sb.append(", nbt=").append(nbt);
        sb.append('}');
        return sb.toString();
    }

    static class Builder {
        public ResourceLocation summon;   // Entity being summoned
        public int id;
        public float health;              // The percentage of health the master needs to be for this summon to trigger
        public int minSpawn;              // Minimum amount to spawn per cycle
        public int maxSpawn;              // Maximum amount to spawn per cycle
        public int minCooldown;           // Minimum cooldown after spawn
        public int maxCooldown;           // Maximum cooldown after spawn
        public boolean bypassMaxSpawns;   // Can bypass the max amount spawned
        public boolean despawnOnDeath;    // Do the summoned entities die when the master entity dies
        public int cap;                   // Hard cap on the total amount of this summon that can spawn by the master entity
        public boolean disableXP;         // Should XP be dropped by the summons on death
        public boolean disableLoot;       // Should Loot be dropped by the summons on death
        public ResourceLocation sound;    // The sound to be played when the summon happens
        public NBTTagCompound nbt;

        public Builder() { }

        public Builder summon(ResourceLocation summon) {
            this.summon = summon;
            return this;
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder health(float health) {
            this.health = health/100f;
            return this;
        }

        public Builder minSpawn(int minSpawn) {
            this.minSpawn = minSpawn;
            return this;
        }

        public Builder maxSpawn(int maxSpawn) {
            this.maxSpawn = maxSpawn;
            return this;
        }

        public Builder minCooldown(int minCooldown) {
            this.minCooldown = minCooldown;
            return this;
        }

        public Builder maxCooldown(int maxCooldown) {
            this.maxCooldown = maxCooldown;
            return this;
        }

        public Builder bypassMaxSpawns(boolean bypassMaxSpawns) {
            this.bypassMaxSpawns = bypassMaxSpawns;
            return this;
        }

        public Builder despawnOnDeath(boolean despawnOnDeath) {
            this.despawnOnDeath = despawnOnDeath;
            return this;
        }

        public Builder cap(int cap) {
            this.cap = cap;
            return this;
        }

        public Builder disableXP(boolean disableXP) {
            this.disableXP = disableXP;
            return this;
        }


        public Builder disableLoot(boolean disableLoot) {
            this.disableLoot = disableLoot;
            return this;
        }

        public Builder sound(ResourceLocation sound) {
            this.sound = sound;
            return this;
        }

        public Builder nbt(NBTTagCompound nbt) {
            this.nbt = nbt;
            return this;
        }

        public SummonSpawnType build() {
            return new SummonSpawnType(summon, id, health, minSpawn, maxSpawn, minCooldown, maxCooldown, bypassMaxSpawns, despawnOnDeath, cap, disableXP, disableLoot, sound, nbt);
        }
    }
}
