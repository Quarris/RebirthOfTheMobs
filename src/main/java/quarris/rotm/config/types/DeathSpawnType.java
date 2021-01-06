package quarris.rotm.config.types;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class DeathSpawnType {
    public final ResourceLocation summon;   // Entity being summoned
    public final int minSpawn;              // Minimum amount to spawn per cycle
    public final int maxSpawn;              // Maximum amount to spawn per cycle
    public final boolean disableXP;         // Should XP be dropped by the summons on death
    public final boolean disableLoot;       // Should Loot be dropped by the summons on death
    public final boolean autoAggro;
    public final ResourceLocation sound;    // The sound to be played when the summon happens
    public final NBTTagCompound nbt;        // NBT to apply to the summoned entity on spawn
    public final float chance;              // Chance that the mobs are spawned

    public DeathSpawnType(ResourceLocation summon, int minSpawn, int maxSpawn, boolean disableXP, boolean disableLoot, boolean autoAggro, ResourceLocation sound, NBTTagCompound nbt, float chance) {
        this.summon = summon;
        this.minSpawn = minSpawn;
        this.maxSpawn = maxSpawn;
        this.disableXP = disableXP;
        this.disableLoot = disableLoot;
        this.autoAggro = autoAggro;
        this.sound = sound;
        this.nbt = nbt;
        this.chance = chance;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DeathSummonType{");
        sb.append("summon=").append(summon);
        sb.append(", minSpawn=").append(minSpawn);
        sb.append(", maxSpawn=").append(maxSpawn);
        sb.append(", disableXP=").append(disableXP);
        sb.append(", disableLoot=").append(disableLoot);
        sb.append(", sound=").append(sound);
        sb.append(", nbt=").append(nbt);
        sb.append(", chance=").append(chance);
        sb.append('}');
        return sb.toString();
    }

    public static class Builder {
        public ResourceLocation summon;   // Entity being summoned
        public int minSpawn;              // Minimum amount to spawn per cycle
        public int maxSpawn;              // Maximum amount to spawn per cycle
        public boolean disableXP;         // Should XP be dropped by the summons on death
        public boolean disableLoot;       // Should Loot be dropped by the summons on death
        public ResourceLocation sound;    // The sound to be played when the summon happens
        public NBTTagCompound nbt;
        public boolean autoAggro;
        public float chance;              // Chance that the mobs are spawned


        Builder() { }

        public Builder summon(ResourceLocation summon) {
            this.summon = summon;
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

        public Builder disableXP(boolean disableXP) {
            this.disableXP = disableXP;
            return this;
        }


        public Builder disableLoot(boolean disableLoot) {
            this.disableLoot = disableLoot;
            return this;
        }

        public Builder autoAggro(boolean autoAggro) {
            this.autoAggro = autoAggro;
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

        public Builder chance(float chance) {
            this.chance = chance / 100f;
            return this;
        }

        public DeathSpawnType build() {
            return new DeathSpawnType(summon, minSpawn, maxSpawn, disableXP, disableLoot, autoAggro, sound, nbt, chance);
        }
    }
}
