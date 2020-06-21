package quarris.rotm.config;

import net.minecraft.util.ResourceLocation;

public class SummonType {

    public final ResourceLocation summon;   // Entity being summoned
    public final float healthPerc;          // The percentage of health the master needs to be for this summon to trigger
    public final int minSpawn;              // Minimum amount to spawn per cycle
    public final int maxSpawn;              // Maximum amount to spawn per cycle
    public final int minCooldownTicks;      // Minimum cooldown after spawn
    public final int maxCooldownTicks;      // Maximum cooldown after spawn
    public final boolean bypassMaxSpawns;   // Can bypass the max amount spawned
    public final boolean despawnOnDeath;    // Do the summoned entities die when the master entity dies
    public final int cap;                // Hard cap on the total amount of this summon that can spawn by the master entity
    public final boolean disableXP;         // Should XP be dropped by the summons on death
    public final boolean disableLoot;       // Should Loot be dropped by the summons on death
    public final ResourceLocation sound;    // The sound to be played when the summon happens
    public final int id;


    public SummonType(ResourceLocation summon, int id, float healthPerc, int minSpawn, int maxSpawn, int minCooldown, int maxCooldown, boolean bypassMaxSpawns, boolean despawnOnDeath, int cap, boolean disableXP, boolean disableLoot, ResourceLocation sound) {
        this.summon = summon;
        this.id = id;
        this.healthPerc = healthPerc;
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
    }

    public SummonType(ResourceLocation summon, float healthPerc, int minSpawn, int maxSpawn, int minCooldown, int maxCooldown, boolean bypassMaxSpawns, boolean despawnOnDeath, int cap, boolean disableXP, boolean disableLoot, ResourceLocation sound) {
        this(summon, 0, healthPerc, minSpawn, maxSpawn, minCooldown, maxCooldown, bypassMaxSpawns, despawnOnDeath, cap, disableXP, disableLoot, sound);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SummonType{");
        sb.append("summon=").append(summon);
        sb.append(", id=").append(id);
        sb.append(", healthPerc=").append(healthPerc);
        sb.append(", minSpawn=").append(minSpawn);
        sb.append(", maxSpawn=").append(maxSpawn);
        sb.append(", minCooldownTicks=").append(minCooldownTicks);
        sb.append(", maxCooldownTicks=").append(maxCooldownTicks);
        sb.append(", bypassMaxSpawns=").append(bypassMaxSpawns);
        sb.append(", despawnOnDeath=").append(despawnOnDeath);
        sb.append(", maxCap=").append(cap);
        sb.append(", disableXP=").append(disableXP);
        sb.append(", disableLoot=").append(disableLoot);
        sb.append(", sound=").append(sound);
        sb.append('}');
        return sb.toString();
    }
}
